FROM --platform=linux/x86_64 eclipse-temurin:17-jre-alpine

RUN apk -U add curl ca-certificates && rm -f /var/cache/apk/*

USER 65534
EXPOSE 8080

COPY build/libs/*.jar app.jar
ENTRYPOINT ["sh", "-c", "java ${JAVA_OPTS} -jar /app.jar"]