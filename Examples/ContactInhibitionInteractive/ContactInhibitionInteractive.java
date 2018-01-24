package Examples.ContactInhibitionInteractive;

import Framework.Extensions.ClinicianSim;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Gui.GuiLabel;
import Framework.Rand;
import Framework.Util;
import Framework.Interfaces.TreatableTumor;

import java.io.Serializable;
import java.util.Random;

import static Framework.Util.*;

//an example of the ClinicianSim in action
public class ContactInhibitionInteractive extends AgentGrid2D<Cell> implements TreatableTumor,Serializable{
    Rand rn;
    //model constants
    public double divProb = 0.025, divProbRes = 0.01, deathProb = 0.001, drugDiffRate = 0.25, drugUptake = 0.998, drugDeath = 0.3;
    //internal model objects
    public PDEGrid2D drug;
    public int[] divHood = MooreHood(false);
    public int[] divIs = new int[divHood.length / 2];

    public ContactInhibitionInteractive(int xDim, int yDim) {
        super(xDim, yDim, Cell.class);
        drug = new PDEGrid2D(xDim, yDim);
        rn=new Rand();
    }
    public void InitTumor(int radius, double resistantProb,Random rn) {
        //get a list of indices that fill a circle at the center of the grid
        int[] circleCoords = CircleHood(true, radius);
        int[] cellIs = new int[circleCoords.length / 2];
        int cellsToPlace = HoodToEmptyIs(circleCoords, cellIs, xDim / 2, yDim / 2);
        //place a new tumor cell at each index
        for (int i = 0; i < cellsToPlace; i++) {
            Cell seedCell = NewAgentSQ(cellIs[i]);
            seedCell.isRes = rn.nextDouble() < resistantProb;
        }
    }

    @Override
    public void Draw(GuiGrid vis, GuiGrid alphaVis, int iLastSelected, GuiLabel drawLbl,double[]treatments) {
        System.out.println(iLastSelected);
        for (int i = 0; i < vis.length; i++) {
            Cell drawMe = GetAgent(i);
            if (drawMe == null) {
                vis.SetPix(i, RGB((double) 0, (double) 0, (double) 0));
                //vis.SetColorHeat(i, (int)(drug.GetPix(i)*20)/20.0); //drug conc (heat colormap)
            } else if (drawMe.isRes) {
                vis.SetPix(i, RGB((double) 0, (double) 1, (double) 0));
            } else {
                vis.SetPix(i, RGB((double) 0, (double) 0, (double) 1));
            }
            alphaVis.SetPix(i, SetAlpha(HeatMapRGB(drug.Get(i)*8.0/3.0), Util.Bound(drug.Get(i)*8.0,0,0.5)));
        }
    }

    @Override
    public void QuackStep(double[] treatmentVals, int step, int stepMS) {
        for (int i = 0; i < 10; i++) {

            for (Cell cell : this) {
                cell.CellStep(this.rn);
            }
            //check if drug should enter through the boundaries
            drug.Diffusion(drugDiffRate, treatmentVals[0]);
            CleanShuffInc(this.rn);
        }
    }


    @Override
    public String[] GetTreatmentNames() {
        return new String[]{"GenericDrug"};
    }

    @Override
    public int[] GetTreatmentColors() {
        return new int[]{RGB(1,1,0)};
    }

    @Override
    public int GetNumIntensities() {
        return 3;
    }

    @Override
    public int VisPixX() {
        return xDim;
    }

    @Override
    public int VisPixY() {
        return yDim;
    }

    @Override
    public int AlphaGridScaleFactor() {
        return 1;
    }

    @Override
    public double GetTox() {
        //return 0;
        return drug.GetAvg()*1.5;
    }

    @Override
    public double GetBurden() {
        return GetPop()*1.0/length;
    }

    @Override
    public double GetMaxTox() {
        return 0.5;
    }

    @Override
    public double GetMaxBurden() {
        return 0.5;
    }

//    @Override
//    public String[] GetSwitchNames() {
//        return null;
//    }

//    @Override
//    public boolean AllowMultiswitch() {
//        return false;
//    }

    public static void main(String[] args) {
        ContactInhibitionInteractive jm=new ContactInhibitionInteractive(100,100);
        jm.InitTumor(5,0.5,new Random(1));
        ClinicianSim ccl=new ClinicianSim(jm,600,10,5,1,30,25,10,100,false,true);
        ccl.RunGui();
        ccl.RunModel();
    }
    public void SetupConstructors(){
        _PassAgentConstructor(Cell.class);
    }
}

class Cell extends AgentSQ2Dunstackable<ContactInhibitionInteractive> {
    public boolean isRes;

    public void CellStep(Rand rn) {
        //Consumption of Drug
        G().drug.Mul(Isq(), G().drugUptake);
        //Chance of Death, depends on resistance and drug concentration
        if (rn.Double() < G().deathProb + (isRes ? 0 : G().drug.Get(Isq()) * G().drugDeath)) {

            Dispose();
        }
        //Chance of Division, depends on resistance
        else if (rn.Double() < (isRes ? G().divProbRes : G().divProb)) {
            int nEmptySpaces = G().HoodToEmptyIs(G().divHood, G().divIs, Xsq(), Ysq());
            //If any empty spaces exist, randomly choose one and create a daughter cell there
            if (nEmptySpaces > 0) {
                Cell daughter = G().NewAgentSQ(G().divIs[rn.Int(nEmptySpaces)]);
                daughter.isRes = this.isRes;
            }
        }
    }
}

