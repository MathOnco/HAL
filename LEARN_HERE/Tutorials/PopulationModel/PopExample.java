package LEARN_HERE.Tutorials.PopulationModel;

import HAL.Rand;

public abstract class PopExample {
    Rand rng=new Rand();
    public final long[]populations;
    private final long[]swap;
    final double[]eventProbs;
    final long[]results;
    abstract double BirthProb(int iPop);
    abstract double DeathProb(int iPop);
    abstract double MutationProb(int iPop1,int iPop2);

    public PopExample(long[]startPops){
        this.populations=new long[startPops.length];
        System.arraycopy(startPops,0,populations,0,startPops.length);
        this.swap=new long[startPops.length];
        System.arraycopy(startPops,0,swap,0,startPops.length);
        this.eventProbs=new double[populations.length+2];
        this.results=new long[populations.length+2];
    }

    public void Step(){
        for (int iPop1 = 0; iPop1 < populations.length; iPop1++) {
            //event calculation
            eventProbs[0]=BirthProb(iPop1);
            eventProbs[1]=DeathProb(iPop1);
            for (int iPop2 = 0; iPop2 < populations.length; iPop2++) {
                if(iPop1!=iPop2){
                    eventProbs[2+iPop2]=MutationProb(iPop1,iPop2);
                }
            }
            //birth and death
            rng.Multinomial(eventProbs,populations[iPop1],results);
            swap[iPop1]+=results[0];
            swap[iPop1]-=results[1];
            //mutation
            for (int iMutPop = 0; iMutPop < populations.length; iMutPop++) {
                if(iMutPop!=iPop1){
                    swap[iMutPop]+=results[2+iMutPop];
                    swap[iPop1]-=results[2+iMutPop];
                }
            }
        }

        //copy over for next step
        System.arraycopy(swap,0,populations,0,populations.length);
    }
}
