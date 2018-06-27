package LEARN_HERE.Diffusbiles;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;

/**
 * Created by Rafael on 10/29/2017.
 */
public class Gradient2D {
    public static void main(String[] args) {
        int xD=10,yD=10;
        UIGrid vis=new UIGrid(xD,yD,10);
        PDEGrid2D diff=new PDEGrid2D(xD,yD);
            for (int y = 0; y < diff.yDim; y++) {
                double setVal=y*1.0/diff.xDim;
                for (int x = 0; x < diff.xDim; x++) {
                    diff.Set(x, y, setVal);
                }
            }
        System.out.println("y gradient:"+diff.GradientY(5,5));
        System.out.println("x gradient:"+diff.GradientX(5,5));
    }
}
