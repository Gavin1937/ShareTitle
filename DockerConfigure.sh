#! /bin/bash

./mvnw package

mkdir -p target/dependency

(cd target/dependency; jar -xf ../*.jar)

