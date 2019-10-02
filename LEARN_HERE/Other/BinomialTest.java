package LEARN_HERE.Other;

import HAL.Rand;

/**
 * Created by Rafael on 7/20/2017.
 */
public class BinomialTest {
    public static void main(String[] args) {
        Rand rn=new Rand();
        System.out.println(rn.Binomial(Long.MAX_VALUE,0.1)*1.0/ Long.MAX_VALUE);
//        long[] res=new long[1000000];
//        for (int i = 0; i < 1000000; i++) {
//            res[i]=rn.Binomial(Integer.MAX_VALUE,0.001);
//        }
//        //System.out.println(Util.ArrToString(res,","));
//        System.out.println(Util.SumArray(res)/(Long.MAX_VALUE*((double)res.length)));
    }
}
