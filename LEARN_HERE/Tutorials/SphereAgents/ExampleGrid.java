package LEARN_HERE.Tutorials.SphereAgents;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.SphericalAgent2D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;
import HAL.Util;

class ExampleCell extends SphericalAgent2D<ExampleCell,ExampleGrid> {
    int color;

    public void Init(){
        super.DefaultInit(0.2);
        this.color=Util.RGB(G.rng.Double(), G.rng.Double(), G.rng.Double());
    }

    public void StepCell(){
        if(G.rng.Double()<0.01){
            //cell will die
            Dispose();
            return;
        }
        double force=SumForces(0.4,(double overlap,ExampleCell other)->{
            if(overlap>0){
                return overlap*0.4;
            }
            return 0;
        });
        ApplyFriction(0.5);
        ForceMove();
        if(force<0.01){
            Divide(0.2, G.divCoords, G.rng).Init();
        }
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng=new Rand();
    int[]divHood=Util.VonNeumannHood(false);
    double[] divCoords =new double[2];

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
    }
    public void StepCells(){
        for(ExampleCell cell:this){
            cell.StepCell();
        }
    }
    public void DrawModel(OpenGL2DWindow win){
        win.Clear(Util.BLACK);
        for (ExampleCell cell : this) {
            win.Circle(cell.Xpt(),cell.Ypt(),0.2,cell.color);
        }
        win.Update();
    }

    public static void main(String[]args){
        int x=10;
        int y=10;
        int timesteps=1000;
        double dieProb=0.1;
        double divProb=0.2;

        OpenGL2DWindow win=new OpenGL2DWindow("2D",500,500,x,y);
        ExampleGrid model=new ExampleGrid(x,y);

        //initialize model

        for (int i = 0; i < timesteps; i++) {
            //win.TickPause(100);
            if(model.Pop()==0){
                model.NewAgentPT(model.xDim/2.0,model.yDim/2.0).Init();
            }
            System.out.println(model.Pop());
            //model step
            model.StepCells();

            //draw
            model.DrawModel(win);
        }
    }
}

