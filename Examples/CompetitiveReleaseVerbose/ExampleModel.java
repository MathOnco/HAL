package Examples.CompetitiveReleaseVerbose;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Tools.FileIO;
import Framework.Rand;
import static Examples.CompetitiveReleaseVerbose.ExampleModel.*;
import static Framework.Util.*;

public class ExampleModel extends AgentGrid2D<ExampleCell> {
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public double DIV_PROB_SEN = 0.025, DIV_PROB_RES = 0.01, DEATH_PROB = 0.001,
            DRUG_DIFF_RATE = 2, DRUG_UPTAKE = 0.91, DRUG_TOXICITY = 0.2, DRUG_BOUNDARY_VAL = 1.0;
    public int DRUG_START = 400, DRUG_CYCLE = 200, DRUG_DURATION = 0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rng;
    public int[] divHood = MooreHood(false);

    public ExampleModel(int xDim, int yDim, Rand generator) {
        super(xDim, yDim, ExampleCell.class);
        rng = generator;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        //setting up starting constants and data collection
        int x = 100, y = 100, visScale = 5, tumorRad = 10, msPause = 0;
        double resistantProb = 0.5;
        GridWindow win = new GridWindow("Competitive Release", x * 3, y, visScale);
        FileIO popsOut = new FileIO("populations.csv", "w");
        //setting up models
        ExampleModel[] models = new ExampleModel[3];
        for (int i = 0; i < models.length; i++) {
            models[i] = new ExampleModel(x, y, new Rand(1));
            models[i].InitTumor(tumorRad, resistantProb);
        }
        models[1].DRUG_DURATION = 200;//constant drug
        models[2].DRUG_DURATION = 40;//pulsed drug
        //Main run loop
        for (int tick = 0; tick < 10000; tick++) {
            win.TickPause(msPause);
            for (int i = 0; i < models.length; i++) {
                models[i].ModelStep(tick);
                models[i].DrawModel(win, i);
            }
            //data recording
            popsOut.Write(models[0].GetPop() + "," + models[1].GetPop() + "," + models[2].GetPop() + "\n");
            if ((tick) % 100 == 0) {
                win.ToPNG("ModelsTick" + tick + ".png");
            }
        }
        //closing data collection
        popsOut.Close();
        win.Close();
    }

    public void InitTumor(int radius, double resistantProb) {
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
        int periodTick = (tick - DRUG_START) % DRUG_CYCLE;
        if (periodTick > 0 && periodTick < DRUG_DURATION) {
            //drug will enter through boundaries
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            //drug will not enter through boundaries
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
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
        //Consumption of Drug
        G().drug.Mul(Isq(), G().DRUG_UPTAKE);
        double deathProb, divProb;
        //Chance of Death, depends on resistance and drug concentration
        if (this.type == RESISTANT) {
            deathProb = G().DEATH_PROB;
        } else {
            deathProb = G().DEATH_PROB + G().drug.Get(Isq()) * G().DRUG_TOXICITY;
        }
        if (G().rng.Double() < deathProb) {
            Dispose();
            return;
        }
        //Chance of Division, depends on resistance
        if (this.type == RESISTANT) {
            divProb = G().DIV_PROB_RES;
        } else {
            divProb = G().DIV_PROB_SEN;
        }
        if (G().rng.Double() < divProb) {
            int options = MapEmptyHood(G().divHood);
            if (options > 0) {
                G().NewAgentSQ(G().divHood[G().rng.Int(options)]).type = this.type;
            }
        }
    }
}
