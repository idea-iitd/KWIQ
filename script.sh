#!/bin/bash
rm -rf *.class
javac Kwin.java
# java Kwin DatasetFilePath startTime queryWindow threshold
java Kwin 15wiki.txt 1078412216 1 7
