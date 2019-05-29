package Examples._7ModelExtension.MetabolismModelAngioChemo;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;
import Framework.Gui.UILabel;
import Framework.Rand;
import Framework.Util;
import Examples._7ModelExtension.MetabolismModel.MetabolismCell;
import Examples._7ModelExtension.MetabolismModel.MetabolismGrid;
import Examples._7ModelExtension.MetabolismModel.MetabolismModelVis;

class AngioChemoVis extends MetabolismModelVis{
    UIGrid visChemo;

    public AngioChemoVis(AngioChemoModel model, boolean active, int scaleFactor) {
        super(model, active, scaleFactor);
        if(active) {
            visChemo = new UIGrid(G.xDim, G.yDim,scaleFactor);
            AddCol(2,new UILabel("Chemo"));
            AddCol(2,visChemo);
        }
    }
    public void Draw(){
        super.Draw();
        DrawDif(visChemo,((AngioChemoModel)G).Chemo, Util::HeatMapRGB);
    }
}

class AngioChemoCell extends MetabolismCell<AngioChemoCell,AngioChemoModel> {
    @Override
    public AngioChemoCell Divide(int iDiv) {
        double chemoConc=G.Chemo.Get(iDiv);
        if(G.rn.Double()<G.CHEMO_MAX_KILL_PROB*2*(1/(1+Math.exp(-(chemoConc)/G.CHEMO_HALF_MAX))-0.5)){
            Die(G.APOPTOTIC_REMOVE_PROB);
            return null;
        };
        cellCycleTime = 1;
        if (type == CANCER) {
            AngioChemoCell child = G.GenTumorCell(iDiv, glycolysisPheno, acidResistancePheno, false);
            Mutate();
            child.Mutate();
            return child;
        }
        return G.GenNormalCell(iDiv, false);
    }
}

public class AngioChemoModel extends MetabolismGrid<AngioChemoCell> {

    double CHEMO_DIFF_RATE=1.0e2*DIFF_TIME_STEP*1.0/(GRID_SIZE*GRID_SIZE);
    double CHEMO_MAX_KILL_PROB=0.9;
    double CHEMO_HALF_MAX=0.25;
//    double

    public PDEGrid2D Chemo;
    public boolean antiAngioOn;
    public boolean chemoOn;
    public AngioChemoModel(int x, int y, Rand rn) {
        super(x, y, AngioChemoCell.class, rn);
        this.Chemo=new PDEGrid2D(x,y,false,false);
    }

    public double GetAvgChemo(){
        int numLayersInX=(int)(xDim*LAYERS_TO_CHECK_PROP);
        int numLayersInY=(int)(yDim*LAYERS_TO_CHECK_PROP);
        return GetAvgConc(Chemo,numLayersInX,numLayersInY);
    }

    public int ChemoDiffLoop(){
        double dif= Double.MAX_VALUE;
        int loops=0;
        double vesselConc=chemoOn?1:0;
        do {
            double avg=GetAvgChemo();
            for (AngioChemoCell v : vessels) {
                Chemo.Set(v.Isq(), vesselConc);
            }
            Chemo.DiffusionADI(CHEMO_DIFF_RATE,avg);
            if(loops>=5) {
                dif = Chemo.MaxDeltaScaled(EPS);//do at least 5 rounds of diffusion
            }
            Chemo.Update();
            loops++;
        }while(dif>DELTA_TOL);
        return loops;
    }
    public void DrugStep(boolean chemoOn,boolean antiAngioOn){
        this.chemoOn=chemoOn;
        this.antiAngioOn=antiAngioOn;
        DefaultGridStep();
    }

    public void DefaultGridStep(){
        if(GetTick()<50){
            DiffLoop(true);
        }
        else {
            DiffLoop(false);
        }
        ChemoDiffLoop();
        for (AngioChemoCell c : this) {
            c.DefaultCellStep();
        }
        CleanAgents();
        ShuffleAgents(rn);
        Angiogenesis();
        IncTick();
    }

        @Override
        public void Angiogenesis(){
        if(!antiAngioOn){
            super.Angiogenesis();
        }
    }


    public static void main(String[] args) {
        AngioChemoModel model=new AngioChemoModel(50,50,new Rand());
        //GridWindow win=new GridWindow(model.xDim,model.yDim,10);
        AngioChemoVis win=new AngioChemoVis(model,true,5);
        model.DefaultSetup(15,model.NORMAL_GLYCOLYTIC_PHENO,model.NORMAL_ACID_RESIST_PHENO);
        for (int i = 0; i < 100000; i++) {
            model.DrugStep(true,false);
            win.Draw();
            win.TickPause(100);
        }
        win.Close();
    }
}
