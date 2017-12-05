#!/bin/bash

inputfile=$1
topicNumber=$2
iterationNumber=$3
numTopPhrases=$4

cd script
bash runSerializeData.sh ${inputfile}
python buildInvertIndex.py ${inputfile}
bash runSparseTP.sh ${inputfile} ${topicNumber} ${iterationNumber} ${numTopPhrases}
