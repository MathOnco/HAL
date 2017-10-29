package Testing;

import Framework.Gui.GuiGridVis;
import Framework.Gui.GuiWindow;
import Framework.Utils;

/**
 * Created by bravorr on 6/2/17.
 */
public class WriteOutTest {
    public static void main(String[] args) {
        GuiWindow testGui=new GuiWindow("test",true);
        GuiGridVis ggv=new GuiGridVis(10,10,5);
        testGui.AddCol(0, ggv);
        ggv.SetPix(4, 4, Utils.RGB((double) 1, (double) 1, (double) 1));
        ggv.ToGIF("test.jpg");
    }
}
