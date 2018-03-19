package Testing;

import Framework.GridsAndAgents.Agent0D;
import Framework.GridsAndAgents.AgentGrid0D;
import Framework.GridsAndAgents.AgentList;
import Framework.Tools.PerformanceTimer;

class ListAgent extends Agent0D<AgentSetTest>{}

public class AgentSetTest extends AgentGrid0D<ListAgent>{
    AgentList<ListAgent> list =new AgentList<>();
    public AgentSetTest() {
        super(ListAgent.class);
    }

    public static void main(String[] args) {
        PerformanceTimer pt=new PerformanceTimer();
        pt.Start("AgentSetTest");
        AgentSetTest g=new AgentSetTest();
        for (int i = 0; i < 100000; i++) {
            ListAgent a=g.NewAgent();
            g.list.AddAgent(a);
            g.list.RemoveAgent(a);
        }
        pt.Stop("AgentSetTest");
    }
}
