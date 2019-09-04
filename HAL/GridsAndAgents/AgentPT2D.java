package HAL.GridsAndAgents;

import HAL.Interfaces.AgentToBool;
import HAL.Interfaces.Coords2DBool;
import HAL.Interfaces.Point2DBool;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.*;

/**
 * extend the AgentPT2D class if you want agents that exist on a 2D continuous lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentPT2D<T extends AgentGrid2D> extends Agent2DBase<T> implements Serializable {
    private double ptX;
    private double ptY;
    AgentPT2D nextSq;
    AgentPT2D prevSq;

    /**
     * Moves the agent to the center of the square at the specified coordinates
     */
    public void MoveSQ(int newX, int newY) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent");
        }
        RemSQ();
        iSq = G.I(newX, newY);
        AddSQ(iSq);
        ptX = newX + 0.5;
        ptY = newY + 0.5;
    }

    /**
     * Moves the agent to the center of the square at the specified coordinates
     */
    public void MoveSQ(int i) {
        if (!alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        int x = G.ItoX(i);
        int y = G.ItoY(i);
        iSq = i;
        this.ptX = x + 0.5;
        this.ptY = y + 0.5;
        AddSQ(i);
    }


    /**
     * Moves the agent to the specified coordinates
     */
    public void MovePT(double newX, double newY) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent");
        }
        int xIntNew = (int) newX;
        int yIntNew = (int) newY;
        int xIntOld = (int) ptX;
        int yIntOld = (int) ptY;
        if (xIntNew != xIntOld || yIntNew != yIntOld) {
            RemSQ();
            iSq = G.I(xIntNew, yIntNew);
            AddSQ(iSq);
        }
        ptX = newX;
        ptY = newY;
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
            newX = Wrap(newX, G.xDim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xsq();
        }
        if (G.wrapY) {
            newY = Wrap(newY, G.yDim);
        } else if (!InDim(newY, G.yDim))
            newY = Ysq();
        MoveSQ(newX, newY);
    }
    public void MoveSafeSQ(int newX, int newY, Coords2DBool IsValidMove) {
        if(IsValidMove.Eval(newX,newY)){
            MoveSafeSQ(newX,newY);
        }
    }

    /**
     * Similar to the move functions, only it will automatically either apply wraparound, or prevent moving along a
     * partiular axis if movement would cause the agent to go out of bounds.
     */
    public void MoveSafePT(double newX, double newY) {
        if (!alive) {
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G.In(newX, newY)) {
            MovePT(newX, newY);
            return;
        }
        if (G.wrapX) {
            newX = Wrap(newX, G.moveSafeXdim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xpt();
        }
        if (G.wrapY) {
            newY = Wrap(newY, G.moveSafeYdim);
        } else if (!InDim(newY, G.yDim))
            newY = Ypt();
        MovePT(newX, newY);
    }
    public void MoveSafePT(double newX, double newY, Point2DBool IsValidMove) {
        if(IsValidMove.Eval(newX,newY)){
            MoveSafePT(newX,newY);
        }
    }

    /**
     * returns the X coordinate of the agent. If the Agent is on-lattice, these functions will return the coordinates of
     * the middle of the square that the agent is on.
     */
    public double Xpt() {
        return ptX;
    }

    /**
     * returns the Y coordinate of the agent. If the Agent is on-lattice, these functions will return the coordinates of
     * the middle of the square that the agent is on.
     */
    public double Ypt() {
        return ptY;
    }

    /**
     * returns the X index of the square that the agent is currently on.
     */
    public int Xsq() {
        return (int) ptX;
    }

    /**
     * returns the age of the agent, in ticks. Be sure to use IncTick on the AgentGrid appropriately for this function
     * to work.
     */
    public int Age() {
        return G.GetTick() - birthTick;
    }

    /**
     * returns the Y index of the square that the agent is currently on.
     */
    public int Ysq() {
        return (int) ptY;
    }


    @Override
    void Setup(double i) {
        Setup((int) i);
    }

    @Override
    void Setup(double xPos, double yPos) {
        this.ptX = xPos;
        this.ptY = yPos;
        iSq = this.G.I(xPos, yPos);
        AddSQ(iSq);
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        ptX = G.ItoX(i) + 0.5;
        ptY = G.ItoY(i) + 0.5;
        iSq = i;
        AddSQ(i);
    }

    @Override
    public void Dispose() {
        if (!alive) {
            throw new RuntimeException("attepting to dispose already dead agent");
        }
        RemSQ();
        G.agents.RemoveAgent(this);
        if (myNodes != null) {
            myNodes.DisposeAll();
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentPT2D toList = this;
        while (toList != null) {
            putHere.add(toList);
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
        AgentPT2D curr = this;
        while (curr != null) {
            if (evalAgent.EvalAgent(curr)) {
                ct++;
                curr = curr.nextSq;
            }
        }
        return ct;
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentPT2D toList = this;
        while (toList != null) {
            if (evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList = toList.nextSq;
        }
    }

    void Setup(int xPos, int yPos) {
        Setup(xPos + 0.5, yPos + 0.5);
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    void AddSQ(int i) {
        if (G.grid[i] != null) {
            ((AgentPT2D) G.grid[i]).prevSq = this;
            this.nextSq = (AgentPT2D) G.grid[i];
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
