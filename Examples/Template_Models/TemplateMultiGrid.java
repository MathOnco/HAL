package Examples.Template_Models;

import HAL.GridsAndAgents.*;
import HAL.Gui.OpenGL3DWindow;
import HAL.Rand;
import HAL.Util;

class CellType1 extends AgentSQ3D {
    int exampleProperty;

}
class CellType2 extends SphericalAgent3D<CellType2,AgentGrid3D<CellType2>> {
    int exampleProperty;

}

public class TemplateMultiGrid {
    AgentGrid3D<CellType1> cells1;
    AgentGrid3D<CellType2> cells2;
    PDEGrid3D pde;
    OpenGL3DWindow win;
    Rand rng=new Rand();
    int[]hood=Util.VonNeumannHood3D(false);

    public TemplateMultiGrid(int x,int y, int z){
        cells1=new AgentGrid3D<>(x,y,z,CellType1.class);
        cells2=new AgentGrid3D<>(x,y,z,CellType2.class);
        pde=new PDEGrid3D(x,y,z);
        win=new OpenGL3DWindow(500,500,x,y,z);
    }
    public void StepAll() {
        for (CellType1 c1 : cells1) {
            c1.exampleProperty = Util.RGB(rng.Double(), rng.Double(), rng.Double());
            c1.MoveSQ(hood[rng.Int(c1.MapHood(hood))]);
        }
        for (CellType2 c2 : cells2) {
            c2.exampleProperty = Util.RGB(rng.Double(), rng.Double(), rng.Double());
            c2.radius = rng.Double();
            c2.xVel += rng.Double(2) - 1;
            c2.yVel += rng.Double(2) - 1;
            c2.zVel += rng.Double(2) - 1;
            c2.ForceMove();
            c2.ApplyFriction(0.0);
        }
        pde.Diffusion(0.1, (x, y, z) -> {
            if (x < 0) {
                return 1.0;
            } else {
                return 0.0;
            }
        });
        pde.Update();

    }
    public void DrawAll(){
        win.ClearBox(Util.BLACK,Util.WHITE);
        for (int x=0;x<pde.xDim;x++){
            for (int y = 0; y < pde.yDim; y++) {
                for (int z = 0; z < pde.zDim; z++) {
                    win.Circle(x+0.5,y+0.5,z+0.5,0.1,Util.HeatMapRGB(pde.Get(x,y,z)));
                }
            }
        }
        for (CellType1 c1 : cells1) {
            win.Circle(c1.Xpt(),c1.Ypt(),c1.Zpt(),0.5,c1.exampleProperty);
        }
        for (CellType2 c2 : cells2) {
            win.Circle(c2.Xpt(),c2.Ypt(),c2.Zpt(),c2.radius,c2.exampleProperty);
        }
        win.Update();
    }
    public static void main(String[] args) {
        TemplateMultiGrid grids=new TemplateMultiGrid(11,11,11);
        grids.cells1.NewAgentSQ(grids.cells1.xDim/2,grids.cells1.yDim/2,grids.cells1.zDim/2);
        grids.cells2.NewAgentSQ(grids.cells2.xDim/2,grids.cells2.yDim/2,grids.cells2.zDim/2);

        while(true){
            grids.StepAll();
            grids.DrawAll();
            grids.win.TickPause(0);
        }
    }
}
