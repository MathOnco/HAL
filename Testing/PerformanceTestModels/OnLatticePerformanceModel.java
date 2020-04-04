package Testing.PerformanceTestModels;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Rand;


import static Examples._6CompetitiveRelease.ExampleModel.RESISTANT;
import static HAL.Util.*;

public class OnLatticePerformanceModel extends AgentGrid2D<ExampleCell> {
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
    //public double DRUG_UPTAKE = 0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public int[] divHood = MooreHood(false);
    OpenGL2DWindow win;

    public OnLatticePerformanceModel(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, ExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    @FunctionalInterface
    interface ModelStep{
        void Run(long[]acc, OnLatticePerformanceModel m, int tick);
    }

    public static double[] RunPerformanceTestOnL(int sideLen, boolean draw){
        int x = sideLen, y = sideLen,ticks=100;
        OnLatticePerformanceModel m=new OnLatticePerformanceModel(x,y,new Rand(0));
        if(draw) {
            m.win = new OpenGL2DWindow(1000, 1000, m.xDim, m.yDim);
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
            if(draw){
                m.DrawModel(m.win);
            }
        }
        if(draw){
            m.win.Close();
        }
        //Diffusion Points, On Lattice StepCells,Total Population, On Lattice DiffusionStep
        return new double[]{sideLen*sideLen,acc[0]*1.0/ticks,m.popTotal*1.0/ticks,acc[1]*1.0/ticks};
    }

    public void InitTumor() {
        for (int i = 0; i < length; i++) {
            NewAgentSQ(i).type=RESISTANT;
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
        ShuffleAgents(rn);
        for (ExampleCell cell : this) {
            cell.CellStep();
        }
    }


    public void DrawModel(OpenGL2DWindow vis) {
        vis.Clear(BLACK);
        for (int i = 0; i < length; i++) {
            ExampleCell drawMe = GetAgent(i);
            //if the cell does not exist, draw the drug concentration
            vis.SetPix(ItoX(i),ItoY(i), drawMe == null ? HeatMapRGB(drug.Get(i)) : drawMe.type);
            //vis.SetPix(i,HeatMapRGB(drug.Get(i)));
        }
        vis.Update();
    }
}

class ExampleCell extends AgentSQ2Dunstackable<OnLatticePerformanceModel> {
    public int type;

    public void CellStep() {
        //Consumption of Drug
        G.drug.Mul(Isq(), G.DRUG_UPTAKE);
        //Chance of Death, depends on resistance and drug concentration
        if (G.rn.Double() < G.DEATH_PROB + (type == RESISTANT ? 0 : G.drug.Get(Isq()) * G.DRUG_DEATH)) {
            Dispose();
            return;
        }
        //Chance of Division, depends on resistance
        else if (G.rn.Double() < (type == RESISTANT ? G.DIV_PROB_RES : G.DIV_PROB_SEN)) {
            int options=MapEmptyHood(G.divHood);
            if(options>0){
                G.NewAgentSQ(G.divHood[G.rn.Int(options)]).type=this.type;
            }
        }
    }
}

