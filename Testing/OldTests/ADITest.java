package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;
import HAL.Util;

public class ADITest {
    public static void main(String[] args) {
        PDEGrid2D g=new PDEGrid2D(100,100);
        GridWindow win=new GridWindow(100,100,10);
        while(true){
            g.DiffusionADI(0.1, 1.0);
            for (int i = 0; i < g.length; i++) {
                g.Scale(i, 0.99);
            }
            g.Update();
            for (int i = 0; i < win.length; i++) {
                win.SetPix(i,Util.HeatMapJet(g.Get(i)));
            }
        }
    }
}
