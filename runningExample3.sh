#!/bin/bash

if [ ! -d result ]; then
    mkdir result
fi

if [ ! -d input ]; then
    mkdir input
fi

if [ ! -e input/chemistry.txt ]; then
    wget -c https://www.dropbox.com/s/kv4a0xutepdn8ws/chemistry.txt.gz?dl=0 -O input/chemistry.txt.gz
    gzip -d input/chemistry.txt.gz
fi

TopicNumber=100
IterationNumber=1000
NumberOfTopPhrasesToShow=10

bash run.sh input/chemistry.txt ${TopicNumber} ${IterationNumber} ${NumberOfTopPhrasesToShow} 
