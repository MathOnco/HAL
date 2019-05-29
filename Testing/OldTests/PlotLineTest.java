package Testing.OldTests;

import Framework.Gui.PlotLine;
import Framework.Gui.PlotWindow;
import Framework.Util;

public class PlotLineTest {
    public static void main(String[] args) {
        PlotWindow win =new PlotWindow(500,500);
        PlotLine testLine=new PlotLine(win, Util.RED);
        for (int i = 0; i < 10; i++) {
            win.Clear();
            for (int j = 0; j < 100; j++) {
                testLine.AddSegment(j,Math.sin((i*5+j)/20.0));
                win.TickPause(10);
            }
        }
    }
}
