package Examples.Template_Models;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.SphericalAgent2D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;
import HAL.Util;

class TemplateOffLatticeCell extends SphericalAgent2D<TemplateOffLatticeCell,TemplateOffLattice2D> {
    int exampleProperty;

    void ExampleFunction(){
        this.exampleProperty=Util.RGB(G.rng.Double(),G.rng.Double(),G.rng.Double());
        this.radius=G.rng.Double();
        this.xVel+=G.rng.Double(2)-1;
        this.yVel+=G.rng.Double(2)-1;
        this.ForceMove();
        this.ApplyFriction(0.0);
    }
}

public class TemplateOffLattice2D extends AgentGrid2D<TemplateOffLatticeCell> {
    Rand rng=new Rand();

    public TemplateOffLattice2D(int x, int y) {
        super(x, y, TemplateOffLatticeCell.class);
    }

    public static void main(String[]args){
        TemplateOffLattice2D grid=new TemplateOffLattice2D(11,11);
        OpenGL2DWindow vis=new OpenGL2DWindow(500,500,11,11);

        grid.NewAgentPT(grid.xDim/2.0,grid.yDim/2.0);

        while(true){
            for (TemplateOffLatticeCell cell : grid) {
                cell.ExampleFunction();
            }
            vis.Clear(Util.BLACK);
            for (TemplateOffLatticeCell cell : grid) {
                vis.Circle(cell.Xpt(),cell.Ypt(),cell.radius,cell.exampleProperty);
            }
            vis.Update();
            vis.TickPause(1000);
        }
    }
}
