# ----------------------------------------------------
# Etapa 1: Build de la aplicacion con Maven y JDK 21
# ----------------------------------------------------
FROM maven:3.9.9-eclipse-temurin-21-alpine AS builder
WORKDIR /app

# Descargar dependencias en capa de cache
COPY pom.xml .
RUN mvn dependency:go-offline -B

# Copiar codigo fuente y compilar el archivo JAR
COPY src ./src
RUN mvn clean package -DskipTests -B

# ----------------------------------------------------
# Etapa 2: Imagen ligera de ejecucion con JRE 21
# ----------------------------------------------------
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app

# Crear usuario de ejecucion seguro sin privilegios de root
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# Copiar el artefacto compilado desde la etapa de construccion
COPY --from=builder /app/target/*.jar app.jar

# Puerto por defecto (Render inyecta su propio puerto en $PORT)
EXPOSE 8080

# Variables de entorno por defecto
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xms256m -Xmx512m -XX:+UseG1GC"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -Dserver.port=${PORT:-8080} -jar app.jar"]
