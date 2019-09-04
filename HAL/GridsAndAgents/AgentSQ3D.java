package HAL.GridsAndAgents;

import HAL.Interfaces.AgentToBool;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

/**
 * extend the AgentSQ3D class if you want agents that exist on a 3D discrete lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid3D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentSQ3D<T extends AgentGrid3D> extends Agent3DBase<T> implements Serializable {
    int xSq;
    int ySq;
    int zSq;
    AgentSQ3D nextSq;
    AgentSQ3D prevSq;

    /**
     * Moves the agent to the specified coordinates
     */
    public void MoveSQ(int x, int y, int z) {
        //moves agent discretely
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        int iNewPos = G.I(x, y, z);
        RemSQ();
        this.xSq = x;
        this.ySq = y;
        this.zSq = z;
        this.iSq = iNewPos;
        AddSQ(iNewPos);
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
     * gets the z coordinate of the square that the agent occupies
     */
    public int Zsq() {
        return zSq;
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
     * gets the z coordinate of the agent
     */
    public double Zpt() {
        return zSq + 0.5;
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
        AgentSQ3D toList = this;
        while (toList != null) {
            putHere.add(toList);
            toList = toList.nextSq;
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentSQ3D toList = this;
        while (toList != null) {
            if (evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList = toList.nextSq;
        }
    }

    @Override
    public void MoveSQ(int iNext) {
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        xSq = G.ItoX(iNext);
        ySq = G.ItoY(iNext);
        zSq = G.ItoZ(iNext);
        iSq = iNext;
        AddSQ(iNext);
    }

    /**
     * Similar to the move functions, only it will automatically either apply wraparound, or prevent moving along a
     * partiular axis if movement would cause the agent to go out of bounds.
     */
    public void MoveSafeSQ(int newX, int newY, int newZ) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if (G.In(newX, newY, newZ)) {
            MoveSQ(newX, newY, newZ);
            return;
        }
        if (G.wrapX) {
            newX = Util.Wrap(newX, G.xDim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xsq();
        }
        if (G.wrapY) {
            newY = Util.Wrap(newY, G.yDim);
        } else if (!InDim(newY, G.yDim)) {
            newY = Ysq();
        }
        if (G.wrapZ) {
            newZ = Util.Wrap(newZ, G.zDim);
        } else if (!InDim(newZ, G.zDim)) {
            newZ = Zsq();
        }
        MoveSQ(newX, newY, newZ);
    }

    @Override
    int GetCountOnSquare() {
        return G.counts[iSq];
    }


    /**
     * returns the age of the agent, in ticks. Be sure to use IncTick on the AgentGrid appropriately for this function
     * to work.
     */
    public int Age() {
        return G.GetTick() - birthTick;
    }

    @Override
    int GetCountOnSquare(AgentToBool evalAgent) {
        int ct = 0;
        AgentSQ3D curr = this;
        while (curr != null) {
            if (evalAgent.EvalAgent(curr)) {
                ct++;
                curr = curr.nextSq;
            }
        }
        return ct;
    }

    @Override
    void Setup(double i) {
        Setup((int) i);
    }

    @Override
    void Setup(double x, double y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }
    //addCoords

    void Setup(int xSq, int ySq, int zSq) {
        this.xSq = xSq;
        this.ySq = ySq;
        this.zSq = zSq;
        this.iSq = G.I(xSq, ySq, zSq);
        AddSQ(iSq);
    }

    void Setup(double xPos, double yPos, double zPos) {
        Setup((int) xPos, (int) yPos, (int) zPos);
    }

    @Override
    void Setup(int i) {
        this.iSq = i;
        this.xSq = G.ItoX(i);
        this.ySq = G.ItoY(i);
        this.zSq = G.ItoZ(i);
        AddSQ(iSq);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }

    void AddSQ(int i) {
        if (G.grid[i] != null) {
            ((AgentSQ3D) G.grid[i]).prevSq = this;
            this.nextSq = (AgentSQ3D) G.grid[i];
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

}
