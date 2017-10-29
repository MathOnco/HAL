package Testing;

import Framework.Tools.BitGenome;
import Framework.Utils;

import java.util.Random;

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
        Random rn=new Random();
        BitGenome.SetGeneLocations(geneSizes,geneLocs);

        BitGenome test=new BitGenome(100000,probs,geneSizes,geneLocs,rn);
        System.out.println(test.ApplyMutations(10,geneHits,baseHits));
        System.out.println(Utils.ArrToString(geneHits,","));
        System.out.println(Utils.ArrToString(baseHits,","));
    }
}
