package Testing.OldTests;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentList;
import Framework.GridsAndAgents.AgentSQ2D;
import Framework.Rand;

class Cell2 extends AgentSQ2D<AgentGrid2D> {

}

public class AgentListTest {
    public static void main(String[] args) {
        Rand rng=new Rand();
        AgentGrid2D<Cell2> cells = new AgentGrid2D<Cell2>(50,50,Cell2.class);
        AgentList<Cell2> cellList = new AgentList<>();
        cellList.AddAgent(cells.NewAgentSQ(0));
        cellList.AddAgent(cells.NewAgentSQ(0));
        cellList.AddAgent(cells.NewAgentSQ(0));
        cellList.AddAgent(cells.NewAgentSQ(0));
        cells.RandomAgent(rng).Dispose();
        cellList.RandomAgent(rng);
    }
}
