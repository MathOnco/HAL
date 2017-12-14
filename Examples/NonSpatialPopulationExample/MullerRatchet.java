package Examples.NonSpatialPopulationExample;

import Framework.Tools.FileIO;
import Framework.Tools.NonSpatialPopModel;
import Framework.Rand;

import static Framework.Util.ArraySum;
import static Framework.Util.HeatMapRGB;

/**
 * Created by Rafael on 10/16/2017.
 */
public class MullerRatchet extends NonSpatialPopModel {
    //carrying CAPACITY_POP (K)=10E7
    //init pop (T_init) =10E7*0.05
    //timestep (dt)=1.0/24 (in days)
    //birth rate (a)=log(2)/dt
    //ProbBirth=a(1-b/a)^m TODO check out why b/a is used
    //ProbDeath=T/K
    //ProbMutWithDrug=0.005
    //ProbMutWithoutDrug=0.5
    //Mut effect (b) (whatever maximizes the ratchet)
    //expt. duration: ?
    //N_Muts ? basically make inf.
    public final long CAPACITY_POP;
    public final double MUT_PROB;
    public final double BIRTH_PROB_BASE;
    public final double BIRTH_PROB_DEC;
    public final boolean IS_REPLATING;
    public final boolean IS_CAPACITY;
    public final FileIO out;
    public final int REPLATE_POP_SIZE;
    public final double REPLATE_POP;
    private final Rand rn;

    public MullerRatchet(long[] initPops, double mutProb, Rand rn, double birthProbBase, double birthProbDec, boolean isCapacity, long capacity,boolean isReplating, int replatePopSize, double replateProp, FileIO out) {
        super(initPops, rn);
        this.CAPACITY_POP = capacity;
        this.IS_CAPACITY=isCapacity;
        this.IS_REPLATING=isReplating;
        this.REPLATE_POP_SIZE =replatePopSize;
        this.REPLATE_POP =replateProp;
        this.MUT_PROB =mutProb;
        this.BIRTH_PROB_BASE =birthProbBase;
        this.BIRTH_PROB_DEC =birthProbDec;
        for (int i = 0; i < initPops.length-1; i++) {
            AddTransition(i,i+1,1);
        }
        Initialize();

        this.out = out;
        this.rn=rn;
    }

    @Override
    public double GetBirthProb(int iPop, long[] currPops, long totalPop) {
        //return BIRTH_PROB_BASE-iPop*BIRTH_PROB_DEC;
        return BIRTH_PROB_BASE *Math.pow(1- BIRTH_PROB_DEC / BIRTH_PROB_BASE,iPop);
    }

    @Override
    public double GetDeathProb(int iPop, long[] currPops, long totalPop) {
        if(!IS_CAPACITY){
            return 0;
        }
        return totalPop*1.0/ CAPACITY_POP;
    }

    @Override
    public double GetMutProb(int iPop, long[] currPops, long totalPop) {
        if(iPop<currPops.length-1) {
            return MUT_PROB;
        }
        return 0;
    }
    @Override
    public long Step() {
        long totalPop = super.Step();
        if (IS_REPLATING && totalPop > REPLATE_POP_SIZE) {
            long[] pops = GetPops();
            for (int i = 0; i < pops.length; i++) {
                pops[i] = rn.Binomial(pops[i], REPLATE_POP);
            }
            return ArraySum(pops);
        }
        return totalPop;
    }
    //public int[] GenColors(){
    //    int[]ret=this.GetPops().length;
    //    for (int i = 0; i < this.GetPops().length; i++) {
    //
    //    }
    //}

    //public static void main(String[] args) {
    //    int[]colors=new int[11];
    //    colors[0]=RGB(0,0,1);
    //    for (int i = 1; i < colors.length; i++) {
    //        colors[i]=HeatMapRGB(i,-2,10);
    //    }

    //    MullerRatchet ex=new MullerRatchet(new long[]{500000,0,0,0,0,0,0,0,0,0}, 10000000,0.1,new Random(),0.5,0.011);
    //    GridWindow win=new GridWindow("testWin",500,300,2);
    //    for (int i = 0; i < 100000; i++) {
    //        ex.Step();
    //        if(i%100000/500==0) {
    //            win.TickPause(10);
    //            ex.DrawPops(win, colors, 10000000);
    //        }
    //    }
    //}
}
