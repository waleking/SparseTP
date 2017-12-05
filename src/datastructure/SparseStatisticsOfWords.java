package datastructure;

import util.ValueTopicOperator;

import java.util.ArrayList;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class SparseStatisticsOfWords {
    public int[][] typeTopicCounts;
    public int[] nK_;//how many words are assigned to topic k

    public int numWordTypes;
    public int numTopics;

    public ValueTopicOperator valueTopicOperator=null;

    public SparseStatisticsOfWords(int numTopics, int numWordTypes,
                                   InstanceList training,ValueTopicOperator valueTopicOperator){
        this.numTopics=numTopics;
        this.numWordTypes=numWordTypes;
        this.valueTopicOperator=valueTopicOperator;
        this.typeTopicCounts=new int[this.numWordTypes][];

        int[] typeTotals = new int[numWordTypes];
        // Create the type-topic counts data structure
        for (Instance document : training) {
            int[][] phrases = document.phrases;
            for (int position = 0; position < phrases.length; position++) {
                int[] phrase=phrases[position];
                if(phrase.length==1){
                    int word=phrase[0];
                    typeTotals[word]++;
                }else{
                    for(int n=0;n<phrase.length-1;n++){
                        int word=phrase[n];
                        typeTotals[word]++;
                    }
                }
            }
        }

        for (int type = 0; type < numWordTypes; type++) {
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
