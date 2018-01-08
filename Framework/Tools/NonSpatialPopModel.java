package Framework.Tools;


import Framework.Gui.GuiGrid;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Arrays;

import static Framework.Util.Bound;
import static Framework.Util.RGB;

/**
 * Created by Rafael on 10/10/2017.
 */
public abstract class NonSpatialPopModel {
    final static int TO_INDEX=0,WEIGHT=1,PROBABILITY=2,BLACK=RGB(0,0,0),BLUE=RGB(0,0,1);
    private final long[][]pops;
    private final double[][]moranTransitionsFinal;
    private final int[][]moranToIndicesFinal;
    private final long[][]moranTransitionPopsFinal;
    private final ArrayList<ArrayList<double[]>> moranTransitions;
    private int step;
    public final Rand rn;
    private boolean initialized=false;
    private int drawStep;
    private long totalPop;

    public NonSpatialPopModel(long[]initPops,Rand rn){
        this.pops=new long[2][];
        this.pops[0]=initPops;
        for (int i = 0; i < initPops.length; i++) {
            if(initPops[i]<0){
                throw new IllegalArgumentException("initial populations must all have size >=0! popSize: "+initPops[i]+" index: "+i);
            }
        }
        this.pops[1]=new long[initPops.length];
        this.rn=rn;
        moranTransitions = new ArrayList<>();
        for (int i = 0; i < pops[0].length; i++) {
            moranTransitions.add(new ArrayList<>());
        }
        moranTransitionsFinal = new double[pops[0].length][];
        moranToIndicesFinal = new int[pops[0].length][];
        moranTransitionPopsFinal=new long[pops[0].length][];
    }
    public int GetStep(){
        return step;
    }
    public long[]GetPops(){
        return pops[step%2];
    }
    public long[] GetPrevPops(){
        return pops[1-step%2];
    }
    public abstract double GetBirthProb(int iPop,long[]currPops,long totalPop);
    public abstract double GetDeathProb(int iPop,long[]currPops,long totalPop);
    public abstract double GetMutProb(int iPop,long[]currPops,long totalPop);
    public void AddTransition(int fromIndex,int toIndex,double weight){
        if(initialized){
            throw new IllegalStateException("can't add transition after initialization");
        }
        if(weight<=0){
            throw new IllegalArgumentException("transition must have nonzero weight! Weight: "+weight);
        }
        if(fromIndex==toIndex){
            throw new IllegalArgumentException("cannot transition to and from the same place!");
        }
        //add transition to set of transitions for that phenotype
        ArrayList<double[]>transitions=moranTransitions.get(fromIndex);
        double weightSum=weight;
        for (double[] transition:transitions) {
            weightSum+=transition[WEIGHT];
            if((int)transition[TO_INDEX]==toIndex){
                throw new IllegalArgumentException("transition probability already exists! From: "+fromIndex+" To: "+toIndex);
            }
        }
        transitions.add(new double[]{toIndex,weight,0});
        for (double[] transition:transitions) {
            transition[PROBABILITY]=transition[WEIGHT]/weightSum;
        }
    }
    public void Initialize(){
        initialized=true;
        //copies transitions into arrays for quicker access
        for (int i = 0; i < pops[0].length; i++) {
            int nTransitions=moranTransitions.get(i).size();
            moranTransitionPopsFinal[i]= new long[nTransitions];
            moranTransitionsFinal[i]= new double[nTransitions];
            moranToIndicesFinal[i]= new int[nTransitions];
            for (int j = 0; j < nTransitions; j++) {
                moranTransitionsFinal[i][j]=moranTransitions.get(i).get(j)[PROBABILITY];
                moranToIndicesFinal[i][j]=(int)moranTransitions.get(i).get(j)[TO_INDEX];
            }
        }
    }
    public void DrawPops(GuiGrid vis, int[]colors, long maxPop){
        long[]currPops=GetPops();
        if(colors.length<currPops.length+1){
            throw new IllegalArgumentException("need a color for each pop color, number of colors: "+colors.length+" number of pops: "+pops.length);
        }
        int iCol=drawStep%vis.xDim;
        for (int i = 0; i < vis.yDim; i++) {
            //clear the column
            vis.SetPix(iCol,i,BLACK);
        }
        if(totalPop<maxPop) {
            vis.SetPix(iCol, (int) ((totalPop * 1.0 * vis.yDim) / maxPop), colors[0]);
        }
        for (int i = 0; i < currPops.length; i++) {
            if(currPops[i]<maxPop){
                vis.SetPix(iCol,(int)((currPops[i]*1.0*vis.yDim)/maxPop),colors[i+1]);
            }
        }
        drawStep++;
    }
    public void DrawPops(GuiGrid vis, long maxPop){
        long[]currPops=GetPops();
        int iCol=GetStep()%vis.xDim;
        for (int i = 0; i < vis.yDim; i++) {
            //clear the column
            vis.SetPix(iCol,i,BLACK);
        }
        vis.SetPix(iCol,Bound((int)((totalPop*1.0*vis.yDim)/maxPop),0,vis.yDim-1),BLUE);
        for (int i = 0; i < currPops.length; i++) {
            if(currPops[i]<maxPop){
                vis.SetPix(iCol,Bound((int)((currPops[i]*1.0*vis.yDim)/maxPop),0,vis.yDim-1),
                        Util.HeatMapRGB((i+2.0)/(currPops.length+2.0)));
            }
        }
    }
    public long Step(){
        if(!initialized){
            Initialize();
        }
        long[]currPops=GetPops();
        long[]nextPops= GetPrevPops();

        Arrays.fill(nextPops,0);//clear the next timestep array

        totalPop= Util.ArraySum(currPops);
        if(totalPop==0){
            step++;
            return totalPop;
        }
        for (int i = 0; i < currPops.length; i++) {
            if (currPops[i] > 0) {
                double deathProb = GetDeathProb(i, currPops,totalPop);
                if (deathProb < 0||deathProb>1) {
                    throw new IllegalStateException("death prob must be >=0! and <=1! DEATH_PROB: " + deathProb);
                }
                double birthProb = GetBirthProb(i, currPops,totalPop);
                if (birthProb < 0||birthProb>1) {
                    throw new IllegalStateException("birth prob must be >=0! and <=1! birthProb: " + birthProb);
                }
                long nDeaths = rn.Binomial(currPops[i], deathProb);
                long nBirths = rn.Binomial(currPops[i], birthProb);
                long nMuts = 0;

                if (nBirths != 0) {
                    double mutProb = GetMutProb(i, currPops,totalPop);
                    if (mutProb < 0||mutProb>1) {
                        throw new IllegalStateException("mut prob must be >=0! and <=1! MUT_PROB: " + mutProb);
                    }
                    nMuts = rn.Binomial(nBirths, mutProb);
                    if (nMuts > 0) {
                        rn.Multinomial(moranTransitionsFinal[i], nMuts,  moranTransitionPopsFinal[i]);
                        for (int j = 0; j < moranToIndicesFinal[i].length; j++) {
                            nextPops[moranToIndicesFinal[i][j]] += moranTransitionPopsFinal[i][j];
                        }
                    }
                }
                nextPops[i] += (currPops[i] + nBirths) - (nDeaths + nMuts);//modify populations based on birth and death
            }
        }
        step++;
        return totalPop;
    }
}
