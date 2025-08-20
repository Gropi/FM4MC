import seaborn as sns
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np

def create_heatmap(csv_file_path):
    try:
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

        # Daten einlesen
        data = pd.read_csv(csv_file_path, delimiter=';')


        # Ersetzen aller -1 durch NaN
        data.replace(-1, np.nan, inplace=True)

        # In Millisekunden umrechnen
        data['timeCalculatingConfigurations'] = data['timeCalculatingConfigurations'] / (1000 * 1000 * 60 * 60)

        pivot_table = data.pivot_table(index='amountOfAlternativesPerTask',
                                       columns='amountOfTasks',
                                       values='timeCalculatingConfigurations',
                                       aggfunc=np.mean)

        cmap = sns.color_palette("coolwarm", as_cmap=True)
        cmap.set_bad('darkred')  # Setzt NaN-Werte auf Dunkelrot

        fig, ax = plt.subplots(figsize=(12, 8))
        sns.heatmap(pivot_table, annot=False, fmt=".2f", cmap=cmap,
                    #vmin=pivot_table.min().min(), vmax=3,
                    cbar_kws={'label': 'Processing time (h)'}, )

        plt.ylabel('Alternatives')
        plt.xlabel('Tasks')

        # Invertieren der Y-Achse
        plt.gca().invert_yaxis()

        cbar = ax.collections[0].colorbar
        # Definieren Sie neue Ticks für die Colorbar
        new_ticks = np.linspace(pivot_table.min().min(), pivot_table.max().max(), num=5, endpoint=True)
        new_tick_labels = [f"{tick:.1f}" for tick in new_ticks]
        new_tick_labels[-1] = 'NaN'  # Letzten Tick-Label durch 'NaN' ersetzen

        # Setzen der neuen Ticks und Labels
        cbar.set_ticks(new_ticks)
        cbar.set_ticklabels(new_tick_labels)

        plt.tight_layout()
        plt.savefig(f"./Results/RQ1a/Figure5/Heatmap-Threshold_NoSlicing.pdf", format="pdf")
        #plt.show()

    except Exception as e:
        print(f"Ein Fehler ist aufgetreten: {e}")

# Beispielaufruf der Funktion (ersetze 'path/to/your/csvfile.csv' durch den tatsächlichen Pfad zur CSV-Datei)
csv_file_path = '../Storage_Slicing/2024_05_19_Measurement_Linear_FM_No_Slicing/benchmark_results.csv'
create_heatmap(csv_file_path)
