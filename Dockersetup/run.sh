#!/bin/bash
# Run FM4MC components or JMH benchmarks and copy results to a mounted output directory
set -e
MODE=${1:-jmh}
OUTPUT_DIR=${OUTPUT_DIR:-/output}
mkdir -p "$OUTPUT_DIR"
cd /app
TMP_DIR=$(mktemp -d)
case "$MODE" in
  manager)
    java -jar Configuration-Manager-*.jar \
      -fmFile "/app/TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
      -configurations "/app/TestData/TestGraphs/TestConfigurationFiles/temporaryConfigurationFile_FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json_10.csv" \
      -edgeIndex 1 \
      -graph "$TMP_DIR/graph.graphml"
    cp "$TMP_DIR/graph.graphml" "$OUTPUT_DIR/graph.graphml"
    ;;
  creator)
    java -cp Configuration-Creator-*.jar Startup \
      -slicing \
      -fmFile "/app/TestData/TestGraphs/TestFMJsons/FM_BenchmarkGraph_6_Services_NoExcludes_4.096_configs.json" \
      -configurations "$TMP_DIR/configurations.csv"
    cp "$TMP_DIR/configurations.csv" "$OUTPUT_DIR/configurations.csv"
    ;;
  jmh)
    java -jar JMH.jar -rf csv -rff "$TMP_DIR/benchmark.csv"
    cp "$TMP_DIR/benchmark.csv" "$OUTPUT_DIR/benchmark.csv"
    ;;
  *)
    echo "Unknown mode: $MODE" >&2
    exit 1
    ;;
esac
rm -rf "$TMP_DIR"
