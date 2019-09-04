package LEARN_HERE.Tutorials.Example;

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

    public void StepCell(double dieProb,double divProb){
        if(G.rng.Double()<dieProb){
            //cell will die
            Dispose();
            return;
        }
        double forceSum=SumForces(radius*2,(double overlap,ExampleCell other)->{
            if(overlap>0){
                return overlap*0.4;
            }
            return 0;
        });
        ApplyFriction(0.5);
        ForceMove();


        if(G.rng.Double()<divProb&&forceSum<0.01){
            //cell will divide
            Divide(radius, G.moveCoords, G.rng);
        }
    }
}

public class ExampleGrid extends AgentGrid2D<ExampleCell> {
    Rand rng = new Rand();
    int[] divHood = Util.VonNeumannHood(false);
    double[] moveCoords = new double[2];

    public ExampleGrid(int x, int y) {
        super(x, y, ExampleCell.class);
    }

    public void Step(double dieProb, double divProb) {
        for (ExampleCell cell : this) {
            cell.StepCell(dieProb, divProb);
        }
    }

    public void DrawModel(OpenGL2DWindow win) {
        win.Clear(Util.BLACK);
        for (ExampleCell cell : this) {
            win.Circle(cell.Xpt(), cell.Ypt(), cell.radius, cell.color);
        }
        win.Update();
    }

    public static void main(String[] args) {
        int x = 100;
        int y = 100;
        int timesteps = 1000;
        double dieProb = 0.1;
        double divProb = 1;

        OpenGL2DWindow win = new OpenGL2DWindow("2D", 500, 500, x, y);
        ExampleGrid model = new ExampleGrid(x, y);

        //initialize model

        for (int i = 0; i < timesteps; i++) {
            if(win.IsClosed()){
                break;
            }
            if (model.Pop() == 0) {
                model.NewAgentPT(model.xDim / 2.0, model.yDim / 2.0).Init();
            }
            //model step
            model.Step(dieProb, divProb);

            //draw
            model.DrawModel(win);
        }
        win.Close();
    }
}

