package Testing;


import Framework.GridsAndAgents.AgentGrid1D;
import Framework.GridsAndAgents.AgentPT1D;
import Framework.Rand;
import static Testing.UnitTester.*;

class TestAgent1D extends AgentPT1D<TestGrid1D> {}

class TestGrid1D extends AgentGrid1D<TestAgent1D> {
    double meanAgentPos;
    double stdDevAgentPos;
    public TestGrid1D(int x) {
        super(x, TestAgent1D.class);
    }
    void CalcAgentMetrics(){
        meanAgentPos=0;
        for (TestAgent1D agent : this) {
            meanAgentPos+=agent.Xpt();
        }
        meanAgentPos/=Pop();
        for (TestAgent1D agent : this) {
            double dev=agent.Xpt()-meanAgentPos;
            stdDevAgentPos+=dev*dev;
        }
        stdDevAgentPos=Math.sqrt(stdDevAgentPos/Pop());
    }
}

public class AgentTests {

    public static void AddTests(UnitTester tester){
        tester.AddTest("Brownian Motion Test",()->{
            Rand rng=new Rand();
            TestGrid1D grid=new TestGrid1D(2000);
            for (int i = 0; i < 1000; i++) {
                grid.NewAgentPT(grid.xDim/2);
            }
            for (int i = 0; i < 1000; i++) {
                for (TestAgent1D agent : grid) {
                    agent.MovePT(rng.Double()-0.5);//avg. velocity = 0.25
                }
                grid.CalcAgentMetrics();
                //AssertEqual("Mean Displacement",);
            }
        });
    }

    public static void main(String[] args) {
        UnitTester tester=new UnitTester();

        AddTests(tester);
        tester.RunTests(false);
    }
}
