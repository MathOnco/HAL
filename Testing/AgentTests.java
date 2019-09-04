package Testing;


import HAL.GridsAndAgents.*;
import HAL.Rand;
import HAL.Tools.MultinomialCalc;

//class TestAgent1D extends AgentSQ1D<TestGrid1D> {}
//
//class TestGrid1D extends AgentGrid1D<TestAgent1D> {
//    Rand rng=new Rand(0);
//    int[]moveHood=Util.GenHood1D(new int[]{-1,0,1});
//    public TestGrid1D(int x) {
//        super(x, TestAgent1D.class);
//    }
//}
//
//class PopPlot extends PlotWindow {
//    int ct;
//    public PopPlot(int xPix, int yPix,int scale) {
//        super(xPix, yPix,scale);
//    }
//    public void Plot(TestGrid1D g){
//        PlotLine pl=new PlotLine(this,Util.CategorialColor(ct));
//        ct++;
//        for (int i = 0; i < g.xDim; i++) {
//            pl.AddSegment(i,g.PopAt(i));
//        }
//    }
//}



//public void BrownianMotion(int[]hood,)

public class AgentTests {

    public static void Add1DTests(UnitTester tester){
        tester.AddTest("Movement1DNoWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentSQ1D> grid=new AgentGrid1D(100,AgentSQ1D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentSQ1D a : grid) {
                    a.MoveSafeSQ(a.Xsq()+1);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(i==grid.length-1){
                    tester.AssertEqual("Agents Stopped at boundary",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("Movement1DWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentSQ1D> grid=new AgentGrid1D(100,AgentSQ1D.class,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1050; i++) {
                for (AgentSQ1D a : grid) {
                    a.MoveSafeSQ(a.Xsq()+1);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(i==50){
                    tester.AssertEqual("Agents Stopped at position 50",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("BirthDeath1D",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentSQ1D> grid=new AgentGrid1D(10,AgentSQ1D.class,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 100; i++) {
                int ct=0;
                for (AgentSQ1D a : grid) {
                    if(ct<50){
                        a.Dispose();
                    }
                    else{
                        a.MoveSafeSQ(a.Xsq()+rng.Int(3)-2);//random movement
                        grid.NewAgentSQ(a.Xsq());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths",100,grid.Pop());
            }
        });
        tester.AddTest("Movement1DOffLNoWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentPT1D> grid=new AgentGrid1D(100,AgentPT1D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT1D a : grid) {
                    a.MoveSafePT(a.Xpt()+0.25);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(i==grid.length-1){
                    tester.AssertEqual("Agents Stopped at boundary",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("Movement1DOffLWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentPT1D> grid=new AgentGrid1D(100,AgentPT1D.class,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT1D a : grid) {
                    a.MoveSafePT(a.Xpt()+0.25);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(i==50){
                    tester.AssertEqual("Agents Stopped at position 50",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("BirthDeath1DOffL",()->{
            Rand rng=new Rand(0);
            AgentGrid1D<AgentPT1D> grid=new AgentGrid1D(10,AgentPT1D.class,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0);
            }
            for (int i = 0; i < 100; i++) {
                int ct=0;
                for (AgentPT1D a : grid) {
                    if(ct<50){
                        a.Dispose();
                    }
                    else{
                        a.MoveSafePT(a.Xpt()+rng.Double()-0.5);//random movement
                        grid.NewAgentPT(a.Xpt());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths",100,grid.Pop());
            }
        });
    }
    public static void Add2DTests(UnitTester tester){
        tester.AddTest("Movement2DNoWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid2D<AgentSQ2D> grid=new AgentGrid2D(100,100,AgentSQ2D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentSQ2D a : grid) {
                    a.MoveSafeSQ(a.Xsq()+1,a.Ysq()+1);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(i==grid.length-1){
                    tester.AssertEqual("Agents Stopped at boundary",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("Movement2DWrap",()->{
            Rand rng=new Rand(0);
            AgentGrid2D<AgentSQ2D> grid=new AgentGrid2D(100,100,AgentSQ2D.class,true,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1050; i++) {
                for (AgentSQ2D a : grid) {
                    a.MoveSafeSQ(a.Xsq()+1,a.Ysq()+1);
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.length; i++) {
                if(grid.ItoX(i)==50&&grid.ItoY(i)==50){
                    tester.AssertEqual("Agents Stopped at position 50,50",100,grid.PopAt(i));
                }
                else{
                    tester.AssertEqual("Agents Stopped everywhere else",0,grid.PopAt(i));
                }
            }
        });
        tester.AddTest("BirthDeath2D",()->{
            Rand rng=new Rand(0);
            AgentGrid2D<AgentSQ2D> grid=new AgentGrid2D(10,10,AgentSQ2D.class,true,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 100; i++) {
                int ct=0;
                for (AgentSQ2D a : grid) {
                    if(ct<50){
                        a.Dispose();
                    }
                    else{
                        a.MoveSafeSQ(a.Xsq()+rng.Int(3)-2,a.Ysq()+rng.Int(3)-2);//random movement
                        grid.NewAgentSQ(a.Xsq());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths",100,grid.Pop());
            }
        });
        tester.AddTest("Movement2DOffLNoWrap",()->{
            Rand rng=new Rand();
            AgentGrid2D<AgentPT2D> grid=new AgentGrid2D(100,100,AgentPT2D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0,0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT2D a : grid) {
                    a.MoveSafePT(a.Xpt()+0.25,a.Ypt()+0.25);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped on Boundary",100,grid.PopAt(grid.xDim-1,grid.yDim-1));
        });
        tester.AddTest("Movement2DOffLWrap",()->{
            Rand rng=new Rand();
            AgentGrid2D<AgentPT2D> grid=new AgentGrid2D(100,100,AgentPT2D.class,true,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0,0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT2D a : grid) {
                    a.MoveSafePT(a.Xpt()+0.25,a.Ypt()+0.25);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped at position 50,50",100,grid.PopAt(50,50));
        });
        tester.AddTest("BirthDeath2DffOL",()->{
            Rand rng=new Rand();
            AgentGrid2D<AgentPT2D> grid=new AgentGrid2D(10,10,AgentPT2D.class,true,true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0,0);
            }
            for (int i = 0; i < 100; i++) {
                int ct=0;
                for (AgentPT2D a : grid) {
                    if(ct<50){
                        a.Dispose();
                    }
                    else{
                        a.MoveSafePT(a.Xpt()+rng.Double()-0.5,a.Ypt()+rng.Double()-0.5);//random movement
                        grid.NewAgentPT(a.Xpt(),a.Ypt());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths",100,grid.Pop());
            }
        });
    }
    public static void Add3DTests(UnitTester tester) {
        tester.AddTest("Movement3DNoWrap", () -> {
            Rand rng = new Rand(0);
            AgentGrid3D<AgentSQ3D> grid = new AgentGrid3D(100, 100, 100, AgentSQ3D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentSQ3D a : grid) {
                    a.MoveSafeSQ(a.Xsq() + 1, a.Ysq() + 1, a.Zsq() + 1);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped on Boundary", 100, grid.PopAt(grid.xDim - 1, grid.yDim - 1, grid.zDim - 1));
        });
        tester.AddTest("Movement3DWrap", () -> {
            Rand rng = new Rand(0);
            AgentGrid3D<AgentSQ3D> grid = new AgentGrid3D(100, 100, 100, AgentSQ3D.class, true, true, true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 1050; i++) {
                for (AgentSQ3D a : grid) {
                    a.MoveSafeSQ(a.Xsq() + 1, a.Ysq() + 1, a.Zsq() + 1);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped at position 50,50", 100, grid.PopAt(50, 50, 50));
        });
        tester.AddTest("BirthDeath3D", () -> {
            Rand rng = new Rand(0);
            AgentGrid3D<AgentSQ3D> grid = new AgentGrid3D(10, 10, 10, AgentSQ3D.class, true, true, true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentSQ(0);
            }
            for (int i = 0; i < 100; i++) {
                int ct = 0;
                for (AgentSQ3D a : grid) {
                    if (ct < 50) {
                        a.Dispose();
                    } else {
                        a.MoveSafeSQ(a.Xsq() + rng.Int(3) - 2, a.Ysq() + rng.Int(3) - 2, a.Zsq() + rng.Int(3) - 2);//random movement
                        grid.NewAgentSQ(a.Xsq());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths", 100, grid.Pop());
            }
        });
        tester.AddTest("Movement3DOffLNoWrap", () -> {
            Rand rng = new Rand();
            AgentGrid3D<AgentPT3D> grid = new AgentGrid3D(100, 100, 100, AgentPT3D.class);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0, 0, 0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT3D a : grid) {
                    a.MoveSafePT(a.Xpt() + 0.25, a.Ypt() + 0.25, a.Zpt() + 0.25);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped on Boundary", 100, grid.PopAt(grid.xDim - 1, grid.yDim - 1, grid.zDim - 1));
        });
        tester.AddTest("Movement3DOffLWrap", () -> {
            Rand rng = new Rand();
            AgentGrid3D<AgentPT3D> grid = new AgentGrid3D(100, 100, 100, AgentPT3D.class, true, true, true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0, 0, 0);
            }
            for (int i = 0; i < 1000; i++) {
                for (AgentPT3D a : grid) {
                    a.MoveSafePT(a.Xpt() + 0.25, a.Ypt() + 0.25, a.Zpt() + 0.25);
                }
                grid.ShuffleAgents(rng);
            }
            tester.AssertEqual("Agents Stopped at position 50,50", 100, grid.PopAt(50, 50, 50));
        });
        tester.AddTest("BirthDeath3DffOL", () -> {
            Rand rng = new Rand();
            AgentGrid3D<AgentPT3D> grid = new AgentGrid3D(10, 10, 10, AgentPT3D.class, true, true, true);
            for (int i = 0; i < 100; i++) {
                grid.NewAgentPT(0, 0, 0);
            }
            for (int i = 0; i < 100; i++) {
                int ct = 0;
                for (AgentPT3D a : grid) {
                    if (ct < 50) {
                        a.Dispose();
                    } else {
                        a.MoveSafePT(a.Xpt() + rng.Double() - 0.5, a.Ypt() + rng.Double() - 0.5, a.Zpt() + rng.Double() - 0.5);//random movement
                        grid.NewAgentPT(a.Xpt(), a.Ypt(), a.Zpt());
                    }
                    ct++;
                }
                grid.ShuffleAgents(rng);
            }
            for (int i = 0; i < grid.xDim; i++) {
                tester.AssertEqual("equal number of births and deaths", 100, grid.Pop());
            }
        });
    }

        public static void AddPopulationTests(UnitTester tester){
        tester.AddTest("PopGrid1D diffusion",()->{
            PDEGrid1D pde=new PDEGrid1D(100);
            PopulationGrid1D popGrid=new PopulationGrid1D(100);
            MultinomialCalc mn=new MultinomialCalc(new Rand(0));
            pde.Set(pde.xDim/2,10000);
            popGrid.Set(popGrid.xDim/2,10000);
            pde.Update();
            popGrid.Update();
            for (int i = 0; i < 1000; i++) {
                pde.Diffusion(0.1);
                popGrid.Diffusion(0.1,mn);
                pde.Update();
                popGrid.Update();
            }
            for (int i = 0; i < popGrid.length; i++) {
                tester.AssertEqual("Pop vs Conc at position"+i,pde.Get(i),popGrid.Get(i),100);
            }
        });
        tester.AddTest("PopGrid2D diffusion",()->{
            PDEGrid2D pde=new PDEGrid2D(20,20);
            PopulationGrid2D popGrid=new PopulationGrid2D(20,20);
            MultinomialCalc mn=new MultinomialCalc(new Rand(0));
            pde.Set(pde.xDim/2,pde.yDim/2,10000);
            popGrid.Set(popGrid.xDim/2,popGrid.yDim/2,10000);
            pde.Update();
            popGrid.Update();
            for (int i = 0; i < 200; i++) {
                pde.Diffusion(0.1);
                popGrid.Diffusion(0.1,mn);
                pde.Update();
                popGrid.Update();
            }
            for (int i = 0; i < popGrid.length; i++) {
                tester.AssertEqual("Pop vs Conc at position"+i,pde.Get(i),popGrid.Get(i),100);
            }
        });
        tester.AddTest("PopGrid3D diffusion",()->{
            PDEGrid3D pde=new PDEGrid3D(10,10,10);
            PopulationGrid3D popGrid=new PopulationGrid3D(10,10,10);
            MultinomialCalc mn=new MultinomialCalc(new Rand(0));
            pde.Set(pde.xDim/2,pde.yDim/2,pde.zDim/2,10000);
            popGrid.Set(popGrid.xDim/2,popGrid.yDim/2,popGrid.zDim/2,10000);
            pde.Update();
            popGrid.Update();
            for (int i = 0; i < 100; i++) {
                pde.Diffusion(0.1);
                popGrid.Diffusion(0.1,mn);
                pde.Update();
                popGrid.Update();
            }
            for (int i = 0; i < popGrid.length; i++) {
                tester.AssertEqual("Pop vs Conc at position"+i,pde.Get(i),popGrid.Get(i),100);
            }
        });
    }

//    public static TestGrid1D GenBrownianDist1D(int xDim, int startX, int[]moveHood, int timesteps){
//        TestGrid1D g=new TestGrid1D(xDim);
//        int sum=0;
//        for (int i = moveHood.length/2; i < moveHood.length; i++) {
//            sum+=moveHood[i];
//        }
//        sum/=moveHood.length/2;
//        for (int i = 0; i < 100000; i++) {
//            g.NewAgentSQ(startX);
//        }
//        for (int i = 0; i < timesteps; i++) {
//            for (TestAgent1D a : g) {
//                int ct=a.MapHood(moveHood);
//                a.MoveSQ(moveHood[g.rng.Int(ct)]);
//            }
//        }
//        return g;
//    }
//
//    public static void BrownianTestOnLattice1D(UnitTester tester,int xDim,int timesteps,int popSize,int lenToCheck,double errTol){
//        TestGrid1D grid = new TestGrid1D(xDim);
//        for (int i = 0; i < popSize; i++) {
//            grid.NewAgentSQ(grid.xDim / 2);
//            //grid.NewAgentSQ(0);
//        }
//        for (int i = 0; i < timesteps; i++) {
//            for (TestAgent1D agent : grid) {
//                int opts=agent.MapHood(grid.moveHood);
//                agent.MoveSQ(grid.moveHood[grid.rng.Int(opts)]);//avg vel. of 2/3
//            }
//        }
//        int pad=(grid.xDim-lenToCheck)/2;
//        for (int j = pad; j < grid.xDim-pad; j++) {
//            System.out.println(j+","+Util.NormalDistPMF(grid.xDim/2,Math.sqrt(0.5*0.5*6),j)+","+grid.PopAt(j) * 1.0 / grid.Pop());
//            //tester.AssertEqual("brownian motion distribution at " + j, CellPMF(grid.xDim/2,1.0/3.0,timesteps,j), grid.PopAt(j) * 1.0 / grid.Pop(), errTol);
//        }
//
//    }
//
//    public static double CellPMF(double startPos, double avgMoveVel, int deltaT, double endPos){
//        return Util.NormalDistPMF(startPos,Math.sqrt(0.5*deltaT*avgMoveVel),endPos);
//    }

    public static void AddTests(UnitTester tester){
        Add1DTests(tester);
        Add2DTests(tester);
        Add3DTests(tester);
        AddPopulationTests(tester);
    }

    public static void main(String[] args) {
       // GenBrownianDist1D(7,0,Util.GenHood1D(new int[]{0,1}),6);
//        PopPlot res=new PopPlot(500,500,2);
//        res.Plot(GenBrownianDist1D(40,20,Util.GenHood1D(new int[]{-1,0,1}),10));
//        res.Plot(GenBrownianDist1D(40,20,Util.GenHood1D(new int[]{-2,-1,0,0,0,0,0,0,0,0,0,1,2}),10));
        UnitTester tester=new UnitTester();
//
        AddTests(tester);
        tester.RunTests(false);
    }
}
