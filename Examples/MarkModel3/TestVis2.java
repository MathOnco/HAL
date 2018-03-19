package Examples.MarkModel3;

import Examples.MarkModel3.Drugs.HAP;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Interfaces.DoubleToColor;

import static Framework.Util.*;

public class TestVis2 {
    GridWindow win;
    OCTissue t;
    public TestVis2(OCTissue t,int scale,String title){
        win=new GridWindow(title,t.xDim+t.xDim*1/3,t.yDim,scale);
        this.t=t;
    }
    public void DrawCells()   //----THIS SHOULD WORK IF NEW COLOR IS ADDED CORRECTLY----
    {
        for (int x = 0; x < t.xDim; x++){
            for (int y = 0; y < t.yDim; y++) {
                    OCCell c = t.GetAgent(x,y);
                    if (c == null) {
                        win.SetPix(x,y, OCTissue.EMPTY_COLOR);
                    } else {
                        win.SetPix(x,y, c.drawColor);
                    }
                }
            }
        win.DrawStringSingleLine("Day:"+(int)(t.tick*t.CELL_TIMESTEP),0,win.yDim-1,RED,BLACK);
    }
    public void DrawDiff(Diff drawMe, DoubleToColor drawFun, int yDisp) {
        for (int x = 0; x < drawMe.grid.xDim; x++){
            for (int y = 0; y < drawMe.grid.yDim; y++) {
                win.SetPix(x + t.xDim, y + drawMe.grid.yDim * yDisp, drawFun.GenColor(drawMe.grid.Get(x, y)));
            }
    }
    }
    public void DrawHap(PDEGrid2D diff, PDEGrid2D activated, int yDisp){
        for (int x = 0; x < diff.xDim; x++) {
            for (int y = 0; y < diff.yDim; y++) {
                win.SetPix(x + t.xDim, y + diff.yDim * yDisp, RGB(activated.Get(x, y), 0, diff.Get(x, y)));
            }
        }
    }
    public void Draw(){
        DrawCells();
        DrawDiff(t.oxygen,(val)->HeatMapBGR(val,0,t.VESSEL_O2),0);

        DrawDiff(t.acid,(val)->HeatMapGRB(ProtonsToPh(val),t.VESSEL_PH,t.NORMAL_PHENO_ACID_RESIST),1);
        DrawDiff(t.glucose,(val)->HeatMapRGB(val,0,t.VESSEL_GLUC),2);
    }
    public void DrawHAP(HAP drawMe){
        DrawCells();
        DrawDiff(t.oxygen,(val)->HeatMapBGR(val,0,t.VESSEL_O2),0);
        //DrawDiff(t.glucose,(val)->HeatMapRGB(val,0,t.VESSEL_GLUC),1);
        DrawDiff(t.acid,(val)->HeatMapGRB(ProtonsToPh(val),t.VESSEL_PH,t.NORMAL_PHENO_ACID_RESIST),1);
        DrawHap(drawMe.conc.grid,drawMe.concActivated.grid,2);
    }
    public void DrawHAPpheno(HAP drawMe){
        DrawCells();
        //DrawDiff(t.oxygen,(val)->HeatMapBGR(val,0,t.VESSEL_O2),0);
        //DrawDiff(t.glucose,(val)->HeatMapRGB(val,0,t.VESSEL_GLUC),1);
        DrawPhenos(0);
        DrawDiff(t.acid,(val)->HeatMapGRB(ProtonsToPh(val),t.VESSEL_PH,t.NORMAL_PHENO_ACID_RESIST),2);
        DrawHap(drawMe.conc.grid,drawMe.concActivated.grid,2);
    }

    public void DrawPhenos(int yDisp)
    {
        for (int x = 0; x < t.xDim/3; x++)
        {
            for (int y = 0; y < t.yDim/3; y++)
            {
                win.SetPix(t.xDim+x,y+t.yDim*yDisp,CbCrPlaneColor(y*1.0/(t.yDim/3),x*1.0/(t.xDim/3)));
            }
        }
        for (OCCell c : t)
        {
            if(c.type ==OCTissue.TUMOR)
            {
                win.SetPix((int)(c.GetGlycPheno(c.glycRate)*(t.xDim/3-1))+t.xDim,(int)(c.GetAcidResistPheno(c.acidResistPH)*(t.yDim/3-1))+t.yDim/3*yDisp,OCTissue.EMPTY_COLOR);
            }
        }
    }


}
