# Evaluation Scripts

This folder contains Python scripts for generating figures and tables used in the evaluation of FM4MC.

## Prerequisites
- Python 3
- Required Python packages listed in [`requirements.txt`](requirements.txt)

## Setup
Create a local Python installation (if needed), virtual environment, and install dependencies:

```bash
bash setup_env.sh                      # Linux/macOS
# or
setup_env.bat          # Windows (downloads Python if missing)
```

## Running all scripts
Execute every evaluation script sequentially. The helper script creates any necessary
`Results` subdirectories before running the Python scripts:

```bash
bash run_all.sh        # Linux/macOS
# or
run_all.bat            # Windows
```

## Running individual scripts
You can also run a single script after activating the virtual environment:

```bash
source venv/bin/activate      # Linux/macOS
venv\Scripts\activate.bat     # Windows

python <script_name>.py
```

Scripts write output files to the `Results` directory tree. The `run_all` helpers
prepare these folders automatically. If you run a script individually, create the
desired output directories beforehand.

## Script Overview
| Script | Output files | Purpose / Interpretation |
|--------|--------------|--------------------------|
| `HeatMap-NoSlicing.py` | `Results/RQ1a/Figure5/Heatmap-Threshold_NoSlicing.pdf` | Heat map of configuration calculation times without slicing. Brighter cells indicate faster processing; dark red marks missing data. |
| `Heatmap_Threshold.py` | `Results/RQ1a/Figure5/Heatmap-Threshold_<threshold>.pdf` | Heat maps for different slicing thresholds. Compare task/alternative combinations to see where slicing improves runtime. |
| `Storage_Use.py` | `Results/RQ1b/Figure6.pdf` | Log–log plot of storage consumption versus number of valid configurations. Use the trend lines to extrapolate storage needs. |
| `Online_FM4MC.py` | `Results/RQ2/Figure7.pdf` | Execution-time curves for FM4MC across edge scenarios. Lower curves mean faster online calculations. |
| `Online_Canete.py` | `Results/RQ2/Figure8.pdf` | Same visualization for the baseline approach. Overlay with FM4MC results to compare performance. |

All generated PDFs can be opened with any viewer and inserted into publications as required.
