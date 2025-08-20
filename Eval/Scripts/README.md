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
