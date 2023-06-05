package LEARN_HERE.Other;

import HAL.Gui.PlotWindow;
import HAL.Util;

public class PlotExample {
    public static void main(String[] args) {
        double[] xData = new double[] { 0.0, 1.0, 2.0, 3.0, 4.0, 5.0 };
        double[] yData = new double[] { 2.0, 1.0, 2.0, 2.0, 3.0, 6.0 };

        PlotWindow plot=new PlotWindow(250,250,2);
        plot.AddLine(Util.RED,xData,yData);
        plot.AddUpdateFn((grid)->grid.Legend(new String[]{"RED HOT LINE"},new int[]{Util.RED},Util.WHITE,Util.BLACK,grid.xDim/3,grid.yDim-1));
    }
}
