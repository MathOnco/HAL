package Testing.OldTests;

import Framework.Gui.GridWindow;

import static Framework.Util.HeatMapJet;

public class JetTest {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,20,10);
        for (int i = 0; i < win.xDim; i++) {
            for (int j = 0; j < win.yDim; j++) {
                win.SetPix(i, j, HeatMapJet(i, 0, win.xDim-1));
            }
        }
    }
}
