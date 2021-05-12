package Testing.OldTests;

import HAL.GridsAndAgents.PopulationGrid3D;
import HAL.Rand;

public class PopGrid3DTest {
    public static void main(String[] args){
        PopulationGrid3D grid=new PopulationGrid3D(10,10,10);
        Rand rng=new Rand();
        while(true) {
            for (int i = 0; i < 2; i++) {
                grid.Add(rng.Int(grid.length), 1);
            }
            grid.Update();
            for (int i = 0; i < grid.length; i++) {
//                if(rng.Int(2)==0) {
                    grid.Add(i, -grid.Get(i));
//                }
                }
            grid.Update();
            if(grid.usingSparseIndices){
                grid.usingSparseIndices=false;
            }else{
                grid.usingSparseIndices=true;
                grid.SetupSparseIndices();
            }
//            int ct = 0;
//            for (int i : grid) {
//                ct++;
//            }
            System.out.println(grid.OccupiedArea());
        }
    }
}
