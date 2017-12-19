package Framework.GridsAndAgents;

import java.io.Serializable;

/**
 * Created by rafael on 2/17/17.
 */
public class AgentBase <T extends GridBase> implements Serializable{
    int birthTick;
    int iList;
    boolean alive;
    T myGrid;

    /**
     * Returns the typeGrid that the agent lives in
     */
    public T G(){
        return myGrid;
    }

    /**
     * Returns how many typeGrid ticks the agent has been alive for
     */
    public int Age(){
        return myGrid.tick-birthTick;
    }
    //note, BirthTick function will now return a value relative to the new age!
    public void SetAge(int newAge){
        if(newAge<0){
            throw new IllegalArgumentException("Can't set age of an agent to less than 0! Age:"+newAge);
        }
        this.birthTick=myGrid.tick-newAge;
    }
    public int _GetAgentListIndex(){
        return iList;
    }

    /**
     * Returns the tick at which the cell was born
     */
    public int BirthTick(){
        return birthTick;
    }

    /**
     * Returns whether the agent is alive or has been disposed
     */
    public boolean IsAlive(){
        return alive;
    }

}
