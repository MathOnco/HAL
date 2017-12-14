package Testing;

import Framework.GridsAndAgents.PDEGrid3D;
import Framework.Gui.GridWindow;
import Framework.Util;

public class DiffTest3D {
    public static void main(String[] args) {
        PDEGrid3D grid3D=new PDEGrid3D(100,10,100,false,false,false);
        GridWindow win =new GridWindow("testing",100,100,5);
        while(true){
            //pick a random position to test
            grid3D.Set(345,1);
            //for (int x = 0; x < grid3D.xDim; x++) {
            //    for (int y = 0; y < grid3D.yDim; y++) {
            //        grid3D.Set(x,y,0,1);
            //    }
            //}
            win.TickPause(10);
            grid3D.Diffusion(0.16,1,false,false,false);
            win.DrawGridDiffXZ(grid3D,(val)-> Util.HeatMapRGB(val, (double) 0, (double) 1));
        }

    }
}
