package Testing.PerformanceTestModels.PerformanceTestOnLattice;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
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
    //public double DRUG_UPTAKE = 0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public int[] divHood = MooreHood(false);

    public ExampleModel(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, ExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    @FunctionalInterface
    interface ModelStep{
        void Run(ExampleModel m,int tick);
    }

    public static void RunModel(int sideLen,ModelStep Step){
        int x = sideLen, y = sideLen;
        ExampleModel m=new ExampleModel(x,y,new Rand(0));
        m.DRUG_START=0;
        m.DRUG_DURATION=m.DRUG_PERIOD;
        m.InitTumor();
        for (int tick = 0; tick < 100000; tick++) {
            Step.Run(m,tick);
        }
    }

    public static void main(String[] args) {
        long start=System.currentTimeMillis();
        RunModel(60, ExampleModel::ModelStep60);
        RunModel(90, ExampleModel::ModelStep90);
        RunModel(120, ExampleModel::ModelStep120);
        RunModel(150, ExampleModel::ModelStep150);
        RunModel(180, ExampleModel::ModelStep180);
        System.out.println(System.currentTimeMillis()-start);


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

    public static void ModelStep60(ExampleModel m,int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
    }
    public static void ModelStep90(ExampleModel m,int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
    }
    public static void ModelStep120(ExampleModel m,int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
    }
    public static void ModelStep150(ExampleModel m,int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
    }
    public static void ModelStep180(ExampleModel m,int tick) {
        m.StepAllCells(tick);
        m.DiffusionStep(tick);
    }

    public void DrawModel(GridWindow vis, int iModel) {
        for (int i = 0; i < length; i++) {
            ExampleCell drawMe = GetAgent(i);
            //if the cell does not exist, draw the drug concentration
            vis.SetPix(ItoX(i)+iModel*xDim,ItoY(i), drawMe == null ? HeatMapRGB(drug.Get(i)) : drawMe.type);
            //vis.SetPix(i,HeatMapRGB(drug.Get(i)));
        }
    }
}

class ExampleCell extends AgentSQ2Dunstackable<ExampleModel> {
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

