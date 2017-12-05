package util;

import net.razorvine.pickle.Unpickler;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.io.FileInputStream;
import java.io.InputStream;

import static java.lang.Math.log;


public class PMIComputer {
    private Map<String,ArrayList<Integer>> index;
    private int doc_num;

    public ArrayList<Integer> getDocList(String phrase){
        return index.get(phrase);
    }

    public double pmiOfSingleTopic(ArrayList<String> topic){
        double score=0.0;
        int pair_num = 0;
        double mpi_sum = 0.0;
        double smooth_factor=1;


            int topK=topic.size();
            for(int i=0;i<topK;i++){
                String phrase1=topic.get(i);
                HashSet<Integer> doc_id_set1;
                if(index.containsKey(phrase1)){
                    doc_id_set1=new HashSet<Integer>(index.get(phrase1));
                }else{
                    doc_id_set1=new HashSet<Integer>();
                }
                double freq1=doc_id_set1.size()+smooth_factor;

                for(int j=i+1;j<topK;j++){
                    String phrase2=topic.get(j);
                    HashSet<Integer> doc_id_set2;
                    if(index.containsKey(phrase2)){
                        doc_id_set2=new HashSet<Integer>(index.get(phrase2));
                    }else{
                        doc_id_set2=new HashSet<Integer>();
                    }
                    double freq2=doc_id_set2.size()+smooth_factor;

                    HashSet<Integer> lt3 = new HashSet<Integer>();
                    lt3.addAll(doc_id_set1);
                    lt3.retainAll(doc_id_set2);

                    double freq3=lt3.size()+smooth_factor;
                    mpi_sum += log(freq3 * doc_num / (freq1 * freq2));
//                    System.out.println("phrase1="+phrase1+",phrase2="+phrase2+",pmi="
//                            +log(freq3 * doc_num / (freq1 * freq2)));
                    pair_num++;
                }
            }
        score=mpi_sum/pair_num;
        return score;
    }


    /**
     * @param topic
     * @return
     */
    public double npmiOfSingleTopic(ArrayList<String> topic){
        double score=0.0;
        int pair_num = 0;
        double nmpi_sum = 0.0;
        double smooth_factor=1;


        int topK=topic.size();
        for(int i=0;i<topK;i++){
            String phrase1=topic.get(i);
            HashSet<Integer> doc_id_set1;
            if(index.containsKey(phrase1)){
                doc_id_set1=new HashSet<Integer>(index.get(phrase1));
            }else{
                doc_id_set1=new HashSet<Integer>();
            }
            double freq1=doc_id_set1.size()+smooth_factor;
            double prob1=freq1/doc_num;

            for(int j=i+1;j<topK;j++){
                String phrase2=topic.get(j);
                HashSet<Integer> doc_id_set2;
                if(index.containsKey(phrase2)){
                    doc_id_set2=new HashSet<Integer>(index.get(phrase2));
                }else{
                    doc_id_set2=new HashSet<Integer>();
                }
                double freq2=doc_id_set2.size()+smooth_factor;
                double prob2=freq2/doc_num;

                HashSet<Integer> lt3 = new HashSet<Integer>();
                lt3.addAll(doc_id_set1);
                lt3.retainAll(doc_id_set2);

                double freq3=lt3.size()+smooth_factor;
                double prob3=freq3/doc_num;
                nmpi_sum += log(prob3/(prob1*prob2))/(-log(prob3));
//                    System.out.println("phrase1="+phrase1+",phrase2="+phrase2+",pmi="
//                            +log(freq3 * doc_num / (freq1 * freq2)));
                pair_num++;
            }
        }
        score=nmpi_sum/pair_num;
        return score;
    }

    public double pmi(ArrayList<ArrayList<String>> topics){
        double score=0.0;
        int pair_num = 0;
        double mpi_sum = 0.0;
        double smooth_factor=1;

        for(int k=0;k<topics.size();k++){
            ArrayList<String> topic=topics.get(k);
            int topK=topic.size();
            for(int i=0;i<topK;i++){
                String phrase1=topic.get(i);
                HashSet<Integer> doc_id_set1;
                if(index.containsKey(phrase1)){
                    doc_id_set1=new HashSet<Integer>(index.get(phrase1));
                }else{
                    doc_id_set1=new HashSet<Integer>();
                }
                double freq1=doc_id_set1.size()+smooth_factor;

                for(int j=i+1;j<topK;j++){
                    String phrase2=topic.get(j);
                    HashSet<Integer> doc_id_set2;
                    if(index.containsKey(phrase2)){
                        doc_id_set2=new HashSet<Integer>(index.get(phrase2));
                    }else{
                        doc_id_set2=new HashSet<Integer>();
                    }
                    double freq2=doc_id_set2.size()+smooth_factor;

                    HashSet<Integer> lt3 = new HashSet<Integer>();
                    lt3.addAll(doc_id_set1);
                    lt3.retainAll(doc_id_set2);

                    double freq3=lt3.size()+smooth_factor;
                    mpi_sum += log(freq3 * doc_num / (freq1 * freq2));
//                    System.out.println("phrase1="+phrase1+",phrase2="+phrase2+",pmi="
//                            +log(freq3 * doc_num / (freq1 * freq2)));
                    pair_num++;
                }
            }
        }
        score=mpi_sum/pair_num;
        return score;
    }

    public PMIComputer(String filename){
        try{
            InputStream stream = new FileInputStream(filename);
            Unpickler unpickler = new Unpickler();
            Map<String,Object> data = (Map<String,Object>)unpickler.load(stream);
            this.doc_num=(int)data.get("doc_num");
            this.index=(Map<String,ArrayList<Integer>>)data.get("index");
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public ArrayList<ArrayList<String>> initTestTopics(String line){
        String[] array=line.split(",");
        ArrayList<ArrayList<String>> topics=new ArrayList<ArrayList<String>>();
        ArrayList<String> topic=new ArrayList<String>();
        for(int i=0;i<array.length;i++){
            String topicPhrase=array[i].trim();
            topic.add(topicPhrase);
        }
        topics.add(topic);
        return topics;
    }

    public void test(){
        ArrayList<ArrayList<String>> topics1=initTestTopics("gun control, second amendment, self defense, " +
                "keep bear arms, right people, united states, anti gun, " +
                "well regulated militia, semi auto,gun owners");
        System.out.println(pmi(topics1));

        ArrayList<ArrayList<String>> topics2=initTestTopics("gun control, anti gun, death penalty, " +
                "gun owners, violent crime, bill mr, gun control laws, " +
                "crime rate, capital punishment, gun ownership");
        System.out.println(pmi(topics2));

        ArrayList<ArrayList<String>> topics3=initTestTopics("self defense, gun control, semi auto, " +
                "gun owners, death penalty, military weapon, dangerous ordnance, " +
                "anti gun, self defense, semi autos");
        System.out.println(pmi(topics3));
    }

    public static void main(String[] args){
        PMIComputer pmiComputer=new PMIComputer("input/20newsgroups.txt.pkl");
//        PMIComputer pmiComputer=new PMIComputer("/Users/huangwaleking/git/topicxonomy/data_invertIndex/index.pkl");
//        pmiComputer.getDocList("gun owner");
        pmiComputer.test();
    }


}
