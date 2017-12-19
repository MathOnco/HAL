package LEARN_HERE.Diffusbiles;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Gui.GuiLabel;
import Framework.Gui.GuiWindow;
import Framework.Gui.TickTimer;
import Framework.Util;

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
        GuiWindow win=new GuiWindow("2D Diffusion Testing",true);
        GuiGrid v1=new GuiGrid(x,y,visScale);
        GuiGrid v2=new GuiGrid(x,y,visScale);
        win.AddCol(0, new GuiLabel("advection"));
        win.AddCol(0, v1);
        win.AddCol(1, new GuiLabel("diffusion"));
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
            //diffusion
            g2.DiffusionADI(0.01);
            //draw results
            v1.DrawGridDiff(g1, Util::HeatMapRGB);
            v2.DrawGridDiff(g2, Util::HeatMapRGB);
        }
        win.Dispose();
    }
}
