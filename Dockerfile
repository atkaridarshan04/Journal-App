FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .
RUN ./mvnw clean package -DskipTests=true

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]