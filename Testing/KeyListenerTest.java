package Testing;

import Framework.Gui.GuiGrid;
import Framework.Gui.GuiWindow;

/**
 * Created by rafael on 7/1/17.
 */
public class KeyListenerTest {
    public static void main(String[] args) {
        GuiWindow gui=new GuiWindow("testing",true);
        GuiGrid ggv=new GuiGrid(100,100,10);
        gui.AddCol(1, ggv);
        gui.RunGui();
    }
}
