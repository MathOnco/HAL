package HAL.GridsAndAgents;


import HAL.Interfaces.AgentToBool;
import HAL.Interfaces.Coords1DBool;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

/**
 * extend the AgentSQ2D class if you want agents that exist on a 2D discrete lattice with the possibility of stacking
 * multiple agents on the same typeGrid square
 *
 * @param <T> the extended AgentGrid2D class that the agents will live in Created by rafael on 11/18/16.
 */
public class AgentSQ1D<T extends AgentGrid1D> extends Agent1DBase<T> implements Serializable {
    AgentSQ1D nextSq;
    AgentSQ1D prevSq;

    /**
     * Moves the agent to the specified square
     */
    public void MoveSQ(int x) {
        //moves agent discretely
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        AddSQ(x);
        iSq = x;
    }

    /**
     * Moves the agent to the specified square, will apply wraparound if it is enabled
     */
    public void MoveSafeSQ(int newX) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G.In(newX)) {
            MoveSQ(newX);
            return;
        }
        if (G.wrapX) {
            MoveSQ(Util.Wrap(newX, G.xDim));
        }
    }
    public void MoveSafeSQ(int newX, Coords1DBool IsValidMove) {
        if(IsValidMove.Eval(newX)){
            MoveSafeSQ(newX);
        }
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq() {
        return iSq;
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt() {
        return iSq + 0.5;
    }

    /**
     * deletes the agent
     */
    public void Dispose() {
        //kills agent
        if (!alive) {
            throw new RuntimeException("attempting to dispose already dead agent");
        }
        RemSQ();
        G.agents.RemoveAgent(this);
        if (myNodes != null) {
            myNodes.DisposeAll();
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentSQ1D toList = this;
        while (toList != null) {
            putHere.add(toList);
            toList = toList.nextSq;
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentSQ1D toList = this;
        while (toList != null) {
            if (evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList = toList.nextSq;
        }
    }

    @Override
    int GetCountOnSquare() {
        return G.counts[Isq()];
    }

    @Override
    int GetCountOnSquare(AgentToBool evalAgent) {
        int ct = 0;
        AgentSQ1D curr = this;
        while (curr != null) {
            if (evalAgent.EvalAgent(curr)) {
                ct++;
                curr = curr.nextSq;
            }
        }
        return ct;
    }

    public int Age() {
        return G.GetTick() - birthTick;
    }
    //addCoords

    void AddSQ(int x) {
        if (G.grid[x] != null) {
            ((AgentSQ1D) G.grid[x]).prevSq = this;
            this.nextSq = (AgentSQ1D) G.grid[x];
        }
        G.grid[x] = this;
        if (G.counts != null) {
            G.counts[x]++;
        }
    }

    void RemSQ() {
        if (G.grid[iSq] == this) {
            G.grid[iSq] = this.nextSq;
        }
        if (nextSq != null) {
            nextSq.prevSq = prevSq;
        }
        if (prevSq != null) {
            prevSq.nextSq = nextSq;
        }
        prevSq = null;
        nextSq = null;
        if (G.counts != null) {
            G.counts[iSq]--;
        }
    }

    void Setup(double i) {
        Setup((int) i);
    }

    void Setup(double xSq, double ySq) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        iSq = i;
        AddSQ(i);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");

    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

}
