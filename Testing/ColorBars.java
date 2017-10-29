package Testing;

import Framework.Gui.GridVisWindow;
import static Framework.Utils.*;

/**
 * Created by Rafael on 10/13/2017.
 */
public class ColorBars {
    public static void main(String[] args) {
        GridVisWindow win=new GridVisWindow("Colors",100,6,10);
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 100; j++) {
                switch (i){
                    case 0:
                        win.SetPix(j,i,HeatMapRGB(j*1.0/100));
                        break;
                    case 1:
                        win.SetPix(j,i,HeatMapRBG(j*1.0/100));
                        break;
                    case 2:
                        win.SetPix(j,i,HeatMapGRB(j*1.0/100));
                        break;
                    case 3:
                        win.SetPix(j,i,HeatMapGBR(j*1.0/100));
                        break;
                    case 4:
                        win.SetPix(j,i,HeatMapBRG(j*1.0/100));
                        break;
                    case 5:
                        win.SetPix(j,i,HeatMapBGR(j*1.0/100));
                        break;
                }
            }
        }
    }
}
