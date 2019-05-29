package Testing.OldTests;

import Framework.Gui.UILabel;
import Framework.Gui.UIWindow;

import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class GuiPrintTest extends JPanel {
    public static void main(String[] args) {
        UIWindow win=new UIWindow();
        win.AddCol(0,new UILabel("testing"));
        win.AddCol(0,new UILabel("testing2"));
        win.RunGui();
        win.Close();
    }

}
