package Examples.PhylogenyTracker;

import Framework.Gui.UIGrid;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GridWindow;
import Framework.Tools.FileIO;
import Framework.Rand;
import Framework.Tools.PhylogenyTracker.Genome;
import Framework.Util;

import java.util.ArrayList;
import java.util.Arrays;

class CellGenome extends Genome<CellGenome>{
    int nMutations;
    int color;
    public CellGenome(CellGenome parent,Rand rn) {
        super(parent, false);
        if(parent!=null) {
            this.nMutations = parent.nMutations + 1;
            this.color=parent.color;
            this.MutateColor(rn,20);
        }else{
            this.nMutations=0;
            this.color=Util.RGB256(128,128,128);
        }
    }
    public int GenMutComp(int initalVal,Rand rn,int mutRate){
        return initalVal+rn.Int(mutRate*2+1)-mutRate;
    }
    public void MutateColor(Rand rn,int mutRate){
        int newR=GenMutComp(Util.GetRed256(color),rn,mutRate);
        int newG=GenMutComp(Util.GetGreen256(color),rn,mutRate);
        int newB=GenMutComp(Util.GetBlue256(color),rn,mutRate);
        color=Util.RGB256(newR,newG,newB);
    }
}

//cells grow and mutate
class CellEx extends AgentSQ2Dunstackable<PhylogenyModel>{
    CellGenome myGenome;

    void PossiblyMutate(Rand rn){
        if(myGenome.nMutations< G.MAX_MUTATIONS && G.rn.Double()< G.MUT_PROB){
            myGenome.DecPop();
            myGenome=new CellGenome(myGenome,rn);
            myGenome.IncPop();
            Draw();
        }
    }

    void Draw(){
        G.vis.SetPix(Isq(), myGenome.color);//sets a single pixel
    }

    void Divide(){
        int nOpts=MapEmptyHood(G.hood);//finds von neumann neighborhood indices around cell.
        if(nOpts>0){
            int iDaughter= G.hood[G.rn.Int(nOpts)];
            CellEx daughter= G.NewAgentSQ(iDaughter);//generate a daughter, the other is technically the original cell
            daughter.myGenome=myGenome;//start both daughters with same number of mutations
            myGenome.IncPop();
            daughter.Draw();
            PossiblyMutate(G.rn);//during division, there is a possibility of mutation of one or both daughters
            daughter.PossiblyMutate(G.rn);
        }
    }
}

public class PhylogenyModel extends AgentGrid2D<CellEx> {
    final static int BLACK= Util.RGB(0,0,0);
    double DIV_PROB =0.2;
    double MUT_PROB =0.003;
    double DIE_PROB =0.1;
    double MUT_ADVANTAGE =1.08;
    int MAX_MUTATIONS =19;
    int[]mutCounts=new int[MAX_MUTATIONS+1];//+1 to count for un-mutated type
    int[]hood=Util.GenHood2D(new int[]{1,0,-1,0,0,1,0,-1}); //equivalent to int[]hood=Util.VonNeumannHood(false);
    Rand rn=new Rand(1);
    UIGrid vis;
    FileIO outputFile;
    CellGenome seed=new CellGenome(null,rn);
    public PhylogenyModel(int x, int y, UIGrid vis) {
        super(x, y, CellEx.class);
        this.vis=vis;
    }
    public PhylogenyModel(int x, int y, UIGrid vis, String outputFileName) {
        super(x, y, CellEx.class);
        this.vis=vis;
        outputFile=new FileIO(outputFileName,"w");
    }
    public void InitTumor(double radius){
        //places tumor cells in a circle
        int[]circleHood= Util.CircleHood(true,radius);//generate circle neighborhood [x1,y1,x2,y2,...]
        int len=MapHood(circleHood,xDim/2,yDim/2);
        for (int i = 0; i < len; i++) {
            CellEx c=NewAgentSQ(circleHood[i]);
            c.myGenome=new CellGenome(seed,rn);
            c.myGenome.IncPop();
            c.Draw();
        }
//        int nStartPos=HoodToEmptyIs(circleHood,indices,xDim/2,yDim/2);//map indices to neighborhood centered around the middle
//        for (int i = 0; i <nStartPos ; i++) {
//            CellEx c=NewAgentSQ(indices[i]);
//            c.nMutations=0;
//            c.Draw();
//        }
    }
    public void StepCells(){
        Arrays.fill(mutCounts,0);//clear the mutation counts
        for (CellEx c : this) {//iterate over all cells in the grid
            if(rn.Double()< DIE_PROB){
                vis.SetPix(c.Isq(),BLACK);
                c.myGenome.DecPop();
                c.Dispose();//removes cell from sptial grid and iteration
            }
            else if(rn.Double()< DIV_PROB*Math.pow(MUT_ADVANTAGE,c.myGenome.nMutations)){//application of mutational advantage
                c.Divide();
            }
        }
        if(outputFile!=null){
            for (CellGenome cellGenome : seed) {
                mutCounts[cellGenome.nMutations]+=cellGenome.Pop();
            }
            outputFile.Write(Util.ArrToString(mutCounts,",")+"\n");//write populations every timestep
        }
        ShuffleAgents(rn);//shuffles order of for loop iteration
//        IncTick();//increments timestep, including newly generated cells in the next round of iteration
    }

    public static void main(String[]args){
        ArrayList<Double[]>out=new ArrayList<>();
        //int x=500,y=500,scaleFactor=2;
        int x=1000,y=1000,scaleFactor=1;
        GridWindow vis=new GridWindow(x,y,scaleFactor);//used for visualization
        PhylogenyModel grid=new PhylogenyModel(x,y,vis,"phylodata.csv");
        grid.InitTumor(5);
        for (int tick = 0; tick < 10000000; tick++) {
            vis.TickPause(0);//set to nonzero value to cap tick rate.
            grid.StepCells();
        }
    }
}