package Testing;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;

import static Framework.Util.*;

/**
 * Created by Rafael on 10/19/2017.
 */
public class DiffusionExampleSimple {
    public static void main(String[] args) {
        int spaceScale = 25;
        int xDim = 50, yDim = 8;
        GridWindow win = new GridWindow("diffusion ex", xDim, yDim, 5);
        PDEGrid2D diff = new PDEGrid2D(xDim*spaceScale, yDim*spaceScale, true, true);

        //initial condition here
        int[] circleCoords = CircleHood(true, 4 * spaceScale);
        int nPositions = diff.MapHood(circleCoords, diff.xDim / 2, diff.yDim / 2);
        for (int i = 0; i < nPositions; i++) {
            diff.Set(circleCoords[i], 1);
        }
        double xVel=0.2;
        double yVel=0.1;
        for (int i = 0; i < 500; i++) {

            //win.TickPause(0);
            //step condition here
            for (int j = 0; j < spaceScale; j++) {
                diff.Advection(xVel, yVel);
                diff.Update();
            }
            //System.out.println(diff.GetAvg());

            //draw
            for (int x = 0; x < win.xDim; x++) {
                for (int y = 0; y < win.yDim; y++) {
                    win.SetPix(x, y, HeatMapBRG(diff.Get(x * spaceScale, y * spaceScale)));
                }
            }
        }
    }
}
