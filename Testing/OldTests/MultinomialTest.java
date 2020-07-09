package Testing.OldTests;

import HAL.Rand;
import HAL.Tools.MultinomialCalc;
import HAL.Util;

import java.util.Arrays;

public class MultinomialTest{
    public static void main(String[] args){
        Rand rng=new Rand();
//        MultinomialCalc mn=new MultinomialCalc(rng);
        int[] out=new int[3];
        double[] probs=new double[]{0.12454966713960078,0.10666159287850284,0.7687887399818963};
        for(int i=0;i<10;i++){
            rng.Multinomial(probs,1,out);
//            mn.Setup(1);
//            for(int j=0;j<3;j++){
//                out[j]=mn.Sample(probs[j]);
//            }
            System.out.println(Util.ArrToString(out,","));
            Arrays.fill(out,0);
        }
    }
}
