package topicmodel;

import datastructure.Alphabet;
import datastructure.InstanceList;
import util.MyFile;

import java.io.File;
import java.util.ArrayList;

public class LinkInformation {
    public static boolean[] loadLinkInformation(Alphabet alphabet,double threshold){
        boolean[] array_linkInfomation=new boolean[alphabet.size()];
        for(int i=0;i<array_linkInfomation.length;i++){
            array_linkInfomation[i]=false;
        }

        MyFile reader = new MyFile("input/phrase_score.txt","r");
        ArrayList<String> lines=reader.readAll();
        for(String line: lines){
            String[] splitted=line.trim().split("\t");
            String phrase=splitted[0];
            double score=Double.valueOf(splitted[1]);
            double df=Double.valueOf(splitted[2]);
            int phraseIdx=alphabet.lookupIndex(phrase);
            if(phraseIdx>0){
                if(score>threshold){
                    array_linkInfomation[phraseIdx]=true;
                }
            }
        }
        reader.close();
        return array_linkInfomation;
    }
}
