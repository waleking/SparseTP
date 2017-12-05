#!/bin/bash

if [ ! -d result ]; then
    mkdir result
fi

if [ ! -d input ]; then
    mkdir input
fi

if [ ! -e input/20newsgroups.txt ]; then
    wget -c https://www.dropbox.com/s/gbhhe0xogdmwo8x/mathematics.txt.gz?dl=0 -O input/mathematics.txt.gz
    gzip -d input/mathematics.txt.gz
fi

bash run.sh input/mathematics.txt 30 1000 10
