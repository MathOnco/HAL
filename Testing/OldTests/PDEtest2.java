package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
import HAL.Util;

public class PDEtest2 {
    public static void main(String[] args) {
        PDEGrid2D test1 = new PDEGrid2D(100, 100);
        PDEGrid2D test2 = new PDEGrid2D(100, 100);
        GridWindow win = new GridWindow(200, 100, 5);
        for (int i = 0; i < 1000; i++) {
            for (int x = 0; x < 200; x++) {
                for (int y = 0; y < 100; y++) {
                    if(x<100){
                        win.SetPix(x,y, Util.HeatMapGBR(test1.Get(x,y)));
                    }
                    else{
                        win.SetPix(x,y,Util.HeatMapRGB(test1.Get(x-100,y)));
                    }
                }
            }
            win.TickPause(10);
            test1.Diffusion(0.1,1);
            test1.Diffusion(0.1,1);
            test2.Diffusion(0.2,1);
        }
    }
}
