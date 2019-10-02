package LEARN_HERE.Diffusbiles;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.UIGrid;
import HAL.Gui.UILabel;
import HAL.Gui.UIWindow;
import HAL.Gui.TickTimer;
import HAL.Util;

/**
 * Created by rafael on 7/18/17.
 */
public class ReactionDiffusion2D {
    static final int x=20;
    static final int y=10;
    static final int time=10000;
    static final int visScale=10;
    static final int tickRate=0;
    public static void main(String[] args) {
        //set up gui
        TickTimer trt=new TickTimer();
        UIWindow win=new UIWindow("2D Diffusion VesselOcclusion",true);
        UIGrid v1=new UIGrid(x,y,visScale);
        UIGrid v2=new UIGrid(x,y,visScale);
        win.AddCol(0, new UILabel("advection"));
        win.AddCol(0, v1);
        win.AddCol(1, new UILabel("diffusion"));
        win.AddCol(1, v2);
        win.RunGui();

        //set up grids
        PDEGrid2D g1=new PDEGrid2D(x,y);
        PDEGrid2D g2=new PDEGrid2D(x,y);

        //run loop
        for (int i = 0; i < time; i++) {
            win.TickPause(5);
            trt.TickPause(tickRate);
            //reaction
            for (int j = 3; j < y-3; j++) {
                g1.Set(10,j,1);
                g2.Set(10,j,1);
            }
            //advection
            g1.Advection(0.01,0.01,0);
            g1.Update();
            //diffusion
            g2.DiffusionADI(0.01);
            g2.Update();
            //draw results
            v1.DrawPDEGrid(g1, Util::HeatMapRGB);
            v2.DrawPDEGrid(g2, Util::HeatMapRGB);
        }
        win.Close();
    }
}
