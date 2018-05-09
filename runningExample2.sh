#!/bin/bash

if [ ! -d result ]; then
    mkdir result
fi

if [ ! -d input ]; then
    mkdir input
fi

if [ ! -e input/mathematics.txt ]; then
    wget -c https://www.dropbox.com/s/gbhhe0xogdmwo8x/mathematics.txt.gz?dl=0 -O input/mathematics.txt.gz
    gzip -d input/mathematics.txt.gz
fi

TopicNumber=100
IterationNumber=1000
NumberOfTopPhrasesToShow=10

bash run.sh input/mathematics.txt ${TopicNumber} ${IterationNumber} ${NumberOfTopPhrasesToShow} 
python correlation_graph.py mathematics ${TopicNumber}
