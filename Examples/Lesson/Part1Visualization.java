package Examples.Lesson;

import Framework.Gui.GridWindow;
import Framework.Util;

public class Part1Visualization {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(500,500,2);
        while(true) {
            for (int i = 0; i < win.length; i++) {
                if (i > 0) {
                    win.SetPix(i - 1, Util.BLACK);
                }
                win.SetPix(i, Util.RED);
                win.TickPause(1);
            }
        }
    }
}
