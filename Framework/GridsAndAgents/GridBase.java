package Framework.GridsAndAgents;

import java.io.Serializable;

public abstract class GridBase implements Serializable{
    int tick;
    int[]actionIs=new int[10];//used with HoodToSingleAction
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
    public void JumpToTick(int tick){
        if(tick<this.tick){
            throw new IllegalStateException("tick argument: "+tick+" is less than current tick: "+this.tick+" Can't jump back in time, as it will make agent ages negative, try calling Reset() instead!");
        }
        this.tick=tick;
    }

    }

