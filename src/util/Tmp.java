package util;

import cc.mallet.types.Dirichlet;

public class Tmp {
    public static double logBetaFunctionOnArray(double[] array){
        double result=0;
        for(int i=0;i<array.length;i++){
            result+=Dirichlet.logGammaStirling(array[i]);
        }
        return result;
    }

    public static void main(String[] args){
        double[] array=new double[]{0.1,0.1,0.1,0.1,0.1};
        System.out.println(Tmp.logBetaFunctionOnArray(array));
    }
}
