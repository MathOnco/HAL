package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Gui.GridWindow;
import HAL.Util;

public class CrankTest {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(8,9,50);
        PDEGrid3D grid=new PDEGrid3D(8,9,10,false,false,false);
        for (int i = 0; i < grid.zDim; i++) {
            grid.Set(0,0,i,10);
        }
        grid.Update();
        for (int i = 0; true; i++) {
            for (int x = 0; x < grid.xDim; x++) {
                for (int y = 0; y < grid.yDim; y++) {
                    win.SetPix(x,y,Util.HeatMapRGB(grid.Get(x,y,0)));
                }
            }
            System.out.println(grid.GetAvg());
            grid.DiffusionADI(0.1);
            //grid.DiffusionCrank(200,(x)->{
            //    if(x==-1){
            //        return 1;
            //    }
            //    return 0;
            //});
            grid.Update();
            win.TickPause(100);
        }
    }
}
