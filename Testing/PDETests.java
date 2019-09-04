package Testing;

import HAL.GridsAndAgents.PDEGrid1D;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Interfaces.*;

import static Testing.UnitTester.*;

public class PDETests {

    static void SteadyStateAssertionCheck1D(String assertID, PDEGrid1D grid, double steadyState, double errorTol, VoidFunction SetState, DoubleToDouble AnswerFn) {
        double dif;
        do {
            SetState.Execute();
            dif = grid.MaxDelta();
            grid.Update();
        } while (dif > steadyState);
        for (int i = 0; i < grid.xDim; i++) {
            AssertEqual(assertID,AnswerFn.Eval(i + 0.5), grid.Get(i), errorTol);
        }
    }
    static void SteadyStateAssertionCheck2D(String assertID,PDEGrid2D grid, double steadyState, double errorTol, VoidFunction SetState, Doubles2DDouble AnswerFn) {
        double dif;
        do {
            SetState.Execute();
            dif = grid.MaxDelta();
            grid.Update();
        } while (dif > steadyState);
        for (int x = 0; x < grid.xDim; x++) {
            for (int y = 0; y < grid.yDim; y++) {
                AssertEqual(assertID,AnswerFn.GenDouble(x + 0.5, y + 0.5), grid.Get(x, y), errorTol);
            }
        }
    }
    static void SteadyStateAssertionCheck3D(String assertID, PDEGrid3D grid, double steadyState, double errorTol, VoidFunction SetState, Doubles3DDouble AnswerFn) {
        double dif;
        do {
            SetState.Execute();
            dif = grid.MaxDelta();
            grid.Update();
        } while (dif > steadyState);
        for (int x = 0; x < grid.xDim; x++) {
            for (int y = 0; y < grid.yDim; y++) {
                for (int z = 0; z < grid.zDim; z++) {
                    AssertEqual(assertID,AnswerFn.GenDouble(x + 0.5, y + 0.5, z + 0.5), grid.Get(x, y, z), errorTol);
                }
            }
        }
    }

    //zero flux boundary, and add source value, then diffuse

    public static void AddTests(UnitTester tester){

        /**
         * this test will resolve to a steady gradient between boundary conditions 0 and 1
         */
        tester.AddTest("1D Diffusion",() -> {
            PDEGrid1D grid = new PDEGrid1D(5);
            SteadyStateAssertionCheck1D("",grid, 0.0001, 0.01, () -> {
                grid.Diffusion(0.1, (x) -> {
                    if (x == -1) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
            }, x -> x*1.0/5);
        });

        /**
         * this test will resolve to a constant value of 1
         */
        tester.AddTest("1D Advection",()->{
            PDEGrid1D grid = new PDEGrid1D(5);
            SteadyStateAssertionCheck1D("",grid, 0.0001, 0.01, () -> {
                grid.Advection(0.1,1);
            }, x->1);
        });


        /**
         * this test will resolve to a steady gradient between boundary conditions 0 and 1 along X
         */
        tester.AddTest("2D Diffusion",() -> {
            PDEGrid2D grid = new PDEGrid2D(5,6);
            SteadyStateAssertionCheck2D("",grid, 0.0001, 0.01, () -> {
                grid.Diffusion(0.1, (x,y) -> {
                    if(y==-1){
                        return grid.Get(x,y+1);
                    }
                    else if(y==grid.yDim){
                        return grid.Get(x,y-1);
                    }
                    else if (x == -1) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
            }, (x,y) -> x*1.0/5);
        });
        /**
         * this test will resolve to a constant value of 1 everywhere, diffusing in from the x boundary
         */
        tester.AddTest("2D Advection",()->{
            PDEGrid2D grid = new PDEGrid2D(5,6);
            SteadyStateAssertionCheck2D("",grid, 0.0001, 0.01, () -> {
                grid.Advection(0.1,0,1);
            }, (x,y)->1);
        });


        /**
         * this test will resolve to a steady gradient between boundary conditions 0 and 1 along X
         */
        tester.AddTest("3D Diffusion",() -> {
            PDEGrid3D grid = new PDEGrid3D(5,6,7);
            SteadyStateAssertionCheck3D("",grid, 0.0001, 0.01, () -> {
                grid.Diffusion(0.1, (x,y,z) -> {
                    if(y==-1){
                        return grid.Get(x,y+1,z);
                    }
                    else if(y==grid.yDim){
                        return grid.Get(x,y-1,z);
                    }
                    else if(z==-1){
                        return grid.Get(x,y,z+1);
                    }
                    else if(z==grid.zDim){
                        return grid.Get(x,y,z-1);
                    }
                    else if (x == -1) {
                        return 0;
                    } else {
                        return 1;
                    }
                });
            }, (x,y,z) -> x*1.0/5);
        });

        /**
         * this test will resolve to a constant value of 1 everywhere, diffusing in from the x boundary
         */
        tester.AddTest("3D Advection",()->{
            PDEGrid3D grid = new PDEGrid3D(5,6,7);
            SteadyStateAssertionCheck3D("",grid, 0.0001, 0.01, () -> {
                grid.Advection(0.1,0,0,1);
            }, (x,y,z)->1);
        });
    }

    public static void main(String[] args) {
        UnitTester tester = new UnitTester();
        AddTests(tester);
        tester.RunTests(false);
    }
}

