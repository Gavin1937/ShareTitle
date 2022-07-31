#! /bin/bash

./mvnw package

mkdir -p target/dependency

(cd target/dependency; jar -xf ../*.jar)

docker build -t gavin1937/sharetitle:latest .
