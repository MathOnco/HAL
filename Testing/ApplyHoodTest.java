package Testing;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2D;

import static Framework.Util.*;

class HoodRat extends AgentSQ2D<AgentGrid2D>{
    int color=RED;
}

public class ApplyHoodTest {
    public static void main(String[] args) {
        AgentGrid2D<HoodRat> g=new AgentGrid2D<>(100,100,HoodRat.class,true,true);
        g.NewAgentSQ(g.xDim/2,g.yDim/2);
        //g.ApplyAgentsHood(new int[]{0,0},g.xDim/2,g.yDim/2,1,null,(a, i)->{a.color=BLUE;},false,false,null);
    }
}
