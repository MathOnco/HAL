package Examples.GameOfLife;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Gui.GuiLabel;
import Framework.Gui.GuiWindow;

import java.util.Random;

import static Framework.Util.MooreHood;
import static Framework.Util.RGB;

/**
 * Created by rafael on 4/16/17.
 */
public class GOLGridDiff extends PDEGrid2D {
    static final int RED=RGB(1,0,0),BLACK=RGB(0,0,0);
    final int[]mooreHood;
    final int[]neighborIs;
    final GuiGrid vis;
    final GuiLabel popLbl;
    final GuiLabel tickLbl;
    final int runTicks;
    final int refreshRateMS;
    GOLGridDiff(int x, int y, double livingProb, int runTicks, int refreshRateMS, GuiGrid vis, GuiLabel popLbl, GuiLabel tickLbl){
        super(x,y);
        this.vis=vis;
        mooreHood=MooreHood(false);
        neighborIs=new int[mooreHood.length/2];
        Random rn=new Random();
        this.runTicks=runTicks;
        this.refreshRateMS=refreshRateMS;
        this.popLbl=popLbl;
        this.tickLbl=tickLbl;
        for (int i = 0; i < length; i++) {
            Set(i,rn.nextDouble()<livingProb?1:0);
        }
    }
    public void Run(GuiWindow win){
        while(GetTick() < runTicks) {
            int totalPop=0;
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    HoodToIs(mooreHood, neighborIs, x, y, true, true);
                    int countNeighbors = 0;
                    for (int i : neighborIs) {
                        countNeighbors += Get(i);
                    }
                    int status = (int) Get(x, y);
                    if ((status == 1 && (countNeighbors == 2 || countNeighbors == 3)) || (status == 0 && countNeighbors == 3)) {
                        SetSwap(x, y, 1);
                    } else {
                        SetSwap(x, y, 0);
                    }
                    if (vis != null) {
                        double nextPop= GetSwap(x, y);
                        vis.SetPix(x, y, nextPop==0?BLACK:RED);
                        totalPop+=nextPop;
                    }
                }
            }
            SwapInc();
            if(popLbl!=null){ popLbl.SetText("Population: "+totalPop); }
            if(tickLbl!=null){ tickLbl.SetText("Tick: "+ GetTick()); }
        }
        win.Dispose();
    }
    public static void main(String[] args){
        int xDim=1000;
        int yDim=1000;
        double livingProb=0.1;
        int scaleFactor=1;
        int runTicks=10000000;
        int refreshRate=0;
        GuiWindow gui=new GuiWindow("GOL with GridDiff",true);
        GuiGrid vis=new GuiGrid(xDim,yDim,scaleFactor,2,1, true);
        GuiLabel popLbl=new GuiLabel("Population:                0",1,1);
        GuiLabel tickLbl=new GuiLabel("GetTick:               0",1,1);

        gui.AddCol(0, popLbl);
        gui.AddCol(1, tickLbl);
        gui.AddCol(0, vis);
        gui.RunGui();
        GOLGridDiff g=new GOLGridDiff(xDim,yDim,livingProb,runTicks,refreshRate,vis,popLbl,tickLbl);
        g.Run(gui);
    }
}
