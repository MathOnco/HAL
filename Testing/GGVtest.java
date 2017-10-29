package Testing;

import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

/**
 * Created by Rafael on 10/4/2017.
 */
public class GGVtest {
    public static void main(String[] args) {
        GridVisWindow gvw=new GridVisWindow("gvw",10,10,100);
        GuiGridVis overlay=new GuiGridVis(10,10,100);
        overlay.SetPix(0, 0,Utils.RGBA((double) 1, (double) 1, (double) 1, 0.5));
        gvw.SetPix(0, 0, Utils.RGB((double) 0, (double) 0, (double) 0));

        gvw.AddAlphaGrid(overlay);
    }
}
