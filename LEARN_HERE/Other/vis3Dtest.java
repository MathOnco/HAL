package LEARN_HERE.Other;

import HAL.Gui.OpenGL3DWindow;

import static HAL.Util.GenCirclePoints;
import static HAL.Util.RGB;

/**
 * Created by bravorr on 6/16/17.
 */
class Vis3Dtest {
    public static void main(String[] args) {
        OpenGL3DWindow v3d=new OpenGL3DWindow("testing", 640,480,20,10,10, true);
        float[] circ=GenCirclePoints(0.5f,100);
        while(!v3d.IsClosed()) {
            v3d.TickPause(0);
            v3d.ClearBox(RGB(0.5f, 0.5f, 0.5f),RGB(1,1,1));
            v3d.Circle(20,0,0,1,RGB(1,0,0));
            v3d.Circle(0,30,0,1,RGB(0,1,0));
            v3d.Circle(0,0,10,1,RGB(0,0,1));
            //v3d.CelSphere((double) 5, (double) 5, (double) 10, (double) 1,RGB((double) 1, (double) 0, (double) 0));
            //v3d.CelSphere((double) 0, (double) 0, (double) 10, (double) 1,RGB((double) 0, (double) 1, (double) 0));
            //v3d.CelSphere((double) 10, (double) 10, (double) 0, (double) 1,RGB((double) 0, (double) 0, (double) 1));
            v3d.Update();
        }
    }
}
