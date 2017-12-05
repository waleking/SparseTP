#!/bin/bash
cd ..
set -e
lib="lib"
src="src"
classes="class"

inputfile=$1

LibPath=${lib}/hppc-0.7.1.jar:${lib}/org.json-20120521.jar:${lib}/pyrolite-4.19.jar:${lib}/log4j-1.2.15.jar:${lib}/commons-math3-3.6.1.jar
javac -classpath ${classes}:$LibPath -sourcepath ${src} -d ${classes} ${src}/topicmodel/SerializeData.java
java -classpath ${classes}:$LibPath topicmodel/SerializeData ${inputfile}
