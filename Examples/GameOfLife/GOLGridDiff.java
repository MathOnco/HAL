package Examples.GameOfLife;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;
import Framework.Gui.UILabel;
import Framework.Gui.UIWindow;

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
    final UIGrid vis;
    final UILabel popLbl;
    final UILabel tickLbl;
    final int runTicks;
    final int refreshRateMS;
    GOLGridDiff(int x, int y, double livingProb, int runTicks, int refreshRateMS, UIGrid vis, UILabel popLbl, UILabel tickLbl){
        super(x,y,true,true);
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
    public void Run(UIWindow win){
        for (int tick = 0; tick < runTicks; tick++) {
            int totalPop=0;
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    int hoodLen=MapHood(mooreHood, x, y);
                    int countNeighbors = 0;
                    for (int i = 0; i < hoodLen; i++) {
                        countNeighbors += Get(mooreHood[i]);
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
            SwapFields();
            if(popLbl!=null){ popLbl.SetText("Population: "+totalPop); }
            if(tickLbl!=null){ tickLbl.SetText("Tick: "+ tick); }
        }
        win.Close();
    }
    public static void main(String[] args){
        int xDim=1000;
        int yDim=1000;
        double livingProb=0.1;
        int scaleFactor=1;
        int runTicks=10000000;
        int refreshRate=0;
        UIWindow gui=new UIWindow("GOL with GridDiff",true);
        UIGrid vis=new UIGrid(xDim,yDim,scaleFactor,2,1, true);
        UILabel popLbl=new UILabel("Population:                0",1,1);
        UILabel tickLbl=new UILabel("GetTick:               0",1,1);

        gui.AddCol(0, popLbl);
        gui.AddCol(1, tickLbl);
        gui.AddCol(0, vis);
        gui.RunGui();
        GOLGridDiff g=new GOLGridDiff(xDim,yDim,livingProb,runTicks,refreshRate,vis,popLbl,tickLbl);
        g.Run(gui);
    }
}
