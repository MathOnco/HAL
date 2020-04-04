package Examples.Template_Models;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2D;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Util;

class TemplateOnLatticeCell extends AgentSQ2D<TemplateOnLattice2D> {
    int exampleProperty;

    void ExampleFunction(){
        this.exampleProperty=Util.RGB(G.rng.Double(),G.rng.Double(),G.rng.Double());
        this.MoveSQ(G.hood[G.rng.Int(MapHood(G.hood))]);
    }
}

class TemplateOnLattice2D extends AgentGrid2D<TemplateOnLatticeCell> {
    Rand rng=new Rand();
    int[]hood=Util.VonNeumannHood(false);

    public TemplateOnLattice2D(int x, int y) {
        super(x, y, TemplateOnLatticeCell.class);
    }

    public static void main(String[]args){
        TemplateOnLattice2D grid=new TemplateOnLattice2D(11,11);
        GridWindow vis=new GridWindow(11,11,50);

        grid.NewAgentSQ(grid.xDim/2,grid.yDim/2);

        while(true){
            for (TemplateOnLatticeCell cell : grid) {
                cell.ExampleFunction();
            }

            for (int i=0;i<grid.length;i++){
                TemplateOnLatticeCell cell=grid.GetAgent(i);
                if(cell!=null){
                    vis.SetPix(i,cell.exampleProperty);
                }else{
                    vis.SetPix(i,Util.BLACK);
                }
            }
            vis.TickPause(1000);
        }
    }
}
