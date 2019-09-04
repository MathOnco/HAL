package Testing.OldTests;

import HAL.Gui.GridWindow;

import static HAL.Util.BLACK;
import static HAL.Util.WHITE;

public class StringTest {
    public static void main(String[] args) {
        GridWindow testWin=new GridWindow("strings",500,500,2);
        testWin.SetString("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",0,499,BLACK,WHITE);
    }
}
