# ====== STAGE 1: Build da aplicação ======
FROM eclipse-temurin:17-jdk-alpine AS builder

WORKDIR /app

# Copia apenas o necessário para baixar dependências primeiro (cache build)
COPY mvnw ./
COPY .mvn .mvn
COPY pom.xml ./

# Baixa dependências antes de copiar o código-fonte
RUN ./mvnw dependency:go-offline -B

# Copia o código-fonte e faz o build
COPY src ./src
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

# ====== STAGE 2: Runtime======
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Copia apenas o jar final do build anterior
COPY --from=builder /app/target/*.jar app.jar

# Expõe a porta padrão
EXPOSE 8080

# Executa o aplicativo
ENTRYPOINT ["java", "-jar", "/app/app.jar"]