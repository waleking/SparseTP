import math
from operator import itemgetter
import sys

def initData(filename):
    dWord=dict()
    dPhrase=dict()
    with open(filename) as f:
        i=0
        for line in f:
            splitted=line.strip().split(",")
            for element in splitted:
                #invert indices for phrases
                if(" " in element):
                    phrase=element
                    if(phrase not in dPhrase):
                        dPhrase[phrase]=set([i])
                    else:
                        dPhrase[phrase].add(i)
                    for word in phrase.split(" "):
                        if(word not in dWord):
                            dWord[word]=set([i])
                        else:
                            dWord[word].add(i)
                #invert indices for phrases
                else:
                    word=element
                    if(word not in dWord):
                        dWord[word]=set([i])
                    else:
                        dWord[word].add(i)
            i=i+1
    N=i
    return dWord,dPhrase,N

def computeNPMI(df1,df2,dfPhrase,N):
    p1=(float(df1)+0.01)/float(N)
    p2=(float(df2)+0.01)/float(N)
    p12=(float(dfPhrase)+0.01)/float(N)
    NPMI=math.log(p12/(p1*p2))/(-math.log(p12))
    return NPMI

def computeNPMIforPhrase_2words(phrase,dfPhrase,dWord,dPhrase,N):
    word1,word2=phrase.split(" ")
    df1=len(dWord[word1])
    df2=len(dWord[word2])
    score=computeNPMI(df1,df2,dfPhrase,N)
    return score

def computeNPMIforPhrase(phrase,dWord,dPhrase,N,debug=False):
    splitted=phrase.split(" ")
    result=float("inf")
    dfPhrase=len(dPhrase[phrase])
    numWordInPhrase=len(splitted)
    for i in range(numWordInPhrase-1):
        two_words=" ".join(splitted[i:i+2])
        score=computeNPMIforPhrase_2words(two_words,dfPhrase,dWord,dPhrase,N)
        if(result>score):
            result=score
    if(debug):
        print("%s's NPMI\t%s" %(phrase,str(score)))
    return score


def extractLinkInformation(filename,outputfile):
    dWord,dPhrase,N=initData(filename)
    dPhraseScore=dict()
    for phrase in dPhrase.keys():
        score=computeNPMIforPhrase(phrase,dWord,dPhrase,N)
        dPhraseScore[phrase]=score
    sortedList=sorted(dPhraseScore.items(),key=itemgetter(1),reverse=True)
    with open(outputfile,"w") as fWriter:
        for phrase,score in sortedList:
            if(len(dPhrase[phrase])>5):
                fWriter.write("%s\t%s\t%s\n" % (phrase,score,len(dPhrase[phrase])))


if __name__=="__main__":
    if(len(sys.argv)<3):
        print("usage: python linkInformation.py filename outputfile")
        sys.exit(-1)
    filename=sys.argv[1]
    outputfile=sys.argv[2]
    extractLinkInformation(filename,outputfile)
