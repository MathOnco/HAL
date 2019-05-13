package Testing;


import Framework.GridsAndAgents.AgentGrid1D;
import Framework.GridsAndAgents.AgentPT1D;
import Framework.GridsAndAgents.AgentSQ1D;
import Framework.Gui.PlotLine;
import Framework.Gui.PlotWindow;
import Framework.Gui.UIPlot;
import Framework.Gui.UIWindow;
import Framework.Rand;
import Framework.Util;

class TestAgent1D extends AgentSQ1D<TestGrid1D> {}

class TestGrid1D extends AgentGrid1D<TestAgent1D> {
    Rand rng=new Rand(0);
    int[]moveHood=Util.GenHood1D(new int[]{-1,0,1});
    public TestGrid1D(int x) {
        super(x, TestAgent1D.class);
    }
}

class PopPlot extends PlotWindow {
    int ct;
    public PopPlot(int xPix, int yPix,int scale) {
        super(xPix, yPix,scale);
    }
    public void Plot(TestGrid1D g){
        PlotLine pl=new PlotLine(this,Util.CategorialColor(ct));
        ct++;
        for (int i = 0; i < g.xDim; i++) {
            pl.AddSegment(i,g.PopAt(i));
        }
    }
}


//todo: find equation for diffusivity of an abm
public class AgentTests {

    public static TestGrid1D GenBrownianDist1D(int xDim, int startX, int[]moveHood, int timesteps){
        TestGrid1D g=new TestGrid1D(xDim);
        int sum=0;
        for (int i = moveHood.length/2; i < moveHood.length; i++) {
            sum+=moveHood[i];
        }
        sum/=moveHood.length/2;
        for (int i = 0; i < 100000; i++) {
            g.NewAgentSQ(startX);
        }
        for (int i = 0; i < timesteps; i++) {
            for (TestAgent1D a : g) {
                int ct=a.MapHood(moveHood);
                a.MoveSQ(moveHood[g.rng.Int(ct)]);
            }
        }
        return g;
    }

    public static void BrownianTestOnLattice1D(UnitTester tester,int xDim,int timesteps,int popSize,int lenToCheck,double errTol){
        TestGrid1D grid = new TestGrid1D(xDim);
        for (int i = 0; i < popSize; i++) {
            grid.NewAgentSQ(grid.xDim / 2);
            //grid.NewAgentSQ(0);
        }
        for (int i = 0; i < timesteps; i++) {
            for (TestAgent1D agent : grid) {
                int opts=agent.MapHood(grid.moveHood);
                agent.MoveSQ(grid.moveHood[grid.rng.Int(opts)]);//avg vel. of 2/3
            }
        }
        int pad=(grid.xDim-lenToCheck)/2;
        for (int j = pad; j < grid.xDim-pad; j++) {
            System.out.println(j+","+Util.NormalDistPMF(grid.xDim/2,Math.sqrt(0.5*0.5*6),j)+","+grid.PopAt(j) * 1.0 / grid.Pop());
            //tester.AssertEqual("brownian motion distribution at " + j, CellPMF(grid.xDim/2,1.0/3.0,timesteps,j), grid.PopAt(j) * 1.0 / grid.Pop(), errTol);
        }

    }

    public static double CellPMF(double startPos, double avgMoveVel, int deltaT, double endPos){
        return Util.NormalDistPMF(startPos,Math.sqrt(0.5*deltaT*avgMoveVel),endPos);
    }

    public static void AddTests(UnitTester tester){
        tester.AddTest("Brownian Motion Test",()-> {
            BrownianTestOnLattice1D(tester,7,6,10000,7,0.1);
        });
    }

    public static void main(String[] args) {
       // GenBrownianDist1D(7,0,Util.GenHood1D(new int[]{0,1}),6);
        PopPlot res=new PopPlot(500,500,2);
        res.Plot(GenBrownianDist1D(40,20,Util.GenHood1D(new int[]{-1,0,1}),10));
        res.Plot(GenBrownianDist1D(40,20,Util.GenHood1D(new int[]{-2,-1,0,0,0,0,0,0,0,0,0,1,2}),10));
//        UnitTester tester=new UnitTester();
//
//        AddTests(tester);
//        tester.RunTests(false);
    }
}
