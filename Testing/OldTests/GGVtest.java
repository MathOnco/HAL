package Testing.OldTests;

import Framework.Gui.GridWindow;
import Framework.Gui.UIGrid;
import Framework.Util;

/**
 * Created by Rafael on 10/4/2017.
 */
public class GGVtest {
    public static void main(String[] args) {
        GridWindow gvw=new GridWindow("gvw",10,10,100);
        UIGrid overlay=new UIGrid(10,10,100);
        overlay.SetPix(0, 0, Util.RGBA((double) 1, (double) 1, (double) 1, 0.5));
        gvw.SetPix(0, 0, Util.RGB((double) 0, (double) 0, (double) 0));

        gvw.AddAlphaGrid(overlay);
    }
}
