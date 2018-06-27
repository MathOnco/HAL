package Testing;

import Framework.Gui.PlotWindow;
import Framework.Rand;
import Framework.Util;

public class PlotTest {
    public static void main(String[] args) {
        PlotWindow win=new PlotWindow("plot",500,500,true,true);
        for (int i = 0; i < 10000; i++) {
            Rand rng=new Rand();
            win.TickPause(100);
            int[]VNHood=Util.MooreHood(true);
            win.Point(rng.Double()*1000-500,rng.Double()*1000-500,Util.RGB(rng.Double(),rng.Double(),rng.Double()),VNHood);
        }
    }

}
