package Framework.GridsAndAgents;


import Framework.Interfaces.AgentToBool;

import java.util.ArrayList;

import static Framework.Util.InDim;
import static Framework.Util.ModWrap;

/**
 * extend the AgentSQ2D class if you want agents that exist on a 2D discrete lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentSQ1D<T extends AgentGrid1D> extends Agent1DBase<T>{
    AgentSQ1D nextSq;
    AgentSQ1D prevSq;

    void Setup(double i){
        Setup((int)i);
    }
    void Setup(double xSq,double ySq){
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        iSq=i;
        AddSQ(i);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");

    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    /**
     * Moves the agent to the specified square
     */
    public void MoveSQ(int x){
        //moves agent discretely
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        AddSQ(x);
        iSq=x;
    }
    void AddSQ(int x){
        if(myGrid.grid[x]!=null){
            ((AgentSQ1D)myGrid.grid[x]).prevSq=this;
            this.nextSq=(AgentSQ1D)myGrid.grid[x];
        }
        myGrid.grid[x]=this;
        if(myGrid.counts!=null) {
            myGrid.counts[x]++;
        }
    }

    void RemSQ(){
        if(myGrid.grid[iSq]==this){
            myGrid.grid[iSq]=this.nextSq;
        }
        if(nextSq!=null){
            nextSq.prevSq=prevSq;
        }
        if(prevSq!=null){
            prevSq.nextSq=nextSq;
        }
        prevSq=null;
        nextSq=null;
        if(myGrid.counts!=null) {
            myGrid.counts[iSq]--;
        }
    }

    /**
     * Moves the agent to the specified square
     */
    public void MoveSafeSQ(int newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX)) {
            MoveSQ(newX);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(newX, G().xDim)) {
            newX = Xsq();
        }
        MoveSQ(newX);
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return iSq;
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return iSq+0.5;
    }

    /**
     * deletes the agent
     */
    public void Dispose(){
        //kills agent
        if(!alive){
            throw new RuntimeException("attempting to dispose already dead agent");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
        if(myNodes!=null){
            myNodes.DisposeAll();
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentSQ1D toList=this;
        while (toList!=null){
            putHere.add(toList);
            toList=toList.nextSq;
        }
    }

    @Override
    void GetAllOnSquareEval(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentSQ1D toList=this;
        while (toList!=null){
            if(evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList=toList.nextSq;
        }
    }

    @Override
    int GetCountOnSquare() {
        return myGrid.counts[Isq()];
    }
    @Override
    int GetCountOnSquareEval(AgentToBool evalAgent) {
        int ct=0;
        AgentSQ1D curr=this;
        while (curr!=null){
            if(evalAgent.EvalAgent(curr)){
                ct++;
                curr=curr.nextSq;
            }
        }
        return ct;
    }

    public int GetAge(){
        return G().GetTick()-birthTick;
    }
    //addCoords
}
