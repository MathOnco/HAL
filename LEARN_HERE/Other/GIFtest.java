package LEARN_HERE.Other;

import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Util;

import java.util.Random;

/**
 * Created by bravorr on 6/2/17.
 */
public class GIFtest {
    public static void main(String[] args) {
        UIWindow testGui=new UIWindow("test",true);
        UIGrid ggv=new UIGrid(10,10,100);
        Random rn=new Random();
        testGui.AddCol(0, ggv);
        ggv.SetPix(4, 4, Util.RGB(rn.nextDouble(),rn.nextDouble(),rn.nextDouble()));
        ggv.ToGIF("test.jpg");
        GifMaker gm=new GifMaker("test.gif",100,true);
        for (int i = 0; i < 10; i++) {
            ggv.SetPix(4, 4, Util.RGB(rn.nextDouble(),rn.nextDouble(),rn.nextDouble()));
            gm.AddFrame(ggv);
        }
        gm.Close();
    }
}
