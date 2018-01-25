package topicmodel;

import datastructure.Alphabet;
import datastructure.InstanceList;
import util.MyFile;

import java.io.File;
import java.util.ArrayList;

public class LinkInformation {
    public static boolean[] loadLinkInformation(Alphabet alphabet,double threshold){
        boolean[] array_linkInfomation=new boolean[alphabet.size()];

        MyFile reader = new MyFile("input/phrase_score.txt","r");
        ArrayList<String> lines=reader.readAll();
        for(String line: lines){
            String[] splitted=line.trim().split("\t");
            String phrase=splitted[0];
            double score=Double.valueOf(splitted[1]);
            int phraseIdx=alphabet.lookupIndex(phrase);
            if(score>threshold){
                array_linkInfomation[phraseIdx]=true;
            }else{
                array_linkInfomation[phraseIdx]=false;
            }
        }
        reader.close();
        return array_linkInfomation;
    }

    public static void main(String[] args){
        InstanceList training = InstanceList.load(new File("input/20newsgroups.txt.serialized"));
        Alphabet alphabet=training.getPhraseAlphabet();
        double threshold=0.1;
        boolean[] array_linkInfomation=loadLinkInformation(alphabet,threshold);
        for(int i=0;i<array_linkInfomation.length;i++){
            System.out.println(array_linkInfomation[i]);
        }
    }
}
