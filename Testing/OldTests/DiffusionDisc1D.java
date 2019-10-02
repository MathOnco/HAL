package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid1D;
import HAL.Gui.GridWindow;
import HAL.Util;

public class DiffusionDisc1D {
    public static void main(String[] args) {
        PDEGrid1D pde=new PDEGrid1D(10);
        double[]diffRates=new double[10];
        for (int i = 0; i < diffRates.length; i++) {
            diffRates[i]=0.1;
        }
        GridWindow win=new GridWindow(10,1,100);
        diffRates[4]=0.01;
        for (int i = 0; i < 100000; i++) {
            pde.Diffusion(diffRates,(int x)->{ if(x<0)return 1;else return 0;} ,(int x)->0.1);
            pde.Update();
            for (int j = 0; j < win.length; j++) {
                win.SetPix(j,Util.HeatMapRGB(pde.Get(j)));
            }
            win.TickPause(10);
        }
    }
}
