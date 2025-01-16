FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app
COPY . .

# Give permissions of mvnw to ensure it's executable
RUN chmod +x ./mvnw
RUN ./mvnw clean package -DskipTests=true

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=builder /app/target/*jar app.jar

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]