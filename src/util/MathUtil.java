package util;

public class MathUtil {
    /**
     * logSumExp=log(a_1+a_2+\cdots+a_n)=log( \sum_{i=1}^{n} exp(log(a_i)))
     * =b+log(\sum_{i=1}^{n} exp(a_i-b)) where b=max_i(a_i)
     */
    public static double logSumExp(double[] array) {
        double result = 0.0;
        int M = array.length;
        // b is the largest number in vector
        double b = Double.NEGATIVE_INFINITY;
        for (int i = 0; i < M; i++) {
            if (b < array[i]) {
                b = array[i];
            }
        }

        if (b == Double.NEGATIVE_INFINITY) {
            result = Double.NEGATIVE_INFINITY;
        } else {
            double exponentialSum = 0.0;
            for (int i = 0; i < M; i++) {
                exponentialSum += Math.exp(array[i] - b);
            }
            result = b + Math.log(exponentialSum);
        }
        return result;
    }



}
