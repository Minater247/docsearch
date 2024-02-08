#/usr/bin/env bash

set -e

javac -cp lib/junit-4.13.2.jar:lib/hamcrest-core-1.3.jar:. *.java

java -cp .:lib/hamcrest-core-1.3.jar:lib/junit-4.13.2.jar org.junit.runner.JUnitCore TestDocSearch
