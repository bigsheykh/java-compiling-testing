#!/usr/bin/env python3

import os
import subprocess

from add_submodules import add_submodules

def resolve_dependencies():
    pom_directories = [(x["repo_complete_address"], x["version"]) for x in add_submodules()]

    for pom_location in pom_directories:
        version = pom_location[1]
        pom_directory = pom_location[0]
        pom_address = os.path.join(pom_directory, "pom.xml")
        if os.path.exists(pom_address):
            print(pom_location)
            process = subprocess.Popen(args=f"git checkout {version}", cwd=pom_directory, shell=True)
            process.wait()

            process = subprocess.Popen(args="mvn dependency:resolve", cwd=pom_directory, shell=True)
            process.wait()
            process = subprocess.Popen(args="mvn dependency:resolve-plugins", cwd=pom_directory, shell=True)
            process.wait()


if __name__ == '__main__':
    resolve_dependencies()
