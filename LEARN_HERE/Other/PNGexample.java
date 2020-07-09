package LEARN_HERE.Other;

import HAL.Gui.GridWindow;
import HAL.Util;

public class PNGexample{
    public static void main(String[] args){
        GridWindow win=new GridWindow(10,10,1);
        for(int i=0;i<win.length;i++){
            win.SetPix(i,Util.GreyScale(i*1.0/win.length));
        }
        win.ToPNG("test.png");
        win.Close();
    }
}
