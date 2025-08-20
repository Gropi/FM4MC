import pandas as pd
import matplotlib.pyplot as plt
import numpy as np
from matplotlib.ticker import FuncFormatter


def format_log_tick(val, pos=None):
    """
    Custom formatter for the x-axis to display ticks as 10^val.
    Since the x-axis is based on log10(complexity), val represents the exponent.
    """
    return f"$10^{{{int(val)}}}$"


def load_and_preprocess_data(csv_path):
    """
    Loads the CSV data, filters out invalid rows, and computes:
      - complexity
      - log_complexity (log10(complexity))
      - storage_MB
    """
    data = pd.read_csv(csv_path, delimiter=';')

    # Remove rows with -1 or NaN values
    data = data[(data != -1).all(axis=1)].dropna()

    # Filter rows with non-positive values in the relevant columns
    data = data[
        (data['amountOfAlternativesPerTask'] > 0) &
        (data['amountOfTasks'] > 0) &
        (data['fileSizeInByte'] > 0)
        ]

    # Calculate complexity and log10(Complexity)
    data['complexity'] = data['amountOfAlternativesPerTask'] ** data['amountOfTasks']
    with np.errstate(divide='ignore', invalid='ignore'):
        data['log_complexity'] = np.log10(data['complexity'])

    # Remove invalid values (NaN or ±∞)
    data = data.replace([np.inf, -np.inf], np.nan).dropna(subset=['log_complexity'])

    # Calculate storage consumption in MB
    data['storage_MB'] = data['fileSizeInByte'] / (1024 * 1024)

    return data


def group_data_by_log_complexity(data):
    """
    Groups the data by log_complexity (i.e. by exactly the same log value)
    and computes the minimum, maximum, and mean of the storage consumption.
    """
    grouped = data.groupby('log_complexity')['storage_MB'].agg(['min', 'max', 'mean']).reset_index()
    return grouped.sort_values(by='log_complexity')


def perform_log_log_regression_on_means(grouped_data, degree=1, num_points=300):
    """
    Performs a log-log regression (power law) on the average storage consumption.

    - x = log10(complexity) (from grouped_data['log_complexity'])
    - y = log10(mean storage_MB)

    Returns:
      x_smooth: finely subdivided x-values in log10(complexity)
      y_smooth: the regression-based predictions in the NORMAL MB scale
    """
    x = grouped_data['log_complexity'].values
    y_log = np.log10(grouped_data['mean'].values)  # log10 of the mean

    # Linear regression in log-log coordinates
    coeffs = np.polyfit(x, y_log, degree)
    poly = np.poly1d(coeffs)

    x_smooth = np.linspace(x.min(), x.max(), num_points)
    y_smooth_log = poly(x_smooth)

    # Convert back to the normal MB scale
    y_smooth = 10 ** y_smooth_log
    return x_smooth, y_smooth


def create_plot(range_grouped, threshold_grouped, range_reg, threshold_reg):
    fig, ax = plt.subplots(figsize=(12, 8))

    # --- No-Slicing: Fläche grau mit diagonaler Schraffur + schwarze Linie ---
    ax.fill_between(range_grouped['log_complexity'], range_grouped['min'], range_grouped['max'],
                    facecolor='#d3d3d3', hatch='///', edgecolor='black', linewidth=0.5,
                    label='No-Slicing Min-Max Range')

    ax.plot(range_reg[0], range_reg[1],
            color='black', linestyle='-', linewidth=2,
            label='No-Slicing (log-log Regression)')

    # --- Slicing: hellere Fläche mit anderer Schraffur + graue gestrichelte Linie mit Marker ---
    ax.fill_between(threshold_grouped['log_complexity'], threshold_grouped['min'], threshold_grouped['max'],
                    facecolor='white', hatch='\\\\\\', edgecolor='black', linewidth=0.5,
                    label='Slicing Min-Max Range')

    ax.plot(threshold_reg[0], threshold_reg[1],
            color='black', linestyle='--', linewidth=2,
            label='Slicing (log-log Regression)')

    # Achsenbeschriftung
    ax.set_xlabel('# Valid Configurations (log scale)')
    ax.set_ylabel('Storage Consumption in MB (log scale)')
    ax.set_yscale('log')
    ax.xaxis.set_major_formatter(FuncFormatter(format_log_tick))
    ax.xaxis.set_major_locator(plt.MaxNLocator(integer=True))

    # Legende und Layout
    ax.legend()
    ax.grid(True, which="major", ls="-", alpha=0.7)

    plt.tight_layout()
    plt.savefig("Results/RQ1b/Figure6.pdf", format="pdf")
    plt.show()



def plot_complexity_vs_storage(range_csv, threshold_csv):
    try:
        # Update Matplotlib settings
        plt.rcParams.update({
            "text.usetex": False,
            "font.family": "serif",
            "font.serif": ["DejaVu Serif"],
            "axes.labelsize": 22,
            "font.size": 22,
            "legend.fontsize": 20,
            "xtick.labelsize": 20,
            "ytick.labelsize": 20,
        })

        # Load data
        range_data = load_and_preprocess_data(range_csv)
        threshold_data = load_and_preprocess_data(threshold_csv)

        # Group data
        range_grouped = group_data_by_log_complexity(range_data)
        threshold_grouped = group_data_by_log_complexity(threshold_data)

        # --------------------------------------------------
        # Only show values with log_complexity >= 3 (i.e. 10^3)
        range_grouped = range_grouped[range_grouped['log_complexity'] >= 1]
        threshold_grouped = threshold_grouped[threshold_grouped['log_complexity'] >= 1]
        # --------------------------------------------------

        # Regression on the mean (log-log)
        range_reg = perform_log_log_regression_on_means(range_grouped)
        threshold_reg = perform_log_log_regression_on_means(threshold_grouped)

        # Create the plot
        create_plot(range_grouped, threshold_grouped, range_reg, threshold_reg)

    except Exception as e:
        print(f"An error occurred: {e}")


if __name__ == '__main__':
    range_csv = '../Storage_Slicing/2024_05_19_Measurement_Linear_FM_No_Slicing/benchmark_results.csv'
    threshold_csv = '../Storage_Slicing/2025_03_11_Measurement_Linear_FM_Slicing/benchmark_results.csv'
    plot_complexity_vs_storage(range_csv, threshold_csv)
