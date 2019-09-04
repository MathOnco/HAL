package HAL.GridsAndAgents;


import HAL.Interfaces.AgentToBool;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

/**
 * extend the AgentSQ2D class if you want agents that exist on a 2D discrete lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentSQ2D<T extends AgentGrid2D> extends Agent2DBase<T> implements Serializable {
    private int xSq;
    private int ySq;
    AgentSQ2D nextSq;
    AgentSQ2D prevSq;

    /**
     * Moves the agent to the specified square
     */
    public void MoveSQ(int x, int y) {
        //moves agent discretely
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        int iNewPos = G.I(x, y);
        RemSQ();
        AddSQ(iNewPos);
        this.xSq = x;
        this.ySq = y;
        iSq = iNewPos;
    }

    /**
     * Moves the agent to the specified square
     */
    public void MoveSQ(int i) {
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        int x = G.ItoX(i);
        int y = G.ItoY(i);
        RemSQ();
        AddSQ(i);
        this.xSq = x;
        this.ySq = y;
        iSq = i;
    }

    /**
     * Similar to the move functions, only it will automatically either apply wraparound, or prevent moving along a
     * partiular axis if movement would cause the agent to go out of bounds.
     */
    public void MoveSafeSQ(int newX, int newY) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G.In(newX, newY)) {
            MoveSQ(newX, newY);
            return;
        }
        if (G.wrapX) {
            newX = Util.Wrap(newX, G.xDim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xsq();
        }
        if (G.wrapY) {
            newY = Util.Wrap(newY, G.yDim);
        } else if (!InDim(newY, G.yDim))
            newY = Ysq();
        MoveSQ(newX, newY);
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq() {
        return xSq;
    }

    /**
     * gets the yDim coordinate of the square that the agent occupies
     */
    public int Ysq() {
        return ySq;
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt() {
        return xSq + 0.5;
    }

    /**
     * gets the yDim coordinate of the agent
     */
    public double Ypt() {
        return ySq + 0.5;
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

    /**
     * returns the age of the agent, in ticks. Be sure to use IncTick on the AgentGrid appropriately for this function
     * to work.
     */
    public int Age() {
        return G.GetTick() - birthTick;
    }

    void AddSQ(int i) {
        if (G.grid[i] != null) {
            ((AgentSQ2D) G.grid[i]).prevSq = this;
            this.nextSq = (AgentSQ2D) G.grid[i];
        }
        G.grid[i] = this;
        G.counts[i]++;
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
        G.counts[iSq]--;
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentSQ2D toList = this;
        while (toList != null) {
            putHere.add(toList);
            toList = toList.nextSq;
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentSQ2D toList = this;
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
        AgentSQ2D curr = this;
        while (curr != null) {
            if (evalAgent.EvalAgent(curr)) {
                ct++;
                curr = curr.nextSq;
            }
        }
        return ct;
    }
    //addCoords
    void Setup(double i) {
        Setup(i);
    }

    void Setup(double xSq, double ySq) {
        Setup((int) xSq, (int) ySq);
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        xSq = G.ItoX(i);
        ySq = G.ItoY(i);
        iSq = i;
        AddSQ(i);

    }

    @Override
    void Setup(int x, int y) {
        this.xSq = x;
        this.ySq = y;
        iSq = G.I(xSq, ySq);
        AddSQ(iSq);

    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

}
