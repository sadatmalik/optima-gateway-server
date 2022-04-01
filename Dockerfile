# Stage 1

# Start with a base image containing Java runtime
FROM openjdk:11-slim as build

# Add Maintainer Info
LABEL maintainer="Sadat Malik <sm@creativefusion.net>"

# The application's jar file set by dockerfile-maven-plugin
ARG JAR_FILE

# Add the application's jar to the container
COPY ${JAR_FILE} app.jar

# Unpackage jar file
# Unpacks the app.jar copied previously into the filesystem of the build image
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf /app.jar)

# Stage 2
# This second image contains the different layers of a Spring Boot app instead
# of the complete JAR file

# Same Java runtime
FROM openjdk:11-slim

# Add volume pointing to /tmp
VOLUME /tmp

# Copy unpackaged application to new container - copies the different layers from
# the first image named build
ARG DEPENDENCY=/target/dependency
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app

# Execute the application - targets the licensing service in the image when the
# container is created
ENTRYPOINT ["java","-cp","app:app/lib/*","com.sadatmalik.optima.gateway.OptimaApiGatewayApplication"]