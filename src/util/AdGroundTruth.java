package util;

import java.util.Arrays;

public class AdGroundTruth {
    public static int[] listPhraseSize;
    public static int[][] wordTopics;
    public static int[] phraseTopics;
    public static int K;

    /**
     * \frac{1}{|l(d,i)|} \sum_{j} I(z^{(P)}_{d,i}=z_{d,j})
     * @return
     */
    private static double howmanyTopicsAreSame(int i){
        double numOfSame=0;
        for(int j=0;j<listPhraseSize[i];j++){
            if(wordTopics[i][j]==phraseTopics[i]){
                numOfSame=numOfSame+1;
            }
        }
        return numOfSame/listPhraseSize[i];
    }

    private static int numWordTopics;
    private static int numTotalTopics;
    private static int numPhraseTopics;
    private static int numPossibles;//K^{numTotalTopics}

    /**
     * turn the code to wordTopics and phraseTopics
     */
    private static void codeToTopics(int code){
        int[] _codesArray=new int[numTotalTopics];
        for(int i=0;i<numTotalTopics;i++){
            int a=code%K;
            code=(code-code%K)/K;
            _codesArray[numTotalTopics-1-i]=a;
        }
        int index=0;
        for(int i=0;i<listPhraseSize.length;i++){
            for(int j=0;j<listPhraseSize[i];j++){
                wordTopics[i][j]=_codesArray[index];
                index++;
            }
        }
        for(int i=0;i<listPhraseSize.length;i++){
            phraseTopics[i]=_codesArray[index];
            index++;
        }
    }

    public static double getGroundTruth(int _K,int[] _listPhraseSize){
        listPhraseSize=_listPhraseSize;
        K=_K;
        wordTopics=new int[listPhraseSize.length][];
        for(int i=0;i<listPhraseSize.length;i++){
            for(int j=0;j<listPhraseSize[i];j++){
                wordTopics[i]=new int[listPhraseSize[i]];
            }
        }
        phraseTopics=new int[listPhraseSize.length];

        numPhraseTopics=listPhraseSize.length;
        numWordTopics=0;
        for(int i=0;i<listPhraseSize.length;i++){
            numWordTopics+=listPhraseSize[i];
        }
        numTotalTopics=numWordTopics+numPhraseTopics;
        //visit all possible situations
        numPossibles=(int)Math.pow(K, numTotalTopics);
        double sum=0;
        for(int code=0;code<numPossibles;code++){
            codeToTopics(code);//update wordTopics and phraseTopics
            double sum_exppart=0;
            for(int i=0;i<listPhraseSize.length;i++){
                sum_exppart+=howmanyTopicsAreSame(i);
            }
            sum+=Math.exp(sum_exppart);
        }
        return sum;
    }
}
