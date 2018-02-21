#!/bin/bash

if [ ! -d result ]; then
    mkdir result
fi

if [ ! -d input ]; then
    mkdir input
fi

if [ ! -e input/argentina.txt ]; then
    wget -c https://www.dropbox.com/s/fdx2z99xc0aepce/argentina.txt.gz?dl=0 -O input/argentina.txt.gz
    gzip -d input/argentina.txt.gz
fi

TopicNumber=100
IterationNumber=1000
NumberOfTopPhrasesToShow=10

bash run.sh input/argentina.txt ${TopicNumber} ${IterationNumber} ${NumberOfTopPhrasesToShow} 
