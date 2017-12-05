package util;

import java.util.ArrayList;

/**
 * how many plans in Ad
 */
public class AdPlanNumber {
    public static int[] listPhraseSize;
    public static int[] accumulatePhraseSize;//listPhraseSize=[2,2,2], then accumulatePhraseSize=[2,4,6]

    public static double[][] f_;

    public static double[][] F_;

    public static void init_listPhraseSize(int[] _listPhraseSize){
        listPhraseSize=_listPhraseSize;
    }

    public static void init_f(int[] listPhraseSize){
        f_=new double[listPhraseSize.length][];
        for(int i=0;i<listPhraseSize.length;i++){
            f_[i]=new double[listPhraseSize[i]+1];
        }
        for(int i=0;i<listPhraseSize.length;i++){
            for(int x=0;x<=listPhraseSize[i];x++){
                if(x==0){
                    f_[i][x]=1;//{[null]}
                }else{
                    f_[i][x]=1;//{[(i,x)]}
                }
            }
        }
    }

    public static double get_f_(int i, int x){
        if(x<=listPhraseSize[i]){
            return f_[i][x];
        }else {
            return 1;
        }
    }

    public static double get_F_(int i, int x){
        if(x<=accumulatePhraseSize[i]){
            return F_[i][x];
        }else {
            return 1;
        }
    }

    public static String output_f_(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<listPhraseSize.length;i++){
            for(int x=0;x<=listPhraseSize[i];x++){
                sb.append(get_f_(i,x));
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    /*
     * two initial condition:  (1) F_0(x)=f_0(x); (2) F_i(0)=1
     */
    public static void init_F(){
        initAccumulatePhraseSize();
        //Step 0, init F_[][] array
        F_=new double[listPhraseSize.length][];
        for(int i=0;i<listPhraseSize.length;i++){
            F_[i]=new double[accumulatePhraseSize[i]+1];
        }
        //Step 1, init by the initial condition F_0(x)=f_0(x)
        for(int x=0;x<=listPhraseSize[0];x++){
            F_[0][x]=f_[0][x];
        }
        //Step 2, init by the initial condition F_i(0)={[null]}
        for(int i=0;i<accumulatePhraseSize.length;i++){
            F_[i][0]=1;
        }
    }

    public static String output_F_(){
        StringBuffer sb=new StringBuffer();
        for(int i=0;i<accumulatePhraseSize.length;i++){
            for(int x=0;x<=accumulatePhraseSize[i];x++){
                sb.append(get_F_(i,x));
                sb.append("\t");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    public static void initAccumulatePhraseSize(){
        accumulatePhraseSize=new int[listPhraseSize.length];
        accumulatePhraseSize[0]= listPhraseSize[0];
        for(int i=1;i<listPhraseSize.length;i++){
            accumulatePhraseSize[i]= accumulatePhraseSize[i-1]+listPhraseSize[i];
        }
    }

    public static void update_F(){
        for(int i=1;i<accumulatePhraseSize.length;i++){
            for(int x=1;x<accumulatePhraseSize[i]+1;x++){
                //update F_[i][x]=\cup_{b=0}^{x} (f_[i][b] \oplus F_[i-1][x-b])
                System.out.println("updating F_["+i+"]["+x+"]. ");
                double result=0;
                for(int b=0;b<=x;b++){
                    if(b<=listPhraseSize[i] && (x-b)<=accumulatePhraseSize[i-1]){
                        //reuse oplus function
                        result=result+get_f_(i,b)*get_F_(i-1,x-b);
                    }
                }
                F_[i][x]=result;
                System.out.println("after updating, F_["+i+"]["+x+"]="+get_F_(i,x));
            }
        }
    }

    public static void main(String[] args){
        int[] _listPhraseSize=new int[]{2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2,2};
        init_listPhraseSize(_listPhraseSize);
        init_f(listPhraseSize);
        System.out.println(output_f_());
        init_F();
        System.out.println(output_F_());
        update_F();
        System.out.println(output_F_());
    }

}
