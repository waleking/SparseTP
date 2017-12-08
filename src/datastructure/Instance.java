package datastructure;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashSet;
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

    private void setPhrases(String phraseStr,HashSet<String> setStopPhrases){
        String[] splitted=phraseStr.trim().split(",");
        if(splitted.length>0){
            int num=0;
            for(String phraseOrWord: splitted){
                if(phraseOrWord.contains(" ")){
                    if(!setStopPhrases.contains(phraseOrWord)) {
                        num++;
                    }else{
                        //(*) nothing as line **
                    }
                }else{
                    num++;
                }
            }
            this.phraseNum=num;
            phrases=new int[num][];
            int index=0;
            for(int i=0;i<splitted.length;i++){
                String[] words=splitted[i].split(" ");
                if(words.length>1){//phrases
                    if(!setStopPhrases.contains(splitted[i])){//not a very frequent phrase
                        phrases[index]=new int[words.length+1];// such that [word 1, word 2, phrase 1], have the additional element for phrase
                        for(int j=0;j<words.length;j++){
                            phrases[index][j]=alphabet.lookupIndex(words[j]);
                        }
                        phrases[index][words.length]=phraseAlphabet.lookupIndex(splitted[i]);
                        phraseAlphabet.lookupIndex(splitted[index]);
                        index++;
                    }else{//
                        //(**) nothing as line *
                    }

                }else{//words
                    phrases[index]=new int[words.length];//only word
                    phrases[index][0]=alphabet.lookupIndex(words[0]);
                    index++;
                }
            }
        }else{
            this.phraseNum=-1;//empty
        }
    }

    private void setPhrasesByJson(JSONObject instanceJson,HashSet<String> setStopPhrases){
        Iterator iter=instanceJson.keys();
        try{
            for(;iter.hasNext();){//only 1 key in categoryInfo
                String keyName=(String)iter.next();
                if(keyName.equals("wordsAndPhrases")){
                    String wordsAndPhrases=(String)instanceJson.get("wordsAndPhrases");
                    setPhrases(wordsAndPhrases,setStopPhrases);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public Instance(String line, Alphabet alphabet,PhraseAlphabet phraseAlphabet,HashSet<String> setStopPhrases){
        this.alphabet=alphabet;
        this.phraseAlphabet=phraseAlphabet;
        setPhrases(line,setStopPhrases);
    }

    public Instance(JSONObject jsonObject, Alphabet alphabet,PhraseAlphabet phraseAlphabet, String isJson,HashSet<String> setStopPhrases){
        if(isJson.equals("isJson")){
            this.alphabet=alphabet;
            this.phraseAlphabet=phraseAlphabet;
            setPhrasesByJson(jsonObject,setStopPhrases);
        }
    }
}
