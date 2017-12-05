package util;

public class Tuple {
    public int phraseId;
    public int numTopicAssignedAsPhrase;

    public Tuple(int phraseId,int numTopicAssignedAsPhrase){
        this.phraseId=phraseId;
        this.numTopicAssignedAsPhrase=numTopicAssignedAsPhrase;
    }

    public int getPhraseId() {
        return phraseId;
    }

    public int getNumTopicAssignedAsPhrase() {
        return numTopicAssignedAsPhrase;
    }

    @Override
    public String toString() {
        return "(" + phraseId + "," + numTopicAssignedAsPhrase + ')';
    }
}
