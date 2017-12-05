package datastructure;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class StatisticsOfWords {
    public int[][] nKV;
    public int[] nK_;//how many words are assigned to topic k

    public int numWordTypes;
    public int numTopics;

    public StatisticsOfWords(int numTopics, int numWordTypes){
        this.numTopics=numTopics;
        this.numWordTypes=numWordTypes;

        this.nK_=new int[this.numTopics];
        this.nKV=new int[this.numTopics][];
        for(int k=0;k<this.numTopics;k++){
            this.nKV[k]=new int[this.numWordTypes];
        }
    }

    public void increase(int k, int v){
        this.nKV[k][v]++;
        this.nK_[k]++;
    }

    public void decrease(int k, int v){
        this.nKV[k][v]--;
        this.nK_[k]--;
    }
}
