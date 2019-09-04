package Testing.OldTests;

import HAL.Gui.GridWindow;
import HAL.Util;

public class HsLuvTest {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(1000,1000);
        for (int i = 0; i < win.xDim; i++) {
            for (int j = 0; j < win.yDim; j++) {
                win.SetPix(i,j, Util.CbCrPlaneColor(i*1.0/(win.xDim-1),j*1.0/(win.yDim-1)));
            }
        }
    }
}
