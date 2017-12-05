#!/bin/bash

inputfile=$1
topicNumber=$2
iterationNumber=$3
numTopPhrases=$4

cd script
echo ${green}===serialize the input file, to speed up the io of topic modeling===${reset}
bash runSerializeData.sh ${inputfile}

echo ${green}===build the invert index for phrases===${reset}
python buildInvertIndex.py ${inputfile}

echo ${green}===topic modeling on phrases===${reset}
bash runSparseTP.sh ${inputfile} ${topicNumber} ${iterationNumber} ${numTopPhrases}
