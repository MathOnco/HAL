package Examples.CompetitiveRelease;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridVisWindow;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Gui.GuiGridVis;

import java.util.Random;

import static Examples.CompetitiveRelease.ExModel.*;
import static Framework.Utils.*;

public class ExModel extends AgentGrid2D<ExCell> {
    //model constants
    public final static int RESISTANT =RGB(0,1,0), SENSITIVE =RGB(0,0,1);
    public double divProb = 0.025, divProbRes = 0.01, deathProb = 0.001, drugStart = 400, drugPeriod = 200,
            drugDuration = 50, drugDiffRate = 0.25, drugUptake = 0.97, drugDeath = 0.2, drugBoundaryVal = 1.0;
    //internal model objects
    public PDEGrid2D drug;
    public Random rn;
    public int[] divHood = MooreHood(false);
    public int[] divIs = new int[divHood.length / 2];

    public ExModel(int xDim, int yDim, Random rn) {
        super(xDim, yDim, ExCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        int x = 75, y = 75, visScale = 5, tumorRad = 4, msPause = 5;
        double resistantProp = 0.5;
        GridVisWindow win = new GridVisWindow("Competitive Release",x, y, visScale);
        ExModel model = new ExModel(x, y, new Random());
        model.InitTumor(tumorRad, resistantProp);
        //Main run loop
        while(model.GetTick()<5000){
            win.TickPause(msPause);
            model.ModelStep();
            model.DrawModel(win);
        }
        win.Dispose();
    }

    public void InitTumor(int radius, double resistantProb) {
        //get a list of indices that fill a circle at the center of the grid
        int[] circleCoords = CircleHood(true, radius);
        int[] cellIs = new int[circleCoords.length / 2];
        int cellsToPlace = HoodToEmptyIs(circleCoords, cellIs, xDim / 2, yDim / 2);
        //place a new tumor cell at each index
        for (int i = 0; i < cellsToPlace; i++) {
            NewAgentSQ(cellIs[i]).type=rn.nextDouble()<resistantProb?RESISTANT:SENSITIVE;
        }
    }

    public void ModelStep() {
        for (ExCell cell : this) {
            cell.CellStep();
        }
        //check if drug should enter through the boundaries
        if (GetTick() > drugStart && (GetTick() - drugStart) % drugPeriod < drugDuration) {
            drug.Diffusion(drugDiffRate, drugBoundaryVal);
        } else {
            drug.Diffusion(drugDiffRate);
        }
        CleanShuffInc(rn);
    }

    public void DrawModel(GuiGridVis vis) {
        for (int i = 0; i < vis.length; i++) {
            ExCell drawMe = GetAgent(i);
            if (drawMe == null) {
                vis.SetPix(i,HeatMapRGB(drug.Get(i)));
                vis.SetPix(i, RESISTANT);
            }
        }
    }
}
class ExCell extends AgentSQ2Dunstackable<ExModel> {
    public int type;

    public void CellStep() {
        //Consumption of Drug
        G().drug.Mul(Isq(), G().drugUptake);
        //Chance of Death, depends on resistance and drug concentration
        if (G().rn.nextDouble() < G().deathProb + (type==RESISTANT ? 0 : G().drug.Get(Isq()) * G().drugDeath)) {
            Dispose();
        }
        //Chance of Division, depends on resistance
        else if (G().rn.nextDouble() < (type==RESISTANT ? G().divProbRes : G().divProb)) {
            int nEmptySpaces = HoodToEmptyIs(G().divHood, G().divIs);
            //If any empty spaces exist, randomly choose one and create a daughter cell there
            if (nEmptySpaces > 0) {
                G().NewAgentSQ(G().divIs[G().rn.nextInt(nEmptySpaces)]).type=this.type;
            }
        }
    }
}
