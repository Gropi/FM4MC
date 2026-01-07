# Evaluation and Reproducibility Guide

This section explains how to interpret the evaluation results and how the scripts in `Eval/Scripts/` reproduce the figures and tables reported in the paper *FM4MC: Improving Feature Models for Microservice Chains—Towards More Efficient Configuration and Validation (ICSE 2026)*.

The primary goal is that a reader can:
1. Identify which raw measurements belong to which research question and paper figure/table.
2. Understand the meaning of each JMH output column (e.g., `Mode`, `Score`, `Score Error (99,9%)`).
3. Reproduce the plots by running the provided scripts.

All evaluation artifacts are located in the `Eval/` directory.

## 1. Evaluation Folder Structure

The evaluation bundle is organized into four top-level folders:

```
Eval/
├── Handcrafted_Microservice_Chains/
├── Online/
├── Storage_Slicing/
└── Scripts/
```

- `Handcrafted_Microservice_Chains/` contains PDFs of the handcrafted microservice chains used for discussion and illustration.
- `Online/` contains JMH measurements for the online phase (FM4MC and the baseline).
- `Storage_Slicing/` contains measurements for the offline phase (slicing runtime and storage consumption) and isolated SAT-solver runs.
- `Scripts/` contains the plotting and aggregation scripts, plus helper scripts to run everything and to set up a Python environment.

All scripts write their outputs to `Eval/Scripts/Results/`.

## 2. JMH Result Files and How to Read Them

### 2.1 JMH Columns in This Project

The JMH result files used in this evaluation follow the schema below (column order may vary slightly between runs):

| Column | Meaning |
|---|---|
| `Benchmark` | Fully qualified benchmark method name |
| `Mode` | JMH measurement mode (see below) |
| `Threads` | Number of benchmark threads used |
| `Samples` | Number of measurement iterations (after warm-up) |
| `Score` | Aggregated performance metric reported by JMH |
| `Score Error (99,9%)` | Half-width of the 99.9% confidence interval |
| `Unit` | Unit of the score (e.g., `ms/op`) |
| `Param: ...` | Benchmark parameters (vary by benchmark and run) |

Common parameters include:
- `Param: _Tasks` and `Param: _Alternatives` (synthetic offline complexity grid)
- `Param: _Thresholds` (slicing threshold)
- `Param: _EdgeIndex`, `Param: _FilePathFM`, `Param: _MaxRequirements` (online phase)

### 2.2 Meaning of `Mode`

The evaluation uses JMH single-shot measurements for pipeline-like operations. In the provided CSV files, `Mode` is typically:

- `ss` (SingleShotTime): the benchmark measures the execution time of a single invocation per iteration.

Interpretation:
- `ss` with `Unit = ms/op` means: **milliseconds per single end-to-end operation**.
- Lower `Score` values indicate faster execution.

### 2.3 Meaning of `Score` and `Score Error (99,9%)`

- `Score` is the central result value produced by JMH for the given benchmark and parameter combination.
- `Score Error (99,9%)` is the statistical uncertainty at a 99.9% confidence level.

Example:

```
Score = 30.57
Score Error (99,9%) = 3.71
Unit = ms/op
```

Interpretation:
- The operation takes ~30.57 ms on average.
- With 99.9% confidence, the true mean is expected within approximately `[30.57 - 3.71, 30.57 + 3.71] ms`.

### 2.4 CSV Formatting Notes

Some JMH exports in this evaluation use:
- comma-separated CSV with quoted fields, and
- comma decimals (e.g., `"30,572058"`).

The plotting scripts normalize these values internally (e.g., converting comma decimals to floats).

## 3. Mapping Raw Data and Scripts to Paper Figures and Tables

This section documents the exact relationship between:
- the raw measurement folders,
- the plotting scripts in `Eval/Scripts/`, and
- the resulting files under `Eval/Scripts/Results/`.

### Table 3 (Offline Phase: Example Feature Models, Thresholds 3–10, Baseline Without Slicing)

**Input data folders:**
- `Eval/Storage_Slicing/2025_03_12_OfflinePhaseHandcraftet_JMH/`
    - `slicingSpeedHandcraftedFMs.csv`
    - `Transformiert.xlsx` (preprocessed/aggregated view)
- `Eval/Storage_Slicing/2025_03_12_OnlySATNewSmall/onlySATSolver.csv`
- `Eval/Storage_Slicing/OnlySAT-Solver-Big/`
    - `jmh-result.csv` / `jmh-results.csv`

**What these datasets represent:**
- The handcrafted offline-phase runs contain the slicing thresholds (3–10) for the example feature models.
- The two OnlySAT datasets provide the unsliced baseline measurements for smaller models and for larger models.

**How to interpret Table 3:**
- Each cell reports the **average runtime (mean) and standard deviation** for computing valid configurations in the offline phase.
- The “No” column corresponds to the unsliced baseline (SAT solving the full model).
- Threshold columns correspond to FM4MC slicing runs with the given threshold.

### Figure 5 (RQ1a: Slicing Speed Heatmaps)

Figure 5 consists of three heatmaps:
- (a) No slicing (baseline)
- (b) Slicing with threshold 250
- (c) Slicing with threshold 1000

**No slicing (Figure 5a):**
- **Raw data:** `Eval/Storage_Slicing/2024_05_19_Measurement_Linear_FM_No_Slicing/benchmark_results.csv`
- **Script:** `Eval/Scripts/HeatMap-NoSlicing.py`
- **Output:** `Eval/Scripts/Results/RQ1a/Figure5/Heatmap-Threshold_NoSlicing.pdf`

**Slicing heatmaps (Figure 5b/c):**
- **Raw data:** `Eval/Storage_Slicing/2024_06_05_Slicing_JMH/result_comma_seperated.csv`
- **Script:** `Eval/Scripts/Heatmap_Threshold.py`
- **Outputs:**
    - `Eval/Scripts/Results/RQ1a/Figure5/Heatmap-Threshold_250.pdf`
    - `Eval/Scripts/Results/RQ1a/Figure5/Heatmap-Threshold_1000.pdf`

**How to interpret Figure 5:**
- X-axis: number of tasks
- Y-axis: number of alternatives per task
- Color: average runtime for computing valid configurations
- Without slicing, higher complexity rapidly becomes infeasible (runs may exceed time limits and are visualized as NaN regions).
- With slicing, runtimes remain in the millisecond range for substantially larger regions of the complexity grid.

### Figure 6 (RQ1b: Storage Consumption)

**Raw data inputs (as used in the script):**
- No slicing:
    - `Eval/Storage_Slicing/2024_05_19_Measurement_Linear_FM_No_Slicing/benchmark_results.csv`
- Slicing (threshold-based):
    - `Eval/Storage_Slicing/2025_03_11_Measurement_Linear_FM_Slicing/benchmark_results.csv`

**Script:** `Eval/Scripts/Storage_Use.py`

**Output:** `Eval/Scripts/Results/RQ1b/Figure6.pdf`

**How to interpret Figure 6:**
- X-axis: number of valid configurations (log scale, expressed as 10^k)
- Y-axis: storage consumption in MB (log scale)
- Shaded ranges show min/max variation per complexity bucket.
- Regression lines show asymptotic growth trends.
- Threshold-based slicing substantially reduces storage growth compared to storing full configuration sets.

### Figure 7 (RQ2: Online Phase Runtime of FM4MC)

**Raw data folder:** `Eval/Online/2025_03_12_FM4MC/`
- `onlineBenchmark.csv`
- `onlineBenchmarkHuge.csv`
- `onlineBenchmark_complete.xlsx` (script input)

**Script:** `Eval/Scripts/Online_FM4MC.py`

**Output:** `Eval/Scripts/Results/RQ2/Figure7.pdf`

**How to interpret Figure 7:**
- Each subplot corresponds to one feature-model size category (Tiny … Huge).
- X-axis: maximum number of requirements (`Param: _MaxRequirements`) used in the benchmark.
- Lines represent different edge hardware presets (`Param: _EdgeIndex` mapped to human-readable names).
- Y-axis (log): end-to-end runtime to compute executable graphs, reported in ms.

### Figure 8 (RQ2: Baseline Runtime, Cañete et al.)

**Raw data folder:** `Eval/Online/2025_03_13_Canete/`
- `jmh-result_complete.xlsx` (script input)

**Script:** `Eval/Scripts/Online_Canete.py`

**Output:** `Eval/Scripts/Results/RQ2/Figure8.pdf`

**How to interpret Figure 8:**
- Same general interpretation as Figure 7, but for the baseline approach.
- The baseline exhibits substantially higher runtimes and does not scale to larger feature models under the same constraints.

## 4. Running the Evaluation Scripts

The `Eval/Scripts/` folder contains helper scripts to set up a Python environment and run all evaluation scripts:

```bash
cd Eval/Scripts
bash setup_env.sh
bash run_all.sh
```

Outputs are written to:

```
Eval/Scripts/Results/
```

