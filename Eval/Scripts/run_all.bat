@echo off
setlocal
set SCRIPT_DIR=%~dp0
call "%SCRIPT_DIR%venv\Scripts\activate"
for %%f in ("HeatMap-NoSlicing.py" "Heatmap_Threshold.py" "Online_Canete.py" "Online_FM4MC.py" "Storage_Use.py") do (
  echo Running %%~f
  python "%SCRIPT_DIR%%%~f"
)
endlocal
