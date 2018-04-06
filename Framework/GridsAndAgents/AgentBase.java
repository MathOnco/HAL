package Framework.GridsAndAgents;

import java.io.Serializable;

/**
 * Created by rafael on 2/17/17.
 */
public abstract class AgentBase<T> implements Serializable{
    int stateID;
    int iList;
    boolean alive;
    T myGrid;
    AgentListNode myNodes;

    /**
     * Returns the typeGrid that the agent lives in
     */
    public T G(){
        return myGrid;
    }

    /**
     * Returns whether the agent is alive or has been disposed
     */
    public boolean IsAlive(){
        return alive;
    }

    abstract public void Dispose();

}
