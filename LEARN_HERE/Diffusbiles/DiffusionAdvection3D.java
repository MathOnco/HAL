package LEARN_HERE.Diffusbiles;

import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Gui.GridWindow;
import HAL.Util;

/**
 * Created by Rafael on 10/28/2017.
 */
public class DiffusionAdvection3D {
    public static void main(String[] args) {
        GridWindow win = new GridWindow("advection",20,20,10,true);
        PDEGrid3D grid=new PDEGrid3D(20,20,20,true,true,true);//last booleans are for wraparound
        grid.Set(grid.xDim/2,grid.yDim/2,grid.zDim/2,1);
        while(true){
            win.TickPause(100);
            grid.Advection(0.1,0,0.1,0);//advection
            grid.Diffusion(0.13);//diffusion
            grid.Update();
            win.DrawPDEGridXY(grid, (val)->(Util.HeatMapBGR(val*1000)));
            //win.DrawPDEGridXZ(grid, (val)->(Util.HeatMapBGR(val*1000)));//uncomment to view from different angles
            //win.DrawPDEGridYZ(grid, (val)->(Util.HeatMapBGR(val*1000)));
        }
    }
}
