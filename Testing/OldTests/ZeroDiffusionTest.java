package Testing.OldTests;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
import HAL.Util;

public class ZeroDiffusionTest {
    public static void main(String[] args) {
        PDEGrid2D testGrid=new PDEGrid2D(100,100);
        GridWindow testWin=new GridWindow(100,100,10);
        Grid2Ddouble rates=new Grid2Ddouble(100,100);
        rates.SetAll(0.15);
        for (int x = 10; x < 90; x++) {
            for (int y = 10; y < 90; y++) {
                rates.Set(x,y,0);
            }
        }
        for (int i = 0; i < 100000; i++) {
            testGrid.Set(50,92,1);
            testGrid.Diffusion(rates,rates);
            testGrid.Update();
            testWin.TickPause(0);
            testWin.DrawPDEGrid(testGrid, Util::HeatMapRGB);
        }
    }
}
