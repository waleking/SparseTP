#!/bin/bash
dirAutoPhrase=/Volumes/MacintoshHD/Users/huangwaleking/git/AutoPhrase
dirSparseTP=/Volumes/MacintoshHD/Users/huangwaleking/git/sparseTP
cd $dirAutoPhrase
rm tmp/*
rm results/*

INPUT_FILE=$1
bash auto_phrase.sh ${INPUT_FILE}
python filter.py
bash phrasal_segmentation.sh ${INPUT_FILE}
python prepare_for_topicmodeling.py

corpusname=$( echo $1 | perl -lne 'print $1 if /.*\/(.*?)\./' ) #such as 20newsgroups, mathematics

cp ${dirAutoPhrase}/results/input_forTopicModel.txt ${dirSparseTP}/input/${corpusname}.txt
echo "after running AutoPhrase, the file is stored as input/${corpusname}.txt"
