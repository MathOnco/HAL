package HAL.GridsAndAgents;

import HAL.Interfaces.AgentToBool;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

/**
 * extend the AgentSQ2Dunstackable class if you want agents that exist on a 2D discrete lattice
 * without the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended Grid2unstackable class that the agents will live in
 * Created by rafael on 11/18/16.
 */

public class AgentSQ1Dunstackable<T extends AgentGrid1D> extends Agent1DBase<T> implements Serializable{

    /**
     * Moves the agent to the square with the specified index
     */
    public void MoveSQ(int x){
        //moves agent discretely
        if(!this.alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        G.grid[iSq]=null;
        iSq=x;
        AddSQ(x);
    }

    /**
     * Moves the agent to the specified square, will apply wraparound if it is enabled
     */
    public void MoveSafeSQ(int newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G.In(newX)) {
            MoveSQ(newX);
            return;
        }
        if (G.wrapX) {
            newX = Util.Wrap(newX, G.xDim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xsq();
        }
        MoveSQ(newX);
    }
    /**
     * Gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return iSq;
    }
    /**
     * Gets the xDim coordinate agent
     */
    public double Xpt(){
        return iSq+0.5;
    }
    /**
     * Deletes the agent
     */
    public void Dispose(){
        if(!this.alive){
            throw new RuntimeException("Attempting to dispose already dead agent!");
        }
        RemSQ();
        G.agents.RemoveAgent(this);
        if(myNodes!=null){
            myNodes.DisposeAll();
        }
    }
    public void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere){
        putHere.add(this);
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        if(evalAgent.EvalAgent(this)) {
            putHere.add(this);
        }
    }

    @Override
    int GetCountOnSquare() {
        return 1;
    }

    @Override
    int GetCountOnSquare(AgentToBool evalAgent) {
        return evalAgent.EvalAgent(this)?1:0;
    }
    /**
     * Gets the index of the square that the agent occupies
     */

    public int Age(){
        return G.GetTick()-birthTick;
    }
    public int Isq(){
        return iSq;
    }

    void Setup(double i){
        Setup((int)i);
    }
    void Setup(double xSq,double ySq){
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }
    void Setup(double xSq,double ySq,double zSq){
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        iSq=i;
        AddSQ(i);
    }
    /**
     * swaps the positions of two agents. useful for the AgentSQunstackable classes,
     * which don't allow stacking of agents, making this maneuver otherwise impossible.
     */
    public void SwapPosition(AgentSQ1Dunstackable<T> other) {
        if (!alive || !other.alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        if (other.G != G) {
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        other.RemSQ();
        this.RemSQ();
        int iThis = this.iSq;
        this.iSq = other.iSq;
        other.iSq=iThis;
        other.AddSQ(other.iSq);
        this.AddSQ(this.iSq);
    }


    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    void AddSQ(int i){
        if(G.grid[i]!=null){
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        G.grid[i]=this;
    }
    void RemSQ(){
        G.grid[iSq]=null;
    }



}