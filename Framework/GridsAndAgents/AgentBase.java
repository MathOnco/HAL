package Framework.GridsAndAgents;

import java.io.Serializable;

/**
 * Created by rafael on 2/17/17.
 */
public abstract class AgentBase<T> implements Serializable{
    int stateID;
    int iList;
    int birthTick;
    boolean alive;
    public final T G;
    AgentListNode myNodes;

    public AgentBase(){
        G=null;
    }

    /**
     * Returns whether the agent is alive or has been disposed
     */
    public boolean IsAlive(){
        return alive;
    }

    public int BirthTick(){
        return birthTick;
    }

    abstract public void Dispose();

}
