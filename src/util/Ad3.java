package util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

/**
 * Created by huangwaleking on 9/11/17.
 * HashMap<TreeMap<String, Integer>, Integer>
 */
public class Ad3 {

    //f_[i][x]=f_i(x)
    public static HashMap<TreeMap<String,Integer>,Double>[][] f_;

    public static HashMap<TreeMap<String,Integer>,Double>[][] F_;

    /*
     * two initial condition:  (1) F_0(x)=f_0(x); (2) F_i(0)={[null]}
     */
    public static void init_F(){
        initAccumulatePhraseSize();
        //Step 0, init F_[][] array
        F_=(HashMap<TreeMap<String,Integer>,Double>[][])(new HashMap[listPhraseSize.length][]);
        for(int i=0;i<listPhraseSize.length;i++){
            F_[i]=(HashMap<TreeMap<String,Integer>,Double>[])(new HashMap[accumulatePhraseSize[i]+1]);
        }
        //Step 1, init by the initial condition F_0(x)=f_0(x)
        for(int x=0;x<=listPhraseSize[0];x++){
            F_[0][x]=f_[0][x];
        }
        //Step 2, init by the initial condition F_i(0)={[null]}
        for(int i=0;i<accumulatePhraseSize.length;i++){
            F_[i][0]=f_[i][0];
        }
    }

    public static void init_f(int[] listPhraseSize){
        f_=(HashMap<TreeMap<String,Integer>,Double>[][])(new HashMap[listPhraseSize.length][]);
        for(int i=0;i<listPhraseSize.length;i++){
            f_[i]=(HashMap<TreeMap<String,Integer>,Double>[])(new HashMap[listPhraseSize[i]+1]);
        }
        for(int i=0;i<listPhraseSize.length;i++){
            for(int x=0;x<=listPhraseSize[i];x++){
                if(x==0){
                    f_[i][x]=new HashMap<TreeMap<String,Integer>,Double>();
                    TreeMap<String,Integer> singleSolution=new TreeMap<String,Integer>();
                    singleSolution.put("",1);//repeated part=1
                    f_[i][x].put(singleSolution,1.0);//singleSolution appears once
                }else{
                    ArrayList<Tuple> l=new ArrayList<>();
                    l.add(new Tuple(i,x));
                    f_[i][x]=new HashMap<TreeMap<String,Integer>,Double>();
                    TreeMap<String,Integer> singleSolution=new TreeMap<String,Integer>();
                    singleSolution.put(Integer.toString(listPhraseSize[i])+"_"+Integer.toString(x),1);
                    f_[i][x].put(singleSolution,1.0);//singleSolution appears once
                }
            }
        }
    }

    public static HashMap<TreeMap<String,Integer>,Double> get_F_(int i, int x){
        if(x<=accumulatePhraseSize[i]){
            return F_[i][x];
        }else {
            HashMap<TreeMap<String,Integer>,Double> empty=new HashMap<TreeMap<String,Integer>,Double>();
            TreeMap<String,Integer> singleSolution=new TreeMap<String,Integer>();
            singleSolution.put("",1);//repeated part=1
            empty.put(singleSolution,1.0);//singleSolution appears once
            return empty;
        }
    }

    public static HashMap<TreeMap<String,Integer>,Double> get_f_(int i, int x){
        if(x<=listPhraseSize[i]){
            return f_[i][x];
        }else {
            HashMap<TreeMap<String,Integer>,Double> empty=new HashMap<TreeMap<String,Integer>,Double>();
            TreeMap<String,Integer> singleSolution=new TreeMap<String,Integer>();
            singleSolution.put("",1);//repeated part=1
            empty.put(singleSolution,1.0);//singleSolution appears once
            return empty;
        }
    }

    public static void update_F(){
        for(int i=1;i<accumulatePhraseSize.length;i++){
            for(int x=1;x<accumulatePhraseSize[i]+1;x++){
                //update F_[i][x]=\cup_{b=0}^{x} (f_[i][b] \oplus F_[i-1][x-b])
//                System.out.println("updating F_["+i+"]["+x+"]. ");
                F_[i][x]=new HashMap<TreeMap<String,Integer>,Double>();
                for(int b=0;b<=x;b++){
                    HashMap<TreeMap<String,Integer>,Double> oplus_result=new HashMap<TreeMap<String,Integer>,Double>();
                    if(b<=listPhraseSize[i] && (x-b)<=accumulatePhraseSize[i-1]){
                        oplus(oplus_result,get_f_(i,b),get_F_(i-1,x-b));
                    }
                    F_[i][x]=MergingTwoHashMap.merge(F_[i][x],oplus_result);
                }
//                System.out.println("after updating, F_["+i+"]["+x+"]="+outputSet(get_F_(i,x)));
            }
        }
    }

    /**
     * outputSet call outputList, which call Tuple.toString()
     * @param set
     * @return
     */
    public static String outputSet(HashMap<TreeMap<String,Integer>,Double> set){
        StringBuffer sb=new StringBuffer();
        if(set!=null){
            sb.append("{");
            for(TreeMap<String,Integer> list: set.keySet()){
                sb.append(outputList(list)+"*"+Double.toString(set.get(list)));
                sb.append(";");
            }
//            for (ArrayList<Tuple> l : set) {
//                sb.append(outputList(l)+",");
//            }
            sb.append("}");
        }else{
            sb.append("null");
        }
        return sb.toString();
    }

    public static String outputList(TreeMap<String,Integer> list){
        StringBuffer sb=new StringBuffer();
        sb.append("[");
        if(list.size()>0){
            for(String t: list.keySet()){
                sb.append(t+"^"+list.get(t));
                //todo
                sb.append(",");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    public static String output_f_(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<listPhraseSize.length;i++){
            for(int x=0;x<=listPhraseSize[i];x++){
                sb.append(outputSet(get_f_(i,x)));
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static String output_F_(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<accumulatePhraseSize.length;i++){
            for(int x=0;x<=accumulatePhraseSize[i];x++){
                sb.append(outputSet(get_F_(i,x)));
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public static int[] listPhraseSize;

    public static int[] accumulatePhraseSize;//listPhraseSize=[2,2,2], then accumulatePhraseSize=[2,4,6]

    public static void initAccumulatePhraseSize(){
        accumulatePhraseSize=new int[listPhraseSize.length];
        accumulatePhraseSize[0]= listPhraseSize[0];
        for(int i=1;i<listPhraseSize.length;i++){
            accumulatePhraseSize[i]= accumulatePhraseSize[i-1]+listPhraseSize[i];
        }
    }

//    public static TreeMap<String,Integer> merge(TreeMap<String,Integer> l1, TreeMap<String,Integer> l2){
//        TreeMap<String,Integer> result=new TreeMap<String,Integer>();
//        if(l1.size()>0){//todo
//            for (String x1: l1.keySet()) {
//                result.put(x1,l1.get(x1));
//            }
//        }
//        if(l2.size()>0){
//            for(String x2: l2.keySet()){
//                result.put(x2,l2.get(x2));
//            }
//        }
//        return result;
//    }

    /**
     * set1 oplus set2= {u+v}, u \in set1, v \in set2
     * then update result=set1 oplus set2
     * @param result
     * @param set1
     * @param set2
     */
    public static void oplus(HashMap<TreeMap<String,Integer>,Double> result,
                             HashMap<TreeMap<String,Integer>,Double> set1,
                             HashMap<TreeMap<String,Integer>,Double> set2){
        for(TreeMap<String,Integer> l1: set1.keySet()){
            double t1=set1.get(l1);
            for(TreeMap<String,Integer> l2: set2.keySet()){
                double t2=set2.get(l2);
                double t_new=t1*t2;
                TreeMap<String,Integer> l_new=MergingTwoTreeMap.merge(l1,l2);
                result.put(l_new,t_new);
            }
        }
    }

    public static void init_listPhraseSize(int[] _listPhraseSize){
        listPhraseSize=_listPhraseSize;
    }


    /*
    public static double getAd(int[] _listPhraseSize,int K){
     */
    public static void getAd(int[] _listPhraseSize,int K){
        init_listPhraseSize(_listPhraseSize);
        init_f(listPhraseSize);
//        System.out.println(output_f_());
        init_F();
//        System.out.println(output_F_());
        update_F();
//        System.out.println(output_F_());

        double Ad= AdDPResult3.getFinalResult(K,listPhraseSize,F_);
        System.out.println("log(Ad) by Dynamic Programming="+Ad);
        System.out.println("Ad by Dynamic Programming="+Math.exp(Ad));
//        return Ad;
    }

    public static void main(String[] args){
        long start = System.currentTimeMillis();
        getAd(new int[]{2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,2,2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},100);
//        getAd(new int[]{2,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3,3},100);
//        getAd(new int[]{3,3,3,3,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2},100);
        long end = System.currentTimeMillis();
        long elapsedMillis = end - start;
        System.out.println("cost"+elapsedMillis + "ms\t");

//        int[] _listPhraseSize=new int[]{2,3,3};
//        init_listPhraseSize(_listPhraseSize);
//        init_f(listPhraseSize);
////        System.out.println(output_f_());
//        init_F();
////        System.out.println(output_F_());
//        update_F();
////        System.out.println(output_F_());
//
//        int K=2;
//        double groundTruth=AdGroundTruth.getGroundTruth(K,listPhraseSize);
//        System.out.println("Ground Truth="+groundTruth);
//        double Ad= AdDPResult.getFinalResult(K,listPhraseSize,F_);
//        System.out.println("Ad by Dynamic Programming="+Ad);
    }
}
