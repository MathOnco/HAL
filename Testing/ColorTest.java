package Testing;

import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;

import static Framework.Utils.*;

/**
 * Created by rafael on 7/4/17.
 */
public class ColorTest {
    public static void main(String[] args) {
        GridVisWindow win=new GridVisWindow("color test",10,10,10,true);
        GuiGridVis alpha=new GuiGridVis(10,10,10);
        GuiGridVis alpha2=new GuiGridVis(10,10,10);
        win.AddAlphaGrid(alpha);
        alpha.SetPix(0,SetAlpha(RGBA(0,2,0,0.4),0));
        System.out.println(GetAlpha256(alpha2.GetPix(0)));
        //System.out.println(GetAlpha256(RGBA256(255,200,1,255)));
    }
}
