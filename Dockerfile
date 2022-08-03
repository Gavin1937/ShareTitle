
FROM openjdk:17-alpine

RUN mkdir -p /app/src/main/webapp && \
	mkdir -p /app/target && \
	mkdir -p /app/data

COPY pom.xml /app
COPY src/main/webapp /app/src/main/webapp
COPY target/ShareTitle.jar /app/target

WORKDIR /app
ENTRYPOINT ["java","-jar","target/ShareTitle.jar"]
