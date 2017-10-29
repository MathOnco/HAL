package Testing;

import Framework.Gui.Vis3DOpenGL;

import static Framework.Utils.*;

/**
 * Created by bravorr on 6/16/17.
 */
class Vis3Dtest {
    public static void main(String[] args) {
        Vis3DOpenGL v3d=new Vis3DOpenGL(640,480,10,10,10,"testing", true);
        float[] circ=GenCirclePoints(0.5f,100);
        while(!v3d.CheckClosed()) {
            v3d.TickPause(0);
            v3d.Clear(RGB(0.5f, 0.5f, 0.5f));
            v3d.CelSphere((double) 5, (double) 5, (double) 10, (double) 1,RGB((double) 1, (double) 0, (double) 0));
            v3d.CelSphere((double) 0, (double) 0, (double) 10, (double) 1,RGB((double) 0, (double) 1, (double) 0));
            v3d.CelSphere((double) 10, (double) 10, (double) 0, (double) 1,RGB((double) 0, (double) 0, (double) 1));
            v3d.Show();
        }
    }
}
