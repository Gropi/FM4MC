# Dockersetup/Dockerfile
FROM eclipse-temurin:21-jdk

# Basic tools
RUN apt-get update && apt-get install -y --no-install-recommends \
    bash ca-certificates git unzip python3 python3-venv python3-pip \
  && rm -rf /var/lib/apt/lists/*

# Copy full repository into /artifact
WORKDIR /artifact
COPY . /artifact
COPY /FM4MC/TestData /TestData

# Fix Windows line endings
RUN sed -i 's/\r$//' FM4MC/gradlew \
 && find . -name "*.sh" -exec sed -i 's/\r$//' {} +

# Ensure scripts are executable
RUN chmod +x FM4MC/gradlew
RUN chmod +x Dockersetup/run_bench.sh || true
RUN chmod +x scripts/*.sh || true
RUN chmod +x Eval/Scripts/*.sh || true

# Build once
WORKDIR /artifact/FM4MC
RUN ./gradlew --no-daemon clean assemble

# Default command: run smoke benchmarks (can be overridden)
WORKDIR /artifact
CMD ["bash", "scripts/smoke_test.sh"]
