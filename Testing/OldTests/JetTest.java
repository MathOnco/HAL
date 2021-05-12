package Testing.OldTests;

import HAL.Gui.GridWindow;

import static HAL.Util.HeatMapJet;
import static HAL.Util.HeatMapParula;

public class JetTest {
    public static void main(String[] args) {
        GridWindow win=new GridWindow(100,20,10);
        for (int i = 0; i < win.xDim; i++) {
            for (int j = 0; j < win.yDim; j++) {
                //win.SetPix(i, j, HeatMapJet(i, 0, win.xDim-1));
                win.SetPix(i, j, HeatMapParula(i, 0, win.xDim-1));
            }
        }
    }
}
