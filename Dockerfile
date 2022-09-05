
# building src
FROM maven:3.8.5-openjdk-17 AS build

WORKDIR /tmp/build

ADD pom.xml ./
ADD src ./src
ADD data ./data

RUN mvn clean package


# building release img
FROM openjdk:17-alpine

WORKDIR /app

RUN mkdir -p ./src/main/webapp && \
    mkdir -p ./target && \
    mkdir -p ./data

COPY pom.xml .
COPY src/main/webapp ./src/main/webapp
COPY --from=build /tmp/build/target/ShareTitle.jar ./target

ENTRYPOINT ["java","-jar","target/ShareTitle.jar"]

