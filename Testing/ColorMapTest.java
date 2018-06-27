package Testing;

import Framework.Gui.GridWindow;
import Framework.Util;

import static Framework.Util.RGB;

public class ColorMapTest {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,100,10);
        win.SetAll((x,y)->Util.ColorMap2D(x*1.0/win.xDim,y*1.0/win.yDim,RGB(1,0,0),RGB(0,1,0),RGB(0,0,1),RGB(0,0,0)));
    }
}
