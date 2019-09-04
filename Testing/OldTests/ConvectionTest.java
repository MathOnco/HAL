package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Gui.TickTimer;
import HAL.Util;

/**
 * Created by bravorr on 7/21/17.
 */
public class ConvectionTest {
    static int x=40;
    static int y=40;
    static int steps=1000000;
    static int time=0;
    public static void main(String[] args) {
        UIWindow win=new UIWindow("ConvectionTest",true);
        UIGrid ggv=new UIGrid(x,y,20);
        double[] xVels=new double[x*y];
        double[] yVels=new double[x*y];
        for (int xi = 0; xi < x; xi++) {
            for (int yi = 0; yi < y; yi++) {

//                xVels[xi*y+yi]=0.9;
//            }
                if(Math.abs(yi-y/2)>Math.abs(xi-x/2)){
                    //at x disp case
                    if(yi<y/2){
                        xVels[xi*y+yi]=-0.1;
                    }
                    else{
                        xVels[xi*y+yi]=0.1;
                    }
                }
                else{
                    //at y disp case
                    if(xi<x/2){
                        yVels[xi*y+yi]=0.1;
                    }
                    else{
                        yVels[xi*y+yi]=-0.1;
                    }
                }
            }
        }
        TickTimer trt=new TickTimer();
        win.AddCol(0, ggv);
        win.RunGui();
        PDEGrid2D g=new PDEGrid2D(x,y);
        for (int i = 1; i <10 ; i++) {
            for (int j = 1; j <  10; j++) {

                g.Set(i, j, 1);
            }
        }
        for (int i = 0; i < steps; i++) {
            trt.TickPause(time);
            ggv.DrawPDEGrid(g,(val)->{
                return Util.HeatMapRBG(Util.ScaleMinToMax(val, (double) 0, (double) 1));
            });
            //g.ConvInhomogeneousSwap(xVels,yVels);
            g.Advection(0.001,0.001,0);
        }
        win.Close();
    }
}
