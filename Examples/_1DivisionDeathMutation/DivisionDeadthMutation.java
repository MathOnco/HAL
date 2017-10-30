package Examples._1DivisionDeathMutation;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;
import Framework.Tools.FileIO;
import Framework.Utils;

import java.util.Arrays;
import java.util.Random;

//cells grow and mutate
class CellEx extends AgentSQ2Dunstackable<DivisionDeadthMutation>{
    int nMutations;

    void Mutate(){
        if(nMutations<G().MAX_MUTATIONS &&G().rn.nextDouble()<G().MUT_PROB){
            nMutations++;
            Draw();
        }
    }

    void Draw(){
        G().vis.SetPix(Isq(),Utils.CategorialColor(nMutations));//sets a single pixel
    }

    void Divide(){
        int nOpts=HoodToEmptyIs(G().hood,G().hoodIs);//finds von neumann neighborhood indices around cell.
        if(nOpts>0){
            int iDaughter=G().hoodIs[G().rn.nextInt(nOpts)];
            CellEx daughter=G().NewAgentSQ(iDaughter);//generate a daughter, the other is technically the original cell
            daughter.nMutations=nMutations;//start both daughters with same number of mutations
            daughter.Draw();
            Mutate();//during division, there is a possibility of mutation of one or both daughters
            daughter.Mutate();
        }
    }
}

public class DivisionDeadthMutation extends AgentGrid2D<CellEx> {
    final static int BLACK=Utils.RGB(0,0,0);
    double DIV_PROB =0.2;
    double MUT_PROB =0.01;
    double DIE_PROB =0.02;
    double MUT_ADVANTAGE =1.08;
    int MAX_MUTATIONS =19;
    int[]mutCounts=new int[MAX_MUTATIONS+1];//+1 to count for un-mutated type
    int[]hood=new int[]{1,0,-1,0,0,1,0,-1}; //equivalent to int[]hood=Utils.VonNeumannHood(false);
    int[]hoodIs=new int[hood.length/2];//stores mapped von neumann indices around cells during divide function
    Random rn=new Random();
    GuiGridVis vis;
    FileIO outputFile=null;
    public DivisionDeadthMutation(int x, int y, GuiGridVis vis) {
        super(x, y, CellEx.class);
        this.vis=vis;
    }
    public DivisionDeadthMutation(int x, int y, GuiGridVis vis,String outputFileName) {
        super(x, y, CellEx.class);
        this.vis=vis;
        outputFile=new FileIO(outputFileName,"w");
    }
    public void InitTumor(double radius){
        //places tumor cells in a circle
        int[]circleHood=Utils.CircleHood(true,radius);//generate circle neighborhood [x1,y1,x2,y2,...]
        int[]indices=new int[circleHood.length/2];//generate array that will hold mapped indices [i1,i2,...]
        int nStartPos=HoodToEmptyIs(circleHood,indices,xDim/2,yDim/2);//map indices to neighborhood centered around the middle
        for (int i = 0; i <nStartPos ; i++) {
            CellEx c=NewAgentSQ(indices[i]);
            c.nMutations=0;
            c.Draw();
        }
    }
    public void StepCells(){
        Arrays.fill(mutCounts,0);//clear the mutation counts
        for (CellEx c : this) {//iterate over all cells in the grid
            mutCounts[c.nMutations]++;//count up all cell types for this timestep
            if(rn.nextDouble()< DIE_PROB){
                vis.SetPix(c.Isq(),BLACK);
                c.Dispose();//removes cell from sptial grid and iteration
            }
            else if(rn.nextDouble()< DIV_PROB*Math.pow(MUT_ADVANTAGE,c.nMutations)){//application of mutational advantage
                c.Divide();
            }
        }
        if(outputFile!=null){
            outputFile.Write(Utils.ArrToString(mutCounts,",")+"\n");//write populations every timestep
        }
        ShuffleAgents(rn);//shuffles order of for loop iteration
        IncTick();//increments timestep, including newly generated cells in the next round of iteration
    }

    public static void main(String[]args){
        int x=500,y=500,scaleFactor=2;
        GridVisWindow vis=new GridVisWindow(x,y,scaleFactor);//used for visualization
        DivisionDeadthMutation grid=new DivisionDeadthMutation(x,y,vis);
        grid.InitTumor(5);
        while(grid.GetTick()<10000){
            vis.TickPause(0);//set to nonzero value to cap tick rate.
            grid.StepCells();
        }
    }
}