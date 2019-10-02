package LEARN_HERE.Other;

import static HAL.Util.*;

/**
 * Created by rafael on 7/4/17.
 */
public class ColorTest {
    public static void main(String[] args) {
        int color=RGB(1,0,0);
        System.out.println(ColorString(color));
        color=SetBlue(color,1);
        System.out.println(ColorString(color));
        color=SetRed256(color,0);
        System.out.println(ColorString(color));
        color=SetGreen256(color,GetBlue256(color));
        System.out.println(ColorString(color));
        color=SetAlpha256(color,0);
        System.out.println(ColorString(color));
    }
}
