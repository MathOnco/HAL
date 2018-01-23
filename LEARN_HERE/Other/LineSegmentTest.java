package LEARN_HERE.Other;

import Framework.Gui.Window2DOpenGL;
import Framework.Gui.Window3DOpenGL;

import static Framework.Util.RGB;

public class LineSegmentTest {
    public static void main(String[] args) {
        Window3DOpenGL win=new Window3DOpenGL("segment",500,500,10,10,10);
        while(!win.CheckClosed()) {
            win.Clear(RGB(0,0,0));
            win.Line(0, 0,0, 10, 10,10, RGB(1, 0, 0));
            //win.LineStrip(new double[]{1,2,3,4},new double[]{4,6,7,4},RGB(0,0,1));
            win.Show();
        }
    }
}
