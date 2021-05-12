package Testing.OldTests;

import HAL.Rand;
import HAL.Util;

public class MutlinomialTest {
    public static void main(String[] args) {
        Rand rng=new Rand();
        int[]ret=new int[3];
        rng.Multinomial(new double[]{0.0,0.5,0.5},1,ret);
        System.out.println( rng.Binomial(1,0.5));
        System.out.println(Util.ArrToString(ret,","));
    }
}
