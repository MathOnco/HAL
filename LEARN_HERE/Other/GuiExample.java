package LEARN_HERE.Other;

import HAL.Gui.*;

import static HAL.Util.RGB;

/**
 * Created by Rafael on 10/30/2017.
 */
public class GuiExample {
    public static void main(String[] args) {
        UIWindow win=new UIWindow("exampleMenu",true);
        win.AddCol(0,new UIBoolInput("boolean",false));
        win.AddCol(0,new UIDoubleInput("double",0.5,0,2));
        win.AddCol(0,new UIIntInput("int",3,0,5));
        win.AddCol(1,new UIStringInput("string","text"));
        win.AddCol(1,new UIComboBoxInput("options",0,new String[]{"option 1","option 2","option 3"}));
        win.AddCol(1,new UIFileChooserInput("file chooser","example.txt"));
        win.AddCol(0,new UIButton("Print",false,(event)->{
            System.out.println(win.GetBool("boolean")+"\n"+win.GetDouble("double")+"\n"+win.GetInt("int")+"\n"+win.GetString("string")+"\n"+win.GetInt("options")+"\n"+win.GetString("file chooser"));
        }).SetColor(RGB(1,0,0),RGB(0,0,1)));
        win.RunGui();
    }
}
