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
        int[] Is = new int[circleCoords.length / 2];
        int nPositions = diff.HoodToIs(circleCoords, Is, diff.xDim / 2, diff.yDim / 2);
        for (int i = 0; i < nPositions; i++) {
            diff.Set(Is[i], 1);
        }
        double[] xVels = new double[diff.length];
        double[] yVels = new double[diff.length];
        for (int x = 0; x < diff.xDim; x++) {
            for (int y = 0; y < diff.yDim; y++) {
                //if(x>0.)
                //xVels[diff.I(x,y)]=((x+0.1)*0.01)/xDim;
                xVels[diff.I(x, y)] = 0.5;
                yVels[diff.I(x, y)] = 0;
            }

        }
        for (int i = 0; i < 500; i++) {

            win.TickPause(0);
            //step condition here
            for (int j = 0; j < spaceScale; j++) {
                diff.Advection2ndLW(xVels, yVels);
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
