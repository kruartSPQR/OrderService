FROM gradle:jdk17-alpine AS builder

WORKDIR /opt/app

COPY . .

RUN gradle clean bootJar --no-daemon

FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

COPY --from=builder /opt/app/build/libs/*.jar orderService.jar

EXPOSE 8079

ENTRYPOINT ["java", "-jar", "orderService.jar"]
