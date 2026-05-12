# Estágio de Build (Usando Maven com Java 17)
FROM maven:3.8.4-openjdk-17 AS build
COPY . .
RUN mvn clean package -DskipTests

# Estágio de Execução (Usando Eclipse Temurin, que é estável e mantida)
FROM eclipse-temurin:17-jdk-alpine
COPY --from=build /target/*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
