package util;

import java.util.ArrayList;

/**
 * Created by huangwaleking on 9/11/17.
 * change the data structure of class Ad
 */
public class Ad2 {

    //f_[i][x]=f_i(x)
    public static ArrayList<String>[][] f_;

    public static ArrayList<String>[][] F_;

    /*
     * two initial condition:  (1) F_0(x)=f_0(x); (2) F_i(0)={[null]}
     */
    public static void init_F(){
        initAccumulatePhraseSize();
        //Step 0, init F_[][] array
        F_=(ArrayList<String>[][])(new ArrayList[listPhraseSize.length][]);
        for(int i=0;i<listPhraseSize.length;i++){
            F_[i]=(ArrayList<String>[])(new ArrayList[accumulatePhraseSize[i]+1]);
        }
        //Step 1, init by the initial condition F_0(x)=f_0(x)
        for(int x=0;x<=listPhraseSize[0];x++){
            F_[0][x]=f_[0][x];
        }
        //Step 2, init by the initial condition F_i(0)={[null]}
        for(int i=0;i<accumulatePhraseSize.length;i++){
            F_[i][0]=new ArrayList<String>();
            F_[i][0].add("");
//            F_[i][0].add();
        }
    }

    public static void init_f(int[] listPhraseSize){
        f_=(ArrayList<String>[][])(new ArrayList[listPhraseSize.length][]);
        for(int i=0;i<listPhraseSize.length;i++){
            f_[i]=(ArrayList<String>[])(new ArrayList[listPhraseSize[i]+1]);
        }
        for(int i=0;i<listPhraseSize.length;i++){
            for(int x=0;x<=listPhraseSize[i];x++){
                if(x==0){
                    f_[i][x]=new ArrayList<String>();
                    f_[i][x].add("");
                }else{
                    String l= (new Tuple(i,x)).toString();
                    f_[i][x]=new ArrayList<String>();
                    f_[i][x].add(l);
                }
            }
        }
    }

    public static ArrayList<String> get_F_(int i, int x){
        if(x<=accumulatePhraseSize[i]){
            return F_[i][x];
        }else {
            ArrayList<String> s = new ArrayList<String>();
            s.add("");
            return s;
        }
    }

    public static ArrayList<String> get_f_(int i, int x){
        if(x<=listPhraseSize[i]){
            return f_[i][x];
        }else {
            ArrayList<String> s = new ArrayList<String>();
            s.add("");
            return s;
        }
    }

    public static void update_F(){
        for(int i=1;i<accumulatePhraseSize.length;i++){
            for(int x=1;x<accumulatePhraseSize[i]+1;x++){
                //update F_[i][x]=\cup_{b=0}^{x} (f_[i][b] \oplus F_[i-1][x-b])
                System.out.println("updating F_["+i+"]["+x+"]. ");
                ArrayList<String> result=new ArrayList<String>();
                for(int b=0;b<=x;b++){
                    if(b<=listPhraseSize[i] && (x-b)<=accumulatePhraseSize[i-1]){
                        oplus(result,get_f_(i,b),get_F_(i-1,x-b));
                    }
                }
                F_[i][x]=result;
//                System.out.println("after updating, F_["+i+"]["+x+"]="+outputSet(get_F_(i,x)));
            }
        }
    }

    /**
     * outputSet call outputList, which call Tuple.toString()
     * @param set
     * @return
     */
    public static String outputSet(ArrayList<String> set){
        StringBuffer sb=new StringBuffer();
        if(set!=null){
            sb.append("{");
            for (String l : set) {
                sb.append(outputList(l)+",");
            }
            sb.append("}");
        }else{
            sb.append("null");
        }
        return sb.toString();
    }

    public static String outputList(String list){
        StringBuffer sb=new StringBuffer();
        sb.append("[");
        sb.append(list.toString());
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

    public static String combineArrayList(String u, String v){
        return u+","+v;
    }

    /**
     * set1 oplus set2= {u+v}, u \in set1, v \in set2
     * then update result=set1 oplus set2
     * @param result
     * @param set1
     * @param set2
     */
    public static void oplus(ArrayList<String> result, ArrayList<String> set1, ArrayList<String> set2){
        for(String u: set1){
            for(String v: set2){
                result.add(combineArrayList(u,v));
            }
        }
    }

    public static void init_listPhraseSize(int[] _listPhraseSize){
        listPhraseSize=_listPhraseSize;
    }


    public static void getAd(int[] _listPhraseSize,int K){
        init_listPhraseSize(_listPhraseSize);
        init_f(listPhraseSize);
        init_F();
        update_F();
//        double Ad= AdDPResult.getFinalResult(K,listPhraseSize,F_);
//        System.out.println("Ad by Dynamic Programming="+Ad);
//        return Ad;
    }

    public static void main(String[] args){
//        getAd(new int[]{2,3,3,3,3,3,3,3,3,3,3,3,3,3},100);

        int[] _listPhraseSize=new int[]{2,3,3,3,3,3,3,3,3,3,3,3};
        init_listPhraseSize(_listPhraseSize);
        init_f(listPhraseSize);
//        System.out.println(output_f_());
        init_F();
//        System.out.println(output_F_());
        update_F();
//        System.out.println(output_F_());
//
//        int K=2;
//        double groundTruth=AdGroundTruth.getGroundTruth(K,listPhraseSize);
//        System.out.println("Ground Truth="+groundTruth);
//        double Ad= AdDPResult.getFinalResult(K,listPhraseSize,F_);
//        System.out.println("Ad by Dynamic Programming="+Ad);
    }
}
