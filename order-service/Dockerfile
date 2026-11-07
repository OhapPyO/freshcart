# Stage 1: Build
FROM eclipse-temurin:21-jdk AS build

WORKDIR /app

# Copy Maven files first
COPY mvnw ./
COPY .mvn/ .mvn/
COPY pom.xml ./

# Allow Maven Wrapper to execute
RUN chmod +x mvnw

# Download dependencies
RUN ./mvnw dependency:go-offline

# Copy source code
COPY src/ ./src/

# Build JAR and skip tests
RUN ./mvnw package -DskipTests

# Stage 2: Run
FROM eclipse-temurin:21-jre AS runtime
# this one even make it smaller size
#FROM eclipse-temurin:21-jre-alpine AS runtime

WORKDIR /app


# Copy only the JAR from the build stage
COPY --from=build /app/target/*.jar app.jar

# Application port
EXPOSE 8080
# Run the application
ENTRYPOINT ["java", "-jar", "app.jar"]