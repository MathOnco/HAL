package Testing;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Util;

/**
 * Created by Rafael on 11/17/2017.
 */
public class PDECoarseTest {
    public static void main(String[] args) {
        int x=100,y=100,visScale=4,coarseScale=4;
        GridWindow w1=new GridWindow("normal",x,y,visScale);
        GridWindow w2=new GridWindow("coarse",x/coarseScale,y/coarseScale,visScale*coarseScale);
        PDEGrid2D g1=new PDEGrid2D(x,y);
        PDEGrid2DCoarse g2 = new PDEGrid2DCoarse(x/coarseScale,y/coarseScale,coarseScale);
        for (int i = 0; i < 10000; i++) {
            g2.SetPartial(x/2,y/2,1,1);
            w2.DrawGridDiff(g2.grid, Util::HeatMapRBG);
            g2.Diffusion(0.25);
            g1.Set(x/2,y/2,1);
            w1.DrawGridDiff(g1, Util::HeatMapRBG);
            g1.Diffusion(0.25);
            System.out.println(g1.GetAvg()+","+g2.grid.GetAvg());
        }
    }
}
