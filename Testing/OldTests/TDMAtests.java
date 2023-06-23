package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid1D;
import HAL.Gui.OpenGL2DWindow;
import HAL.Util;

public class TDMAtests {
    public static void main(String[] args) {
        OpenGL2DWindow.MakeMacCompatible(args);
        OpenGL2DWindow win=new OpenGL2DWindow(500,500,100,1);
        PDEGrid1D pde=new PDEGrid1D(10);
        pde.Set(pde.xDim/2,1);
        pde.Update();
        for (int i = 0; i < 1000; i++) {
            pde.Diffusion(0.1);
            pde.Set(pde.xDim/2,1);
            pde.Update();
            for (int j = 0; j < pde.length; j++) {
                win.SetPix(j, Util.HeatMapRGB(pde.Get(j)));
            }
        }
    }
}
