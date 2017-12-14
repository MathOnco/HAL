package Framework.Tools;

import java.util.Random;

/**
 * Created by rafael on 9/8/17.
 */
public class RandomNumberList {
    Random rn;
    private double[]rands;
    int iRand;
    int sizeExponent;
    public RandomNumberList(int initialCapacity,int sizeExponent,Random rn){
        this.rn=rn;
        if(initialCapacity<=0){
            throw new IllegalArgumentException("random number list should be seeded with positive CAPACITY_POP");
        }
        GenRands(initialCapacity);
    }
    private void GenRands(int capacity){
        iRand=0;
        rands=new double[capacity];
        for (int i = 0; i < rands.length; i++) {
            rands[i]=rn.nextDouble();
        }
    }
    public void ResetCounter(){
        iRand=0;
    }
    public double rn(){
        if(iRand<rands.length){
            iRand++;
            return rands[iRand-1];
        }
        GenRands(rands.length*sizeExponent);
        return rn();
    }
}
