package LEARN_HERE.Other;

import HAL.Rand;
import HAL.Util;

public class NonSpatialABM {
    long[]pops;
    Rand rng=new Rand();
    long totalPop;
    public NonSpatialABM(long[]pops){
        this.pops=pops;
        totalPop= Util.ArraySum(pops);
    }

    double DivProb(int iPop){return 0.5*Math.pow(0.9,iPop)*(1-totalPop*1.0/10000);}
    double DieProb(int iPop){return 0.01;}
    double MutProb(int iPop){return 0.1;}
    void Step(){
        for (int i = pops.length-1; i >= 0; i--) {
            //die
            pops[i]-=rng.Binomial(pops[i],DieProb(i));
            //divide
            long nDivs=rng.Binomial(pops[i], DivProb(i));
            pops[i]+=nDivs;
            //mutate
            if(i!=pops.length-1) {
                long nMuts = rng.Binomial(nDivs, MutProb(i));
                pops[i]-=nMuts;
                pops[i+1]+=nMuts;
            }
        }
        totalPop= Util.ArraySum(pops);
    }

    public static void main(String[] args) {
        long[]startPops=new long[10];
        startPops[0]=100;
        NonSpatialABM model=new NonSpatialABM(startPops);
        for (int i = 0; i < 100000; i++) {
            model.Step();
            System.out.println(Util.ArrToString(model.pops,",")+","+model.totalPop);
        }
    }
}
