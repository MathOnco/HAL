package HAL.GridsAndAgents;

import HAL.Interfaces.AgentToBool;

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
