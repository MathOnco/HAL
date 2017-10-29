package Examples.SKELETON_EXAMPLES;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentSQ2D;
import Framework.Gui.GridVisWindow;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

import java.util.Random;

import static Examples.SKELETON_EXAMPLES.OnLatticeDemo.*;
//static import allows us to use color names inside agent class even though they are defined in the grid class

/**
 * Created by Rafael on 10/28/2017.
 */

//EVERYTHING IS BASICALLY THE SAME IN 3D
class DemoAgentOnLattice extends AgentSQ2D<OnLatticeDemo> {//must feed grid class type
    int color;
    void Init(int color){
        this.color=color;
    }
    void Move(GuiGridVis vis, int dispX, int dispY){//the GridVisWindow is also a GuiGridVis, we pass it to update the drawing
        vis.SetPix(Isq(),BLACK);
        MoveSafeSQ(Xsq()+dispX,Ysq()+dispY);//MoveSafe uses wraparound/will stop the agent if it reaches the edge of the domain
        vis.SetPix(Isq(),RED);
    }
    //Use HoodToLocalIs(mooreHood,localIs), HoodToEmptyIs(mooreHood,localIs), or HoodToOccupiedIs(mooreHood,localIs) to set localIs array with indices surrounding agent
    //then use G().GetAgent with the localIs array to find nearby agents on this grid or other grids

    //can also use Isq() to get the index of the square that the agent occupies, for overlap interactions with other agent or diffusible grids
}

public class OnLatticeDemo extends AgentGrid2D<DemoAgentOnLattice> {//must feed agent class type
    final static int RED=Utils.RGB(1,0,0),BLACK=Utils.RGB(0,0,0);
    int[]mooreHood= Utils.MooreHood(false);//neighborhood coordinate array [x1,y1,x2,y2...]
    int[]localIs=new int[mooreHood.length/2];//indices array [i1,i2...]
    Random rn;
    public OnLatticeDemo(int x, int y, Random rn) {
        super(x, y, DemoAgentOnLattice.class,true,true);//class argument required for the NewAgent functions to work
        //booleans at the end of super call state whether wraparound should occur on the left and right/up and down edges
        this.rn=new Random();
    }

    public static void main(String[] args) {
        int x=10,y=10,scaleFactor=10;
        OnLatticeDemo grid=new OnLatticeDemo(x,y,new Random(0));//seed ensures determinism, remove number argument for stochastic
        GridVisWindow win=new GridVisWindow("skeleton",x,y,scaleFactor,true,true);//set the last boolean to false to deactivate the gui for efficiency

        //INITIALIZATION

        grid.NewAgentSQ(x/2,y/2).Init(RED);
        //be sure to use IncTick after initialization to setup the agents for iteration
        grid.IncTick();

        //RUN LOOP

        while(true){
            win.TickPause(1000);//1 second pause between ticks
            for (DemoAgentOnLattice agent2D : grid) {
                win.SetPix(agent2D.Isq(),agent2D.color);
                agent2D.Move(win,1,0);
            }
            grid.CleanShuffInc(grid.rn);
            //Most common end tick function, composed of the 3 functions below
            //grid.CleanAgents() Clean speeds up for looping over agents
            //grid.ShuffleAgents(grid.rn) Shuffle randomizes the order of iteration
            //grid.IncTick() IncTick increments the tick
        }
    }
}
