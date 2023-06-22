package Testing.OldTests;

import HAL.Gui.OpenGL3DWindow;
import HAL.Rand;
import HAL.Util;

public class LightingTest {
    public static void main(String[] args) {
        if(OpenGL3DWindow.MakeMacCompatible(args)){
            return;
        }
        OpenGL3DWindow win=new OpenGL3DWindow(1000,1000,10,10,10);
        win.AddLight(Util.RGB(0.1,0.1,0.1),Util.RGB(1.0,1.0,1.0),-100,-100,-100);
        Rand rng=new Rand();
        int i=0;
        while(true){
            if(i==win.length+1)i=0;
            win.Clear(Util.BLACK);
            int xi=win.ItoX(i);
            int yi=win.ItoY(i);
            int zi=win.ItoZ(i);
//            win.AddLight(Util.RGB(0.1,0.1,0.1),Util.RGB(0.5,0.5,0.5),5.5,5.5,-10);
            win.ShineLight(5,5,-1);
            win.Line(xi+0.5,yi+0.5,zi+0.5,xi+0.5,yi+1,zi+0.5,Util.GREEN);
            for (int x = 0; x < 10; x+=4) {
                for (int y = 0; y < 10; y+=4) {
                    for (int z = 0; z < 10; z += 4) {
                        win.CubeLighting(x, x+1, y,y+1,z,z+1, Util.WHITE);
                    }
                }

            }
            win.Update();
            win.TickPause(100);
            i++;
        }
    }
}
