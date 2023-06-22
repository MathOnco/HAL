package Testing.OldTests;

import HAL.Gui.OpenGL3DWindow;
import HAL.Util;

public class CubeTest {
    public static void main(String[] args) {
        if(OpenGL3DWindow.MakeMacCompatible(args)){
            return;
        }
        OpenGL3DWindow win =new OpenGL3DWindow(500,500,10,10,10);
        while(!win.IsClosed()){
            win.ClearBox(Util.BLACK,Util.WHITE);
//            win.Cube(2,6,2,6,2,6,Util.RED);
            win.Voxel(1,1,1,Util.RED);
            win.Update();
        }
    }
}
