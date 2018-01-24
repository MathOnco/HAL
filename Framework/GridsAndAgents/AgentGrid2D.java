package Framework.GridsAndAgents;

import Framework.Interfaces.AgentStepFunction;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Extend the Grid2unstackable class if you want a 2D lattice with one or more agents per typeGrid square
 * @param <T> the AgentSQ2D or AgentPT2D extending agent class that will inhabit the typeGrid
 */
public class AgentGrid2D<T extends AgentBaseSpatial> extends GridBase2D implements Iterable<T>{
    AgentList<T> agents;
    T[] grid;
//    int[] counts;

    public T GetAgent(int index) {
        return grid[index];
    }

    public void _PassAgentConstructor(Class<T> agentClass){
        agents.SetupConstructor(agentClass);
    }

    public T GetAgent(int x, int y) {
        return grid[I(x, y)];
    }
    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid2D(int x, int y, Class<T> agentClass, boolean wrapX, boolean wrapY){
        super(x,y,wrapX,wrapY);
        //creates a new typeGrid with given dimensions
        agents=new AgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
//        counts= new int[length];
    }
    public AgentGrid2D(int x, int y, Class<T> agentClass){
        super(x,y,false,false);
        //creates a new typeGrid with given dimensions
        agents=new AgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
//        counts= new int[length];
    }

//    void RemAgentFromSquare(T agent,int iGrid){
//        //internal function, removes agent from typeGrid square
//        if(typeGrid[iGrid]==agent){
//            typeGrid[iGrid]=(T)agent.nextSq;
//        }
//        if(agent.nextSq!=null){
//            agent.nextSq.prevSq=agent.prevSq;
//        }
//        if(agent.prevSq!=null){
//            agent.prevSq.nextSq=agent.nextSq;
//        }
//        agent.prevSq=null;
//        agent.nextSq=null;
////        counts[iGrid]--;
//    }
//    void AddAgentToSquare(T agent,int iGrid){
//        if(typeGrid[iGrid]!=null){
//            typeGrid[iGrid].prevSq=agent;
//            agent.nextSq=typeGrid[iGrid];
//        }
//        typeGrid[iGrid]=agent;
////        counts[iGrid]++;
//    }

    T GetNewAgent(){
        return agents.GetNewAgent();
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x, int y){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x, double y){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y);
        return newAgent;
    }
    /**
     * returns an uninitialized agent at the specified index
     */
    public T NewAgentSQ(int index){
        T newAgent=GetNewAgent();
        newAgent.Setup(index);
        return newAgent;
    }

    public T NewAgentPTSafe(double newX, double newY, double fallbackX, double fallbackY, boolean wrapX, boolean wrapY){
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(xDim, newX)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(yDim, newY))
            newY = fallbackY;
        return NewAgentPT(newX,newY);
    }
    public T NewAgentPTSafe(double newX, double newY, double fallbackX, double fallbackY){
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(xDim, newX)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(yDim, newY))
            newY = fallbackY;
        return NewAgentPT(newX,newY);
    }
//    void RemoveAgent(T agent,int iGrid){
//        //internal function, removes agent from world
//        RemAgentFromSquare(agent, iGrid);
//        agents.RemoveAgent(agent);
//    }


    /**
     * shuffles the agent list to randomize iteration
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void ShuffleAgents(Rand rn){
        agents.ShuffleAgents(rn);
    }

    /**
     * cleans the list of agents, removing dead ones, may improve the efficiency of the agent iteration if many agents have died
     * do not call this while in the middle of iteration
     */
    public void CleanAgents(){
        agents.CleanAgents();
    }
    public void ChangeGridsSQ(T foreignAgent,int newX,int newY){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid2D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY);
    }
    public void ChangeGridsPT(T foreignAgent,double newX,double newY){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid2D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY);
    }
    public void ChangeGridsSQ(T foreignAgent,int newI){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid2D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newI);
    }

    public void AgentsInRad(final ArrayList<T> retAgentList, final double x, final double y, final double rad, boolean wrapX, boolean wrapY){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                int retX=xSq; int retY=ySq;
                boolean inX= Util.InDim(xDim,retX);
                boolean inY= Util.InDim(yDim,retY);
                if((!wrapX&&!inX)||(!wrapY&&!inY)){
                    continue;
                }
                if(wrapX&&!inX){
                    retX= Util.ModWrap(retX,xDim);
                }
                if(wrapY&&!inY){
                    retY= Util.ModWrap(retY,yDim);
                }
                GetAgents(retAgentList, I(retX,retY));
            }
        }
    }
    public void AgentsInRad(final ArrayList<T> retAgentList, final double x, final double y, final double rad){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                int retX=xSq; int retY=ySq;
                boolean inX= Util.InDim(xDim,retX);
                boolean inY= Util.InDim(yDim,retY);
                if((!wrapX&&!inX)||(!wrapY&&!inY)){
                    continue;
                }
                if(wrapX&&!inX){
                    retX= Util.ModWrap(retX,xDim);
                }
                if(wrapY&&!inY){
                    retY= Util.ModWrap(retY,yDim);
                }
                GetAgents(retAgentList, I(retX,retY));
            }
        }
    }

    /**
     * calls CleanAgents, then SuffleAgents, then IncTick. useful to call at the end of a round of iteration
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void CleanShuffInc(Rand rn){
        CleanAgents();
        ShuffleAgents(rn);
        IncTick();
    }
    public void ShuffInc(Rand rn){
        ShuffleAgents(rn);
        IncTick();
    }
    public void CleanInc(){
        CleanAgents();
        IncTick();
    }
    public Iterator<T> iterator(){
        return agents.iterator();
    }
//    public int PopAt(int x, int y){
//        //gets population count at location
//        return counts[I(x,y)];
//    }
//    public int PopAt(int i){
//        //gets population count at location
//        return counts[i];
//    }
    public List<T> _AllAgents(){return (List<T>)this.agents.GetAllAgents();}
    public List<T> _AllDeads(){return (List<T>)this.agents.GetAllDeads();}

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     * @param retAgentList the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>retAgentList, int x, int y){
        T agent= grid[I(x,y)];
        if(agent!=null) {
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified index
     * @param putHere the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>putHere, int index){
        T agent= grid[index];
        if(agent!=null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * calls dispose on all agents in the typeGrid, resets the tick timer to 0.
     */
    public void Reset(){
        IncTick();
        for (T a : this) {
           a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        tick=0;
    }
    public void ResetHard(){
        IncTick();
        for (T a : this) {
            a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        this.agents.Reset();
        tick=0;
    }
    public void MultiThreadAgents(int nThreads, AgentStepFunction<T> StepFunction){
        int last=agents.iLastAlive;
        Util.MultiThread(nThreads,nThreads,(iThread)->{
            ArrayList<T> agents=this.agents.agents;
            int start=iThread/nThreads*last;
            int end=(iThread+1)/nThreads*last;
            for (int i = start; i < end; i++) {
                T a=agents.get(i);
                if(a.alive&&a.birthTick<tick){
                    StepFunction.AgentStepFunction(a);
                }
            }
        });
    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int GetPop(){
        //gets population
        return agents.pop;
    }
    public int HoodToEmptyIs(int[] coords, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        int toCheck= HoodToIs(coords,ret,centerX,centerY,wrapX,wrapY);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToEmptyIs(int[] coords, int[] ret, int centerX, int centerY){
        int toCheck= HoodToIs(coords,ret,centerX,centerY);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] coords, int[] ret, int centerX, int centerY){
        int toCheck= HoodToIs(coords,ret,centerX,centerY);
        return FindOccupiedIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] coords, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        int toCheck= HoodToIs(coords,ret,centerX,centerY,wrapX,wrapY);
        return FindOccupiedIs(ret,toCheck);
    }
    public int FindEmptyIs(int[] IsToCheck,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])==null){
                IsToCheck[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int FindEmptyIs(int[] IsToCheck,int[]retIs,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])==null){
                retIs[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int FindOccupiedIs(int[] IsToCheck,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])!=null){
                IsToCheck[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
    public int FindOccupiedIs(int[] IsToCheck,int[] retIs,int lenToCheck){
        int validCount=0;
        for (int i = 0; i < lenToCheck; i++) {
            if(GetAgent(IsToCheck[i])!=null){
                retIs[validCount]=IsToCheck[i];
                validCount++;
            }
        }
        return validCount;
    }
}