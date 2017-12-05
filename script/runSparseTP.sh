#!/bin/bash
cd ..
set -e
lib="lib"
src="src"
classes="class"

if [ ! -e ${classes} ]; then
    mkdir ${classes}
fi

inputfile=$1
topicNumber=$2
iterationNumber=$3
numTopPhrases=$4

LibPath=${lib}/hppc-0.7.1.jar:${lib}/org.json-20120521.jar:${lib}/pyrolite-4.19.jar:${lib}/log4j-1.2.15.jar:${lib}/commons-math3-3.6.1.jar
javac -classpath ${classes}:$LibPath -sourcepath ${src} -d ${classes} ${src}/topicmodel/SparseTP.java
#java -classpath ${classes}:$LibPath topicmodel/SparseTP input/20newsgroups.txt 20 1000
java -classpath ${classes}:$LibPath topicmodel/SparseTP ${inputfile} ${topicNumber} ${iterationNumber} ${numTopPhrases}
