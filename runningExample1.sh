#!/bin/bash

if [ ! -d result ]; then
    mkdir result
fi

if [ ! -d input ]; then
    mkdir input
fi

if [ ! -e input/20newsgroups.txt ]; then
    wget -c https://www.dropbox.com/s/2ifzu84j56knvsn/20newsgroups.txt.gz?dl=0 -O input/20newsgroups.txt.gz
    gzip -d input/20newsgroups.txt.gz
fi

bash run.sh input/20newsgroups.txt 30 1000 10
