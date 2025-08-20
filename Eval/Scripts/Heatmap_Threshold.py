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
        data = pd.read_csv(csv_file_path, delimiter=',')

        # Entfernen aller Zeilen, die den Wert -1 enthalten
        data = data[(data != -1).all(axis=1)]
        data = data.dropna()

        data['Score'] = data['Score'].str.replace(',', '.').astype(float).fillna(0)
        data['Score Error (99,9%)'] = data['Score Error (99,9%)'].str.replace(',', '.').astype(float).fillna(0)
        data['Param: _Thresholds'] = data['Param: _Thresholds'].astype(int)

        # In Millisekunden umrechnen
        data['Score'] = data['Score'] / (1000)
        data['Score Error (99,9%)'] = data['Score Error (99,9%)'] / (1000)

        clauseGenerator = data[data['Benchmark'] == 'ASEPaper.CalculatorFMOfflinePhase.clauseGeneratorFMBenchmark']
        slicing = data[data['Benchmark'] == 'ASEPaper.SlicerFMOfflinePhase.sliceFMBenchmark']

        # Für jede Threshold-Stufe eine Heatmap erstellen
        thresholds = data['Param: _Thresholds'].unique()
        for threshold in thresholds:
            subset = data[data['Param: _Thresholds'] == threshold]
            pivot_table = subset.pivot_table(
                values='Score',
                columns='Param: _Tasks',
                index='Param: _Alternatives',
                aggfunc=np.mean)

            plt.figure(figsize=(12, 8))
            sns.heatmap(pivot_table, annot=False, fmt=".2f", cmap='coolwarm',
                        vmin=pivot_table.min().min(), vmax=pivot_table.max().max(),
                        cbar_kws={'label': 'Processing time (ms)'},)

            #sns.heatmap(pivot_table, annot=False, fmt=".2f", cmap='coolwarm')
            #plt.title(f'Execution Time Heatmap for Threshold {threshold}')
            plt.ylabel('Alternatives')
            plt.xlabel('Tasks')

            # Invertieren der Y-Achse
            plt.gca().invert_yaxis()

            plt.tight_layout()
            plt.savefig(f"./Results/RQ1a/Figure5/Heatmap-Threshold_{threshold}.pdf", format="pdf")

            #plt.show()

    except Exception as e:
        print(f"Ein Fehler ist aufgetreten: {e}")

# Beispielaufruf der Funktion (ersetze 'path/to/your/csvfile.csv' durch den tatsächlichen Pfad zur CSV-Datei)
csv_file_path = '../Storage_Slicing/2024_06_05_Slicing_JMH/result_comma_seperated.csv'
create_heatmap(csv_file_path)
