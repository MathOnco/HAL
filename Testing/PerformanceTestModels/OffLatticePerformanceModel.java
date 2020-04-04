package Testing.PerformanceTestModels;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.GridsAndAgents.SphericalAgent2D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;

import static Examples._6CompetitiveRelease.ExampleModel.RESISTANT;
import static HAL.Util.*;
import static HAL.Util.HeatMapGRB;

public class OffLatticePerformanceModel extends AgentGrid2D<OffLatticeExampleCell> {
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

    double FORCE_SCALER=0.5;

    //public double DRUG_UPTAKE = 0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public double[]scratch=new double[2];

    public OffLatticePerformanceModel(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, OffLatticeExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }


    public static double[] RunPerformanceTestOffL(int sideLen, boolean draw){
        int x = sideLen, y = sideLen,ticks=100;
        OffLatticePerformanceModel m=new OffLatticePerformanceModel(x,y,new Rand(0));
        OpenGL2DWindow win =null;
        if(draw) {
            win = new OpenGL2DWindow(1000, 1000, m.xDim, m.yDim);
        }
        m.DRUG_START=0;
        m.DRUG_DURATION=m.DRUG_PERIOD;
        m.InitTumor();
        long[]acc=new long[2];
        for (int tick = 0; tick < ticks; tick++) {
            long startCells=System.nanoTime();
            m.StepAllCells(tick);
            long endCells=System.nanoTime();
            m.DiffusionStep(tick);
            acc[0]+=endCells-startCells;
            acc[1]+=System.nanoTime()-endCells;
            m.popTotal+=m.Pop();
            if(draw) {
                win.Clear(BLACK);
                for (OffLatticeExampleCell cell : m) {
                    win.Circle(cell.Xpt(), cell.Ypt(), cell.radius/3, HeatMapGRB(m.drug.Get(cell.Xpt(),cell.Ypt())*0.8+0.2));
                }
                win.Update();
            }
        }
        if(draw) {
            win.Close();
        }
        //Diffusion Points, Off Lattice StepCells,Total Population, Off Lattice DiffusionStep
        return new double[]{sideLen*sideLen,acc[0]*1.0/ticks,m.popTotal*1.0/ticks,acc[1]*1.0/ticks};
    }

    public void InitTumor() {
        for (int i = 0; i < drug.length; i++) {
            OffLatticeExampleCell c=NewAgentPT(rn.Double()*xDim,rn.Double()*yDim);
            c.type=RESISTANT;
            c.radius=0.5;
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
        for (OffLatticeExampleCell cell : this) {
            cell.BirthDeath();
        }
        for (OffLatticeExampleCell cell : this) {
            cell.Movement();
        }
    }

}

class OffLatticeExampleCell extends SphericalAgent2D<OffLatticeExampleCell, OffLatticePerformanceModel> {
    public int type;

    public void BirthDeath() {
        //Consumption of Drug
        G.drug.Mul(Xpt(),Ypt(), G.DRUG_UPTAKE);
        //Chance of Death, depends on resistance and drug concentration
        if (G.rn.Double() < G.DEATH_PROB + (type == RESISTANT ? 0 : G.drug.Get(Xpt(),Ypt()) * G.DRUG_DEATH)) {
            Dispose();
            return;
        }
        double pressure=SumForces(1,(overlap, other) -> overlap*G.FORCE_SCALER);
        //contact inhibition and division probability influence division event
        if (G.rn.Double()*pressure*1000 < (type == RESISTANT ? G.DIV_PROB_RES : G.DIV_PROB_SEN)) {
            OffLatticeExampleCell c=Divide(radius*1.0/3,G.scratch,G.rn);
            c.radius=radius;
            c.xVel=0;
            c.yVel=0;
            c.type=RESISTANT;
        }

    }
    public void Movement() {
        ForceMove();
        ApplyFriction(0);
    }
}
