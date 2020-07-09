package Testing.OldTests;

import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;
import HAL.Util;

/**
 * Created by rafael on 9/7/17.
 */
public class SigmoidTest {
    public static void PlotSigmoid(UIGrid vis, double stretch, double inflectionValue, double min, double max, int color){
        for (int i = 0; i < vis.xDim; i++) {
            double val=Util.Rescale(i,0,vis.xDim*-8,0,1);
            double sigRes=Util.Sigmoid(val,stretch,inflectionValue,min,max);
            vis.SetPix(i, (int) (sigRes*(vis.yDim-1)),color);
        }
    }
    public static void PlotSigmoid2(UIGrid vis, double stretch, double inflectionValue, double min, double max, int color){
        for (int i = 0; i < vis.xDim; i++) {
            double val=Util.Rescale(i,0,vis.xDim*-8,0,1);
            double sigRes=Util.Sigmoid(val,stretch,inflectionValue,min,max);
            vis.SetPix(i, (int) (sigRes*(vis.yDim-1)),color);
        }
    }
    public static void ContactSigmoid(UIGrid vis, double bias, double neighborWeight, int nNeighbors, int color){
        bias=bias*2;
        neighborWeight=Math.exp(-neighborWeight*4);
        for (int i = 0; i < vis.xDim; i++) {
            double val = Util.Rescale(i, 0, (int) (vis.xDim), 0, 1);
            double sigRes = Util.Sigmoid(-val, neighborWeight, 0, 0, bias);
            vis.SetPix(i, (int) (sigRes * (vis.yDim - 1)), color);
        }
    }
    public static void main(String[] args) {
        GridWindow win=new GridWindow(1000,1000,1);
        win.Clear(Util.BLACK);
//        for (int i = 0; i < 20; i++) {
//            ContactSigmoid(win,1,i*1.0/19,8,Util.CategorialColor(i));
//        }
    }
}
