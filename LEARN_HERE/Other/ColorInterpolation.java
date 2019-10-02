package LEARN_HERE.Other;

import HAL.Gui.GridWindow;
import HAL.Gui.UIGrid;

import static HAL.Util.Interpolate2D;
import static HAL.Util.RGB256;

public class ColorInterpolation {
    static double[]c1=new double[]{0,113.9850,188.9550};
    static double[]c2=new double[]{216.75,82.875,24.99};
    static double[]c3=new double[]{236.895,176.97,31.875};
    static double[]c4=new double[]{125.97,46.92,141.78};
    public static double InterpComponent(UIGrid colorMe, int x, int y, int rgbIndex){
        return Interpolate2D(x*1.0/colorMe.xDim, y*1.0/colorMe.yDim, c2[rgbIndex],c4[rgbIndex],c1[rgbIndex],c3[rgbIndex]);
    }
    public static void ColorPix(UIGrid colorMe, int x, int y){
        colorMe.SetPix(x,y,RGB256((int)InterpComponent(colorMe,x,y,0),(int)InterpComponent(colorMe,x,y,1),(int)InterpComponent(colorMe,x,y,2)));
    }
    public static void main(String[] args) {
        GridWindow colorDisp=new GridWindow(100,100,5);
        for (int x = 0; x < colorDisp.xDim; x++) {
            for (int y = 0; y < colorDisp.yDim; y++) {
                ColorPix(colorDisp,x,y);
            }
        }
        colorDisp.ToPNG("test.png");
    }
}
