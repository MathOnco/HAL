package Testing;

import Framework.Gui.Vis2DOpenGL;
import Framework.Gui.TickTimer;
import Framework.Utils;

import static Framework.Utils.RGB;

/**
 * Created by rafael on 5/29/17.
 */
public class Vis2DCircle {
    public static void main(String[] args) {
        Vis2DOpenGL vis = new Vis2DOpenGL(1000, 1000,50,50,"Test", true);
        float[] pts= Utils.GenCirclePoints(1.0f,4);
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
