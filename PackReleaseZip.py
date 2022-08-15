#!/usr/bin/env python3

from pathlib import Path
from zipfile import ZipFile
from subprocess import Popen, PIPE
from platform import system as currentOS

OSTYPE = currentOS()

def build() -> None:
    if OSTYPE == "Windows":
        mvn = Popen(["mvnw.cmd","clean","package"])
    else:
        mvn = Popen(["./mvnw","clean","package"])
    mvn.wait()

def pack() -> None:
    # prep
    root = Path()
    archive = root/"ShareTitle.zip"
    pom = root/"pom.xml"
    webapp = root/"src/main/webapp/WEB-INF/jsp"
    jar = root/"target/ShareTitle.jar"
    
    # rm old archive
    if archive.exists():
        archive.unlink()
    
    # pack new archive
    with ZipFile(archive, "w") as zip:
        zip.write(pom, "ShareTitle/pom.xml")
        for jsp in webapp.iterdir():
            zip.write(jsp, "ShareTitle/src/main/webapp/WEB-INF/jsp/"+jsp.name)
        zip.write(jar, "ShareTitle/target/ShareTitle.jar")


if __name__ == "__main__":
    build()
    pack()
