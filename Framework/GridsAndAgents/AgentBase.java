package Framework.GridsAndAgents;

import java.io.Serializable;

/**
 * Created by rafael on 2/17/17.
 */
public class AgentBase<T extends GridBase> implements Serializable{
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
     * Returns how many typeGrid ticks the agent has been alive for
     */
    public int _GetAgentListIndex(){
        return iList;
    }

    /**
     * Returns whether the agent is alive or has been disposed
     */
    public boolean IsAlive(){
        return alive;
    }

}
