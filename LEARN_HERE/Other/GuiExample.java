package LEARN_HERE.Other;

import Framework.Gui.*;

import static Framework.Util.RGB;

/**
 * Created by Rafael on 10/30/2017.
 */
public class GuiExample {
    public static void main(String[] args) {
        GuiWindow win=new GuiWindow("exampleMenu",true);
        win.AddCol(0,new GuiBoolField("boolean",false));
        win.AddCol(0,new GuiDoubleField("double",0.5,0,2));
        win.AddCol(0,new GuiIntField("int",3,0,5));
        win.AddCol(1,new GuiStringField("string","text"));
        win.AddCol(1,new GuiComboBoxField("options",0,new String[]{"option 1","option 2","option 3"}));
        win.AddCol(1,new GuiFileChooserField("file chooser","example.txt"));
        win.AddCol(0,new GuiButton("Print",false,(event)->{
            System.out.println(win.GetBool("boolean")+"\n"+win.GetDouble("double")+"\n"+win.GetInt("int")+"\n"+win.GetString("string")+"\n"+win.GetInt("options")+"\n"+win.GetString("file chooser"));
        }).SetColor(RGB(1,1,1),RGB(0,0,0)));
        win.RunGui();
    }
}
