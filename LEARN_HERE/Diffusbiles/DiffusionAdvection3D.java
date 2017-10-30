package LEARN_HERE.Diffusbiles;

import Framework.GridsAndAgents.PDEGrid3D;
import Framework.Gui.GridVisWindow;
import Framework.Utils;

/**
 * Created by Rafael on 10/28/2017.
 */
public class DiffusionAdvection3D {
    public static void main(String[] args) {
        GridVisWindow win = new GridVisWindow("advection",20,20,10,true);
        PDEGrid3D grid=new PDEGrid3D(20,20,20,true,true,true);//last booleans are for wraparound
        grid.Set(grid.xDim/2,grid.yDim/2,grid.zDim/2,1);
        while(true){
            win.TickPause(100);
            grid.Advection(0.1,0,0.1);//advection
            grid.Diffusion(0.13);//diffusion
            win.DrawGridDiffXY(grid, (val)->(Utils.HeatMapBGR(val*1000)));
            //win.DrawGridDiffXZ(grid, (val)->(Utils.HeatMapBGR(val*1000)));//uncomment to view from different angles
            //win.DrawGridDiffYZ(grid, (val)->(Utils.HeatMapBGR(val*1000)));
        }
    }
}
