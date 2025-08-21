# Build FM4MC using Gradle and package runtime artifacts
FROM eclipse-temurin:21-jdk AS build
WORKDIR /workspace
# Copy sources
COPY FM4MC/ /workspace/FM4MC/
# Build jars, normalizing line endings for the Gradle wrapper
RUN cd FM4MC \
    && sed -i 's/\r$//' gradlew \
    && chmod +x gradlew \
    && ./gradlew --no-daemon build

# Runtime image containing built jars and test data
FROM eclipse-temurin:21-jre
WORKDIR /app
# Copy runtime artifacts
COPY --from=build /workspace/FM4MC/Canete/build/libs/Canete.jar /app/
COPY --from=build /workspace/FM4MC/Collector/build/libs/Collector.jar /app/
COPY --from=build /workspace/FM4MC/JMH/build/libs/JMH.jar /app/
# Include required test data
COPY FM4MC/TestData /app/TestData
# Copy run script
COPY Dockersetup/run_bench.sh /app/run_bench.sh
RUN sed -i 's/\r$//' /app/run_bench.sh && chmod +x /app/run_bench.sh
# Default entry point runs the benchmark and exports data
ENTRYPOINT ["/app/run_bench.sh"]
