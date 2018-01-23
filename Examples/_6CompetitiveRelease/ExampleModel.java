package Examples._6CompetitiveRelease;

        import Framework.GridsAndAgents.AgentGrid2D;
        import Framework.GridsAndAgents.PDEGrid2D;
        import Framework.Gui.GifMaker;
        import Framework.Gui.GridWindow;
        import Framework.GridsAndAgents.AgentSQ2Dunstackable;
        import Framework.Gui.GuiGrid;
        import Framework.Tools.FileIO;
        import Framework.Rand;
        import Framework.Tools.PerformanceTimer;

        import static Examples._6CompetitiveRelease.ExampleModel.*;
        import static Framework.Util.*;

public class ExampleModel extends AgentGrid2D<ExampleCell> {
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public double DIV_PROB = 0.025, DIV_PROB_RES = 0.01, DEATH_PROB = 0.001, DRUG_START = 400, DRUG_PERIOD = 200,
            DRUG_DURATION = 40, DRUG_DIFF_RATE = 2, DRUG_UPTAKE = 0.91, DRUG_DEATH = 0.2, DRUG_BOUNDARY_VAL = 1.0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public int[] divHood = MooreHood(false);
    public int[] divIs = new int[divHood.length / 2];

    public ExampleModel(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, ExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        int x = 100, y = 100, visScale = 5, tumorRad = 10, msPause = 0;
        double resistantProp = 0.5;
        GridWindow win = new GridWindow("Competitive Release", x*3, y, visScale,true,false);
        ExampleModel[] models = new ExampleModel[3];
        FileIO popsOut=new FileIO("populations.csv","w");
        for (int i = 0; i < models.length; i++) {
            models[i]=new ExampleModel(x,y,new Rand(1));
            models[i].InitTumor(tumorRad, resistantProp);
        }
        models[0].DRUG_DURATION =0;//no drug
        models[1].DRUG_DURATION =models[1].DRUG_PERIOD;//constant drug
        //Main run loop
        while (models[0].GetTick() <= 10000) {
            win.TickPause(msPause);
            for (int i = 0; i < models.length; i++) {
                models[i].ModelStep();
                models[i].DrawModel(win,i);
            }
            //data recording
            popsOut.Write(models[0].GetPop()+","+models[1].GetPop()+","+models[2].GetPop()+"\n");
            if((models[0].GetTick()-1)%100==0) {
                win.ToPNG("ModelsTick" + (models[0].GetTick()-1)+".png");
            }
        }
        //popsOut.Close();
        win.Dispose();
    }

    public void InitTumor(int radius, double resistantProb) {
        //get a list of indices that fill a circle at the center of the grid
        int[] circleCoords = CircleHood(true, radius);
        int[] cellIs = new int[circleCoords.length / 2];
        int cellsToPlace = HoodToEmptyIs(circleCoords, cellIs, xDim / 2, yDim / 2);
        //place a new tumor cell at each index
        for (int i = 0; i < cellsToPlace; i++) {
            NewAgentSQ(cellIs[i]).type = rn.Double() < resistantProb ? RESISTANT : SENSITIVE;
        }
    }

    public void ModelStep() {
        for (ExampleCell cell : this) {
            cell.CellStep();
        }
        //check if drug should enter through the boundaries
        if (GetTick() > DRUG_START && (GetTick() - DRUG_START) % DRUG_PERIOD < DRUG_DURATION) {
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
        CleanShuffInc(rn);
    }

    public void DrawModel(GuiGrid vis, int iModel) {
        for (int i = 0; i < length; i++) {
            ExampleCell drawMe = GetAgent(i);
            //if the cell does not exist, draw the drug concentration
            vis.SetPix(ItoX(i)+iModel*xDim,ItoY(i), drawMe == null ? HeatMapRGB(drug.Get(i)) : drawMe.type);
        }
    }
}
class ExampleCell extends AgentSQ2Dunstackable<ExampleModel> {
    public int type;

    public void CellStep() {
        //Consumption of Drug
        G().drug.Mul(Isq(), G().DRUG_UPTAKE);
        //Chance of Death, depends on resistance and drug concentration
        if (G().rn.Double() < G().DEATH_PROB + (type == RESISTANT ? 0 : G().drug.Get(Isq()) * G().DRUG_DEATH)) {
            Dispose();
        }
        //Chance of Division, depends on resistance
        else if (G().rn.Double() < (type == RESISTANT ? G().DIV_PROB_RES : G().DIV_PROB)) {
            int nEmptySpaces = HoodToEmptyIs(G().divHood, G().divIs);
            //If any empty spaces exist, randomly choose one and create a daughter cell there
            if (nEmptySpaces > 0) {
                G().NewAgentSQ(G().divIs[G().rn.Int(nEmptySpaces)]).type = this.type;
            }
        }
    }
}
