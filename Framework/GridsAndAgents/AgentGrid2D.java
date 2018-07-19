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
    ArrayList<AgentsIterator2D> usedIterIs =new ArrayList<>();
    int[] counts;
    final double moveSafeXdim;
    final double moveSafeYdim;

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
        } else if (!Util.InDim(x, xDim)) {
            return null;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(y, yDim)) {
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
        counts= new int[length];
        moveSafeXdim=Math.nextAfter(xDim,0);
        moveSafeYdim=Math.nextAfter(yDim,0);
    }
    public AgentGrid2D(int x, int y, Class<T> agentClass){
        this(x,y,agentClass,false,false);

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
        return agents.GetNewAgent(tick);
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
    public T NewAgentPTSafe(double newX, double newY){
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            return null;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim)) {
            return null;
        }
        return NewAgentPT(newX,newY);
    }

    public T NewAgentPTSafe(double newX, double newY, double fallbackX, double fallbackY){
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.ModWrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim)) {
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
//    public void ChangeGridsSQ(T foreignAgent,int newX,int newY){
//        if(!foreignAgent.alive){
//            throw new IllegalStateException("can't move dead agent between grids!");
//        }
//        if(foreignAgent.myGrid.getClass()!=this.getClass()){
//            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
//        }
//        foreignAgent.RemSQ();
//        agents.AddAgent(foreignAgent);
//        ((AgentGrid2D)foreignAgent.myGrid).agents.NullAgent(foreignAgent);
//        foreignAgent.myGrid=this;
//        foreignAgent.Setup(newX,newY);
//    }
//    public void ChangeGridsPT(T foreignAgent,double newX,double newY){
//        if(!foreignAgent.alive){
//            throw new IllegalStateException("can't move dead agent between grids!");
//        }
//        if(foreignAgent.myGrid.getClass()!=this.getClass()){
//            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
//        }
//        foreignAgent.RemSQ();
//        agents.AddAgent(foreignAgent);
//        ((AgentGrid2D)foreignAgent.myGrid).agents.NullAgent(foreignAgent);
//        foreignAgent.myGrid=this;
//        foreignAgent.Setup(newX,newY);
//    }
//    public void ChangeGridsSQ(T foreignAgent,int newI){
//        if(!foreignAgent.alive){
//            throw new IllegalStateException("can't move dead agent between grids!");
//        }
//        if(foreignAgent.myGrid.getClass()!=this.getClass()){
//            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
//        }
//        foreignAgent.RemSQ();
//        ((AgentGrid2D)foreignAgent.myGrid).agents.NullAgent(foreignAgent);
//        agents.AddAgent(foreignAgent);
//        foreignAgent.myGrid=this;
//        foreignAgent.Setup(newI);
//    }

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
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        if (wrapY) {
            y = Util.ModWrap(y, yDim);
        } else if (!Util.InDim(y, yDim)) {
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
    int SubsetAgents(ArrayList<T>agents, AgentToBool<T> EvalAgent){
        int len=agents.size();
        int ret=0;
        for (int i = 0; i < len; i++) {
            T agent=agents.get(i);
            if(EvalAgent.EvalAgent(agent)){
                agents.set(ret,agent);
                ret++;
            }
        }
        return ret;
    }
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double rad, boolean wrapX, boolean wrapY){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                int retX=xSq; int retY=ySq;
                boolean inX= Util.InDim(retX, xDim);
                boolean inY= Util.InDim(retY, yDim);
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
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double rad, boolean wrapX, boolean wrapY){
        int nAgents;
        double radSq=rad*rad;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                int retX=xSq; int retY=ySq;
                boolean inX= Util.InDim(retX, xDim);
                boolean inY= Util.InDim(retY, yDim);
                if((!wrapX&&!inX)||(!wrapY&&!inY)){
                    continue;
                }
                if(wrapX&&!inX){
                    retX= Util.ModWrap(retX,xDim);
                }
                if(wrapY&&!inY){
                    retY= Util.ModWrap(retY,yDim);
                }
                GetAgents(retAgentList,(agent)->{
                    Agent2DBase a=(Agent2DBase)agent;
                    return Util.DistSquared(a.Xpt(),a.Ypt(),x,y,xDim,yDim,wrapX,wrapY)<radSq;
                },retX,retY);
            }
        }
    }
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y, int width, int height){
        int xEnd=x+width;
        int yEnd=y+height;
        int xWrap;
        int yWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX= Util.InDim(xi, xDim);
            if((!wrapX&&!inX)){
                continue;
            }
            xWrap=xi;
            if(wrapX&&!inX){
                xWrap= Util.ModWrap(xi,xDim);
            }
            for (int yi = y; yi <= yEnd; yi++) {
                boolean inY= Util.InDim(yi, yDim);
                if((!wrapY&&!inY)){
                    continue;
                }
                yWrap=yi;
                if(wrapY&&!inY){
                    yWrap= Util.ModWrap(yi,yDim);
                }
                GetAgents(retAgentList,xWrap,yWrap);
            }
        }
    }
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double rad){
        GetAgentsRadApprox(retAgentList,x,y,rad,wrapX,wrapY);
    }
    public void GetAgentsHood(ArrayList<T> retAgentList,int[] hood,int centerX,int centerY){
        int iStart=hood.length/3;
        for(int i=iStart;i<hood.length;i+=2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.ModWrap(y, yDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y);
        }

    }
    public void GetAgentsHoodMapped(ArrayList<T> retAgentList,int[] hood,int hoodLen){
        for(int i=0;i<hoodLen;i++) {
            GetAgents(retAgentList,hood[i]);
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
        if(Pop()>0){
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        ResetTick();
    }
    public void ResetHard(){
//        IncTick();
        for (T a : this) {
            a.Dispose();
        }
        if(Pop()>0){
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

    public int PopAt(int i){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquare();
    }
    public int PopAt(int x,int y){
        return PopAt(I(x,y));
    }
    public int PopAt(int i,AgentToBool EvalAgent){
        T agent=grid[i];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquareEval(EvalAgent);
    }
    public int PopAt(int x,int y,AgentToBool EvalAgent){
        return PopAt(I(x,y),EvalAgent);
    }
//    public int CountInHood(int[]hood,int i){
//        return CountInHood(hood,ItoX(i),ItoY(i),wrapX,wrapY);
//    }
//    public int CountInHood(int[]hood,int i,AgentToBool<T> EvalAgent){
//        return CountInHood(hood,ItoX(i),ItoY(i),EvalAgent,wrapX,wrapY);
//    }
//    public int CountInHood(int[]hood,int centerX,int centerY,boolean wrapX,boolean wrapY){
//        int ct=0;
//        int iStart=hood.length/3;
//        for(int i=iStart;i<hood.length;i+=2) {
//            int x = hood[i] + centerX;
//            int y = hood[i + 1] + centerY;
//            if (!Util.InDim(xDim, x)) {
//                if (wrapX) {
//                    x = Util.ModWrap(x, xDim);
//                } else {
//                    continue;
//                }
//            }
//            if (!Util.InDim(yDim, y)) {
//                if (wrapY) {
//                    y = Util.ModWrap(y, yDim);
//                } else {
//                    continue;
//                }
//            }
//            ct+=CountAt(x,y);
//        }
//        return ct;
//    }
//    public int CountInHood(int[]hood,int x,int y,AgentToBool<T> EvalAgent,boolean wrapX,boolean wrapY){
//        int ct=0;
//        int[]Is=GetFreshHoodIs(hood.length/2);
//        int searchLen=HoodToIs(hood,Is,x,y,wrapX,wrapY);
//        for (int i = 0; i < searchLen; i++) {
//            ct+=CountAt(Is[i]);
//        }
//        usedHoodIs.add(Is);
//        return ct;
//    }
//    public int CountInHood(int[]hood,int x,int y){
//        return CountInHood(hood,x,y,wrapX,wrapY);
//    }
    public T RandomAgent(Rand rn){
        CleanAgents();
        if(Pop()==0){
            return null;
        }
        return agents.agents.get(rn.Int(Pop()));
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
    public int ApplyAgentsRad(double rad, double x, double y, AgentRadDispToAction2D<T> action){
        ArrayList<T> agents = GetFreshAgentSearchArr();
        int nAgents=0;
        double radSq=rad*rad;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                int retX=xSq; int retY=ySq;
                boolean inX= Util.InDim(retX, xDim);
                boolean inY= Util.InDim(retY, yDim);
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
//    public int ApplyAgentsInRad(int nActions, int[] hood, int centerX, int centerY, Rand rn, AgentToBool<T> evalAgent, AgentAction<T> action, boolean wrapX, boolean wrapY) {
//        ArrayList<T> agents = GetFreshAgentSearchArr();
//    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int Pop(){
        //gets population
        return agents.pop;
    }
    public int MapEmptyHood(int[] hood,int centerX,int centerY){
        return MapHood(hood,centerX,centerY,(i,x,y)->GetAgent(i)==null);
    }
    public int MapEmptyHood(int[] hood,int centerI){
        return MapEmptyHood(hood,ItoX(centerI),ItoY(centerI));
    }
    public int MapOccupiedHood(int[] hood,int centerX,int centerY){
        return MapHood(hood,centerX,centerY,(i,x,y)->GetAgent(i)!=null);
    }
    public int MapOccupiedHood(int[] hood,int centerI){
        return MapOccupiedHood(hood,ItoX(centerI),ItoY(centerI));
    }
    public Iterable<T> IterAgentsSafe(int x,int y){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int x, int y){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int i){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,i);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRadApprox(double x, double y, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,y,rad,wrapX,wrapY);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRad(double x, double y, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRad(agents,x,y,rad,wrapX,wrapY);
        return agents;
    }
    public Iterable<T> IterAgentsRect(int x, int y, int width, int height){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,width,height);
        return GetFreshAgentsIterator(agents);
    }

    public Iterable<T> IterAgentsHood(int[]hood, int centerX, int centerY){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHood(myAgents,hood,centerX,centerY);
        return GetFreshAgentsIterator(myAgents);
    }
    public Iterable<T> IterAgentsHood(int[]hood, int centerI){
        return IterAgentsHood(hood,ItoX(centerI),ItoY(centerI));
    }
    public Iterable<T> IterAgentsHoodMapped(int[]hood,int hoodLen){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(myAgents,hood,hoodLen);
        return GetFreshAgentsIterator(myAgents);
    }
    AgentsIterator2D GetFreshAgentsIterator(ArrayList<T> agents){
        AgentsIterator2D ret;
        if(usedIterIs.size()>0){
            ret=usedIterIs.remove(usedIterIs.size()-1);
        }
        else{
            ret=new AgentsIterator2D(this);
        }
        ret.Setup(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,int y,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,int y,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x,y);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,int y,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,int y,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x,y);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int i,Rand rn){
        return RandomAgent(ItoX(i),ItoY(i),rn);
    }
    public T RandomAgent(int i,Rand rn,AgentToBool<T> EvalAgent){
        return RandomAgent(ItoX(i),ItoY(i),rn,EvalAgent);
    }
    public T RandomAgentRad(double x,double y,double rad,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,y,rad,wrapX,wrapY);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent2DBase agent=(Agent2DBase)agents.get(i);
            if(DistSquared(x,y,agent.Xpt(),agent.Ypt())<radSq){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRad(double x,double y,double rad,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,y,rad,wrapX,wrapY);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent2DBase agent=(Agent2DBase)agents.get(i);
            if(DistSquared(x,y,agent.Xpt(),agent.Ypt())<radSq&&EvalAgent.EvalAgent((T)agent)){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRect(int x,int y,int width,int height,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,width,height);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRect(int x,int y,int width,int height,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,y,width,height);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHood(int[] hood,int x,int y,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x,y);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHood(int[] hood,int x,int y,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x,y);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHoodMapped(int[]hood,int hoodLen,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents,hood,hoodLen);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHoodMapped(int[]hood,int hoodLen,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents,hood,hoodLen);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }

    private class AgentsIterator2D implements Iterator<T>,Iterable<T>{
        final AgentGrid2D<T> myGrid;
        ArrayList<T>myAgents;
        int numAgents;
        int iCount;
        AgentsIterator2D(AgentGrid2D<T> grid){
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