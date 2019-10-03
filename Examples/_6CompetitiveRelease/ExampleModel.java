package Examples._6CompetitiveRelease;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Tools.FileIO;
import HAL.Rand;
import static Examples._6CompetitiveRelease.ExampleModel.*;
import static HAL.Util.*;

public class ExampleModel extends AgentGrid2D<ExampleCell> {
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public double TIMESTEP = 2.0 / 24;//2 hours per timestep
    public double SPACE_STEP = 20;//um
    public double DIV_PROB_SEN = ProbScale(0.5, TIMESTEP);
    public double DIV_PROB_RES = ProbScale(0.2, TIMESTEP);
    public double DEATH_PROB = ProbScale(0.02, TIMESTEP);
    public double DRUG_DEATH = ProbScale(0.8, TIMESTEP);
    public double DRUG_START = 20 / TIMESTEP;
    public double DRUG_PERIOD = 15 / TIMESTEP;
    public double DRUG_DURATION = 2 / TIMESTEP;
    public double DRUG_UPTAKE = -0.03 * TIMESTEP;
    public double DRUG_DIFF_RATE = 0.02 * 60 * 60 * 24 * (TIMESTEP / (SPACE_STEP * SPACE_STEP));
    public double DRUG_BOUNDARY_VAL = 1.0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rng;
    public int[] divHood = MooreHood(false);

    public ExampleModel(int xDim, int yDim, Rand rng) {
        super(xDim, yDim, ExampleCell.class);
        this.rng = rng;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        //setting up starting constants and data collection
        int x = 100, y = 100, visScale = 5, tumorRad = 10, msPause = 5;
        double resistantProb = 0.5;
        GridWindow win = new GridWindow("Competitive Release", x * 3, y, visScale);
        FileIO popsOut = new FileIO("populations.csv", "w");
        //setting up models
        ExampleModel[] models = new ExampleModel[3];
        for (int i = 0; i < models.length; i++) {
            models[i] = new ExampleModel(x, y, new Rand(0));
            models[i].InitTumor(tumorRad, resistantProb);
        }
        models[0].DRUG_DURATION = 0;//no drug
        models[1].DRUG_DURATION = 200;//constant drug
        //Main run loop
        for (int tick = 0; tick < 10000; tick++) {
            win.TickPause(msPause);
            for (int i = 0; i < models.length; i++) {
                models[i].ModelStep(tick);
                models[i].DrawModel(win, i);
            }
            //data recording
            popsOut.Write(models[0].Pop() + "," + models[1].Pop() + "," + models[2].Pop() + "\n");
            if (tick % (int) (10 / models[0].TIMESTEP) == 0) {
                win.ToPNG("ModelsDay" + tick * models[0].TIMESTEP + ".png");
            }
        }
        //closing data collection
        popsOut.Close();
        win.Close();
    }

    public void InitTumor(double radius, double resistantProb) {
        //get a list of indices that fill a circle at the center of the grid
        int[] tumorNeighborhood = CircleHood(true, radius);
        int hoodSize = MapHood(tumorNeighborhood, xDim / 2, yDim / 2);
        for (int i = 0; i < hoodSize; i++) {
            if (rng.Double() < resistantProb) {
                NewAgentSQ(tumorNeighborhood[i]).type = RESISTANT;
            } else {
                NewAgentSQ(tumorNeighborhood[i]).type = SENSITIVE;
            }
        }
    }

    public void ModelStep(int tick) {
        ShuffleAgents(rng);
        for (ExampleCell cell : this) {
            cell.CellStep();
        }
        double periodTick = (tick - DRUG_START) % DRUG_PERIOD;
        if (periodTick > 0 && periodTick < DRUG_DURATION) {
            //drug will enter through boundaries
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            //drug will not enter through boundaries
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
        drug.Update();
    }

    public void DrawModel(GridWindow vis, int iModel) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                ExampleCell drawMe = GetAgent(x, y);
                if (drawMe != null) {
                    vis.SetPix(x + iModel * xDim, y, drawMe.type);
                } else {
                    vis.SetPix(x + iModel * xDim, y, HeatMapRGB(drug.Get(x, y)));
                }
            }
        }
    }
}

class ExampleCell extends AgentSQ2Dunstackable<ExampleModel> {
    public int type;

    public void CellStep() {
        //uptake of Drug
        G.drug.Mul(Isq(), G.DRUG_UPTAKE);
        double deathProb, divProb;
        //Chance of Death, depends on resistance and drug concentration
        if (this.type == RESISTANT) {
            deathProb = G.DEATH_PROB;
        } else {
            deathProb = G.DEATH_PROB + G.drug.Get(Isq()) * G.DRUG_DEATH;
        }
        if (G.rng.Double() < deathProb) {
            Dispose();
            return;
        }
        //Chance of Division, depends on resistance
        if (this.type == RESISTANT) {
            divProb = G.DIV_PROB_RES;
        } else {
            divProb = G.DIV_PROB_SEN;
        }
        if (G.rng.Double() < divProb) {
            int options = MapEmptyHood(G.divHood);
            if (options > 0) {
                G.NewAgentSQ(G.divHood[G.rng.Int(options)]).type = this.type;
            }
        }
    }
}
