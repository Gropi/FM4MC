import os
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from matplotlib.ticker import LogLocator, NullFormatter


def create_combined_heatmap(main_excel):
    try:
        plt.rcParams.update({
            "text.usetex": False,
            "font.family": "serif",
            "font.serif": ["DejaVu Serif"],
            "axes.labelsize": 16,
            "font.size": 16,
            "legend.fontsize": 14,
            "xtick.labelsize": 12,
            "ytick.labelsize": 12,
        })

        # Mapping für unseren Ansatz (Spalte Param: _EdgeIndex)
        edge_mapping = {
            1: "Country Side",
            2: "Small City",
            3: "Highway",
            4: "Medium City",
            5: "Full",
        }
        # Mapping für Param: _FilePathFM – jetzt nur anhand des Dateinamens (enthält)
        file_mapping = {
            "FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json": "Tiny",
            "FM_BenchmarkGraph_6_Services_NoExcludes_57.344_configs.json": "Small",
            "FM_BenchmarkGraph_18_Services_NoExcludes_139.968_configs.json": "Medium",
            "FM_BenchmarkGraph_16_Services_Excludes_1.520.640_configs.json": "Big",
            "FM_BenchmarkGraph_16_Services_NoExcludes_14.348.907_configs.json": "Huge"
        }

        def map_file_name(file_path):
            filename = os.path.basename(file_path)
            for key, alias in file_mapping.items():
                if key in filename:
                    return alias
            return file_path

        # Daten für unseren Ansatz laden und säubern
        data_main = pd.read_excel(main_excel)
        data_main = data_main[(data_main != -1).all(axis=1)].dropna()
        if data_main['Score'].dtype == 'object':
            data_main['Score'] = data_main['Score'].str.replace(',', '.').astype(float)
        data_main['Score'] = data_main['Score'].fillna(0)
        if data_main['Score Error (99,9%)'].dtype == 'object':
            data_main['Score Error (99,9%)'] = data_main['Score Error (99,9%)'].str.replace(',', '.').astype(float)
        data_main['Score Error (99,9%)'] = data_main['Score Error (99,9%)'].fillna(0)
        data_main['Param: _EdgeIndex'] = data_main['Param: _EdgeIndex'].astype(int)
        data_main['Param: _MaxRequirements'] = data_main['Param: _MaxRequirements'].astype(int)
        data_main['Param: _EdgeIndex'] = data_main['Param: _EdgeIndex'].replace(edge_mapping)
        data_main['Param: _FilePathFM'] = data_main['Param: _FilePathFM'].apply(map_file_name)

        # Alle File-Aliase (Union beider Datensätze) ermitteln und sortieren
        order = ["Tiny", "Small", "Medium", "Big", "Huge"]
        unique_file_paths = list({x for x in list(data_main['Param: _FilePathFM'].unique())})
        unique_file_paths = sorted(unique_file_paths, key=lambda x: order.index(x) if x in order else 999)

        # Dummy-Handles für die Legende erstellen – gleiche Farben werden in beiden Reihen genutzt
        styles = ['-', '--', '-.', ':', '-']
        markers = ['o', 's', '^', 'D', 'v']
        legend_handles = []
        for idx, edge in enumerate(edge_mapping.values()):
            line, = plt.plot([], [], linestyle=styles[idx], marker=markers[idx], label=edge)
            legend_handles.append(line)

        # Erstelle ein Subplot-Layout: 2 Zeilen (Obere Zeile: Unser Ansatz, Untere Zeile: Other) und N Spalten
        n_plots = len(unique_file_paths)
        fig, axes = plt.subplots(nrows=1, ncols=n_plots, figsize=(20, 5), sharex=True, sharey=True)

        fixed_edge_order = ["Country Side", "Small City", "Highway", "Medium City", "Full"]

        for i, file_alias in enumerate(unique_file_paths):
            # Filtere Daten für die aktuelle Spalte
            df_main = data_main[data_main['Param: _FilePathFM'] == file_alias]
            linewidth = 1.5

            # Bestimme globalen min/max-Bereich für diese Spalte
            if not df_main.empty:
                global_min = df_main['Param: _MaxRequirements'].min()
                global_max = df_main['Param: _MaxRequirements'].max()
            else:
                global_min, global_max = 0, 10

            # Da es nur eine Zeile gibt, verwende axes[i] statt axes[0, i]
            ax_our = axes[i]
            for idx, edge in enumerate(fixed_edge_order):
                df_edge_main = df_main[df_main['Param: _EdgeIndex'] == edge]
                if not df_edge_main.empty:
                    df_edge_main = df_edge_main.sort_values('Param: _MaxRequirements')
                    x = df_edge_main['Param: _MaxRequirements']
                    y = df_edge_main['Score']
                    err = df_edge_main['Score Error (99,9%)']
                    ax_our.errorbar(x, y, yerr=err, fmt=f'{markers[idx]}{styles[idx]}',
                                    capsize=3, linewidth=linewidth)
            ax_our.set_yscale('log')
            ax_our.yaxis.set_major_locator(LogLocator(base=10.0, numticks=10))
            ax_our.yaxis.set_minor_locator(LogLocator(base=10.0, subs=[]))
            ax_our.yaxis.set_minor_formatter(NullFormatter())
            ax_our.grid(True, which='major', axis='y')
            ax_our.set_xticks(np.arange(global_min, global_max + 1, 2))
            ax_our.set_xlabel('Requirements')
            if i == 0:
                ax_our.set_ylabel('Ø Exec. Time (ms)')

            # Titel unterhalb der x-Achse
            label_letter = chr(97 + i)  # a, b, ...
            ax_our.text(0.5, -0.18, f"({label_letter}) {file_alias}", transform=ax_our.transAxes,
                          ha='center', va='top', fontsize=18)

        # Legende oberhalb der gesamten Figur platzieren
        fig.legend(legend_handles, [h.get_label() for h in legend_handles],
                   loc='upper center', ncol=len(fixed_edge_order), frameon=False)
        fig.tight_layout(rect=[0, 0, 1, 0.88])
        plt.subplots_adjust(top=0.9)  # Abstand zwischen Legende und Plots reduzieren
        plt.savefig("./Results/RQ2/Figure7.pdf", format="pdf", bbox_inches='tight')
        plt.close(fig)

    except Exception as e:
        print(f"Ein Fehler ist aufgetreten: {e}")


# Beispielaufrufe:
our = '../Online/2025_03_12_FM4MC/onlineBenchmark_complete.xlsx'
create_combined_heatmap(our)
