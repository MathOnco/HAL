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
public class AgentSQ3Dunstackable<T extends AgentGrid3D> extends Agent3DBase<T> implements Serializable {
    int xSq;
    int ySq;
    int zSq;

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
     * Moves the agent to the specified coordinates
     */
    @Override
    public void MoveSQ(int i) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent!");
        }
        RemSQ();
        xSq = G.ItoX(i);
        ySq = G.ItoY(i);
        zSq = G.ItoZ(i);
        iSq = i;
        AddSQ(i);
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
        putHere.add(this);
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        if (evalAgent.EvalAgent(this)) {
            putHere.add(this);
        }

    }

    @Override
    int GetCountOnSquare(AgentToBool evalAgent) {
        return evalAgent.EvalAgent(this) ? 1 : 0;
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
    void Setup(double i) {

    }

    /**
     * returns the age of the agent, in ticks. Be sure to use IncTick on the AgentGrid appropriately for this function
     * to work.
     */
    public int Age() {
        return G.GetTick() - birthTick;
    }

    @Override
    int GetCountOnSquare() {
        return 1;
    }

    @Override
    void Setup(double x, double y) {

    }

    /**
     * swaps the positions of two agents. useful for the AgentSQunstackable classes,
     * which don't allow stacking of agents, making this maneuver otherwise impossible.
     */
    public void SwapPosition(AgentSQ3Dunstackable<T> other) {
        if (!alive || !other.alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        if (other.G != G) {
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        other.RemSQ();
        this.RemSQ();
        int xThis=this.xSq;
        int yThis=this.ySq;
        int zThis=this.zSq;
        int iThis = this.iSq;
        this.xSq = other.xSq;
        this.ySq = other.ySq;
        this.zSq=other.zSq;
        this.iSq = other.iSq;
        other.xSq=xThis;
        other.ySq=yThis;
        other.zSq=zThis;
        other.iSq=iThis;
        other.AddSQ(other.iSq);
        this.AddSQ(this.iSq);
    }


    @Override
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
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        G.grid[i] = this;
    }


    void RemSQ() {
        G.grid[iSq] = null;
    }


}
