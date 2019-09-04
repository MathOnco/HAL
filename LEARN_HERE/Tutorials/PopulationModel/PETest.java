package LEARN_HERE.Tutorials.PopulationModel;

import HAL.Util;

public class PETest extends PopExample{
    public PETest(long[] startPops) {
        super(startPops);
    }

    @Override
    double BirthProb(int iPop) {
        return 0.2;
    }

    @Override
    double DeathProb(int iPop) {
        return 0.1;
    }

    @Override
    double MutationProb(int iPop1, int iPop2) {
        return 0.0001;
    }

    public static void main(String[] args) {
        PETest test1=new PETest(new long[]{100,0,0,0,0,0,0,0,0,0});
        for (int i = 0; i < 1000; i++) {
            test1.Step();
            System.out.println(Util.ArrToString(test1.populations,","));
        }
    }
}
