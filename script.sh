#!/bin/bash
rm -rf *.class
javac Kwin.java
java Kwin DatasetFilePath startTime queryWindow threshold
