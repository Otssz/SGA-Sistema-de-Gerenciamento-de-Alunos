# ---- Etapa 1: build ----
FROM eclipse-temurin:17-jdk-alpine AS build
WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN apk add --no-cache maven && \
    mvn -q -DskipTests package

# ---- Etapa 2: runtime mínimo ----
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

LABEL org.opencontainers.image.title="SGA-Edu Backend"
LABEL org.opencontainers.image.description="Sistema de Gestão Acadêmica Escolar"

# Cria usuário não-root e diretórios enquanto ainda é root
RUN addgroup -S sgaedu && adduser -S sgaedu -G sgaedu && \
    mkdir -p /app/documentos && \
    chown -R sgaedu:sgaedu /app

COPY --from=build /app/target/*.jar app.jar

VOLUME ["/app/documentos"]

USER sgaedu

EXPOSE 8080

ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
