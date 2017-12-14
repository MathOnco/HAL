package Testing;

import Framework.Gui.Window2DOpenGL;
import Framework.Gui.TickTimer;
import Framework.Util;

import static Framework.Util.RGB;

/**
 * Created by rafael on 5/29/17.
 */
public class Vis2DCircle {
    public static void main(String[] args) {
        Window2DOpenGL vis = new Window2DOpenGL("Test", 1000, 1000,50,50, true);
        float[] pts= Util.GenCirclePoints(1.0f,4);
        TickTimer trt=new TickTimer();
        float x=0;
        while(!vis.CheckClosed()){
            vis.Clear(RGB(x, (float) 0, (float) 0));
            vis.FanShape((float) 10, (float) 10, (float) 1, pts,RGB((float) 1, (float) 1, (float) 1));
            vis.Show();
            x+=0.000001;
        }
        vis.Dispose();
    }
}
