package LEARN_HERE.Other;

import Framework.Gui.GifMaker;
import Framework.Gui.GuiGridVis;
import Framework.Gui.GuiWindow;
import Framework.Utils;

import java.util.Random;

/**
 * Created by bravorr on 6/2/17.
 */
public class GIFtest {
    public static void main(String[] args) {
        GuiWindow testGui=new GuiWindow("test",true);
        GuiGridVis ggv=new GuiGridVis(10,10,100);
        Random rn=new Random();
        testGui.AddCol(0, ggv);
        ggv.SetPix(4, 4, Utils.RGB(rn.nextDouble(),rn.nextDouble(),rn.nextDouble()));
        ggv.ToGIF("test.jpg");
        GifMaker gm=new GifMaker("test.gif",100,true);
        for (int i = 0; i < 10; i++) {
            ggv.SetPix(4, 4, Utils.RGB(rn.nextDouble(),rn.nextDouble(),rn.nextDouble()));
            gm.AddFrame(ggv);
        }
        gm.Close();
    }
}
