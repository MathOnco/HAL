package LEARN_HERE.Diffusibles;

import HAL.GridsAndAgents.PDEGrid3D;

/**
 * Created by Rafael on 10/29/2017.
 */
public class Gradient3D {
    public static void main(String[] args) {
        int xD=10,yD=10,zD=10;
        PDEGrid3D diff=new PDEGrid3D(xD,yD,zD);
            for (int z = 0; z < diff.zDim; z++) {
            double setVal=z*1.0/diff.xDim;
                for (int y = 0; y < diff.yDim; y++) {
        for (int x = 0; x < diff.xDim; x++) {
                        diff.Set(x, y,z, setVal);
                    }
                }
            }
            diff.Update();
        System.out.println("x gradient:"+diff.GradientX(5,5,5));
        System.out.println("y gradient:"+diff.GradientY(5,5,5));
        System.out.println("z gradient:"+diff.GradientZ(5,5,5));
    }
}
