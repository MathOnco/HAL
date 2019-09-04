package Examples.ClinicianSim;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.UIGrid;
import HAL.Rand;
import HAL.Tools.InteractiveModel.InteractiveModel;
import HAL.Tools.InteractiveModel.TreatableTumor;

import static Examples.ClinicianSim.ExampleModelInteractive.RESISTANT;
import static HAL.Util.*;

public class ExampleModelInteractive extends AgentGrid2D<ExampleCellInteractive> implements TreatableTumor{
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public double DIV_PROB_SEN = 0.025, DIV_PROB_RES = 0.01, DEATH_PROB = 0.001, DRUG_START = 400, DRUG_PERIOD = 200,
            DRUG_DURATION = 40, DRUG_DIFF_RATE = 2, DRUG_UPTAKE = -0.09, DRUG_DEATH = 0.2, DRUG_BOUNDARY_VAL = 1.0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public int[] divHood = MooreHood(false);
    public double[]pops=new double[3];


    public ExampleModelInteractive(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, ExampleCellInteractive.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        ExampleModelInteractive ex=new ExampleModelInteractive(100,100,new Rand());
        ex.InitTumor(10,0.5);
        InteractiveModel sim=new InteractiveModel(ex,400,20,100,100,5,100,50,1000,true,true);
        sim.RunGui();
    }

    public void InitTumor(int radius, double resistantProb) {
        //get a list of indices that fill a circle at the center of the grid
        int[] tumorNeighborhood = CircleHood(true, radius);
        int hoodSize=MapHood(tumorNeighborhood,xDim/2,yDim/2);
        for (int i = 0; i < hoodSize; i++) {
            NewAgentSQ(tumorNeighborhood[i]).type = rn.Double() < resistantProb ? RESISTANT : SENSITIVE;
        }
    }

    public void ModelStep(boolean drugOn) {
        ShuffleAgents(rn);
        for (ExampleCellInteractive cell : this) {
            cell.CellStep();
        }
 //       drug.Update();
        //check if drug should enter through the boundaries
        if (drugOn) {
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
        drug.Update();
    }

    public void DrawModel(UIGrid vis) {
        for (int i = 0; i < length; i++) {
            ExampleCellInteractive drawMe = GetAgent(i);
            //if the cell does not exist, draw the drug concentration
            vis.SetPix(i, drawMe == null ? HeatMapRGB(drug.Get(i)) : drawMe.type);
        }
    }

    @Override
    public void Draw(UIGrid vis, int drawState) {
        DrawModel(vis);
    }

    @Override
    public void InteractiveStep(double[] treatmentVals, int step) {
        for (int i = 0; i < 10; i++) {
            if(treatmentVals[0]!=0){
                ModelStep(true);
            }else{
                ModelStep(false);
            }
        }
        pops[0]=0;pops[1]=0;pops[2]=Pop();
        for (ExampleCellInteractive c : this) {
            pops[c.type==RESISTANT?0:1]++;
        }
    }

    @Override
    public String[] GetTreatmentNames() {
        return new String[]{"Drug"};
    }

    @Override
    public int[] GetTreatmentColors() {
        return new int[]{RGB256(238,110,46)};
    }

    @Override
    public int[] GetNumIntensities() {
        return new int[]{1};
    }

    @Override
    public int[] GetPlotColors() {
        return new int[]{RESISTANT,SENSITIVE,RED};
    }

    @Override
    public String[] GetPlotLegendNames() {
        return new String[]{"A","B","C"};
    }

    @Override
    public double[] GetPlotVals() {
        return pops;
    }

    @Override
    public void SetupConstructors() {
        _PassAgentConstructor(ExampleCellInteractive.class);
    }
}
class ExampleCellInteractive extends AgentSQ2Dunstackable<ExampleModelInteractive> {
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
