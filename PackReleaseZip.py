#!/usr/bin/env python3

from pathlib import Path
from zipfile import ZipFile
from subprocess import Popen
from platform import system as currentOS

OSTYPE = currentOS()

def build() -> None:
    
    print("Host OS: "+OSTYPE)
    print("Building project...")
    
    if OSTYPE == "Windows":
        mvn = Popen(["mvnw.cmd","clean","package"])
    else:
        mvn = Popen(["./mvnw","clean","package"])
    mvn.wait()

def pack() -> None:
    
    print("Packing Release...")
    
    # prep
    root = Path(".")
    archive = root/"ShareTitle.zip"
    
    # rm old archive
    if archive.exists():
        archive.unlink()
    
    # files to add
    singlefiles = [
        root/"pom.xml",
        root/"sharetitle.service",
        root/"README.md",
        root/"LICENSE",
        root/"AutoUpdate.py",
        root/"target/ShareTitle.jar"
    ]
    recursivefile = [
        root/"src/main/webapp/WEB-INF/jsp",
    ]
    
    # pack new archive
    print("Save Release to: "+str(archive))
    with ZipFile(archive, "w") as zip:
        for single in singlefiles:
            zip.write(single, "ShareTitle/"+str(single.relative_to(root)))
        for recur in recursivefile:
            for r in recur.rglob("*"):
                if r.is_file():
                    zip.write(r, "ShareTitle/"+str(r.relative_to(root)))


if __name__ == "__main__":
    build()
    pack()
