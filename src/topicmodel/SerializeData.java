package topicmodel;

import datastructure.*;
import org.json.JSONObject;
import util.MyFile;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashSet;

/**
 * Created by huangwaleking on 5/4/17.
 */
public class SerializeData {
    /**
     * filter out very frequent phrases (e.g., united states, new york, los angeles, bueno aires)
     */
    public static HashSet<String> loadStopPhrases(){
        HashSet<String> setStopPhrases=new HashSet<String>();
        MyFile reader=new MyFile("input/stopPhrases.txt","r");
        ArrayList<String> lines=reader.readAll();
        for(String line: lines){
            if(!line.equals("")){
                setStopPhrases.add(line.trim());
            }
        }
        return setStopPhrases;
    }

    public static void serialize(String inputFilename, String outputFilename, String fileType){
        HashSet<String> setStopPhrases=loadStopPhrases();

        MyFile reader=new MyFile(""+inputFilename,"r");
        ArrayList<String> lines=reader.readAll();

        Alphabet alphabet=new Alphabet();
        PhraseAlphabet phraseAlphabet=new PhraseAlphabet();
        DocAlphabet docAlphabet=new DocAlphabet();
        InstanceList instances=new InstanceList(alphabet,docAlphabet,phraseAlphabet);

        for(int i=0;i<lines.size();i++){
            String line=lines.get(i).trim();
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
                System.out.println("processed "+i+" lines");
            }
        }
        System.out.println("Alphabet's size="+alphabet.size());
        System.out.println("Phrase Alphabets's size="+phraseAlphabet.size());
        System.out.println("Instance number="+instances.size());
        ObjectOutputStream oos;
        try {
            oos = new ObjectOutputStream(new FileOutputStream(outputFilename));
            oos.writeObject(instances);
            oos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args){
        if(args.length<1){
            System.out.println("need 1 parameter: inputFileName");
            System.out.println("e.g., input/20newsgroups.txt, it will generate the serialized data file input/20newsgroups.txt.serialized");
        }else{
            String inputFileName=args[0];// input/20newsgroups.txt
            String serializedFile=inputFileName+".serialized";
            SerializeData.serialize(inputFileName,
            serializedFile,"txt");
        }
    }
}
