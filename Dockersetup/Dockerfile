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
COPY --from=build /workspace/FM4MC/*/build/libs/*.jar /app/
# Include required test data
COPY FM4MC/TestData /app/TestData
# Copy run script
COPY Dockersetup/run.sh /app/run.sh
RUN sed -i 's/\r$//' /app/run.sh && chmod +x /app/run.sh
# Default entry point runs a component or benchmark; mode can be overridden via CMD
ENTRYPOINT ["/app/run.sh"]
CMD ["jmh"]
