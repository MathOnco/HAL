package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Gui.GridWindow;
import HAL.Util;

import static HAL.Util.HeatMapRGB;

public class AdvectionTest1D {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,10,10);
        PDEGrid3D grid=new PDEGrid3D(100,10,10);
        while(true){
            win.TickPause(0);
            grid.Advection(0.1,0.1,0.1,0);
            grid.Set(0,1);
            grid.Update();
            win.DrawPDEGridXY(grid, Util::HeatMapRGB);
        }
    }
}
