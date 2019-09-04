package Testing.OldTests;

import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;

import static HAL.Util.CircleHood;
import static HAL.Util.HeatMapBRG;

/**
* Created by Rafael on 10/19/2017.
*/

//public class AdvectionSolver {
//
//}

public class ConvergenceTestAdvection {


   public static void main(String[] args) {
       int spaceScale = 2;
       int xDim = 50, yDim = 8;
       for (int k=0;k<4;k++) {
           spaceScale=2*spaceScale;
           GridWindow win = new GridWindow("diffusion ex", spaceScale*xDim, spaceScale*yDim, 64/spaceScale);
           PDEGrid2D diff = new PDEGrid2D(xDim*spaceScale, yDim*spaceScale, true, true);

           //initial condition here
           int[] circleCoords = CircleHood(true, 4 * spaceScale);
           int nPositions = diff.MapHood(circleCoords, diff.xDim / 2, diff.yDim / 2);
           for (int i = 0; i < nPositions; i++) {
               diff.Set(circleCoords[i], 1);
           }
           double[] xVels = new double[diff.length];
           double[] yVels = new double[diff.length];
           for (int x = 0; x < diff.xDim; x++) {
               for (int y = 0; y < diff.yDim; y++) {
                   //if(x>0.)
                   //xVels[diff.I(x,y)]=((x+0.1)*0.01)/xDim;
                   //xVels[diff.I(x,y)]=0.1*(1-Math.cos(Math.PI*(x-diff.xDim/2)/(diff.xDim))/2);
                   xVels[diff.I(x, y)] = 0.1;
//                if (x>3*diff.xDim/4) {
//                    xVels[diff.I(x, y)] = -0.1;
//                }

                   //yVels[diff.I(x, y)] = 0;
               }

           }
           for (int i = 0; i < 500; i++) {

               win.TickPause(10);
               //step condition here
               for (int j = 0; j < spaceScale; j++) {
                   diff.Advection(xVels, yVels);
               }
               System.out.println(diff.GetAvg());

               //draw
               for (int x = 0; x < win.xDim; x++) {
                   for (int y = 0; y < win.yDim; y++) {
                       win.SetPix(x, y, HeatMapBRG(diff.Get(x , y )));
                   }
               }
           }
       }

   }
}

