# Rinha de Backend 2025 – Kotlin + Ktor + Exposed

Projeto otimizado para performance extrema, pronto para competir na Rinha de Backend.

## Build

```sh
gradle clean build
```

## Run local

```sh
java -jar build/libs/*.jar
```

## Docker

```sh
docker build -t rinha .
docker run --rm -p 9999:9999 rinha
```

## Configuração

- Porta: 9999
- Banco: PostgreSQL (ver application.conf)
- Pool de conexões: 2
- JVM flags otimizadas para containers

## Endpoints

Implemente conforme as regras da Rinha de Backend 2025.

---

Ajuste o código para JDBC puro para extrair ainda mais performance, se necessário.
