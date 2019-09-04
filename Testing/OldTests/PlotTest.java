package Testing.OldTests;

import HAL.Gui.PlotWindow;
import HAL.Util;

public class PlotTest {
    public static void main(String[] args) {
        PlotWindow win=new PlotWindow(250,250,2);
        win.AddPoint(1000,-1000, Util.RGB(1,0,0));
    }
}
