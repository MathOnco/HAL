package Examples._1DivisionDeathMutation;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

import java.util.Random;

//cells grow and mutate
class CellEx extends AgentSQ2Dunstackable<DivisionDeadthMutation>{
    int nMutations;

    void Mutate(){
        if(nMutations<G().maxMutations&&G().rn.nextDouble()<G().MUT_PROB){
            nMutations++;
            Draw();
        }
    }

    void Draw(){
        G().vis.SetPix(Isq(),Utils.CategorialColor(nMutations));
    }

    void Divide(){
        int nOpts=HoodToEmptyIs(G().hood,G().hoodIs);
        if(nOpts>0){
            int iDaughter=G().hoodIs[G().rn.nextInt(nOpts)];
            CellEx daughter=G().NewAgentSQ(iDaughter);
            daughter.nMutations=nMutations;
            daughter.Draw();
            Mutate();
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
    int maxMutations=19;
    int[]hood= Utils.VonNeumannHood(false);
    int[]hoodIs=new int[hood.length/2];
    Random rn=new Random();
    GuiGridVis vis;
    public DivisionDeadthMutation(int x, int y, GuiGridVis vis) {
        super(x, y, CellEx.class);
        this.vis=vis;
    }
    public void InitTumor(double radius){
        //places tumor cells in a circle
        int[]circleHood=Utils.CircleHood(true,radius);
        int[]indices=new int[circleHood.length/2];
        int nStartPos=HoodToEmptyIs(circleHood,indices,xDim/2,yDim/2);
        for (int i = 0; i <nStartPos ; i++) {
            CellEx c=NewAgentSQ(indices[i]);
            c.nMutations=0;
            c.Draw();
        }
    }
    public void StepCells(){
        for (CellEx c : this) {
            if(rn.nextDouble()< DIE_PROB){
                vis.SetPix(c.Isq(),BLACK);
                c.Dispose();
            }
            else if(rn.nextDouble()< DIV_PROB*Math.pow(MUT_ADVANTAGE,c.nMutations)){
                c.Divide();
            }
        }
        ShuffleAgents(rn);
        IncTick();
    }

    public static void main(String[]args){
        int x=500,y=500;
        GridVisWindow vis=new GridVisWindow(x,y,2);
        DivisionDeadthMutation grid=new DivisionDeadthMutation(x,y,vis);
        grid.InitTumor(5);
        for (int i = 0; i < 1000000; i++) {
            vis.TickPause(0);//used to pause the
            grid.StepCells();
        }
    }
}