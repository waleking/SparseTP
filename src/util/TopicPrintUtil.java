package util;

import datastructure.Alphabet;
import datastructure.IDSorter;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class TopicPrintUtil {

    public static PMIComputer pmiComputer;

    public static void init(String filename){
        pmiComputer=new PMIComputer(filename);
    }

    /**
     * sort itemsV according to value
     * @param numTypes
     * @return
     */
    public static TreeSet<IDSorter> getSortedWords (int numTypes, int[] itemsV) {
        TreeSet<IDSorter> sortedWords=new TreeSet<IDSorter>();
        for(int v=0;v<numTypes;v++){
            sortedWords.add(new IDSorter(v,itemsV[v]));
        }
        return sortedWords;
    }


    /**
     * sort itemsKV according to value
     * @param numTopics
     * @param numTypes
     * @param itemsKV
     * @return
     */
    public static ArrayList<TreeSet<IDSorter>> getSortedWords (int numTopics, int numTypes,
                                                               int[][] itemsKV) {

        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList<TreeSet<IDSorter>>(numTopics);

        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            topicSortedWords.add(new TreeSet<IDSorter>());
        }

        // Collect gamma
        for(int topic=0;topic<numTopics;topic++){
            TreeSet<IDSorter> sortedWords=topicSortedWords.get(topic);
            for(int v=0;v<numTypes;v++){
                sortedWords.add(new IDSorter(v,itemsKV[topic][v]));
            }
        }
        return topicSortedWords;
    }

    public static ArrayList<TreeSet<IDSorter>> getSortedWords (int numTopics, int numTypes,
                                                               double[][] itemsKV) {

        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList<TreeSet<IDSorter>>(numTopics);

        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            topicSortedWords.add(new TreeSet<IDSorter>());
        }

        // Collect gamma
        for(int topic=0;topic<numTopics;topic++){
            TreeSet<IDSorter> sortedWords=topicSortedWords.get(topic);
            for(int v=0;v<numTypes;v++){
                sortedWords.add(new IDSorter(v,itemsKV[topic][v]));
            }
        }
        return topicSortedWords;
    }

    /**
     * sort itemsKV according to value
     * @param numTopics
     * @param numTypes
     * @param typeTopicCounts
     * @param valueTopicOperator
     * @return
     */
    public static ArrayList<TreeSet<IDSorter>> getSortedWords (int numTopics, int numTypes,
                                                               int[][] typeTopicCounts,ValueTopicOperator valueTopicOperator) {

        ArrayList<TreeSet<IDSorter>> topicSortedWords = new ArrayList<TreeSet<IDSorter>>(numTopics);

        // Initialize the tree sets
        for (int topic = 0; topic < numTopics; topic++) {
            topicSortedWords.add(new TreeSet<IDSorter>());
        }

        // Collect gamma
        for(int type=0;type<numTypes;type++){
            int index=0;
            int[] topicCounts=typeTopicCounts[type];
            while(index<topicCounts.length && topicCounts[index]>0){
                int topic=topicCounts[index] & valueTopicOperator.topicMask;
                int count=topicCounts[index] >> valueTopicOperator.topicBits;
                topicSortedWords.get(topic).add(new IDSorter(type,count));
                index++;
            }
        }
        return topicSortedWords;
    }

    public static String showTopics(int numEntityToShow, int topicNum, int numTypes,
                                    double[][] itemsKV, Alphabet alphabet,boolean showDigitNum){
        ArrayList<TreeSet<IDSorter>> topicSortedWords=getSortedWords(topicNum, numTypes, itemsKV);
        ArrayList<ArrayList<String>> topics=topicsPhraes(numEntityToShow,topicNum,topicSortedWords,alphabet);
//        return "pmi="+pmiComputer.pmi(topics)+"\n"+
//                showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
        return showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
    }


    public static String showTopics(int numEntityToShow, int topicNum, int numTypes,
                                    int[][] itemsKV, Alphabet alphabet,boolean showDigitNum){
        ArrayList<TreeSet<IDSorter>> topicSortedWords=getSortedWords(topicNum, numTypes, itemsKV);
        ArrayList<ArrayList<String>> topics=topicsPhraes(numEntityToShow,topicNum,topicSortedWords,alphabet);
        double pmiscore=pmiComputer.pmi(topics);
//        LogUtil.logger().info("pmi="+pmiscore);
//        return "pmi="+pmiscore+"\n"+
//                showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
        return showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
    }

    public static String showTopics(int numEntityToShow, int topicNum, int numTypes,
                                    int[][] itemsKV, Alphabet alphabet,boolean showDigitNum,
                                    ValueTopicOperator valueTopicOperator){
        ArrayList<TreeSet<IDSorter>> topicSortedWords=getSortedWords(topicNum, numTypes, itemsKV,valueTopicOperator);
        ArrayList<ArrayList<String>> topics=topicsPhraes(numEntityToShow,topicNum,topicSortedWords,alphabet);
        double pmiscore=pmiComputer.pmi(topics);
//        LogUtil.logger().info("sparsetp pmi="+pmiscore);
//        return "pmi="+pmiscore+"\n"+
//                showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
        return showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
    }


    /**
     * @param numEntityToShow
     * @param topicNum
     * @param numTypes
     * @param itemsKV
     * @param alphabet
     * @param showDigitNum
     * @param valueTopicOperator
     * @param oldToNew
     * @return
     */
    public static String showSortedTopics(int numEntityToShow, int topicNum, int numTypes,
                                    int[][] itemsKV, Alphabet alphabet,boolean showDigitNum,
                                    ValueTopicOperator valueTopicOperator,HashMap<Integer,Integer> oldToNew,
                                          int[] topicsSize){
        ArrayList<TreeSet<IDSorter>> topicSortedWords=getSortedWords(topicNum, numTypes, itemsKV,valueTopicOperator);
        ArrayList<ArrayList<String>> topics=topicsPhraes(numEntityToShow,topicNum,topicSortedWords,alphabet);
        double pmiscore=pmiComputer.pmi(topics);
//        LogUtil.logger().info("sparsetp pmi="+pmiscore);
//        return "pmi="+pmiscore+"\n"+
//                showTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum);
        return showSortedTopics(numEntityToShow,topicNum,topicSortedWords,alphabet,showDigitNum,oldToNew,topicsSize,topics);
    }

    /**
     * different from its variation version that only contains 1 topic.
     * @param numEntityToShow
     * @param numTypes
     * @param itemsV
     * @param alphabet
     * @return
     */
    public static String showSingleTopic(int numEntityToShow, int numTypes,
                                    int[] itemsV, Alphabet alphabet){
        StringBuilder out = new StringBuilder();

        TreeSet<IDSorter> sortedWords=getSortedWords(numTypes, itemsV);
        Iterator<IDSorter> iterator = sortedWords.iterator();
        int i=0;
        out.append ("topic"  + ":\t");
        while (iterator.hasNext() && i < numEntityToShow) {
            IDSorter info = iterator.next();
            out.append(alphabet.lookupObject(info.getID()) + "(" + info.getWeight() + "),");
            i++;
        }
        out.append ("\n");
        return out.toString();
    }


    /**
     * @param numEntityToShow
     * @param topicNum
     * @param topicSortedWords
     * @param alphabet
     * @param showDigitNum
     * @return
     */
    private static String showSortedTopics(int numEntityToShow, int topicNum,
                                     ArrayList<TreeSet<IDSorter>> topicSortedWords,
                                     Alphabet alphabet,boolean showDigitNum,
                                     HashMap<Integer,Integer> oldToNew,
                                           int[] topicsSize,
                                           ArrayList<ArrayList<String>> topics){
        String[] strArrays=new String[topicNum];

        // Print results for each topic
        for (int topic = 0; topic < topicNum; topic++) {
            TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);
            Iterator<IDSorter> iterator = sortedWords.iterator();

            int i=0;
            StringBuilder out = new StringBuilder();
            out.append ("topic" + Integer.toString(topic) + ":\t"
                    +"(#phrases="+topicsSize[topic]+"\tpmi=" +pmiComputer.pmiOfSingleTopic(topics.get(topic))
                    +"\tnpmi="+pmiComputer.npmiOfSingleTopic(topics.get(topic))+")\t");
            while (iterator.hasNext() && i < numEntityToShow) {
                IDSorter info = iterator.next();
                if(showDigitNum==true){
                    out.append(alphabet.lookupObject(info.getID()) + "(" + info.getWeight() + "),");
                }else{
                    out.append(alphabet.lookupObject(info.getID()) + ",");
                }
                i++;
            }
            out.append ("\n");
            strArrays[oldToNew.get(topic)]=out.toString();
        }
        String result="";
        for(int k=0;k<topicNum;k++){
            result+=strArrays[k];
        }
        return result;
    }


    private static String showTopics(int numEntityToShow, int topicNum,
                                     ArrayList<TreeSet<IDSorter>> topicSortedWords,Alphabet alphabet,boolean showDigitNum){
        StringBuilder out = new StringBuilder();

        // Print results for each topic
        for (int topic = 0; topic < topicNum; topic++) {
            TreeSet<IDSorter> sortedWords = topicSortedWords.get(topic);
            Iterator<IDSorter> iterator = sortedWords.iterator();

            int i=0;
            out.append ("topic" + Integer.toString(topic) + ":\t");
            while (iterator.hasNext() && i < numEntityToShow) {
                IDSorter info = iterator.next();
                if(showDigitNum==true){
                    out.append(alphabet.lookupObject(info.getID()) + "(" + info.getWeight() + "),");
                }else{
                    out.append(alphabet.lookupObject(info.getID()) + ",");
                }
                i++;
            }
            out.append ("\n");
        }
        return out.toString();
    }

    /**
     * return ArrayList<ArrayList<String>> topics
     * @param numEntityToShow
     * @param topicNum
     * @param topicSortedWords
     * @param alphabet
     * @return
     */
    private static ArrayList<ArrayList<String>> topicsPhraes(int numEntityToShow, int topicNum,
                                     ArrayList<TreeSet<IDSorter>> topicSortedWords,Alphabet alphabet){
        ArrayList<ArrayList<String>> topics=new ArrayList<ArrayList<String>>();

        // Print results for each k
        for (int k = 0; k < topicNum; k++) {
            TreeSet<IDSorter> sortedWords = topicSortedWords.get(k);
            Iterator<IDSorter> iterator = sortedWords.iterator();

            int i=0;
            ArrayList<String> topic=new ArrayList<String>();
            while (iterator.hasNext() && i < numEntityToShow) {
                IDSorter info = iterator.next();
                if(info.getWeight()>0){
                    topic.add((String) alphabet.lookupObject(info.getID()));
                }
                i++;
            }
            topics.add(topic);
        }
        return topics;
    }
}
