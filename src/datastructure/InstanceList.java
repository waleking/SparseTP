package datastructure;

import java.io.*;
import java.util.ArrayList;

/**
 * Created by huangwaleking on 5/5/17.
 */
public class InstanceList extends ArrayList<Instance> implements Serializable {
    private Alphabet alphabet;
    private DocAlphabet docAlphabet;
    private PhraseAlphabet phraseAlphabet;
    // Serialization of InstanceList
    private static final long serialVersionUID = 1;

    public InstanceList(Alphabet alphabet, DocAlphabet docAlphabet,PhraseAlphabet phraseAlphabet){
        this.alphabet=alphabet;
        this.docAlphabet=docAlphabet;
        this.phraseAlphabet=phraseAlphabet;
    }

    public Alphabet getAlphabet() {
        return alphabet;
    }

    public DocAlphabet getDocAlphabet() {
        return docAlphabet;
    }

    public PhraseAlphabet getPhraseAlphabet() {
        return phraseAlphabet;
    }

    public boolean add (Instance instance){
        return super.add(instance);
    }
}
