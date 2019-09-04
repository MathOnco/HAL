package Testing.OldTests;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.Gui.GridWindow;

import static HAL.Util.*;

/**
 * Created by Rafael on 10/19/2017.
 */
public class DiffusionExampleSimple {
    public static void main(String[] args) {
        //int spaceScale = 10;
        int xDim = 500, yDim = 500;
        GridWindow win = new GridWindow("diffusion ex", xDim, yDim,1);
        PDEGrid2D diff = new PDEGrid2D(xDim, yDim, true, true);
        Grid2Ddouble xVels= new Grid2Ddouble(diff.xDim,diff.yDim);
        Grid2Ddouble yVels= new Grid2Ddouble(diff.xDim,diff.yDim);
        for (int x = 0; x < diff.xDim; x++) {
            for (int y = 0; y < diff.yDim; y++) {
//                if(x>diff.xDim/2){
//                    xVels.Set(x,y,-0.5);
//                }
//                else{
//                    xVels.Set(x,y,0.5);
//                }
//                if(y>diff.yDim/2){
//                    yVels.Set(x,y,-0.5);
//                }
//                else{
//                    yVels.Set(x,y,0.5);
//                }
                if(y>=x&&diff.yDim-y>=x){
                    yVels.Set(x,y,0.2);
                }
                if(x>=y&&diff.yDim-y>=x){
                    xVels.Set(x,y,-0.2);
                }
                if(x>y&&diff.yDim-y<x){
                    yVels.Set(x,y,-0.2);
                }
                if(x<y&&diff.yDim-y<x){
                    xVels.Set(x,y,0.2);
                }
            }
        }

        //initial condition here
        int[] circleCoords = CircleHood(true, 100 );
        int nPositions = diff.MapHood(circleCoords, diff.xDim/3, diff.yDim/3);
        for (int i = 0; i < nPositions; i++) {
            diff.Set(circleCoords[i], 1);
        }
        for (int i = 0; i < 1000000; i++) {
            win.TickPause(10);
            //step condition here
                diff.Advection(xVels, yVels);
                diff.Update();
            System.out.println(diff.GetAvg());

            //draw
            for (int x = 0; x < win.xDim; x++) {
                for (int y = 0; y < win.yDim; y++) {
                    win.SetPix(x, y, HeatMapBRG(diff.Get(x, y)));
                }
            }
        }
    }
}
