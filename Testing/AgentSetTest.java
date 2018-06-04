package Testing;

import Framework.GridsAndAgents.Agent0D;
import Framework.GridsAndAgents.AgentGrid0D;
import Framework.GridsAndAgents.AgentList;
import Framework.Tools.Timer;

class ListAgent extends Agent0D<AgentSetTest>{}

public class AgentSetTest extends AgentGrid0D<ListAgent>{
    AgentList<ListAgent> list =new AgentList<>();
    public AgentSetTest() {
        super(ListAgent.class);
    }

    public static void main(String[] args) {
        Timer t=new Timer();
        AgentSetTest g=new AgentSetTest();
        for (int i = 0; i < 100000; i++) {
            ListAgent a=g.NewAgent();
            g.list.AddAgent(a);
            g.list.RemoveAgent(a);
        }
        t.Lap("AgentSetTest");
    }
}
