import os
import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
from matplotlib.ticker import LogLocator, NullFormatter

def create_combined_heatmap(other_excel):
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
        # Mapping für den anderen Ansatz (Spalte Param: _FilePathEdgeNodes)
        edge_nodes_mapping = {
            "EdgeNodes_CountrySide.json": "Country Side",
            "EdgeNodes_SmallCity.json": "Small City",
            "EdgeNodes_Highway.json": "Highway",
            "EdgeNodes_MediumCity.json": "Medium City",
            "EdgeNodes_Full.json": "Full"
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

        # Daten des anderen Ansatzes laden und säubern
        data_other = pd.read_excel(other_excel)
        data_other = data_other[(data_other != -1).all(axis=1)].dropna()
        if data_other['Score'].dtype == 'object':
            data_other['Score'] = data_other['Score'].str.replace(',', '.').astype(float)
        data_other['Score'] = data_other['Score'].fillna(0)
        if data_other['Score Error (99,9%)'].dtype == 'object':
            data_other['Score Error (99,9%)'] = data_other['Score Error (99,9%)'].str.replace(',', '.').astype(float)
        data_other['Score Error (99,9%)'] = data_other['Score Error (99,9%)'].fillna(0)
        data_other['Param: _MaxRequirements'] = data_other['Param: _MaxRequirements'].astype(int)
        data_other['Param: _FilePathEdgeNodes'] = data_other['Param: _FilePathEdgeNodes'].astype(str).map(edge_nodes_mapping)
        data_other['Param: _FilePathFM'] = data_other['Param: _FilePathFM'].apply(map_file_name)

        # Dummy-Handles für die Legende erstellen – gleiche Farben werden in beiden Reihen genutzt
        styles = ['-', '--', '-.', ':', '-']
        markers = ['o', 's', '^', 'D', 'v']
        legend_handles = []
        for idx, edge in enumerate(edge_mapping.values()):
            line, = plt.plot([], [], linestyle=styles[idx], marker=markers[idx], label=edge)
            legend_handles.append(line)

        # Erstelle ein Subplot-Layout: 1 Zeile und 2 Spalten
        fig, axes = plt.subplots(nrows=1, ncols=3, figsize=(15, 5), sharex=True, sharey=True)

        fixed_edge_order = ["Country Side", "Small City", "Highway", "Medium City", "Full"]

        # Daten für die beiden Diagramme filtern
        unique_file_paths = list(data_other['Param: _FilePathFM'].unique())
        order = ["Tiny", "Small", "Medium", "Big", "Huge"]
        unique_file_paths = sorted(unique_file_paths, key=lambda x: order.index(x) if x in order else 999)
        if len(unique_file_paths) < 2:
            print("Nicht genügend unterschiedliche Dateipfade für zwei Diagramme.")
            return

        for i in range(3):
            file_alias = unique_file_paths[i]
            df_other = data_other[data_other['Param: _FilePathFM'] == file_alias]
            linewidth = 1.5

            # Bestimme globalen min/max-Bereich für die x-Achse
            if not df_other.empty:
                global_min = df_other['Param: _MaxRequirements'].min()
                global_max = df_other['Param: _MaxRequirements'].max()
            else:
                global_min, global_max = 0, 10

            ax_other = axes[i]
            for idx, edge in enumerate(fixed_edge_order):
                df_edge_other = df_other[df_other['Param: _FilePathEdgeNodes'] == edge]
                if not df_edge_other.empty:
                    df_edge_other = df_edge_other.sort_values('Param: _MaxRequirements')
                    x2 = df_edge_other['Param: _MaxRequirements']
                    y2 = df_edge_other['Score']
                    err2 = df_edge_other['Score Error (99,9%)']
                    ax_other.errorbar(x2, y2, yerr=err2, fmt=f'{markers[idx]}{styles[idx]}',
                                      capsize=3, linewidth=linewidth)
            ax_other.set_yscale('log')
            ax_other.yaxis.set_major_locator(LogLocator(base=10.0, numticks=10))
            ax_other.yaxis.set_minor_locator(LogLocator(base=10.0, subs=[]))
            ax_other.yaxis.set_minor_formatter(NullFormatter())
            ax_other.grid(True, which='major', axis='y')
            ax_other.set_xticks(np.arange(global_min, global_max + 1, 2))
            ax_other.set_xlabel('Requirements')
            if i == 0:
                ax_other.set_ylabel('Ø Exec. Time (ms)')

            # Titel unterhalb der x-Achse
            label_letter = chr(97 + i)  # a, b, ...
            ax_other.text(0.5, -0.18, f"({label_letter}) {file_alias}", transform=ax_other.transAxes,
                          ha='center', va='top', fontsize=18)

        # Legende oberhalb der gesamten Figur platzieren
        fig.legend(legend_handles, [h.get_label() for h in legend_handles],
                   loc='upper center', ncol=len(fixed_edge_order), frameon=False)
        fig.tight_layout(rect=[0, 0, 1, 0.88])
        plt.subplots_adjust(top=0.9)  # Abstand zwischen Legende und Plots reduzieren
        plt.savefig("./Results/RQ2/Figure8.pdf", format="pdf", bbox_inches='tight')
        plt.close(fig)

    except Exception as e:
        print(f"Ein Fehler ist aufgetreten: {e}")


# Beispielaufrufe:
canete = '../Online/2025_03_13_Canete/jmh-result_complete.xlsx'
create_combined_heatmap(canete)
