# Eval/Scripts — Figure Regeneration

This folder contains the scripts used to process raw benchmark data and generate publication-ready figures (PDF).

Two supported use cases:
1) Inspect pre-generated figures shipped with the artifact: `Results/`
2) Regenerate figures from the raw data under `Eval/`

---

## Requirements
- Python 3.x
- Dependencies listed in `requirements.txt`

---

## Setup

### Linux/macOS (or Windows via Git Bash/WSL)
```bash
bash setup_env.sh
```

### Windows (CMD/PowerShell)
```bat
setup_env.bat
```

The setup scripts create a local virtual environment and install required dependencies.

---

## Regenerate all figures

### Linux/macOS (or Windows via Git Bash/WSL)
```bash
bash run_all.sh
```

### Windows (CMD/PowerShell)
```bat
run_all.bat
```

Outputs:
- PDFs under `Results/` (grouped by research question / figure group)

---

## Running a single script
After activating the virtual environment, run:

```bash
python <script_name>.py
```

If you execute scripts individually, ensure that expected input paths exist and that output directories are created.

---

## Notes
Figure generation is deterministic. Benchmark measurements are hardware-dependent due to SAT solving; therefore,
absolute values may differ between machines.

## Script Overview
| Script | Output files | Purpose / Interpretation |
|--------|--------------|--------------------------|
| `HeatMap-NoSlicing.py` | `Results/RQ1a/Figure5/Heatmap-Threshold_NoSlicing.pdf` | Heat map of configuration calculation times without slicing. Brighter cells indicate faster processing; dark red marks missing data. |
| `Heatmap_Threshold.py` | `Results/RQ1a/Figure5/Heatmap-Threshold_<threshold>.pdf` | Heat maps for different slicing thresholds. Compare task/alternative combinations to see where slicing improves runtime. |
| `Storage_Use.py` | `Results/RQ1b/Figure6.pdf` | Log–log plot of storage consumption versus number of valid configurations. Use the trend lines to extrapolate storage needs. |
| `Online_FM4MC.py` | `Results/RQ2/Figure7.pdf` | Execution-time curves for FM4MC across edge scenarios. Lower curves mean faster online calculations. |
| `Online_Canete.py` | `Results/RQ2/Figure8.pdf` | Same visualization for the baseline approach. Overlay with FM4MC results to compare performance. |

All generated PDFs can be opened with any viewer and inserted into publications as required.
