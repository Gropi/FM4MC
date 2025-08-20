@echo off
setlocal
set SCRIPT_DIR=%~dp0
call "%SCRIPT_DIR%venv\Scripts\activate"
for %%d in (Results\RQ1a\Figure5 Results\RQ1b Results\RQ2) do (
  if not exist "%SCRIPT_DIR%%%d" mkdir "%SCRIPT_DIR%%%d"
)

for %%f in ("HeatMap-NoSlicing.py" "Heatmap_Threshold.py" "Online_Canete.py" "Online_FM4MC.py" "Storage_Use.py") do (
  echo Running %%~f
  python "%SCRIPT_DIR%%%~f"
)
endlocal
