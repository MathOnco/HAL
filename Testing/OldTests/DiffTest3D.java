package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Gui.GridWindow;
import HAL.Util;

public class DiffTest3D {
    public static void main(String[] args) {
        PDEGrid3D grid3D=new PDEGrid3D(100,10,100,false,false,false);
        GridWindow win =new GridWindow("testing",100,100,5);
        while(true){
            //pick a random position to test
            //for (int x = 0; x < grid3D.xDim; x++) {
            //    for (int y = 0; y < grid3D.yDim; y++) {
            //        grid3D.SetPix(x,y,0,1);
            //    }
            //}
            win.TickPause(10);
            grid3D.Advection(0.1,0.1,0.1,0);
            grid3D.Set(345,1);
            grid3D.Update();
            win.DrawPDEGridXZ(grid3D,(val)-> Util.HeatMapRGB(val, (double) 0, (double) 1));
        }

    }
}
