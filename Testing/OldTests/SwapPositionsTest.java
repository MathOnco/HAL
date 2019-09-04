package Testing.OldTests;


import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GridWindow;

import static HAL.Util.RGB;

class SwapMe extends AgentSQ2Dunstackable<SwapPositionsTest>{
    int color;

}

public class SwapPositionsTest extends AgentGrid2D<SwapMe>{
    public SwapPositionsTest() {
        super(2, 1, SwapMe.class);
    }

    public static void main(String[] args) {
        SwapPositionsTest swap=new SwapPositionsTest();
        swap.NewAgentSQ(0).color=RGB(1,0,0);
        swap.NewAgentSQ(1).color=RGB(0,0,1);
        GridWindow win=new GridWindow("swapTest",2,1,100,true);
        while(true){
            win.DrawAgents(swap,(SwapMe c)->c.color);
            swap.GetAgent(0).SwapPosition(swap.GetAgent(1));
            win.TickPause(1000);
        }
    }
}
