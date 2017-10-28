package Framework.GridsAndAgents;

import java.util.ArrayList;

/**
 * Created by rafael on 8/10/17.
 */
public abstract class AgentBaseSpatial<T extends GridBase> extends AgentBase<T> {
    int iSq;
    public int Isq(){
        return iSq;
    }
    public void SwapPosition(AgentBaseSpatial other){
        if(!alive||!other.alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        if(other.myGrid!=myGrid){
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        int iOther=other.Isq();
        int iThis=Isq();
        other.RemSQ();
        this.RemSQ();
        other.MoveSQ(iThis);
        this.MoveSQ(iOther);
    }
    abstract public void MoveSQ(int iNext);
    abstract void Setup(double i);
    abstract void Setup(double x,double y);
    abstract void Setup(double x,double y,double z);
    abstract void Setup(int i);
    abstract void Setup(int x,int y);
    abstract void Setup(int x,int y,int z);
    abstract void RemSQ();
    abstract void AddSQ(int iNext);
    abstract public void Dispose();
    abstract void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere);
}
