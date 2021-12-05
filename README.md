# ACID - An API Compatibility Issue Detector for Android Apps

ACID is a tool that detects both API invocation compatibility issues and API callback compatibility issues. It leverages API differences between API levels and applies static analysis of	app source code to locate API usages for compatibility issue detection.

## Prerequisites
- Python 3.8
- Java version 8
- Boost Graph Library (1.66.0)
- Graphviz 

## Python Requirements
- matplotlib>=3.0.2
- beautifulsoup4>=4.6.3

## How to run
- If you do not have the requirements installed in python: run "pip install -r requirements.txt" to install all the requirements
- It contains macOS executables. First decompile APK file using jadx or http://www.javadecompilers.com/apk
- To run: "dist/acid/acid <--path of decompiled apk file, It should have two folders, sources and resources-->"
- The output will be found in compatibility_issues.txt file in the tool folder.
