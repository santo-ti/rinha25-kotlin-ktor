FROM gradle:8.14.3-jdk17 AS builder
WORKDIR /app
COPY . .
RUN gradle clean installDist

FROM eclipse-temurin:17-jre
WORKDIR /app
COPY --from=builder /app/build/install/rinha-backend/ .
EXPOSE 9999
CMD ["bin/rinha-backend"]
