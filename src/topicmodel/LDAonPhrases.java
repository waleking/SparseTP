package topicmodel;

import datastructure.*;
import util.Randoms;
import util.TimeClock;
import util.TopicPrintUtil;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Weijing Huang on 6/24/17.
 */
@SuppressWarnings("Duplicates")
public class LDAonPhrases {
    protected ArrayList<TopicAssignment> data;//include instance and topic assignments

    private int numTopics;
    private double alpha;
    private double beta;
    protected double betaSum;
    private int numPhraseTypes;//size of phrase vocabulary

    private Randoms random;
    protected StatisticsOfPhrases statisticsOfPhrases;//nKV, nK_

    public LDAonPhrases(int numTopics, double alpha, double beta) {
        this(numTopics, alpha, beta, new Randoms(0));
    }

    public LDAonPhrases(int numTopics, double alpha, double beta, Randoms random) {
        this.numTopics = numTopics;
        this.alpha = alpha;
        this.beta = beta;

        this.random = random;
    }

    public void addInstances(InstanceList training) {
        this.data = new ArrayList<TopicAssignment>();

        this.numPhraseTypes = training.getPhraseAlphabet().size();
        this.betaSum = this.numPhraseTypes * this.beta;
        this.statisticsOfPhrases = new StatisticsOfPhrases(numTopics, numPhraseTypes);

        for (Instance instance : training) {
            int[][] phrases = instance.getPhrases();
            int[][] phraseTopicSequence = new int[phrases.length][];

            for (int position = 0; position < instance.getPhraseNum(); position++) {
                int[] phrase = instance.getPhrases()[position];
                phraseTopicSequence[position] = new int[phrase.length];
                if(phrase.length==1){//word
                }else{//phrase
                    int phraseTopic=random.nextInt(numTopics);
                    phraseTopicSequence[position][phrase.length-1]=phraseTopic;
                    statisticsOfPhrases.increase(phraseTopic,phrase[phrase.length-1]);
                }
            }

            TopicAssignment t = new TopicAssignment(instance, phraseTopicSequence);
            data.add(t);
        }
    }

    public void sample() {
        for (int doc = 0; doc < data.size(); doc++) {
            TopicAssignment t = data.get(doc);
            int[][] phrases = t.instance.getPhrases();
            int[][] topicSequence = t.topicSequence;
            if (phrases.length > 0) {
                sampleForSingleDoc(phrases, topicSequence);
            }
        }
    }

    public void sampleForSingleDoc(int[][] phrases, int[][] topicSequence) {
        int[] ndk = new int[this.numTopics];
        //update ndk
        for (int i = 0; i < phrases.length; i++) {
            int[] phrase = phrases[i];
            if(phrase.length==1){
            }else{//phrase
                ndk[ topicSequence[i][phrase.length-1] ]++;
            }
        }
        //update topicSequence
        for (int position = 0; position < phrases.length; position++) {
            if (phrases[position].length > 1) {
                sampleForPhrase(phrases[position], topicSequence[position], ndk);
            }
        }
    }

    /**
     * compute the topic distribution for a_single_word
     */
    private void sampleForPhrase(int[] phrase, int[] phraseTopic, int[] ndk) {
        int oldTopic = phraseTopic[phrase.length-1];
        int word = phrase[phrase.length-1];

        double[] topic_bucket = new double[numTopics];
        double word_topic_dist_sum = 0;

        //reset
        this.statisticsOfPhrases.decrease(oldTopic, word);
        ndk[oldTopic]--;

        //sample
        for (int k = 0; k < numTopics; k++) {

            double p = (ndk[k] + alpha) * (this.statisticsOfPhrases.nKV[k][word] + beta) /
                    (this.statisticsOfPhrases.nK_[k] + this.betaSum);
            word_topic_dist_sum += p;
            topic_bucket[k] = word_topic_dist_sum;
        }
        int newTopic = -1;
        double sample = random.nextUniform() * word_topic_dist_sum;
        for (int k = 0; k < numTopics; k++) {
            if (sample < topic_bucket[k]) {
                newTopic = k;
                break;
            }
        }
        //update statistics
        this.statisticsOfPhrases.increase(newTopic, word);
        phraseTopic[phrase.length-1] = newTopic;
        ndk[newTopic]++;
    }

    public static void portal(String inputFilename,String pklName) {
        TimeClock clock = new TimeClock();
        InstanceList training = InstanceList.load(new File(inputFilename));
        TopicPrintUtil.init(pklName);
        System.out.println(clock.tick("loading data"));

        int numTopics = 30;
        double alpha = 0.1;
        double beta = 0.01;

        LDAonPhrases ldaOnPhrase = new LDAonPhrases(numTopics, alpha, beta);
        ldaOnPhrase.addInstances(training);
        System.out.println(clock.tick("initialization model"));

        boolean showDigitNum = true;
        for (int i = 0; i < 1000; i++) {
            ldaOnPhrase.sample();
            System.out.println(clock.tick("iteration" + i));
            if (i % 10 == 0) {
                System.out.println(TopicPrintUtil.showTopics(10, numTopics, ldaOnPhrase.numPhraseTypes,
                        ldaOnPhrase.statisticsOfPhrases.nKV, training.getPhraseAlphabet(), showDigitNum));
                System.out.println(clock.tick("showing topics"));
            }
        }
    }


    public static void main(String[] args) {
        portal("input/mathematics.txt.serialized","input/mathematics.txt.pkl");
//        portal("input/Argentina.json.serialized");
    }

}