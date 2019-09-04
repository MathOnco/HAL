package Testing.OldTests;

import HAL.Rand;
import HAL.Util;

/**
 * Created by rafael on 7/23/17.
 */
public class BitGenomeTest {
    public static void main(String[] args) {
        int[] geneSizes=new int[]{10000,10000,10000,10000,10000,10000,10000,10000,10000,10000};
        double[] probs=new double[]{0.9,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.01,0.02};
        int[]geneLocs=new int[geneSizes.length+1];
        int[] geneHits=new int[10];
        int[] baseHits=new int[10];
        Rand rn=new Rand();

        System.out.println(Util.ArrToString(geneHits,","));
        System.out.println(Util.ArrToString(baseHits,","));
    }
}
