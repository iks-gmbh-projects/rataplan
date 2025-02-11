FROM maven:3-eclipse-temurin-11-alpine AS build-setup
WORKDIR /build
ADD pom.xml ./
ADD rataplan-auth/pom.xml rataplan-auth/
ADD rataplan-backend/pom.xml rataplan-backend/
ADD surveyTool/pom.xml surveyTool/
RUN ["mvn", "dependency:resolve", "dependency:resolve-plugins"]

FROM build-setup AS build-auth
ADD rataplan-auth/src/ rataplan-auth/src/
RUN ["mvn", "-pl", "rataplan-auth", "-am", "verify", "-Duser.timezone=Europe/Berlin"]

FROM build-setup AS build-backend
ADD rataplan-backend/src/ rataplan-backend/src/
RUN ["mvn", "-pl", "rataplan-backend", "-am", "verify", "-Duser.timezone=Europe/Berlin"]

FROM build-setup AS build-survey
ADD surveyTool/src/ surveyTool/src/
RUN ["mvn", "-pl", "surveyTool", "-am", "verify", "-Duser.timezone=Europe/Berlin"]

FROM eclipse-temurin:11-jre-alpine AS run-auth
LABEL org.opencontainers.image.source=https://github.com/iks-gmbh-projects/rataplan
COPY --from=build-auth /build/rataplan-auth/target/rataplan-auth.jar ./
ENTRYPOINT ["java", "-jar", "rataplan-auth.jar"]
EXPOSE 8081

FROM eclipse-temurin:11-jre-alpine AS run-backend
LABEL org.opencontainers.image.source=https://github.com/iks-gmbh-projects/rataplan
COPY --from=build-backend /build/rataplan-backend/target/rataplan-backend.jar ./
ENTRYPOINT ["java", "-jar", "rataplan-backend.jar"]
EXPOSE 8080

FROM eclipse-temurin:11-jre-alpine AS run-survey
LABEL org.opencontainers.image.source=https://github.com/iks-gmbh-projects/rataplan
COPY --from=build-survey /build/surveyTool/target/surveyTool.jar ./
ENTRYPOINT ["java", "-jar", "surveyTool.jar"]
EXPOSE 8082
