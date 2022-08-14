#!/usr/bin/env python3

from shutil import which
from subprocess import Popen, PIPE
from platform import system as currentOS
from os import environ
from sys import exit
import re

OSTYPE = currentOS()


def hasJava() -> bool:
    java = which("java")
    if java is None or len(java) <= 0:
        return False
    proc = Popen([java,"-version"], stdout=PIPE, stderr=PIPE)
    output = proc.stderr.read().decode('utf-8')
    m = re.match( r'.*version "(.*)".*', output )
    
    # no java
    if m is None:
        return False
    
    # java version too old
    elif int(m.group(1).split('.')[0]) < 17:
        return False
    
    else:
        return True

def hasJavaHome() -> bool:
    return "JAVA_HOME" in environ

def hasDocker() -> bool:
    docker = which("docker")
    if docker is None or len(docker) <= 0:
        return False
    proc = Popen([docker,"--version"], stdout=PIPE, stderr=PIPE)
    output = proc.stdout.read().decode('utf-8')
    m = re.match( r'.*Docker version (.*), build.*', output )
    
    # no docker
    if m is None:
        return False
    
    else:
        return True


if __name__ == "__main__":
    
    if not hasJava():
        print("Cannot find java in this machine or current java version is too old.")
        exit(1)
    
    if not hasDocker():
        print("Cannot find docker in this machine.")
        exit(1)
    
    if not hasJavaHome():
        print("Warning: Cannot find JAVA_HOME in this machine, maven packaging may not fully works.")
    
    
    print("Host OS: "+OSTYPE)
    print("Packaging project...")
    if OSTYPE == "Windows":
        mvn = Popen(["mvnw.cmd","clean","package"])
    else:
        mvn = Popen(["./mvnw","clean","package"])
    mvn.wait()
    print("\n\n\nBuilding docker image...")
    docker = Popen(["docker","build","-t","gavin1937/sharetitle:latest","."])
    docker.wait()

