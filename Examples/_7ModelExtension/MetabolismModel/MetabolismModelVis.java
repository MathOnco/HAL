package Examples._7ModelExtension.MetabolismModel;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;
import Framework.Gui.UILabel;
import Framework.Gui.UIWindow;
import Framework.Interfaces.DoubleToColor;
import Framework.Util;

public class MetabolismModelVis extends UIWindow {
    public final MetabolismGrid G;
    public final UIGrid cellVis;
    public final UIGrid oxygenVis;
    public final UIGrid glucoseVis;
    public final UIGrid acidVis;
    public final UIGrid phenoVis;
    public final UILabel days;

    public MetabolismModelVis(MetabolismGrid model, boolean active,int scaleFactor) {
        super(active);
        if(active) {
            //first col
            this.G = model;
            this.days=new UILabel("day: ________");
            AddCol(0, days);
            cellVis = new UIGrid(G.xDim, G.yDim, 3*scaleFactor,1,10);
            AddCol(0, cellVis);

            //second col
            AddCol(1, new UILabel("Oxygen"));
            oxygenVis = new UIGrid(G.oxygen.xDim, G.oxygen.yDim, 1*scaleFactor);
            AddCol(1,oxygenVis);
            AddCol(1, new UILabel("Glucose"));
            glucoseVis = new UIGrid(G.glucose.xDim, G.glucose.yDim, 1*scaleFactor);
            AddCol(1,glucoseVis);
            AddCol(1, new UILabel("Acid"));
            acidVis = new UIGrid(G.protons.xDim, G.protons.yDim, 1*scaleFactor);
            AddCol(1,acidVis);

            AddCol(2,new UILabel("Pheno"));
            phenoVis=new UIGrid(G.xDim*scaleFactor,G.yDim*scaleFactor,1);
            AddCol(2,phenoVis);
        }
        else{
            days=null;
            G=null;
            cellVis=null;
            oxygenVis=null;
            glucoseVis=null;
            acidVis=null;
            phenoVis=null;
        }
    }
    public void DrawDif(UIGrid vis, PDEGrid2D dif, DoubleToColor Color){
        for (int i = 0; i < dif.length; i++) {
            vis.SetPix(i,Color.GenColor(dif.Get(i)));
        }
    }
    public void Draw(){
        if(!IsRunning()) {
            RunGui();
        }
        if(IsActive()) {
            days.SetText("day: "+(int)(G.GetTick()*G.CELL_TIME_STEP));
            G.DrawCells(cellVis);
            DrawDif(oxygenVis, G.oxygen, (v) -> Util.HeatMapBGR(Util.Scale0to1(v, 0, G.OXYGEN_VESSEL_CONC)));
            DrawDif(glucoseVis,G.glucose,(v)-> Util.HeatMapRGB(Util.Scale0to1(v,0,G.GLUCOSE_VESSEL_CONC)));
            DrawDif(acidVis,G.protons,(v)-> Util.HeatMapGRB(Util.Scale0to1(Util.ProtonsToPh(v),G.MAX_ACID_RESIST_PHENO,7.4)));
            G.DrawPhenos(phenoVis);
        }
    }

}
