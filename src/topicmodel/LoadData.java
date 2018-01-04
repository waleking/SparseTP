package topicmodel;

import datastructure.*;
import org.json.JSONObject;
import util.MyFile;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by huangwaleking on 5/4/17.
 */
public class LoadData {
    /**
     * filter out very frequent phrases (e.g., united states, new york, los angeles, bueno aires)
     */
    public static HashSet<String> loadStopPhrases(){
        HashSet<String> setStopPhrases=new HashSet<String>();
        MyFile reader=new MyFile("data/stopPhrases.txt","r");
        ArrayList<String> lines=reader.readAll();
        for(String line: lines){
            if(!line.equals("")){
                setStopPhrases.add(line.trim());
            }
        }
        return setStopPhrases;
    }

    public static InstanceList load(String inputFilename, String fileType){
        HashSet<String> setStopPhrases=loadStopPhrases();

        Alphabet alphabet=new Alphabet();
        PhraseAlphabet phraseAlphabet=new PhraseAlphabet();
        DocAlphabet docAlphabet=new DocAlphabet();
        InstanceList instances=new InstanceList(alphabet,docAlphabet,phraseAlphabet);

        try{
            BufferedReader br = new BufferedReader(new FileReader(""+inputFilename));
            String line;
            int i=0;
            while ((line = br.readLine()) != null) {
                if(fileType=="json"){
                    try{
                        JSONObject instanceJson=new JSONObject(line);
                        Instance instance=new Instance(instanceJson,alphabet,phraseAlphabet,"isJson",setStopPhrases);
                        instances.add(instance);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }else{
                    Instance instance=new Instance(line,alphabet,phraseAlphabet,setStopPhrases);
                    instances.add(instance);
                }
                if(i%100==0){
                    System.out.println("loaded "+i+" lines");
                }
                i=i+1;
            }

        }catch (Exception e){
            e.printStackTrace();
        }

        System.out.println("Alphabet's size="+alphabet.size());
        System.out.println("Phrase Alphabets's size="+phraseAlphabet.size());
        System.out.println("Instance number="+instances.size());
        return instances;
    }

    public static void main(String[] args){
        if(args.length<1){
            System.out.println("need 1 parameter: inputFileName");
            System.out.println("e.g., input/20newsgroups.txt, it will load the  data file input/20newsgroups.txt");
        }else{
            String inputFileName=args[0];// input/20newsgroups.txt
            LoadData.load(inputFileName,"txt");
        }
    }
}
