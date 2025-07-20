FROM eclipse-temurin:21-jre-alpine AS base
WORKDIR /app
COPY build/libs/*.jar app.jar
ENV JAVA_OPTS="-XX:+UseZGC -XX:+AlwaysPreTouch -XX:+UseNUMA -XX:+UnlockExperimentalVMOptions -XX:+UseContainerSupport -XX:MaxRAMPercentage=80 -XX:InitialRAMPercentage=80 -XX:MinRAMPercentage=80 -XX:+DisableExplicitGC -XX:+PerfDisableSharedMem -XX:+ParallelRefProcEnabled -XX:MaxInlineLevel=15 -XX:MaxInlineSize=1024 -XX:InlineSmallCode=1024 -XX:ThreadStackSize=512 -XX:+OptimizeStringConcat -XX:+UseStringDeduplication -XX:+UseCompressedOops -XX:+UseCompressedClassPointers -XX:+ExitOnOutOfMemoryError -XX:ActiveProcessorCount=2"
EXPOSE 9999
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
