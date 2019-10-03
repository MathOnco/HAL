package Testing.PerformanceTestModels.PerformanceTestSpherical;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.GridsAndAgents.SphericalAgent2D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;

import static Examples._6CompetitiveRelease.ExampleModel.RESISTANT;
import static HAL.Util.*;

public class ExampleModel extends AgentGrid2D<ExampleCell> {
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public double TIMESTEP=2.0/24;//2 hours per timestep
    public double SPACE_STEP=20;//um
    public double DIV_PROB_SEN = ProbScale(0.5,TIMESTEP);
    public double DIV_PROB_RES = ProbScale(0.2,TIMESTEP);
    public double DEATH_PROB = ProbScale(0.02,TIMESTEP);
    public double DRUG_START = 20/TIMESTEP;
    public double DRUG_PERIOD = 15/TIMESTEP;
    public double DRUG_DURATION = 3/TIMESTEP;
    public double DRUG_DIFF_RATE = 0.02*60*60*24*(TIMESTEP/(SPACE_STEP*SPACE_STEP));//diffusion rate in um/seconds
    public double DRUG_UPTAKE = -0.03 *TIMESTEP;
    public double DRUG_DEATH = ProbScale(0.8,TIMESTEP);
    public double DRUG_BOUNDARY_VAL = 1.0;
    public long popTotal;

    double FORCE_EXPONENT=2;//these constants have been found to be rather stable, but tweak them and see what happens!
    //double FORCE_SCALER=0.7;
    double FORCE_SCALER=0.5;

    //public double DRUG_UPTAKE = 0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public double[]scratch=new double[2];

    public ExampleModel(int xDim, int yDim, Rand rn) {
        super(xDim/2, yDim/2, ExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    @FunctionalInterface
    interface ModelStep{
        void Run(ExampleModel m, int tick);
    }

    public static void RunModel(int sideLen,ModelStep Step,boolean draw){
        int x = sideLen, y = sideLen;
        ExampleModel m=new ExampleModel(x,y,new Rand(0));
        OpenGL2DWindow win =null;
        if(draw) {
            win = new OpenGL2DWindow(1000, 1000, m.xDim, m.yDim);
        }
        m.DRUG_START=0;
        m.DRUG_DURATION=m.DRUG_PERIOD;
        m.InitTumor();
        for (int tick = 0; tick < 10000; tick++) {
            Step.Run(m,tick);
            if(draw) {
                win.Clear(BLACK);
                for (ExampleCell cell : m) {
                    win.Circle(cell.Xpt(), cell.Ypt(), cell.radius/3, HeatMapGRB(m.drug.Get((int)(cell.Xpt()*2),(int)(cell.Ypt()*2))*0.8+0.2));
                }
                win.Update();
            }
        }
        System.out.println("sideLen:"+sideLen+" AvgPop:"+m.popTotal*1.0/10000);
        if(draw) {
            win.Close();
        }
    }

    public static void main(String[] args) {
        AwaitInput();
        RunModel(60, ExampleModel::ModelStep60,false);
        RunModel(90, ExampleModel::ModelStep90,false);
        RunModel(120, ExampleModel::ModelStep120,false);
        RunModel(150, ExampleModel::ModelStep150,false);
        RunModel(180, ExampleModel::ModelStep180,false);
    }

    public void InitTumor() {
        for (int i = 0; i < drug.length; i++) {
            ExampleCell c=NewAgentPT(rn.Double()*xDim,rn.Double()*yDim);
            c.type=RESISTANT;
            c.radius=0.25;
        }
    }

    public void DiffusionStep(int tick){
        if (tick > DRUG_START && (tick - DRUG_START) % DRUG_PERIOD < DRUG_DURATION) {
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
        drug.Update();
    }
    public void StepAllCells(int tick){
        for (ExampleCell cell : this) {
            cell.BirthDeath();
        }
        for (ExampleCell cell : this) {
            cell.Movement();
        }
    }


    public static void ModelStep60(ExampleModel m, int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
        m.popTotal+=m.Pop();
    }
    public static void ModelStep90(ExampleModel m, int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
        m.popTotal+=m.Pop();
    }
    public static void ModelStep120(ExampleModel m, int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
        m.popTotal+=m.Pop();
    }
    public static void ModelStep150(ExampleModel m, int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
        m.popTotal+=m.Pop();
    }
    public static void ModelStep180(ExampleModel m, int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
        m.popTotal+=m.Pop();
    }

}

class ExampleCell extends SphericalAgent2D<ExampleCell, ExampleModel> {
    public int type;

    public void BirthDeath() {
        //Consumption of Drug
        G.drug.Mul((int)(Xpt()*2),(int)(Ypt()*2), G.DRUG_UPTAKE);
        //Chance of Death, depends on resistance and drug concentration
        if (G.rn.Double() < G.DEATH_PROB + (type == RESISTANT ? 0 : G.drug.Get((int)(Xpt()*2),(int)(Ypt()*2)) * G.DRUG_DEATH)) {
            Dispose();
            return;
        }
        double pressure=SumForces(0.5,(overlap, other) -> overlap*G.FORCE_SCALER);
        //contact inhibition and division probability influence division event
        if (G.rn.Double()*pressure*1000 < (type == RESISTANT ? G.DIV_PROB_RES : G.DIV_PROB_SEN)) {
           ExampleCell c=Divide(radius*2.0/3,G.scratch,G.rn);
           c.radius=radius;
           c.xVel=0;
           c.yVel=0;
        }

    }
    public void Movement() {
        ForceMove();
        ApplyFriction(0);
    }
}

