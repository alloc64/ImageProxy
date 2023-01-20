FROM gradle:7.5.1-jdk17-focal AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN gradle :image-proxy-api:clean :image-proxy-api:build --no-daemon -x test --parallel

FROM openjdk:17
COPY --from=build /home/gradle/src/image-proxy-api/build/libs/image-proxy-api-1.0.0.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
