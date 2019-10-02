package LEARN_HERE.Tutorials.OnLatticeExample;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentPT2D;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Util;

class ExampleCell extends AgentPT2D<ExampleGrid> {
    int color;

    public void Init(){
        this.color=Util.RGB(G.rng.Double(), G.rng.Double(), G.rng.Double());
    }

    public void StepCell(double dieProb,double divProb){
        if(G.rng.Double()<dieProb){
            //cell will die
            Dispose();
            return;
        }
        if(G.rng.Double()<divProb){
            //cell will divide
            int options=MapEmptyHood(G.divHood);
            if(options>0){
                G.NewAgentSQ(G.divHood[G.rng.Int(options)]).Init();
            }
        }
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng=new Rand();
    int[]divHood=Util.VonNeumannHood(false);

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
    }
    public void StepCells(double dieProb,double divProb){
        for(ExampleCell cell:this){
            cell.StepCell(dieProb,divProb);
        }
    }
    public void DrawModel(GridWindow win){
        for (int i = 0; i < length; i++) {
            int color=Util.BLACK;
            ExampleCell cell=GetAgent(i);
            if(cell!=null){
                color=cell.color;
            }
            win.SetPix(i,color);
        }
    }

    public static void main(String[]args){
        int x=100;
        int y=100;
        int timesteps=1000;
        double dieProb=0.1;
        double divProb=0.2;

        GridWindow win=new GridWindow(x,y,5);
        ExampleGrid model=new ExampleGrid(x,y);

        //initialize model

        for (int i = 0; i < timesteps; i++) {
            win.TickPause(20);
            if(model.Pop()==0){
                model.NewAgentSQ(model.xDim/2,model.yDim/2).Init();
            }
            //model step
            model.StepCells(dieProb,divProb);

            //draw
            model.DrawModel(win);
        }
        //can you make divProb a cell property that can mutate?
    }
}
