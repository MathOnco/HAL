package Framework.Tools;

import Framework.Utils;

import java.util.BitSet;
import java.util.Random;

/**
 * Created by rafael on 7/23/17.
 */
public class BitGenome extends BitSet{
    //used to introduce variable sizes to genes or parts of the BitGenome
    private final double[]geneProbs;//probabilities of hitting different genes
    private final int[]geneSizes;//sizes of regions with variable probability of being hit
    private final int[] geneLocations;//
    private Random rn;
    public BitGenome(int nBases,double[]probs,int[]sizes,int[]geneLocations,Random rn){
        super(nBases);
        this.geneProbs=probs;
        this.geneSizes=sizes;
        this.rn=rn;
        if(geneProbs.length!=geneSizes.length){
            throw new IllegalArgumentException("probs and sizes arrays must be same size!");
        }
        this.geneLocations=geneLocations;
    }
    public static void SetGeneLocations(int[] geneSizes,int[]geneLocations){
        int sum=0;
        for (int i = 0; i < geneSizes.length; i++) {
            sum+=geneSizes[i];
            geneLocations[i+1]=sum;
        }
    }
    public int ApplyMutations(int nMuts,int[] geneHits,int[]baseHits) {
        Utils.RandomVariableSample(geneProbs, geneHits, nMuts, rn);
        int alreadyHit=0;
        for (int i = 0; i < nMuts; i++) {
            int iMin = geneLocations[geneHits[i]];
            int iMax = geneLocations[geneHits[i] + 1];
            int mutLoc=rn.nextInt(iMax - iMin) + iMin;
            baseHits[i]=mutLoc;
            if(get(mutLoc)){
                alreadyHit++;
            }
            set(mutLoc,true);
        }
        return alreadyHit;
    }
}
