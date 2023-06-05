package Testing.OldTests;

import HAL.GridsAndAgents.Agent0D;
import HAL.GridsAndAgents.AgentGrid0D;

import java.util.ArrayList;
import java.util.List;


class AgentGrid0DAgent extends Agent0D<AgentGrid0D>{

}

public class AgetGrid0DTest {
    public static void main(String[] args) {
        AgentGrid0D<AgentGrid0DAgent> g=new AgentGrid0D<>(AgentGrid0DAgent.class);
        g.NewAgent();
        List<AgentGrid0DAgent> l=g.AllAgents();
        System.out.println(l.get(0));
    }
}
