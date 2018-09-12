package Framework.GridsAndAgents;

import Framework.Interfaces.AgentToBool;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * AgentBaseSpatial functions are shared amongst all spatial agent types
 */
public abstract class AgentBaseSpatial<T> extends AgentBase<T> implements Serializable {
    int iSq;

    /**
     * returns the index of the square that the agent is currently on
     */
    public int Isq() {
        return iSq;
    }

    /**
     * swaps the positions of two agents. useful mostly for the AgentSQ2unstackable and AgentSQ3unstackable classes,
     * which don't allow stacking of agents, making this maneuver otherwise difficult.
     */
    public void SwapPosition(AgentBaseSpatial other) {
        if (!alive || !other.alive) {
            throw new RuntimeException("attempting to move dead agent");
        }
        if (other.G != G) {
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        int iOther = other.Isq();
        int iThis = Isq();
        other.RemSQ();
        this.RemSQ();
        other.MoveSQ(iThis);
        this.MoveSQ(iOther);
    }

    /**
     * moves the agent to the middle of the square at the indices/index specified
     */
    abstract public void MoveSQ(int iNext);

    abstract void Setup(double i);

    abstract void Setup(double x, double y);

    abstract void Setup(double x, double y, double z);

    abstract void Setup(int i);

    abstract void Setup(int x, int y);

    abstract void Setup(int x, int y, int z);

    abstract void RemSQ();

    abstract void AddSQ(int iNext);

    abstract void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere);

    abstract int GetCountOnSquare();

    abstract int GetCountOnSquare(AgentToBool evalAgent);

    abstract void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent);
}
