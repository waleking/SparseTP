package datastructure;

/**
 * Created by huangwaleking on 5/7/17.
 */
public class TopicAssignment {
    public Instance instance;

    //for word, topicSequence[i]'s length is 1; for phrase, topicSequence[i]'s length>1
    public int[][] topicSequence;

    public TopicAssignment(Instance instance, int[][] topicSequence) {
        this.instance = instance;
        this.topicSequence=topicSequence;
    }
}
