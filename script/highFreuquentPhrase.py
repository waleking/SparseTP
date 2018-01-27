import linkInformation
import sys
from operator import itemgetter


if __name__=="__main__":
    if(len(sys.argv)<3):
        print("usage: python highFreuquentPhrase.py inputfile output" )
        sys.exit(-1)
    inputfile=sys.argv[1]
    outputfile=sys.argv[2]

    dWord,dPhrase,N=linkInformation.initData(inputfile)
    dPhraseDF=dict()
    for phrase in dPhrase.keys():
        df = len(dPhrase[phrase])
        dPhraseDF[phrase]=df
    sortedPhrasesWithPhrequency=sorted(dPhraseDF.items(),key=itemgetter(1),reverse=True)

    fWriter=open(outputfile,"w")
    for i in range(0,len(sortedPhrasesWithPhrequency)):
        phrase,df=sortedPhrasesWithPhrequency[i]
        portion=float(df)/float(N)
        if(portion>0.05):
            fWriter.write("%s\n" % phrase)

    for phrase in dPhraseDF.keys():
        df=dPhraseDF[phrase]
        if(df<5):
            fWriter.write("%s\n" % phrase)
    fWriter.close()
