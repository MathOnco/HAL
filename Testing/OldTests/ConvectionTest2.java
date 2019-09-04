package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.UIGrid;
import HAL.Gui.UILabel;
import HAL.Gui.UIWindow;
import HAL.Gui.TickTimer;
import HAL.Util;

public class ConvectionTest2 {
    static int sideLen=1000;
    static int steps=100000;
    public static void Setup(PDEGrid2D g, double[]diffRates){
        for (int x = 1; x < 11; x++) {
            for (int y = 1; y < 11; y++) {
                g.Set(x,y,1);
            }
        }
        for (int i = 0; i < diffRates.length; i++) {
            if(g.ItoX(i)>g.xDim/2) {
                diffRates[i] = 0.1;
            }
            else{
                diffRates[i]=0.2;
            }
        }
    }
    public static void main(String[] args) {
        PDEGrid2D g=new PDEGrid2D(sideLen,sideLen,false,false);
        UIWindow win=new UIWindow("Advection1stOrder Example",true);
        UIGrid vis=new UIGrid(sideLen,sideLen,1);
        UILabel lbl=new UILabel("tick");
        TickTimer trt=new TickTimer();
        double[]diffRates=new double[g.length];
        win.AddCol(0, lbl);
        win.AddCol(0, vis);
        win.RunGui();
        Setup(g,diffRates);
        for (int i = 0; i < steps; i++) {
            trt.TickPause(0);
            //g.Advection1stOrder(0.01,0);
            g.Diffusion(0.1,1);
            //g.MultiThread(4,(x,y,j)->{
            //    Util.Diffusion2(x,y,g.GetField(),g.GetSwapField(),g.xDim,g.yDim,0.1,true,1,false,false);
            //});
            g.Diffusion(0.1,1);
            vis.DrawPDEGrid(g,(val)->{
                return Util.HeatMapRBG(Util.ScaleMinToMax(val, (double) 0, (double) 1));
            });
        }
    }
}
