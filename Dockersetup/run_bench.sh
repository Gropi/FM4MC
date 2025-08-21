#!/bin/bash
# Run JMH benchmarks and copy results to mounted output directory
set -e
WORKDIR="$(dirname "$0")"
OUTPUT_DIR=${OUTPUT_DIR:-/output}
mkdir -p "$OUTPUT_DIR"
cd /app
# Execute JMH benchmarks; write CSV report to output directory
java -jar JMH.jar -rf csv -rff "$OUTPUT_DIR/benchmark.csv"
