package Testing;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Rand;
import Framework.Tools.PerformanceTimer;
import Framework.Util;

import java.util.ArrayList;
import java.util.PrimitiveIterator;


public class IterationTest {
    public static void main(String[] args) {
        AgentGrid2D<AgentSQ2Dunstackable> g=new AgentGrid2D<AgentSQ2Dunstackable>(100,100,AgentSQ2Dunstackable.class);
        int[]testHood= Util.RectangleHood(true,90,90);
        ArrayList<AgentSQ2Dunstackable> fair=new ArrayList<>();
        Rand rn=new Rand();
        PerformanceTimer pt=new PerformanceTimer();
        int[]testIs=new int[100*100];
        int[]storage=new int[100*100];
        for (int i = 0; i < g.length; i++) {
            g.NewAgentSQ(i);
        }
        pt.Start("old way");
        for (int i = 0; i < 10000; i++) {
            int num=g.HoodToIs(testHood,testIs,rn.Int(100),rn.Int(100));
            for (int j = 0; j < num; j++) {
                g.GetAgents(fair,j);
            }
            int len=fair.size();
            for (int j = 0; j < len; j++) {
                AgentSQ2Dunstackable a=fair.get(j);
                storage[a.Isq()]=a.Xsq();

            }
            fair.clear();
        }
        pt.Stop("old way");
        pt.Start("new way");
        for (int i = 0; i < 10000; i++) {
            g.ApplyAgentsHood(testHood,rn.Int(100),rn.Int(100),(a,ct)->storage[a.Isq()]=a.Xsq());
        }
        pt.Stop("new way");
        pt.Start("newer way");
        for (int i = 0; i < 10000; i++) {
            for(AgentSQ2Dunstackable a:g.IterAgents(testHood,rn.Int(100),rn.Int(100))){
                storage[a.Isq()]=a.Xsq();
            }
        }
        pt.Stop("newer way");
    }
}
