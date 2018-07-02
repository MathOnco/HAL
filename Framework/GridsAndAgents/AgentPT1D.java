package Framework.GridsAndAgents;

import Framework.Interfaces.AgentToBool;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * extend the AgentPT2D class if you want agents that exist on a 2D continuous lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid2D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentPT1D<T extends AgentGrid1D> extends Agent1DBase<T>{
    private double ptX;
    AgentPT1D nextSq;
    AgentPT1D prevSq;
    @Override
    void Setup(double i){
        this.ptX =i;
        iSq=(int)i;
        AddSQ(iSq);
    }
    @Override
    void Setup(double xPos,double yPos){
        throw new IllegalStateException("shouldn't be adding 1D agent to 2D typeGrid");
    }

    @Override
    void Setup(double x, double y, double z) {
        throw new IllegalStateException("shouldn't be adding 1D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        ptX =i+0.5;
        iSq =i;
        AddSQ(i);
    }

    @Override
    public void Dispose() {
        if(!alive){
            throw new RuntimeException("attepting to dispose already dead agent");
        }
        RemSQ();
        G.agents.RemoveAgent(this);
        if(myNodes!=null){
            myNodes.DisposeAll();
        }
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentPT1D toList=this;
        while (toList!=null){
            putHere.add(toList);
            toList=toList.nextSq;
        }
    }

    @Override
    int GetCountOnSquare() {
        return G.counts[Isq()];
    }

    @Override
    int GetCountOnSquareEval(AgentToBool evalAgent) {
        int ct=0;
        AgentPT1D curr=this;
        while (curr!=null){
            if(evalAgent.EvalAgent(curr)){
                ct++;
                curr=curr.nextSq;
            }
        }
        return ct;
    }

    @Override
    void GetAllOnSquareEval(ArrayList<AgentBaseSpatial> putHere, AgentToBool evalAgent) {
        AgentPT1D toList=this;
        while (toList!=null){
            if(evalAgent.EvalAgent(toList)) {
                putHere.add(toList);
            }
            toList=toList.nextSq;
        }
    }

    void Setup(int xPos,int yPos){
        Setup(xPos+0.5,yPos+0.5);
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    void AddSQ(int i){
        if(G.grid[i]!=null){
            ((AgentPT1D)G.grid[i]).prevSq=this;
            this.nextSq=(AgentPT1D)G.grid[i];
        }
        G.grid[i]=this;
        G.counts[i]++;
    }
    void RemSQ(){
        if(G.grid[iSq]==this){
            G.grid[iSq]=this.nextSq;
        }
        if(nextSq!=null){
            nextSq.prevSq=prevSq;
        }
        if(prevSq!=null){
            prevSq.nextSq=nextSq;
        }
        prevSq=null;
        nextSq=null;
        G.counts[iSq]--;
    }
    /**
     * Moves the agent to the center of the square at the specified coordinates
     */
    public void MoveSQ(int newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        RemSQ();
        iSq=newX;
        AddSQ(iSq);
        ptX =newX+0.5;
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void MovePT(double newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        int xIntNew=(int)newX;
        int xIntOld=(int) ptX;
        if(xIntNew!=xIntOld) {
            RemSQ();
            iSq=xIntNew;
            AddSQ(iSq);
        }
        ptX =newX;
    }

    public void MoveSafePT(double newX){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G.In(newX)) {
            MovePT(newX);
            return;
        }
        if (G.wrapX) {
            newX = ModWrap(newX, G.moveSafeXdim);
        } else if (!InDim(newX, G.xDim)) {
            newX = Xpt();
        }
        MovePT(newX);
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return ptX;
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return (int) ptX;
    }
    public int GetAge(){
        return G.GetTick()-birthTick;
    }

    public<T extends AgentPT1D> double Xdisp(T other){
        return  G.wrapX? DispWrap(other.Xpt(),Xpt(), G.xDim):Xpt()-other.Xpt();
    }
}
