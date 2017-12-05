package util;

import datastructure.TopicAssignment;

import java.util.ArrayList;

public class CountingPhraseAssignedAsWords {
    public static int count(ArrayList<TopicAssignment> data){
        int count=0;
        for (int doc = 0; doc < data.size(); doc++) {
            TopicAssignment t = data.get(doc);
            int[][] topicSequence = t.topicSequence;
            int numWordAndPhrases=topicSequence.length;
            for(int n=0;n<numWordAndPhrases;n++){
                int[] wordOrPhraseTopics=topicSequence[n];
                int length=wordOrPhraseTopics.length-1;
                if(wordOrPhraseTopics.length>1){
                    //phrase
                    int phraseTopic=wordOrPhraseTopics[wordOrPhraseTopics.length-1];
                    for(int i=0;i<wordOrPhraseTopics.length-1;i++){
                        if(wordOrPhraseTopics[i]==phraseTopic){
                            count++;
                        }
                    }
                }//end of component words in phrase
            }//end of doc
        }//end of corpus
        return count;
    }
}
