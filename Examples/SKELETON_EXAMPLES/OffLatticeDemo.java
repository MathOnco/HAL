package Examples.SKELETON_EXAMPLES;

/**
 * Created by Rafael on 10/28/2017.
 */

import Framework.Extensions.SphericalAgent2D;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Gui.GridVisWindow;
import Framework.Gui.Vis2DOpenGL;
import Framework.Utils;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Rafael on 10/28/2017.
 */

//EVERYTING IS BASICALLY THE SAME IN 3D
class DemoAgentOffLattice extends SphericalAgent2D<DemoAgentOffLattice,OffLatticeDemo> {//must feed type of agent and grid in this case
    int color;
    void Init(int color,double radius){
        this.color=color;
        this.radius=radius;//radius is an internal state variable
    }

    void Step1(){
        SumForces(radius*2,G().neighbors,this::ForceResponse);
        //Use SumForces(neighbors,ForceResponse,radius*2) to set xVel and yVel with a trajectory to resolve collisions
        //after that, neighbors will contain all agents that are at least within radius*2 away from the agent
        //some of the agents in the neighbors arraylist will technically be further away, this is done for efficiency.
        //use Utils.DistSquared to separate those that are too far for neighborhood interactions.
        //you can also use G().agentsInRad(neighbors) to get the set of neighbors without the force calculation
        //of course you can also use neighborhoods or Isq() for interaction as presented in the OnLattice SKELETON_EXAMPLES Example
        xVel+=0.1;//we add some velocity to give the agent an internal drive to move
    }
    void Step2(){
        ApplyFriction(G().FRICTION);//equivalent to: xVel*=G().FRICTION; yVel*=G().FRICTION;
        ForceMove();
    }
    double ForceResponse(double overlapDistance){
        if(overlapDistance>0){
            return 0.1;//a constant force response, not very realistic
        }
        return 0;//if there is no overlap, there is no separation force
    }

}

public class OffLatticeDemo extends AgentGrid2D<DemoAgentOffLattice> {//must feed agent type
    final static int RED= Utils.RGB(1,0,0),BLACK=Utils.RGB(0,0,0);
    double FRICTION=0.5;
    double CELL_RADIUS =0.5;
    ArrayList<DemoAgentOffLattice>neighbors=new ArrayList<>();
    Random rn;
    public OffLatticeDemo(int x, int y, Random rn) {
        super(x, y, DemoAgentOffLattice.class,true,true);//class argument required for the NewAgent functions to work
        this.rn=new Random();
    }
    public void Draw(GridVisWindow win){
    }

    public static void main(String[] args) {
        int x=10,y=10,scaleFactor=10;
        OffLatticeDemo grid=new OffLatticeDemo(x,y,new Random(0));//seed ensures determinism, remove number argument for stochastic
        Vis2DOpenGL win=new Vis2DOpenGL(500,500,x,y,"example",true);//set last boolean to false to shut off gui for efficiency

        //INITIALIZATION

        grid.NewAgentSQ(x/2,y/2).Init(RED,grid.CELL_RADIUS);
        //be sure to use IncTick after initialization to setup the agents for iteration
        grid.IncTick();

        //RUN LOOP

        while(!win.CheckClosed()){//makes the gui respond to clicking the close button
            win.TickPause(10);//10 milli pause between ticks
            for (DemoAgentOffLattice agent2D : grid) {
                agent2D.Step1();//all agents calculate their moves before actually moving
            }
            win.Clear(BLACK);//clear the opengl display
            for (DemoAgentOffLattice agent2D : grid) {
                agent2D.Step2();//all agents move and are drawn
                win.Circle(agent2D.Xpt(),agent2D.Ypt(),agent2D.radius,agent2D.color);
            }
            win.Show();//update the opengl display
            grid.CleanInc();
            //Most common end tick function for off-lattice, composed of the 2 functions below
            //grid.CleanAgents() Clean speeds up for looping over agents
            //grid.IncTick() IncTick increments the tick
        }
        win.Dispose();//closes the gui window when the close button is clicked
    }
}
