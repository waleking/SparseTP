package util;

import datastructure.IDSorter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

public class AnalysisOnPhraseTopic {
    /**
     * rank by statistics in descending order, with originalTopicId->newTopicId.
     * @param statistics
     * @return
     */
    public static HashMap<Integer,Integer> getOrder(int[] statistics){
        HashMap<Integer,Integer> orderMap_oldToNew=new HashMap<Integer,Integer>();

        //<id, statistics[id]>
        TreeSet<IDSorter> sortedTopicIds=TopicPrintUtil.getSortedWords(statistics.length,statistics);

        //we want to relocate the topics as <originalTopicId,newTopicId>
        Iterator<IDSorter> iterator = sortedTopicIds.iterator();
        int newTopicId=0;
        while (iterator.hasNext() && newTopicId < statistics.length) {
            IDSorter info = iterator.next();
            int originalId=info.getID();
            orderMap_oldToNew.put(originalId,newTopicId);
            newTopicId++;
        }
        return orderMap_oldToNew;
    }
}
