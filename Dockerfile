# Use Eclipse Temurin JDK 17 (more stable than openjdk)
FROM eclipse-temurin:17-jdk-alpine

# Set working directory
WORKDIR /app

# Copy pom.xml first for better layer caching
COPY pom.xml .

# Install Maven
RUN apk add --no-cache maven

# Download dependencies
RUN mvn dependency:go-offline -B

# Copy source code
COPY src ./src

# Package application
RUN mvn clean package -DskipTests

# Create runtime stage
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# Install curl for health checks
RUN apk add --no-cache curl

# Copy webapp-runner and WAR file
COPY --from=0 /app/target/dependency/webapp-runner.jar webapp-runner.jar
COPY --from=0 /app/target/*.war app.war

# Expose port
EXPOSE $PORT

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
  CMD curl -f http://localhost:$PORT/ || exit 1

# Start application
CMD java -jar webapp-runner.jar --port $PORT app.war 