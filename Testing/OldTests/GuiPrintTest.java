package Testing.OldTests;

import HAL.Gui.UILabel;
import HAL.Gui.UIWindow;

import javax.swing.*;

public class GuiPrintTest extends JPanel {
    public static void main(String[] args) {
        UIWindow win=new UIWindow();
        win.AddCol(0,new UILabel("testing"));
        win.AddCol(0,new UILabel("testing2"));
        win.RunGui();
        win.Close();
    }

}
