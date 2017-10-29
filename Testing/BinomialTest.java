package Testing;

import Framework.Tools.Binomial;
import Framework.Utils;

import java.util.Random;

/**
 * Created by Rafael on 7/20/2017.
 */
public class BinomialTest {
    public static void main(String[] args) {
        Binomial bn=new Binomial();
        Random rn=new Random();
        long[] res=new long[100];
        for (int i = 0; i < 100; i++) {
            res[i]=bn.SampleLong(Long.MAX_VALUE,0.7,rn);
        }
        System.out.println(Utils.ArrToString(res,","));
    }
}
