# Build the FM4MC Docker image and run benchmarks, collecting results
$ErrorActionPreference = "Stop"
$imageName = "fm4mc"
$scriptDir = Split-Path -Parent $MyInvocation.MyCommand.Path
$rootDir = Split-Path $scriptDir

docker build -f "$scriptDir/Dockerfile" -t $imageName $rootDir

$outputDir = Join-Path $scriptDir "output"
if (-not (Test-Path $outputDir)) {
    New-Item -ItemType Directory -Path $outputDir | Out-Null
}
$resolvedOutput = (Resolve-Path $outputDir).Path
docker run --rm -v "$resolvedOutput:/output" $imageName
Write-Host "Benchmark results stored in Dockersetup/output/benchmark.csv"
