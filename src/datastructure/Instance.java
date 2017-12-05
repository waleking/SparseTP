package datastructure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.Iterator;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class Instance implements Serializable {
    private String name;
    // Serialization of Instance
    private static final long serialVersionUID = 1;

    public int[][] phrases;
    private int phraseNum=-1;

    private Alphabet alphabet;
    private PhraseAlphabet phraseAlphabet;

    public int[][] getPhrases() {
        return phrases;
    }
    public int getPhraseNum() {
        return phraseNum;
    }

    private void setPhrases(String phraseStr){
        String[] splitted=phraseStr.trim().split(",");
        if(splitted.length>0){
            this.phraseNum=splitted.length;
            phrases=new int[phraseNum][];
            for(int i=0;i<phraseNum;i++){
                String[] words=splitted[i].split(" ");
                if(words.length>1){
                    phrases[i]=new int[words.length+1];// such that [word 1, word 2, phrase 1], have the additional element for phrase
                    for(int j=0;j<words.length;j++){
//                    if(!words[j].equals("")){
                        phrases[i][j]=alphabet.lookupIndex(words[j]);
//                    }
                    }
                    phrases[i][words.length]=phraseAlphabet.lookupIndex(splitted[i]);
                }else{
                    phrases[i]=new int[words.length];//only word
                    phrases[i][0]=alphabet.lookupIndex(words[0]);
                }

                if(words.length>1){
                    phraseAlphabet.lookupIndex(splitted[i]);
                }
            }
        }else{
            this.phraseNum=-1;//empty
        }
    }

    private void setPhrasesByJson(JSONObject instanceJson){
        Iterator iter=instanceJson.keys();
        try{
            for(;iter.hasNext();){//only 1 key in categoryInfo
                String keyName=(String)iter.next();
                if(keyName.equals("wordsAndPhrases")){
                    String wordsAndPhrases=(String)instanceJson.get("wordsAndPhrases");
                    setPhrases(wordsAndPhrases);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Instance(String line, Alphabet alphabet,PhraseAlphabet phraseAlphabet){
        this.alphabet=alphabet;
        this.phraseAlphabet=phraseAlphabet;
        setPhrases(line);
    }

    public Instance(JSONObject jsonObject, Alphabet alphabet,PhraseAlphabet phraseAlphabet, String isJson){
        if(isJson.equals("isJson")){
            this.alphabet=alphabet;
            this.phraseAlphabet=phraseAlphabet;
            setPhrasesByJson(jsonObject);
        }
    }
}
