package Testing.OldTests;

import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;

/**
 * Created by rafael on 7/1/17.
 */
public class KeyListenerTest {
    public static void main(String[] args) {
        UIWindow gui=new UIWindow("testing",true);
        UIGrid ggv=new UIGrid(100,100,10);
        gui.AddCol(1, ggv);
        gui.RunGui();
    }
}
