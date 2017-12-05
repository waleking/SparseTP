package datastructure;

import util.ValueTopicOperator;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class SparseStatisticsOfPhrases {
    public int[][] typeTopicCounts;
    public int[] nK_;//how many words are assigned to topic k

    public int numPhraseTypes;
    public int numTopics;

    public ValueTopicOperator valueTopicOperator=null;

    public SparseStatisticsOfPhrases(int numTopics, int numPhraseTypes,
                                   InstanceList training,ValueTopicOperator valueTopicOperator){
        this.numTopics=numTopics;
        this.numPhraseTypes=numPhraseTypes;
        this.valueTopicOperator=valueTopicOperator;
        this.typeTopicCounts=new int[this.numPhraseTypes][];

        int[] typeTotals = new int[numPhraseTypes];
        // Create the type-topic counts data structure
        for (Instance document : training) {
            int[][] phrases = document.phrases;
            for (int position = 0; position < phrases.length; position++) {
                int[] phrase=phrases[position];
                if(phrase.length>1){
                    int phraseTerm=phrase[phrase.length-1];
                    typeTotals[phraseTerm]++;
                }
            }
        }

        for (int type = 0; type < numPhraseTypes; type++) {
            typeTopicCounts[type] = new int[ Math.min(numTopics, typeTotals[type]) ];
        }
        this.nK_=new int[this.numTopics];
    }

    public void increase(int k, int v){
        valueTopicOperator.inc(this.typeTopicCounts[v],k);
        this.nK_[k]++;
    }

    public void decrease(int k, int v){
        valueTopicOperator.dec(this.typeTopicCounts[v],k);
        this.nK_[k]--;
    }
}
