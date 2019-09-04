package Testing.OldTests;

import HAL.Gui.OpenGL2DWindow;
import HAL.Gui.TickTimer;
import HAL.Util;

import static HAL.Util.RGB;

/**
 * Created by rafael on 5/29/17.
 */
public class Vis2DCircle {
    public static void main(String[] args) {
        OpenGL2DWindow vis = new OpenGL2DWindow("Test", 1000, 1000,50,50, true);
        float[] pts= Util.GenCirclePoints(1.0f,4);
        TickTimer trt=new TickTimer();
        float x=0;
        while(!vis.IsClosed()){
            vis.Clear(RGB(x, (float) 0, (float) 0));
            vis.FanShape((float) 10, (float) 10, (float) 1, pts,RGB((float) 1, (float) 1, (float) 1));
            vis.Update();
            x+=0.000001;
        }
        vis.Close();
    }
}
