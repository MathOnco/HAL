package Testing;

import Framework.GridsAndAgents.PDEGrid1D;
import Framework.Gui.GridWindow;
import Framework.Util;

public class LogisticDiffusionTest {
    public static void main(String[] args) {
        PDEGrid1D grid1 = new PDEGrid1D(1000);
        PDEGrid1D grid2 = new PDEGrid1D(1000);
        PDEGrid1D grid3 = new PDEGrid1D(1000);
        GridWindow win = new GridWindow(100, 3, 20);
        //initial conditions
        grid1.Set(0,1);
        grid2.Set(0,1);
        grid3.Set(0,1);

        //running
//        GifMaker gif=new GifMaker("LogisticDiffusion.gif",0,true);
        for (int i = 0; i < 2000; i++) {
            win.TickPause(10);
            double diffRate=0.25;
            double growthRate=0.25;
            UpdateCorrect(grid1,diffRate,growthRate);
            UpdateApprox(grid2,diffRate,growthRate);
            UpdateApprox2(grid3,diffRate,growthRate);
            Draw(grid1,win,0);
            Draw(grid2,win,1);
            Draw(grid3,win,2);
//            if(i%100==0) {
//                gif.AddFrame(win);
//            }
        }
//        gif.Close();
    }
    public static void UpdateCorrect(PDEGrid1D grid,double diffRate,double growthRate){
        grid.DiffusionSwap(diffRate);
        for (int i = 0; i < grid.length; i++) {
            double currPop=grid.Get(i);
            grid.Add(i,growthRate*(1-currPop)*currPop);
        }
    }
    public static void UpdateApprox(PDEGrid1D grid,double diffRate,double growthRate){
        grid.Diffusion(diffRate);
        for (int i = 0; i < grid.length; i++) {
            double currPop = grid.Get(i);
            grid.Add(i, growthRate * (1 - currPop) * currPop);
        }
    }
    public static void UpdateApprox2(PDEGrid1D grid,double diffRate,double growthRate){
        for (int i = 0; i < grid.length; i++) {
            double currPop = grid.Get(i);
            grid.Add(i, growthRate * (1 - currPop) * currPop);
        }
        grid.Diffusion(diffRate);
    }
    public static void Draw(PDEGrid1D grid,GridWindow win,int lane){
        for (int i = 0; i < win.xDim; i++) {
            win.SetPix(i,lane, Util.HeatMapRGB(grid.Get(i*10)));
        }
    }
}

