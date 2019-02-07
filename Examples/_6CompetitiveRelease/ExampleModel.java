package Examples._6CompetitiveRelease;

        import Framework.GridsAndAgents.AgentGrid2D;
        import Framework.GridsAndAgents.PDEGrid2D;
        import Framework.Gui.GridWindow;
        import Framework.GridsAndAgents.AgentSQ2Dunstackable;
        import Framework.Gui.PlotWindow;
        import Framework.Tools.FileIO;
        import Framework.Rand;
        import static Examples._6CompetitiveRelease.ExampleModel.*;
        import static Framework.Util.*;

public class ExampleModel extends AgentGrid2D<ExampleCell> {
    //model constants
    public final static int RESISTANT = RGB(0, 1, 0), SENSITIVE = RGB(0, 0, 1);
    public final static int NO_DRUG=0,CONSTANT_DRUG=1,PULSED_DRUG=2;
    public double DIV_PROB_SEN = 0.025, DIV_PROB_RES = 0.01, DEATH_PROB = 0.001, DRUG_START = 400, DRUG_PERIOD = 200,
            DRUG_DURATION = 40, DRUG_DIFF_RATE = 2, DRUG_UPTAKE = -0.09, DRUG_DEATH = 0.2, DRUG_BOUNDARY_VAL = 1.0;
    //internal model objects
    public PDEGrid2D drug;
    public Rand rn;
    public int[] divHood = MooreHood(false);

    public ExampleModel(int xDim, int yDim, Rand rn) {
        super(xDim, yDim, ExampleCell.class);
        this.rn = rn;
        drug = new PDEGrid2D(xDim, yDim);
    }

    public static void main(String[] args) {
        int x = 100, y = 100, visScale = 5, tumorRad = 10, msPause = 5;
        double resistantProp = 0.5;
        GridWindow win = new GridWindow("Competitive Release", x*3, y, visScale,true);
        PlotWindow plot=new PlotWindow("Tumor Burden Over Time",250,250,2);
        ExampleModel[] models = new ExampleModel[3];
        FileIO popsOut=new FileIO("populations.csv","w");
        for (int i = 0; i < models.length; i++) {
            models[i]=new ExampleModel(x,y,new Rand(0));
            models[i].InitTumor(tumorRad, resistantProp);
        }
        models[NO_DRUG].DRUG_DURATION =0;
        models[CONSTANT_DRUG].DRUG_DURATION =models[CONSTANT_DRUG].DRUG_PERIOD;
        //Main run loop
        for (int tick = 0; tick < 10000; tick++) {
            win.TickPause(msPause);
            for (int i = 0; i < models.length; i++) {
                models[i].ModelStep(tick);
                models[i].DrawModel(win,i);
            }
            //data recording
            popsOut.Write(models[0].Pop()+","+models[1].Pop()+","+models[2].Pop()+"\n");
            plot.AddPoint(tick,models[NO_DRUG].Pop(),RED);
            plot.AddPoint(tick,models[CONSTANT_DRUG].Pop(),BLUE);
            plot.AddPoint(tick,models[PULSED_DRUG].Pop(),GREEN);
            if((tick)%100==0) {
                win.ToPNG("ModelsTick" +tick+".png");
            }
        }
        popsOut.Close();
        win.Close();
    }

    public void InitTumor(int radius, double resistantProb) {
        //get a list of indices that fill a circle at the center of the grid
        int[] tumorNeighborhood = CircleHood(true, radius);
        int hoodSize=MapHood(tumorNeighborhood,xDim/2,yDim/2);
        for (int i = 0; i < hoodSize; i++) {
            NewAgentSQ(tumorNeighborhood[i]).type = rn.Double() < resistantProb ? RESISTANT : SENSITIVE;
        }
    }

    public void ModelStep(int tick) {
        ShuffleAgents(rn);
        for (ExampleCell cell : this) {
            cell.CellStep();
        }
 //       drug.Update();
        //check if drug should enter through the boundaries
        if (tick > DRUG_START && (tick - DRUG_START) % DRUG_PERIOD < DRUG_DURATION) {
            drug.DiffusionADI(DRUG_DIFF_RATE, DRUG_BOUNDARY_VAL);
        } else {
            drug.DiffusionADI(DRUG_DIFF_RATE);
        }
        drug.Update();
    }

    public void DrawModel(GridWindow vis, int iModel) {
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
