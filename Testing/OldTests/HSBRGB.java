package Testing.OldTests;
import static HAL.Util.*;
/**
 * Created by Rafael on 10/25/2017.
 */
public class HSBRGB {
    public static void main(String[] args) {
        float[]hsb=new float[3];
        int color=RGB(1,0.5,0);
        System.out.println(ColorString(color));
        ColorToHSB(color,hsb);
        System.out.println(ArrToString(hsb,","));
        int color2=HSBColor(hsb[0],1,1);
        System.out.println(ColorString(color2));
    }
}
