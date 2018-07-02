package Framework.GridsAndAgents;

import Framework.Interfaces.AgentRadDispToAction2D;
import Framework.Interfaces.AgentToBool;
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
public class AgentGrid1D<T extends AgentBaseSpatial> extends GridBase1D implements Iterable<T>{
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches =new ArrayList<>();
    ArrayList<AgentsIterator1D> usedIterIs =new ArrayList<>();
    final int[] counts;
    final double moveSafeXdim;

    public void _PassAgentConstructor(Class<T> agentClass){
        agents.SetupConstructor(agentClass);
    }

    public T GetAgent(int x) {
        return grid[x];
    }
    public T GetAgentSafe(int x) {
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return null;
        }
        return grid[x];
    }
    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid1D(int x, Class<T> agentClass, boolean wrapX){
        super(x,wrapX);
        //creates a new typeGrid with given dimensions
        agents=new InternalGridAgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
        counts = new int[length];
        moveSafeXdim=Math.nextAfter(xDim,0);
    }
    public AgentGrid1D(int x, Class<T> agentClass){
        this(x,agentClass,false);
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
    public T NewAgentSQ(int x){
        T newAgent=GetNewAgent();
        newAgent.Setup(x);
        return newAgent;
    }
//    public void IncTick(){
//        super.IncTick();
//        agents.stateID++;
//    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x){
        T newAgent=GetNewAgent();
        newAgent.Setup(x);
        return newAgent;
    }

    public T NewAgentPTSafe(double newX, double fallbackX){
        if (In(newX)) {
            return NewAgentPT(newX);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            newX = fallbackX;
        }
        return NewAgentPT(newX);
    }
    public T NewAgentPTSafe(double newX){
        if (In(newX)) {
            return NewAgentPT(newX);
        }
        if (wrapX) {
            newX = Util.ModWrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            return null;
        }
        return NewAgentPT(newX);
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

    public Iterator<T> iterator(){
        return agents.iterator();
    }
    public List<T> _AllAgents(){return (List<T>)this.agents.GetAllAgents();}
    public List<T> _AllDeads(){return (List<T>)this.agents.GetAllDeads();}

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     * @param putHere the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>putHere, int x){
        T agent= grid[x];
        if(agent!=null) {
            agent.GetAllOnSquare(putHere);
        }
    }
    public void GetAgentsSafe(ArrayList<T>putHere, int x){
        if (wrapX) {
            x = Util.ModWrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        T agent= grid[x];
        if(agent!=null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    public void GetAgents(ArrayList<T>putHere,AgentToBool EvalAgent,int x){
        T agent=grid[x];
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
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double rad, boolean wrapX) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.ModWrap(retX, xDim);
            }
            GetAgents(retAgentList, retX);
        }
    }
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double rad, boolean wrapX){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.ModWrap(retX, xDim);
            }
            GetAgents(retAgentList, (agent) -> {
                Agent1DBase a = (Agent1DBase) agent;
                return Dist(a.Xpt(), x) < rad;
            }, retX);
        }
    }
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int width, int height){
        int xEnd=x+width;
        int xWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX = Util.InDim(xi, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            xWrap = xi;
            if (wrapX && !inX) {
                xWrap = Util.ModWrap(xi, xDim);
            }
            GetAgents(retAgentList, xWrap);
        }
    }
    public void GetAgentsHood(ArrayList<T> retAgentList,int[] hood,int centerX){
        int iStart=hood.length/3;
        for(int i=iStart;i<hood.length;i+=2) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.ModWrap(x, xDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x);
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
//        tick=0;
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

    public int PopAt(int x){
        T agent=grid[x];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquare();
    }
    public int PopAt(int x,AgentToBool EvalAgent){
        T agent=grid[x];
        if(agent==null){
            return 0;
        }
        return agent.GetCountOnSquareEval(EvalAgent);
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
//            ct+=CountAt(Is[i]); //        }
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
                if((!wrapX&&!inX)){
                    continue;
                }
                if(wrapX&&!inX){
                    retX= Util.ModWrap(retX,xDim);
                }
                GetAgents(agents, retX);
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
    public int MapEmptyHood(int[] hood,int centerX){
        return MapHood(hood,centerX,(i)->GetAgent(i)==null);
    }
    public int MapOccupiedHood(int[] hood,int centerX){
        return MapHood(hood,centerX,(i)->GetAgent(i)!=null);
    }
    public Iterable<T> IterAgentsSafe(int x){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgents(int x){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRadApprox(double x, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,rad,wrapX);
        return GetFreshAgentsIterator(agents);
    }
    public Iterable<T> IterAgentsRad(double x, double rad){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRad(agents,x,rad,wrapX);
        return agents;
    }
    public Iterable<T> IterAgentsRect(int x, int width, int height){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,width,height);
        return GetFreshAgentsIterator(agents);
    }

    public Iterable<T> IterAgentsHood(int[]hood, int centerX){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHood(myAgents,hood,centerX);
        return GetFreshAgentsIterator(myAgents);
    }
    public Iterable<T> IterAgentsHoodMapped(int[]hood,int hoodLen){
        ArrayList<T> myAgents=GetFreshAgentSearchArr();
        GetAgentsHoodMapped(myAgents,hood,hoodLen);
        return GetFreshAgentsIterator(myAgents);
    }
    AgentsIterator1D GetFreshAgentsIterator(ArrayList<T> agents){
        AgentsIterator1D ret;
        if(usedIterIs.size()>0){
            ret=usedIterIs.remove(usedIterIs.size()-1);
        }
        else{
            ret=new AgentsIterator1D(this);
        }
        ret.Setup(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentSafe(int x,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsSafe(agents,x);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgent(int x,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgents(agents,x);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRad(double x,double rad,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,rad,wrapX);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent2DBase agent=(Agent2DBase)agents.get(i);
            if(Dist(x,agent.Xpt())<radSq){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRad(double x,double rad,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents,x,rad,wrapX);
        int ct=0;
        double radSq=rad*rad;
        for (int i = 0; i < agents.size(); i++) {
            Agent2DBase agent=(Agent2DBase)agents.get(i);
            if(Dist(x,agent.Xpt())<radSq&&EvalAgent.EvalAgent((T)agent)){
                agents.set(ct,agents.get(i));
                ct++;
            }
        }
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRect(int x,int width,int height,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,width,height);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentRect(int x,int width,int height,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsRect(agents,x,width,height);
        int ct=SubsetAgents(agents,EvalAgent);
        T ret= agents.get(rn.Int(ct));
        usedAgentSearches.add(agents);
        return ret;
    }

    public T RandomAgentHood(int[] hood,int x,Rand rn){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x);
        T ret= agents.get(rn.Int(agents.size()));
        usedAgentSearches.add(agents);
        return ret;
    }
    public T RandomAgentHood(int[] hood,int x,Rand rn,AgentToBool<T> EvalAgent){
        ArrayList<T> agents=GetFreshAgentSearchArr();
        GetAgentsHood(agents,hood,x);
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

    private class AgentsIterator1D implements Iterator<T>,Iterable<T>{
        final AgentGrid1D<T> myGrid;
        ArrayList<T>myAgents;
        int numAgents;
        int iCount;
        AgentsIterator1D(AgentGrid1D<T> grid){
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