package Framework.GridsAndAgents;

import java.io.Serializable;

public abstract class GridBase implements Serializable{
    int tick;
    public GridBase(){
    }

    /**
     * gets the current typeGrid tick
     */
    public int GetTick(){
        return tick;
    }

    /**
     * increments the current typeGrid tick
     */
    public void IncTick(){
        tick+=1;
    }
}

