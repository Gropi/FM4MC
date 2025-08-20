# Evaluation Scripts

This folder contains Python scripts for generating figures and tables used in the evaluation of FM4MC.

## Prerequisites
- Python 3
- Required Python packages listed in [`requirements.txt`](requirements.txt)
- Output directories expected by the scripts (e.g., `Results/RQ1a/Figure5`) must exist prior to execution

## Setup
Create a local virtual environment and install dependencies:

```bash
bash setup_env.sh      # Linux/macOS
# or
setup_env.bat          # Windows
```

## Running all scripts
Execute every evaluation script sequentially:

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

Each script writes output files to the `Results` directory tree, so ensure the necessary folders exist before execution.
