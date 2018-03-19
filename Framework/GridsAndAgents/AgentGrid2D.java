package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/*TODO: add the following neighborhood search functions:
    applyAll functional notation
    way to iterate over individual agents
*/
/**
 * Extend the Grid2unstackable class if you want a 2D lattice with one or more agents per typeGrid square
 * @param <T> the AgentSQ2D or AgentPT2D extending agent class that will inhabit the typeGrid
 */
public class AgentGrid2D<T extends AgentBaseSpatial> extends GridBase2D implements Iterable<T>{
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches =new ArrayList<>();
    ArrayList<AgentsIterator> usedIterIs =new ArrayList<>();
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
    public T GetAgentSafe(int x, int y) {
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(xDim, x)) {
            return null;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(yDim, y)) {
            return null;
        }
        return grid[I(x, y)];
    }
    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid2D(int x, int y, Class<T> agentClass, boolean wrapX, boolean wrapY){
        super(x,y,wrapX,wrapY);
        //creates a new typeGrid with given dimensions
        agents=new InternalGridAgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
//        counts= new int[length];
    }
    public AgentGrid2D(int x, int y, Class<T> agentClass){
        super(x,y,false,false);
        //creates a new typeGrid with given dimensions
        agents=new InternalGridAgentList<T>(agentClass,this);
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
//    public void IncTick(){
//        super.IncTick();
//        agents.stateID++;
//    }

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
        } else if (!Util.InDim(yDim, newY)) {
            newY = fallbackY;
        }
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
        } else if (!Util.InDim(yDim, newY)) {
            newY = fallbackY;
        }
        return NewAgentPT(newX,newY);
    }
//    void RemoveNode(T agent,int iGrid){
//        //internal function, removes agent from world
//        RemAgentFromSquare(agent, iGrid);
//        agents.RemoveNode(agent);
//    }


    public void CleanShuffle(Rand rn){
        CleanAgents();
        ShuffleAgents(rn);
    }

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
                GetAgents(retAgentList, retX,retY);
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
                GetAgents(retAgentList, retX,retY);
            }
        }
    }

//    /**
//     * calls CleanAgents, then SuffleAgents, then IncTick. useful to call at the end of a round of iteration
//     * do not call this while in the middle of iteration
//     * @param rn the Random number generator to be used
//     */
//    public void CleanShuffInc(Rand rn){
//        CleanAgents();
//        ShuffleAgents(rn);
//        IncTick();
//    }
//    public void ShuffInc(Rand rn){
//        ShuffleAgents(rn);
//        IncTick();
//    }
//    public void CleanInc(){
//        CleanAgents();
//        IncTick();
//    }
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
     * @param putHere the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>putHere, int x, int y){
        T agent= grid[I(x,y)];
        if(agent!=null) {
            agent.GetAllOnSquare(putHere);
        }
    }
    public void GetAgentsSafe(ArrayList<T>putHere, int x, int y){
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(xDim, x)) {
            return;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(yDim, y)) {
            return;
        }
        T agent= grid[I(x,y)];
        if(agent!=null) {
            agent.GetAllOnSquare(putHere);
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

    public void GetAgents(ArrayList<T>putHere,AgentToBool EvalAgent,int x,int y){
        T agent=grid[I(x,y)];
        if(agent!=null) {
            agent.GetAllOnSquareEval(putHere,EvalAgent);
        }
    }
    public void GetAgents(ArrayList<T>putHere,AgentToBool EvalAgent,int i){
        T agent=grid[i];
        if(agent!=null) {
            agent.GetAllOnSquareEval(putHere,EvalAgent);
        }
    }

    /**
     * calls dispose on all agents in the typeGrid, resets the tick timer to 0.
     */
    public void Reset(){
//        IncTick();
        for (T a : this) {
           a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
//        tick=0;
    }
    public void ResetHard(){
//        IncTick();
        for (T a : this) {
            a.Dispose();
        }
        if(GetPop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        this.agents.Reset();
//        tick=0;
    }
//    public void MultiThreadAgents(int nThreads, AgentStepFunction<T> StepFunction){
//        int last=agents.iLastAlive;
//        Util.MultiThread(nThreads,nThreads,(iThread)->{
//            ArrayList<T> agents=this.agents.agents;
//            int start=iThread/nThreads*last;
//            int end=(iThread+1)/nThreads*last;
//            for (int i = start; i < end; i++) {
//                T a=agents.get(i);
//                if(a.alive&&a.birthTick<tick){
//                    StepFunction.AgentStepFunction(a);
//                }
//            }
//        });
//    }

    public int CountAt(int i){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquare();
    }

    public int CountAt(int x,int y){
        return CountAt(I(x,y));
    }
    public int CountAt(int i,AgentToBool EvalAgent){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquareEval(EvalAgent);
    }
    public int CountAt(int x,int y,AgentToBool EvalAgent){
        return CountAt(I(x,y),EvalAgent);
    }
    public int CountInHood(int[]hood,int i){
        return CountInHood(hood,ItoX(i),ItoY(i),wrapX,wrapY);
    }
    public int CountInHood(int[]hood,int i,AgentToBool<T> EvalAgent){
        return CountInHood(hood,ItoX(i),ItoY(i),EvalAgent,wrapX,wrapY);
    }
    public int CountInHood(int[]hood,int x,int y,boolean wrapX,boolean wrapY){
        int ct=0;
        int[]Is=GetFreshHoodIs(hood.length/2);
        int searchLen=HoodToIs(hood,Is,x,y,wrapX,wrapY);
        for (int i = 0; i < searchLen; i++) {
            ct+=CountAt(Is[i]);
        }
        usedHoodIs.add(Is);
        return ct;
    }
    public int CountInHood(int[]hood,int x,int y,AgentToBool<T> EvalAgent,boolean wrapX,boolean wrapY){
        int ct=0;
        int[]Is=GetFreshHoodIs(hood.length/2);
        int searchLen=HoodToIs(hood,Is,x,y,wrapX,wrapY);
        for (int i = 0; i < searchLen; i++) {
            ct+=CountAt(Is[i]);
        }
        usedHoodIs.add(Is);
        return ct;
    }
    public int CountInHood(int[]hood,int x,int y){
        return CountInHood(hood,x,y,wrapX,wrapY);
    }
    public T RandomAgent(Rand rn){
        CleanAgents();
        if(GetPop()==0){
            return null;
        }
        return agents.agents.get(rn.Int(GetPop()));
    }
    ArrayList<T> GetFreshAgentSearchArr(){
        ArrayList<T> agents;
        if(usedAgentSearches.size()>0){
            agents= usedAgentSearches.remove(usedAgentSearches.size()-1);
            agents.clear();
            return agents;
        }
        return new ArrayList<T>();
    }
    public int ApplyAgentsSQ(int i, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,null,action);
    }
    public int ApplyAgentsSQ(int x, int y, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y), null,null,action);
    }
    public int ApplyAgentsSQ(int i, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int x, int y, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y), null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int x, int y, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y), rn,null,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, i, rn,null,action);

    }
    public int ApplyAgentsSQ(int nActions, int x, int y, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y), rn,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        if(evalAgent!=null) {
            GetAgents(agents, evalAgent, i);
        }
        else{
            GetAgents(agents, i);
        }
        int nFound = agents.size();
        if (nActions < 0 || nFound <= nActions) {
            for (T agent : agents) {
                action.Action(agent,nFound);
            }
        } else {
            for (int j = 0; j < nActions; j++) {
                int iRand = rn.Int(nFound - j);
                T agent=agents.get(iRand);
                action.Action(agent, nFound);
                agents.set(iRand,agents.get(nFound - j - 1));
            }
        }
        usedAgentSearches.add(agents);
        return nFound;
    }
    public int ApplyAgentsRad(double rad, double x, double y, AgentRadDispToAction2D<T> action){
        ArrayList<T> agents = GetFreshAgentSearchArr();
        int nAgents=0;
        double radSq=rad*rad;
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
                GetAgents(agents, retX,retY);
                for (int i = 0; i < agents.size(); i++) {
                    T agent=agents.get(i);
                    double xDisp=((Agent2DBase)(agent)).Xpt()-x;
                    double yDisp=((Agent2DBase)(agent)).Ypt()-y;
                    double distSq=xDisp*xDisp+yDisp*yDisp;
                    if(distSq<=radSq) {
                        action.Action(agent, xDisp, yDisp, distSq);
                        nAgents++;
                    }
                }
                agents.clear();
            }
        }
        usedAgentSearches.add(agents);
        return nAgents;
    }
    public int ApplyAgentsRad(double rad, double x, double y, AgentRadDispToAction2D<T> action, boolean wrapX, boolean wrapY){
        ArrayList<T> agents = GetFreshAgentSearchArr();
        int nAgents=0;
        double radSq=rad*rad;
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
                GetAgents(agents, retX,retY);
                for (int i = 0; i < agents.size(); i++) {
                    T agent=agents.get(i);
                    double xDisp=((Agent2DBase)(agent)).Xpt()-x;
                    double yDisp=((Agent2DBase)(agent)).Ypt()-y;
                    double distSq=xDisp*xDisp+yDisp*yDisp;
                    if(distSq<=radSq) {
                        action.Action(agent, xDisp, yDisp, distSq);
                        nAgents++;
                    }
                }
                agents.clear();
            }
        }
        usedAgentSearches.add(agents);
        return nAgents;
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), null, -1, null, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, null, -1, null, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), null, -1, null, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, null, -1, null, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int nActions, int[]hood, int centerI, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn, nActions, null, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, rn, nActions, null, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn, nActions, null, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, rn, nActions, null, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn, nActions, null, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, Rand rn,IntToInt GetNumActions, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn,-1, GetNumActions, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, Rand rn,IntToInt GetNumActions, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, rn, -1, GetNumActions, null,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn, -1,GetNumActions, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(hood, centerX, centerY, rn, -1,GetNumActions, evalAgent,action,wrapX,wrapY);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY) {
        return ApplyAgentsHood(hood, ItoX(centerI), ItoY(centerI), rn, -1,GetNumActions, evalAgent,action,wrapX,wrapY);
    }
    int ApplyAgentsHood(int[] hood, int centerX, int centerY, Rand rn, int nActions, IntToInt GetNumActions, AgentToBool<T> IsValidAgent, AgentToAction<T> Action, boolean wrapX, boolean wrapY) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        for (int i = 0; i < hood.length / 2; i++) {
            int x = hood[i * 2] + centerX;
            int y = hood[i * 2 + 1] + centerY;
            if (!Util.InDim(xDim, x)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(yDim, y)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            if(IsValidAgent !=null) {
                GetAgents(agents, IsValidAgent, x, y);
            }
            else{
                GetAgents(agents, x, y);
            }
        }
        int nFound = agents.size();
        if(GetNumActions!=null){
            nActions=GetNumActions.Eval(nFound);
            nActions=nActions<0?0:nActions;
        }
        if (nActions < 0 || nFound <= nActions) {
            for (T agent : agents) {
                Action.Action(agent,nFound);
            }
        } else {
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                T agent=agents.get(iRand);
                Action.Action(agent, nFound);
                agents.set(iRand,agents.get(nFound - i - 1));
            }
        }
        usedAgentSearches.add(agents);
        return nFound;
    }

//    public int ApplyAgentsInRad(int nActions, int[] hood, int centerX, int centerY, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY) {
//        ArrayList<T> agents = GetFreshAgentSearchArr();
//    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int GetPop(){
        //gets population
        return agents.pop;
    }
    public int MapEmptyHood(int[] hood,int centerX,int centerY){
        int toCheck=MapHood(hood,centerX,centerY);
        return FindEmptyIs(hood,toCheck);
    }
    public int MapEmptyHood(int[] hood,int centerI){
        return MapEmptyHood(hood,ItoX(centerI),ItoY(centerI));
    }
    public int MapOccupiedHood(int[] hood,int centerX,int centerY){
        int toCheck=MapHood(hood,centerX,centerY);
        return FindOccupiedIs(hood,toCheck);
    }
    public int MapOccupiedHood(int[] hood,int centerI){
        return MapOccupiedHood(hood,ItoX(centerI),ItoY(centerI));
    }
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,wrapX,wrapY);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY){
        int toCheck= HoodToIs(hood,ret,centerX,centerY);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY){
        int toCheck= HoodToIs(hood,ret,centerX,centerY);
        return FindOccupiedIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY, boolean wrapX, boolean wrapY){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,wrapX,wrapY);
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
    public Iterable<T> IterAgentsSafe(int x,int y){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int x,int y){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int i){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,i);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsInRadApprox(double x,double y,double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        AgentsInRad(agents,x,y,rad,wrapX,wrapY);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsInRad(double x,double y,double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        AgentsInRad(agents,x,y,rad,wrapX,wrapY);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent2DBase agent=(Agent2DBase)agents.get(i);
            if(DistSquared(x,y,agent.Xpt(),agent.Ypt(),wrapX,wrapY)<radSq){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        AgentsIterator ret= GetFreshAgentsIterator(agents);
        ret.numAgents=ct;
        return ret;
    }
    public Iterable<T> IterAgentsInRect(int x,int y,int xRad,int yRad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        int xStart=x-xRad;
        int xEnd=x+xRad;
        int yStart=y-yRad;
        int yEnd=y+yRad;
        int xWrap;
        int yWrap;
        for (int xi = xStart; xi <= xEnd; xi++) {
            xWrap=Util.ModWrap(xi,xDim);
            for (int yi = yStart; yi <= yEnd; yi++) {
                yWrap=Util.ModWrap(yi,yDim);
                GetAgents(agents,xWrap,yWrap);
            }
        }
        return GetFreshAgentsIterator(agents);
    }

    public Iterable<T> IterAgents(int[]hood, int centerX, int centerY){
        int[]inds =GetFreshHoodIs(hood.length);
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        int numIs =HoodToIs(hood, inds,centerX,centerY);
        for (int i = 0; i < numIs; i++) {
            GetAgents(myAgents,inds[i]);
        }
        usedHoodIs.add(inds);
        return GetFreshAgentsIterator(myAgents);
    }
    public Iterable<T> IterAgents(int[]hood, int centerI){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        return IterAgents(hood,ItoX(centerI),ItoY(centerI));
    }
    AgentsIterator GetFreshAgentsIterator(ArrayList<T> agents){
        AgentsIterator ret;
        if(usedIterIs.size()>0){
            ret=usedIterIs.remove(usedIterIs.size()-1);
        }
        else{
            ret=new AgentsIterator(this);
        }
        ret.Setup(agents);
        return ret;
    }

    private class AgentsIterator implements Iterator<T>,Iterable<T>{
        final AgentGrid2D<T> myGrid;
        ArrayList<T>myAgents;
        int numAgents;
        int iCount;
        AgentsIterator(AgentGrid2D<T> grid){
            myGrid=grid;
        }
        public void Setup(ArrayList<T> myAgents){
            this.myAgents=myAgents;
            iCount=0;
            numAgents=myAgents.size();
        }

        @Override
        public boolean hasNext() {
            if(iCount== numAgents){
                myGrid.usedAgentSearches.add(myAgents);
                myGrid.usedIterIs.add(this);
                return false;
            }
            return true;
        }

        @Override
        public T next() {
            T ret=myAgents.get(iCount);
            iCount++;
            return ret;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }
}