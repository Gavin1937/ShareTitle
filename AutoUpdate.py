#!/usr/bin/env python3

###############################################
#                                            #
#  This script will help                     #
#  released package to update automatically  #
#                                            #
###############################################

import urllib.request
from zipfile import ZipFile
from pathlib import Path

FILE_URL = "https://github.com/Gavin1937/ShareTitle/releases/download/head/ShareTitle.zip"

def download() -> None:
    print("Downloading Latest Release...")
    urllib.request.urlretrieve(FILE_URL, "ShareTitle.zip")

def apply() -> None:
    print("Applying New Update...")
    with ZipFile("ShareTitle.zip") as zip:
        zip.extractall(".")

def clean() -> None:
    print("Cleaning...")
    zip = Path("ShareTitle.zip")
    if zip.exists():
        zip.unlink()

if __name__ == "__main__":
    download()
    apply()
    clean()
