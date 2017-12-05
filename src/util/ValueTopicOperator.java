package util;

/**
 * Created by huangwaleking on 4/5/17.
 */
public class ValueTopicOperator {
    public int topicMask;
    public int topicBits;

    /**
     * get value of the topic
     * @param input
     * @return
     */
    public int getValue(int input){
        return input >> topicBits;
    }

    /**
     * get topic id of the topic
     * @param input
     * @return
     */
    public int getTopic(int input){
        return input & topicMask;
    }

    /**
     * value
     * @param value
     * @param topic
     * @return
     */
    public int encode(int value, int topic){
        return (value << topicBits) + topic;
    }

    public void setNumTopics(int numTopics) {
        if (Integer.bitCount(numTopics) == 1) {
            // exact power of 2
            topicMask = numTopics - 1;
            topicBits = Integer.bitCount(topicMask);
        }
        else {
            // otherwise add an extra bit
            topicMask = Integer.highestOneBit(numTopics) * 2 - 1;
            topicBits = Integer.bitCount(topicMask);
        }
    }

    /**
     * shift the increased value to the left
     * @param index
     * @param array
     */
    public void sortToLeft(int index, int[] array){
        // Now ensure that the array is still sorted by
        //  bubbling this value up.
        while (index > 0 &&
                array[index] > array[index - 1]) {
            int temp = array[index];
            array[index] = array[index - 1];
            array[index - 1] = temp;

            index--;
        }
    }

    /**
     * Shift the reduced value to the right
     * @param index
     * @param array
     */
    public void sortToRight(int index,int[] array){
        while(index<array.length-1 && array[index]<array[index+1]){
            int temp=array[index];
            array[index]=array[index+1];
            array[index+1]=temp;
            index++;
        }
    }

    /**
     * decreas the topic assignment in array
     * (inverse operation to dec() function)
     * @param array
     * @param topic
     */
    public void dec(int[] array,int topic){
        int index=findTopicLocationInArray(array,topic);
        int currentValue=getValue(array[index]);
        currentValue--;
        if(currentValue==0){
            array[index]=0;
        }else{
            array[index]=encode(currentValue,topic);
        }
        sortToRight(index,array);
    }

    /**
     * find the topic in the array
     * @return index
     */
    public int findTopicLocationInArray(int[] array,int topic){
        int index=0;
        int tmpTopic=getTopic(array[index]);
        while(array[index]>0 && tmpTopic!=topic){
            index++;
            tmpTopic=getTopic(array[index]);
        }
        return index;
    }

    /**
     * given topic, get the value
     */
    public int getValueInArray(int[] array, int topic){
        int index=0;
        while(array[index]>0){
            if(getTopic(array[index])==topic){
                return getValue(array[index]);
            }
            index++;
        }
        return 0;
    }

    /**
     * increase the topic assignment in array
     * (inverse operation to dec() function)
     * @param array
     * @param topic
     */
    public void inc(int[] array,int topic){
        int index=findTopicLocationInArray(array,topic);
        int currentValue=getValue(array[index]);
        if(currentValue==0){//a new topic in the array
            int newValue=1;
            array[index]=encode(newValue,topic);
        }else{
            int newValue=currentValue+1;
            array[index]=encode(newValue,topic);
            sortToLeft(index,array);
        }
    }

    /**
     * since array is unreadble, get the readble array
     * , such as "topic: number of topic assignments"
     * @param array
     * @return
     */
    public String getReadableStr(int[] array){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<array.length;i++){
            int topic=getTopic(array[i]);
            int value=getValue(array[i]);
            sb.append(Integer.toString(topic)+":"+Integer.toString(value)+",");
        }
        return sb.toString();
    }

    /**
     * clean the array
     */
    public void resetToZero(int[] array){
        int index=0;
        while(index<array.length && array[index]>0 ){
            array[index]=0;
            index++;
        }
    }

    public static void main(String[] args){
        ValueTopicOperator t=new ValueTopicOperator();
        int[] topicAssignment={1,2,4,0,1,1,2,5,2,2,2,2,5,5,5,5};
        int K=6;
        t.setNumTopics(K);
        int[] myIntArray = new int[K];
        for(int i=0;i<topicAssignment.length;i++){
            t.inc(myIntArray,topicAssignment[i]);
            System.out.println("insert"+Integer.toString(topicAssignment[i])+"__"+t.getReadableStr(myIntArray));
        }
        System.out.println("value of topic 1 is "+t.getValueInArray(myIntArray,1));//expected value is 3
        System.out.println("value of topic 2 is "+t.getValueInArray(myIntArray,2));//expected value is 6
        System.out.println("value of topic 3 is "+t.getValueInArray(myIntArray,3));//expected value is 0

        for(int i=0;i<topicAssignment.length;i++){
            t.dec(myIntArray,topicAssignment[i]);
            System.out.println("delete"+Integer.toString(topicAssignment[i])+"__"+t.getReadableStr(myIntArray));
        }
        System.out.println("value of topic 1 is "+t.getValueInArray(myIntArray,1));//expected value is 0

        for(int i=0;i<topicAssignment.length;i++){
            t.inc(myIntArray,topicAssignment[i]);
            System.out.println("insert"+Integer.toString(topicAssignment[i])+"__"+t.getReadableStr(myIntArray));
        }
        t.resetToZero(myIntArray);
        System.out.println("after clearance, myIntArray="+t.getReadableStr(myIntArray));

    }
}
