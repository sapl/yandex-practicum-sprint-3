FROM gradle:jdk21-corretto AS builder
WORKDIR /app
COPY . .
RUN gradle build --info -x test

FROM alpine/java:21-jdk
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT exec java -jar app.jar

