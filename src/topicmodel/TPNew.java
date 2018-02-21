package topicmodel;

import datastructure.*;
import util.*;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static java.lang.Math.exp;

/**
 * Created by huangwaleking on 6/24/17.
 */
public class SparseTP {
    protected ArrayList<TopicAssignment> data;//include instance and topic assignments

    private int numTopics;
    private double alpha;
    private double beta;
    private double betaPhrase;
    protected double betaSum;
    protected double betaSumForPhrases;
    private int numWordTypes;//size of word vocabulary
    private int numPhraseTypes;//size of phrase vocabulary
    protected Alphabet alphabet;

    private double lambda=0.000001;
    private double linkInformationThreshold=0.1;

    /**
     * A. word part
     * add by weijing huang
     */
    //s
    double smoothing_only_sum = 0;
    //r
    double document_topic_sum = 0;
    //q
    double topic_word_sum = 0;
    double[] smoothing_only_bucket;
    double[] document_topic_bucket;
    //coefficient for the term q
    double[] topic_word_coef;
    double word_topic_dist_sum=0;
    //mrf_part
    double mrf_sum=0;
    double mrf_exponent=0;

    //mrf_part for phrase
    double mrf_sum_phrasepart=0;
    double mrf_exponent_phrasepart=0;
    int[] localWordTypeTopicCounts;//used for sampleForPhrase, to count the topic distribution of words in the phrase
    double[] mrf_bucket_phrasepart;//used for sampleForPhrase, the topics are aligned with localWordTypeTopicCounts, (s_k+r_k+q_k)*(exp(?/(phrase.length-1)-1)

    /**
     * B. phrase part
     * add by weijing huang
     */
    //s
    double smoothing_only_sum_phrasepart = 0;
    //r
    double document_topic_sum_phrasepart = 0;
    //q
    double topic_word_sum_phrasepart = 0;
    double[] smoothing_only_bucket_phrasepart;
    double[] document_topic_bucket_phrasepart;
    //coefficient for the term q
    double[] topic_word_coef_phrasepart;
    ValueTopicOperator valueTopicOperator=null;

    //link information, some phrase like "light years" which NPMI=-0.21 does not have the strong coherence between
    //topics of phrases and topics of compositional words
    boolean[] array_linkInfomation;

    private Randoms random;
    protected SparseStatisticsOfWords sparseStatisticsOfWords;//nKV, nK_
    protected SparseStatisticsOfPhrases sparseStatisticsOfPhrases;//nKV, nK_ of phrases

    public SparseTP(int numTopics, double alpha, double beta, double betaPhrase, Alphabet alphabet) {
        this(numTopics, alpha, beta, betaPhrase, new Randoms());
        this.alphabet=alphabet;
    }

    public SparseTP(int numTopics, double alpha, double beta, double betaPhrase, Randoms random) {
        this.numTopics = numTopics;
        this.alpha = alpha;
        this.beta = beta;
        this.betaPhrase=betaPhrase;

        this.random = random;

        this.valueTopicOperator=new ValueTopicOperator();
        this.valueTopicOperator.setNumTopics(this.numTopics);
    }

    public void addInstances(InstanceList training) {
        this.array_linkInfomation=LinkInformation.loadLinkInformation(training.getPhraseAlphabet(),linkInformationThreshold);
        this.data = new ArrayList<TopicAssignment>();

        this.numWordTypes = training.getAlphabet().size();
        this.numPhraseTypes = training.getPhraseAlphabet().size();
        this.betaSum = this.numWordTypes * this.beta;
        this.betaSumForPhrases=this.numPhraseTypes * this.betaPhrase;
        this.sparseStatisticsOfWords = new SparseStatisticsOfWords(numTopics, numWordTypes,
                training,this.valueTopicOperator);
        this.sparseStatisticsOfPhrases = new SparseStatisticsOfPhrases(numTopics, numPhraseTypes,
                training,this.valueTopicOperator);

        for (Instance instance : training) {
            int[][] phrases = instance.getPhrases();
            int[][] phraseTopicSequence = new int[phrases.length][];

            for (int position = 0; position < instance.getPhraseNum(); position++) {
                int[] phrase = instance.getPhrases()[position];
                phraseTopicSequence[position] = new int[phrase.length];
                if(phrase.length==1){//word
                    int topic = random.nextInt(numTopics);
                    phraseTopicSequence[position][0] = topic;
                    sparseStatisticsOfWords.increase(topic, phrase[0]);
                }else{//phrase
                    for (int i = 0; i < phrase.length-1; i++) {
                        int wordTopic = random.nextInt(numTopics);
                        phraseTopicSequence[position][i] = wordTopic;
                        sparseStatisticsOfWords.increase(wordTopic, phrase[i]);
                    }
                    int phraseTopic = random.nextInt(numTopics);
                    phraseTopicSequence[position][phrase.length-1] = phraseTopic;
                    sparseStatisticsOfPhrases.increase(phraseTopic, phrase[phrase.length-1]);
                }
            }

            TopicAssignment t = new TopicAssignment(instance, phraseTopicSequence);
            data.add(t);
        }
    }

    public void sample() {
        //A. word part
        smoothing_only_bucket=new double[this.numTopics];
        document_topic_bucket=new double[this.numTopics];
        topic_word_coef=new double[this.numTopics];

        /**
         * compute smoothing-only bucket
         *s(z) = alpha(z) * beta / (beta * |V| + Nkv[k,.])
         */
        smoothing_only_sum=0;
        for(int k=0;k<this.numTopics;k++){
            smoothing_only_bucket[k]=alpha*beta/(betaSum+this.sparseStatisticsOfWords.nK_[k]);
            smoothing_only_sum+=smoothing_only_bucket[k];
            topic_word_coef[k]=alpha / (this.sparseStatisticsOfWords.nK_[k] + betaSum);
        }

        //B. phrase part
        smoothing_only_bucket_phrasepart=new double[this.numTopics];
        document_topic_bucket_phrasepart=new double[this.numTopics];
        topic_word_coef_phrasepart=new double[this.numTopics];

        /**
         * compute smoothing-only bucket
         *s(z) = alpha(z) * beta / (beta * |V| + Nkv[k,.])
         */
        smoothing_only_sum_phrasepart=0;
        for(int k=0;k<this.numTopics;k++){
            smoothing_only_bucket_phrasepart[k]=alpha*betaPhrase/(betaSumForPhrases+this.sparseStatisticsOfPhrases.nK_[k]);
            smoothing_only_sum_phrasepart+=smoothing_only_bucket_phrasepart[k];
            topic_word_coef_phrasepart[k]=alpha / (this.sparseStatisticsOfPhrases.nK_[k] + betaSumForPhrases);
        }

        localWordTypeTopicCounts=new int[numTopics];
        mrf_bucket_phrasepart=new double[numTopics];

        for (int doc = 0; doc < data.size(); doc++) {
            TopicAssignment t = data.get(doc);
            int[][] phrases = t.instance.getPhrases();
            int[][] topicSequence = t.topicSequence;

            int[] ndk = new int[this.numTopics];
            //update ndk
            for (int i = 0; i < phrases.length; i++) {
                int[] phrase = phrases[i];
                if(phrase.length==1){
                    int k=topicSequence[i][0];
                    ndk[k]++;
                }else{//phrase
                    for (int j = 0; j < phrase.length; j++) {//consider words and phrase together
                        int k = topicSequence[i][j];
                        ndk[k]++;
                    }
                }
            }
            sampleWordsPart(phrases, topicSequence,ndk);
            samplePhrasesPart(phrases, topicSequence,ndk);
        }
        LogUtil.logger().info("num of constraints="+CountingPhraseAssignedAsWords.count(data));
    }

    public void sampleWordsPart(int[][] phrases, int[][] topicSequence,int[] ndk) {

        /**
         * compute doc topic bucket & update topic word coefficient
         * r_{d,k} = n_{d,k} * beta / (betaSum + n_{k,.})
         * q_coefficient(z, d) = (alpha_k + N_{d,k}) / (betaSum + n_{k,.})
         */
        document_topic_sum=0;
        Arrays.fill(document_topic_bucket,0);
        for(int k=0;k<this.numTopics;k++){
            if(ndk[k] != 0){
                int n=ndk[k];
                document_topic_bucket[k] = beta * n / (betaSum + this.sparseStatisticsOfWords.nK_[k]);
                document_topic_sum+=document_topic_bucket[k];
                topic_word_coef[k] = (alpha + n) / (betaSum + this.sparseStatisticsOfWords.nK_[k]);
            }
        }
        //update topicSequence
        for (int position = 0; position < phrases.length; position++) {
            sampleInPhrase(phrases[position], topicSequence[position], ndk);
        }
        //reset topic_word_coef for the next document
        for(int k=0;k<this.numTopics;k++){
            if(ndk[k]!=0){
                topic_word_coef[k]=alpha/(betaSum+sparseStatisticsOfWords.nK_[k]);
            }
        }
    }

    public void samplePhrasesPart(int[][] phrases, int[][] topicSequence, int[] ndk) {
        /**
         * compute doc topic bucket & update topic word coefficient
         * r_{d,k} = n_{d,k} * beta / (betaSum + n_{k,.})
         * q_coefficient(z, d) = (alpha_k + N_{d,k}) / (betaSum + n_{k,.})
         */
        document_topic_sum_phrasepart=0;
        Arrays.fill(document_topic_bucket_phrasepart,0);
        for(int k=0;k<this.numTopics;k++){
            if(ndk[k] != 0){
                int n=ndk[k];
                document_topic_bucket_phrasepart[k] = betaPhrase * n / (betaSumForPhrases + this.sparseStatisticsOfPhrases.nK_[k]);
                document_topic_sum_phrasepart+=document_topic_bucket_phrasepart[k];
                topic_word_coef_phrasepart[k] = (alpha + n) / (betaSumForPhrases + this.sparseStatisticsOfPhrases.nK_[k]);
            }
        }
        //update topicSequence
        for (int position = 0; position < phrases.length; position++) {
            int[] phrase=phrases[position];
            if(phrase.length>1){
                sampleForPhrase(phrases[position], topicSequence[position], ndk);
            }
        }
        //reset topic_word_coef for the next document
        for(int k=0;k<this.numTopics;k++){
            if(ndk[k]!=0){
                topic_word_coef_phrasepart[k]=alpha/(betaSumForPhrases+sparseStatisticsOfPhrases.nK_[k]);
            }
        }
    }

    /**
     * compute the topic distribution for a phrase
     */
    public void sampleForPhrase(int[] phrase, int[] phraseTopic, int[] ndk) {
        int type, oldTopic, newTopic;
        int[] currentTypeTopicCounts;
        int[] tokensPerTopic=this.sparseStatisticsOfPhrases.nK_;
        int n = phrase.length-1;

        //set localWordTypeTopicCounts
        for(int i=0;i<phrase.length-1;i++){
            int wordTopic=phraseTopic[i];
            this.valueTopicOperator.inc(this.localWordTypeTopicCounts,wordTopic);
        }

        type = phrase[n];
        oldTopic = phraseTopic[n];

        // Grab the relevant row from our two-dimensional array
        currentTypeTopicCounts = this.sparseStatisticsOfPhrases.typeTopicCounts[type];

        //	Remove this token from all counts.
        //add by weijing huang
        //(1) reset sum
        smoothing_only_sum_phrasepart-=smoothing_only_bucket_phrasepart[oldTopic];
        document_topic_sum_phrasepart-=document_topic_bucket_phrasepart[oldTopic];

        //(2) reset ss
        //I want to put valueTopicOperator.dec(currentTypeTopicCounts,oldTopic) in step (6), but here also ok!
        valueTopicOperator.dec(currentTypeTopicCounts,oldTopic);
        tokensPerTopic[oldTopic]--;
        ndk[oldTopic]--;
        assert(tokensPerTopic[oldTopic] >= 0) : "old Topic " + oldTopic + " below 0";

        //(3) update bucket
        double tmp1=betaPhrase/(betaSumForPhrases+tokensPerTopic[oldTopic]);
        smoothing_only_bucket_phrasepart[oldTopic]=alpha*tmp1;
        document_topic_bucket_phrasepart[oldTopic]=ndk[oldTopic]*tmp1;

        //(4) update sum
        smoothing_only_sum_phrasepart+=smoothing_only_bucket_phrasepart[oldTopic];
        document_topic_sum_phrasepart+=document_topic_bucket_phrasepart[oldTopic];

        //(5) update coef
        topic_word_coef_phrasepart[oldTopic] = (alpha + ndk[oldTopic]) / (betaSumForPhrases + tokensPerTopic[oldTopic]);

        // Now calculate and add up the scores for each topic for this word
        //(6) update topic_word_sum
        topic_word_sum_phrasepart=0;
        int index=0;
        while(index<currentTypeTopicCounts.length && currentTypeTopicCounts[index]>0){
            int valueTopic=currentTypeTopicCounts[index];
            int nkv=this.valueTopicOperator.getValue(valueTopic);
            int k=this.valueTopicOperator.getTopic(valueTopic);
            topic_word_sum_phrasepart+=nkv*topic_word_coef_phrasepart[k];
            index++;
        }
        //(6.addition1), update MRF part
        int index_mrf=0;
        mrf_sum_phrasepart=0;
        if(array_linkInfomation[type]==true){//set Markov Random Field only when link information is true
            while(index_mrf<localWordTypeTopicCounts.length && localWordTypeTopicCounts[index_mrf]>0){
                int tmpValueTopic=localWordTypeTopicCounts[index_mrf];
                int tmpvalue=this.valueTopicOperator.getValue(tmpValueTopic);
                int tmpTopic=this.valueTopicOperator.getTopic(tmpValueTopic);

                mrf_exponent_phrasepart=exp(lambda*(float)tmpvalue/(float)(phrase.length-1));
                int nkv=this.valueTopicOperator.getValueInArray(currentTypeTopicCounts,tmpTopic);
                mrf_bucket_phrasepart[index_mrf]=(smoothing_only_bucket_phrasepart[tmpTopic]+document_topic_bucket_phrasepart[tmpTopic]
                        +topic_word_coef_phrasepart[tmpTopic]*nkv)*(mrf_exponent_phrasepart-1);
                mrf_sum_phrasepart+=mrf_bucket_phrasepart[index_mrf];
                index_mrf++;
            }
        }else{//set Markov Random Field only when link information is true
            mrf_sum_phrasepart=0;
        }

        double total_mass=smoothing_only_sum_phrasepart+document_topic_sum_phrasepart
                +topic_word_sum_phrasepart+mrf_sum_phrasepart;

        // Choose a random point between 0 and the sum of all topic scores
        double sample = random.nextUniform() * total_mass;

        // Figure out which topic contains that point
        newTopic = -1;
        if(sample<mrf_sum_phrasepart){
            index_mrf=0;
            while(index_mrf<localWordTypeTopicCounts.length && localWordTypeTopicCounts[index_mrf]>0){
                int tmpValueTopic=localWordTypeTopicCounts[index_mrf];
                int tmpTopic=this.valueTopicOperator.getTopic(tmpValueTopic);
                sample-=mrf_bucket_phrasepart[index_mrf];
                if(sample<=0){
                    newTopic=tmpTopic;
                    break;
                }
                index_mrf++;
            }
        }else{
            sample-=mrf_sum_phrasepart;
            if(sample < topic_word_sum_phrasepart){
                int index2=0;
                while(index2<currentTypeTopicCounts.length && currentTypeTopicCounts[index2]>0){
                    int valueTopic=currentTypeTopicCounts[index2];
                    int nkv=this.valueTopicOperator.getValue(valueTopic);
                    int k=this.valueTopicOperator.getTopic(valueTopic);
                    sample-=topic_word_coef_phrasepart[k]*nkv;
                    if(sample<=0){
                        newTopic=k;
                        break;
                    }
                    index2++;
                }
            }else{
                sample-=topic_word_sum_phrasepart;
                //[topic_word_sum,topic_word_sum+document_topic_sum)
                if(sample<document_topic_sum_phrasepart){
                    for(int k=0;k<numTopics;k++){
                        if(ndk[k]!=0){
                            sample-=document_topic_bucket_phrasepart[k];
                        }
                        if(sample<=0){
                            newTopic=k;
                            break;
                        }
                    }
                }else{
                    //[topic_word_sum+document_topic_sum, total_mass)
                    sample-=document_topic_sum_phrasepart;
                    for(int k=0;k<numTopics;k++){
                        sample-=smoothing_only_bucket_phrasepart[k];
                        if(sample<=0){
                            newTopic=k;
                            break;
                        }
                    }
                }
            }
        }

        assert(newTopic>=0);

        // Make sure we actually sampled a topic
        if (newTopic == -1) {
            throw new IllegalStateException ("SparseTP_Minus: New topic not sampled. at "+n);
        }

        // Put that new topic into the counts
        phraseTopic[n] = newTopic;

        //update
        //(1) reset sum
        smoothing_only_sum_phrasepart-=smoothing_only_bucket_phrasepart[newTopic];
        document_topic_sum_phrasepart-=document_topic_bucket_phrasepart[newTopic];

        //(2) reset ss
        valueTopicOperator.inc(currentTypeTopicCounts,newTopic);
        ndk[newTopic]++;
        tokensPerTopic[newTopic]++;
        assert(tokensPerTopic[newTopic] >= 0) : "old Topic " + oldTopic + " below 0";

        //(3) update bucket
        double tmp2=betaPhrase/(betaSumForPhrases+tokensPerTopic[newTopic]);
        smoothing_only_bucket_phrasepart[newTopic]=alpha*tmp2;
        document_topic_bucket_phrasepart[newTopic]=ndk[newTopic]*tmp2;


        //(4) update sum
        smoothing_only_sum_phrasepart+=smoothing_only_bucket_phrasepart[newTopic];
        document_topic_sum_phrasepart+=document_topic_bucket_phrasepart[newTopic];

        //(5) update coef
        topic_word_coef_phrasepart[newTopic] = (alpha + ndk[newTopic]) / (betaSumForPhrases + tokensPerTopic[newTopic]);

        //resest localWordTypeTopicCounts, which is used for counting the topic distribution of words in the phrase
        this.valueTopicOperator.resetToZero(this.localWordTypeTopicCounts);
        //reset this.mrf_bucket_phrasepart
        index_mrf=0;
        while(index_mrf<mrf_bucket_phrasepart.length && mrf_bucket_phrasepart[index_mrf]>0){
            mrf_bucket_phrasepart[index_mrf]=0;
            index_mrf++;
        }
    }

    /**
     * compute the topic distribution for words
     */
    public void sampleInPhrase(int[] phrase, int[] phraseTopic, int[] ndk) {
        int phraseLength=phrase.length>1?phrase.length-1:1;//include the word and phrase situations together
        boolean ifContainPhrase=phrase.length>1?true:false;
        //add by huangweijing
        int oldPhraseTopic=phrase.length>1?phraseTopic[phrase.length-1]:-1;

        int type, oldTopic, newTopic;
        int[] currentTypeTopicCounts;
        int[] tokensPerTopic=this.sparseStatisticsOfWords.nK_;
        for (int n = 0; n < phraseLength; n++) {
            type = phrase[n];
            oldTopic = phraseTopic[n];

            // Grab the relevant row from our two-dimensional array
            currentTypeTopicCounts = this.sparseStatisticsOfWords.typeTopicCounts[type];

            //	Remove this token from all counts.
            //add by weijing huang
            //(1) reset sum
            smoothing_only_sum-=smoothing_only_bucket[oldTopic];
            document_topic_sum-=document_topic_bucket[oldTopic];

            //(2) reset ss
            //I want to put valueTopicOperator.dec(currentTypeTopicCounts,oldTopic) in step (6), but here also ok!
            valueTopicOperator.dec(currentTypeTopicCounts,oldTopic);
            tokensPerTopic[oldTopic]--;
            ndk[oldTopic]--;
            assert(tokensPerTopic[oldTopic] >= 0) : "old Topic " + oldTopic + " below 0";

            //(3) update bucket
            double tmp1=beta/(betaSum+tokensPerTopic[oldTopic]);
            smoothing_only_bucket[oldTopic]=alpha*tmp1;
            document_topic_bucket[oldTopic]=ndk[oldTopic]*tmp1;


            //(4) update sum
            smoothing_only_sum+=smoothing_only_bucket[oldTopic];
            document_topic_sum+=document_topic_bucket[oldTopic];

            //(5) update coef
            topic_word_coef[oldTopic] = (alpha + ndk[oldTopic]) / (betaSum + tokensPerTopic[oldTopic]);

            // Now calculate and add up the scores for each topic for this word
            //(6) update topic_word_sum
            topic_word_sum=0;
            int index=0;
            while(index<currentTypeTopicCounts.length && currentTypeTopicCounts[index]>0){
                int valueTopic=currentTypeTopicCounts[index];
                int nkv=this.valueTopicOperator.getValue(valueTopic);
                int k=this.valueTopicOperator.getTopic(valueTopic);
                topic_word_sum+=nkv*topic_word_coef[k];
                index++;
            }
            //(6.addition1), update MRF part
            double total_mass=0;
            if(ifContainPhrase==true){
                int phraseType=phrase[phraseLength];
                if(array_linkInfomation[phraseType]==true){//set Markov Random Field only when link information is true
                    mrf_exponent=exp(lambda/(float)(phraseLength));
                    int nkv=this.valueTopicOperator.getValueInArray(currentTypeTopicCounts,oldPhraseTopic);
                    mrf_sum=(smoothing_only_bucket[oldPhraseTopic]+document_topic_bucket[oldPhraseTopic]
                            +topic_word_coef[oldPhraseTopic]*nkv)*(mrf_exponent-1);
                    total_mass=smoothing_only_sum+document_topic_sum+topic_word_sum+mrf_sum;
                }else{//set Markov Random Field only when link information is true
                    mrf_exponent=0;
                    mrf_sum=0;
                    total_mass=smoothing_only_sum+document_topic_sum+topic_word_sum;//no mrf_sum
                }
            }else{
                mrf_exponent=0;
                mrf_sum=0;
                total_mass=smoothing_only_sum+document_topic_sum+topic_word_sum;//no mrf_sum
            }


            // Choose a random point between 0 and the sum of all topic scores
            double sample = random.nextUniform() * total_mass;

            // Figure out which topic contains that point
            newTopic = -1;
            if(ifContainPhrase==true && sample<mrf_sum){
                newTopic=oldPhraseTopic;
            }else{
                sample-=mrf_sum;
                if(sample < topic_word_sum){
                    int index2=0;
                    while(index2<currentTypeTopicCounts.length && currentTypeTopicCounts[index2]>0){
                        int valueTopic=currentTypeTopicCounts[index2];
                        int nkv=this.valueTopicOperator.getValue(valueTopic);
                        int k=this.valueTopicOperator.getTopic(valueTopic);
                        sample-=topic_word_coef[k]*nkv;
                        if(sample<=0){
                            newTopic=k;
                            break;
                        }
                        index2++;
                    }
                }else{
                    sample-=topic_word_sum;
                    //[topic_word_sum,topic_word_sum+document_topic_sum)
                    if(sample<document_topic_sum){
                        for(int k=0;k<numTopics;k++){
                            if(ndk[k]!=0){
                                sample-=document_topic_bucket[k];
                            }
                            if(sample<=0){
                                newTopic=k;
                                break;
                            }
                        }
                    }else{
                        //[topic_word_sum+document_topic_sum, total_mass)
                        sample-=document_topic_sum;
                        for(int k=0;k<numTopics;k++){
                            sample-=smoothing_only_bucket[k];
                            if(sample<=0){
                                newTopic=k;
                                break;
                            }
                        }
                    }
                }
            }


            assert(newTopic>=0);

            // Make sure we actually sampled a topic
            if (newTopic == -1) {
                throw new IllegalStateException ("SparseTP_Minus: New topic not sampled. at "+n);
            }

            // Put that new topic into the counts
            phraseTopic[n] = newTopic;

            //update
            //(1) reset sum
            smoothing_only_sum-=smoothing_only_bucket[newTopic];
            document_topic_sum-=document_topic_bucket[newTopic];

            //(2) reset ss
            valueTopicOperator.inc(currentTypeTopicCounts,newTopic);
            ndk[newTopic]++;
            tokensPerTopic[newTopic]++;
            assert(tokensPerTopic[newTopic] >= 0) : "old Topic " + oldTopic + " below 0";

            //(3) update bucket
            double tmp2=beta/(betaSum+tokensPerTopic[newTopic]);
            smoothing_only_bucket[newTopic]=alpha*tmp2;
            document_topic_bucket[newTopic]=ndk[newTopic]*tmp2;


            //(4) update sum
            smoothing_only_sum+=smoothing_only_bucket[newTopic];
            document_topic_sum+=document_topic_bucket[newTopic];

            //(5) update coef
            topic_word_coef[newTopic] = (alpha + ndk[newTopic]) / (betaSum + tokensPerTopic[newTopic]);
        }
    }

    public static void portal(String inputFilename, String pklName, int topicNumber,
             int iterationNumber, int numTopPhrases) {
        TimeClock clock = new TimeClock();
        InstanceList training = InstanceList.load(new File(inputFilename));
        TopicPrintUtil.init(pklName);
        System.out.println(clock.tick("loading data"));

        int numTopics = topicNumber;
        double alpha = 2/(double)numTopics;
        double beta = 0.01;
        double betaPhrase=0.01;

        SparseTP sparseTP_ = new SparseTP(numTopics, alpha, beta, betaPhrase, training.getAlphabet());
        sparseTP_.addInstances(training);
        System.out.println(clock.tick("initialization model"));

        boolean showDigitNum = true;
        for (int i = 0; i < iterationNumber; i++) {
            sparseTP_.sample();
            LogUtil.logger().info(clock.tick("iteration" + i));
            if (i % 10 == 0) {
                System.out.println(TopicPrintUtil.showTopics(10, numTopics, sparseTP_.numPhraseTypes,
                        sparseTP_.sparseStatisticsOfPhrases.typeTopicCounts, training.getPhraseAlphabet(), showDigitNum,
                        sparseTP_.valueTopicOperator));
//                System.out.println(TopicPrintUtil.showTopics(10, numTopics, sparseTP_.numWordTypes,
//                        sparseTP_.sparseStatisticsOfWords.typeTopicCounts, training.getAlphabet(), showDigitNum,
//                        sparseTP_.valueTopicOperator));
                System.out.println(clock.tick("showing topics"));
            }
        }
        //reorder topics according to the size of topics
        HashMap<Integer,Integer> oldToNew=AnalysisOnPhraseTopic.getOrder(sparseTP_.sparseStatisticsOfPhrases.nK_);
        String sortedTopics=TopicPrintUtil.showSortedTopics(numTopPhrases, numTopics, sparseTP_.numPhraseTypes,
                sparseTP_.sparseStatisticsOfPhrases.typeTopicCounts, training.getPhraseAlphabet(), showDigitNum,
                sparseTP_.valueTopicOperator,oldToNew, sparseTP_.sparseStatisticsOfPhrases.nK_);
        System.out.println(sortedTopics);
        LogUtil.logger().info(sortedTopics);

        File f = new File("result");
        if(!f.exists()) {
            new File("result").mkdir();
        }
        String resultFilename="result/"+inputFilename.split("\\.")[0].split("/")[1]
                +"_K"+topicNumber+"_iteration"+iterationNumber+".txt";
        LogUtil.logger().info("The topics are restored in "+resultFilename+", and the topics are ordered by the " +
                "number of topical phrases in descending order");
        MyFile resultWriter=new MyFile(resultFilename,"w");
        resultWriter.print(sortedTopics);
        resultWriter.close();

        //sort word topics, and restore the result in a file.
        oldToNew=AnalysisOnPhraseTopic.getOrder(sparseTP_.sparseStatisticsOfWords.nK_);
        String sortedWordTopics=TopicPrintUtil.showSortedTopics(numTopPhrases, numTopics, sparseTP_.numWordTypes,
                sparseTP_.sparseStatisticsOfWords.typeTopicCounts, training.getAlphabet(), showDigitNum,
                sparseTP_.valueTopicOperator,oldToNew, sparseTP_.sparseStatisticsOfWords.nK_);
        String resultFilenameForWords="result/"+inputFilename.split("\\.")[0].split("/")[1]
                +"_K"+topicNumber+"_iteration"+iterationNumber+".word.txt";
        resultWriter=new MyFile(resultFilenameForWords,"w");
        resultWriter.print(sortedWordTopics);
        resultWriter.close();
    }


    public static void main(String[] args) {
        if(args.length<4){
            System.out.println("need 4 parameters: inputFileName, topicNumber, iterationNumber, and numTopPhrases");
            System.out.println("e.g., input/20newsgroups.txt 20 1000 20");
        }else{
            String inputFileName=args[0];// input/20newsgroups.txt
            String serializedFile=inputFileName+".serialized";
            String inputPkl=inputFileName+".pkl";
            int topicNumber=Integer.parseInt(args[1]);
            int iterationNumber=Integer.parseInt(args[2]);
            int numTopPhrases=Integer.parseInt(args[3]);
            portal(serializedFile,inputPkl,topicNumber,iterationNumber,numTopPhrases);
        }
    }

}
