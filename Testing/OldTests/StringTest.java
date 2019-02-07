package Testing.OldTests;

import Framework.Gui.GridWindow;

import static Framework.Util.BLACK;
import static Framework.Util.WHITE;

public class StringTest {
    public static void main(String[] args) {
        GridWindow testWin=new GridWindow("strings",500,500,2);
        testWin.SetString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",0,499,BLACK,WHITE);
    }
}
