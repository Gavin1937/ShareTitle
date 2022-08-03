#! /bin/bash +x

./mvnw clean package

docker build -t gavin1937/sharetitle:latest .
