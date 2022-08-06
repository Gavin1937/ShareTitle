#!/usr/bin/env python3

from os import system

if __name__ == "__main__":
    system("./mvnw clean package")
    system("docker build -t gavin1937/sharetitle:latest .")
