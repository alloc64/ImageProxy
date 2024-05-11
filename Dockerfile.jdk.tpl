FROM gradle:7.5.1-jdk17-focal AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
RUN --mount=type=cache,sharing=locked,target=/root/.gradle gradle :<%=name%>:clean :<%=name%>:build --no-daemon -x test --parallel

FROM openjdk:17
COPY --from=build /home/gradle/src/<%=name%>/build/libs/<%=name%>-<%=version%>.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
