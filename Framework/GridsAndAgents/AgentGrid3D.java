package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Extend the Grid2unstackable class if you want a 3D lattice with one or more agents per typeGrid voxel
 * @param <T> the AgentSQ3D or AgentPT3D extending agent class that will inhabit the typeGrid
 */
public class AgentGrid3D<T extends AgentBaseSpatial> extends GridBase3D implements Iterable<T>{
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> agentSearches=new ArrayList<>();
    int iagentSearch=0;
    //int[] counts;


    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass, boolean wrapX, boolean wrapY, boolean wrapZ){
        super(x,y,z,wrapX,wrapY,wrapZ);
        agents=new InternalGridAgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
        //counts= new int[length];
    }
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass){
        super(x,y,z,false,false,false);
        agents=new InternalGridAgentList<T>(agentClass,this);
        grid=(T[])new AgentBaseSpatial[length];
        //counts= new int[length];
    }
//    void AddAgentToSquare(T agent,int iGrid){
//        //internal function, adds agent to typeGrid voxel
//        if(typeGrid[iGrid]==null) {
//            typeGrid[iGrid]=agent;
//        }
//        else{
//            typeGrid[iGrid].prevSq=agent;
//            agent.nextSq=typeGrid[iGrid];
//            typeGrid[iGrid]=agent;
//        }
//        counts[iGrid]++;
//    }
//    void RemAgentFromSquare(T agent,int iGrid){
//        //internal function, removes agent from typeGrid voxel
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
//        counts[iGrid]--;
//    }
//public void IncTick(){
//    agents.stateID++;
//}
    T GetNewAgent(){
        return agents.GetNewAgent();
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x, int y, int z){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y,z);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x, double y, double z){
        T newAgent=GetNewAgent();
        newAgent.Setup(x,y,z);
        return newAgent;
    }
    /**
     * returns an uninitialized agent at the specified index
     */
    public T NewAgentSQ(int index){
        T newAgent=agents.GetNewAgent();
        newAgent.Setup(index);
        return newAgent;
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

//    public void MultiThreadAgents(int nThreads, AgentStepFunction<T> StepFunction){
//        int last=agents.iLastAlive;
//        Util.MultiThread(nThreads,nThreads,(iThread)->{
//            ArrayList<T> agents=this.agents.agents;
//            int start=iThread/nThreads*last;
//            int end=(iThread+1)/nThreads*last;
//            for (int i = start; i < end; i++) {
//                T a=agents.get(i);
//                if(a.alive&&a.stateID<st){
//                    StepFunction.AgentStepFunction(a);
//                }
//            }
//        });
//    }
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
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ,wrapX,wrapY,wrapZ);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToEmptyIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ);
        return FindEmptyIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ);
        return FindOccupiedIs(ret,toCheck);
    }
    public int HoodToOccupiedIs(int[] hood, int[] ret, int centerX, int centerY, int centerZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        int toCheck= HoodToIs(hood,ret,centerX,centerY,centerZ,wrapX,wrapY,wrapZ);
        return FindOccupiedIs(ret,toCheck);
    }
    public void ChangeGridsSQ(T foreignAgent,int newX,int newY,int newZ){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY,newZ);
    }
    public void ChangeGridsPT(T foreignAgent,double newX,double newY,double newZ){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newX,newY,newZ);
    }
    public void ChangeGridsSQ(T foreignAgent,int newI){
        if(!foreignAgent.alive){
            throw new IllegalStateException("can't move dead agent between grids!");
        }
        if(foreignAgent.myGrid.getClass()!=this.getClass()){
            throw new IllegalStateException("can't move agent to a different type of typeGrid!");
        }
        foreignAgent.RemSQ();
        ((AgentGrid3D)foreignAgent.myGrid).agents.RemoveAgent(foreignAgent);
        agents.AddAgent(foreignAgent);
        foreignAgent.Setup(newI);
    }

    public int MapEmptyHood(int[] hood,int centerX,int centerY,int centerZ){
        int toCheck=MapHood(hood,centerX,centerY,centerZ);
        return FindEmptyIs(hood,toCheck);
    }
    public int MapEmptyHood(int[] hood,int centerI){
        int toCheck=MapHood(hood,centerI);
        return FindEmptyIs(hood,toCheck);
    }
    public int MapOccupiedHood(int[] hood,int centerX,int centerY,int centerZ){
        int toCheck=MapHood(hood,centerX,centerY,centerZ);
        return FindOccupiedIs(hood,toCheck);
    }
    public int MapOccupiedHood(int[] hood,int centerI){
        int toCheck=MapHood(hood,centerI);
        return FindOccupiedIs(hood,toCheck);
    }

    /**
     * returns the number of agents on the voxel at the specified coordinates
     */
//    public int PopAt(int x, int y, int z){
//        //gets population count at location
//        return counts[I(x,y,z)];
//    }
    /**
     * returns the number of agents on the voxel at the specified index
     */
//    public int PopAt(int i){
//        //gets population count at location
//        return counts[i];
//    }
    public T NewAgentPTSafe(double newX, double newY, double newZ, double fallbackX, double fallbackY, double fallbackZ, boolean wrapX, boolean wrapY, boolean wrapZ){
        if (In(newX, newY,newZ)) {
            return NewAgentPT(newX, newY,newZ);
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
        if (wrapZ) {
            newZ = Util.ModWrap(newZ, zDim);
        } else if (!Util.InDim(zDim, newZ))
            newZ = fallbackZ;
        if(!In(newX,newY,newZ)){
            System.out.println("");
        }
        return NewAgentPT(newX,newY,newZ);
    }
    public T NewAgentPTSafe(double newX, double newY, double newZ, double fallbackX, double fallbackY, double fallbackZ){
        if (In(newX, newY,newZ)) {
            return NewAgentPT(newX, newY,newZ);
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
        if (wrapZ) {
            newZ = Util.ModWrap(newZ, zDim);
        } else if (!Util.InDim(zDim, newZ))
            newZ = fallbackZ;
        return NewAgentPT(newX,newY,newZ);
    }
    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public ArrayList<T> AllAgents(){
        return new ArrayList<>(this.agents.GetAllAgents());
    }

    /**
     * Gets the last agent to move to the specified coordinates
     * not recommended if the model calls for multiple agents on the same square, as it will only return one of these
     * returns null if no agent exists
     */
    public T GetAgent(int x, int y, int z){
        return grid[I(x,y,z)];
    }
    public T GetAgentSafe(int x, int y,int z) {
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
        if (wrapZ) {
            z = Util.ModWrap(z, yDim);
        } else if (!Util.InDim(yDim, z)) {
            return null;
        }
        return grid[I(x,y,z)];
    }

    /**
     * Gets the last agent to move to the specified index
     * not recommended if the model calls for multiple agents on the same square, as it will only return one of these
     * returns null if no agent exists
     */
    public T GetAgent(int index){
        return grid[index];
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

    public void AgentsInRad(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad, boolean wrapX, boolean wrapY, boolean wrapZ){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, I(retX, retY, retZ));
                }
            }
        }
    }
    public void AgentsInRad(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad){
        int nAgents;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, I(retX, retY, retZ));
                }
            }
        }
    }
    ArrayList<T> GetFreshAgentSearchArr(){
        ArrayList<T> agents;
        if(iagentSearch>=agentSearches.size()){
            agents=new ArrayList<T>();
            agentSearches.add(agents);
        }
        else{
            agents=agentSearches.get(iagentSearch);
            agents.clear();
        }
        return agents;
    }

    public int ApplyAgentsRad(double rad, double x, double y,double z, AgentRadDispToAction3D<T> action){
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        int nAgents=0;
        double radSq=rad*rad;
        for (int xSq = (int)Math.floor(x-rad); xSq <(int)Math.ceil(x+rad) ; xSq++) {
            for (int ySq = (int)Math.floor(y-rad); ySq <(int)Math.ceil(y+rad) ; ySq++) {
                for (int zSq = (int)Math.floor(z-rad); zSq <(int)Math.ceil(z+rad) ; zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(xDim, retX);
                    boolean inY = Util.InDim(yDim, retY);
                    boolean inZ = Util.InDim(zDim, retZ);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && ! inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.ModWrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.ModWrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.ModWrap(retZ, zDim);
                    }
                    GetAgents(agents, retX, retY, retZ);
                    for (int i = 0; i < agents.size(); i++) {
                        T agent = agents.get(i);
                        double xDisp = ((Agent3DBase) (agent)).Xpt() - x;
                        double yDisp = ((Agent3DBase) (agent)).Ypt() - y;
                        double zDisp = ((Agent3DBase) (agent)).Zpt() - z;
                        double distSq = xDisp * xDisp + yDisp * yDisp + zDisp*zDisp;
                        if (distSq <= radSq) {
                            action.Action(agent, xDisp, yDisp,zDisp, distSq);
                            nAgents++;
                        }
                    }
                    agents.clear();
                }
            }
        }
        iagentSearch--;
        return nAgents;
    }

    public int ApplyAgentsSQ(int i, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,null,action);
    }
    public int ApplyAgentsSQ(int x, int y,int z, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y,z), null,null,action);
    }
    public int ApplyAgentsSQ(int i, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, i, null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int x, int y,int z, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(-1, I(x,y,z), null,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int x, int y,int z, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y,z), rn,null,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, i, rn,null,action);

    }
    public int ApplyAgentsSQ(int nActions, int x, int y,int z, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsSQ(nActions, I(x,y,z), rn,evalAgent,action);
    }
    public int ApplyAgentsSQ(int nActions, int i, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
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
        iagentSearch--;
        return nFound;
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, int centerZ, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, null, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), null, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerX, int centerY, int centerZ, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, null, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[]hood, int centerI, int nActions, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,centerX,centerY,centerZ, rn, null, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(nActions, hood,centerX,centerY,centerZ, rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int nActions, int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyAgentsHood(nActions, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, null, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn, IntToInt GetNumActions,AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, rn, GetNumActions, null,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn, AgentToBool<T> evalAgent,IntToInt GetNumActions, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerX, int centerY, int centerZ, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action) {
        return ApplyAgentsHood(-1, hood,centerX,centerY,centerZ, rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    public int ApplyAgentsHood(int[] hood, int centerI, Rand rn,IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        return ApplyAgentsHood(-1, hood,ItoX(centerI),ItoY(centerI),ItoZ(centerI), rn, GetNumActions, evalAgent,action,wrapX,wrapY,wrapZ);
    }
    int ApplyAgentsHood(int nActions, int[] hood, int centerX, int centerY, int centerZ, Rand rn, IntToInt GetNumActions, AgentToBool<T> evalAgent, AgentToAction<T> action, boolean wrapX, boolean wrapY, boolean wrapZ) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        for (int i = 0; i < hood.length / 3; i++) {
            int x = hood[i * 2] + centerX;
            int y = hood[i * 2 + 1] + centerY;
            int z = hood[i * 2 + 2] + centerZ;
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
            if (!Util.InDim(zDim, z)) {
                if (wrapZ) {
                    z = Util.ModWrap(z, zDim);
                } else {
                    continue;
                }
            }
            if(evalAgent!=null) {
                GetAgents(agents, evalAgent, x, y,z);
            }
            else{
                GetAgents(agents, x, y,z);
            }
        }
        int nFound = agents.size();
        if (nActions < 0 || nFound <= nActions) {
            for (T agent : agents) {
                action.Action(agent,nFound);
            }
        } else {
            for (int i = 0; i < nActions; i++) {
                int iRand = rn.Int(nFound - i);
                T agent=agents.get(iRand);
                action.Action(agent, nFound);
                agents.set(iRand,agents.get(nFound - i - 1));
            }
        }
        iagentSearch--;
        return nFound;
    }


    public T RandomAgent(Rand rn){
        CleanAgents();
        if(GetPop()==0){
            return null;
        }
        return agents.agents.get(rn.Int(GetPop()));
    }
    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     * @param retAgentList the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>retAgentList, int x, int y, int z){
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }
    public void GetAgents(ArrayList<T>retAgentList, AgentToBool<T>evalAgent, int x, int y, int z){
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquareEval(retAgentList,evalAgent);
        }
    }

    public void GetAgents(ArrayList<T>retAgentList, AgentToBool<T>evalAgent, int i){
        T agent= GetAgent(i);
        if(agent!=null){
            agent.GetAllOnSquareEval(retAgentList,evalAgent);
        }
    }


    public void GetAgentsSafe(ArrayList<T>retAgentList, int x, int y, int z){
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
        if (wrapZ) {
            z = Util.ModWrap(z, yDim);
        } else if (!Util.InDim(yDim, z)) {
            return;
        }
        T agent= GetAgent(x,y,z);
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }
    /**
     * appends to the provided arraylist all agents on the square at the specified index
     * @param retAgentList the arraylist ot be added to
     */
    public void GetAgents(ArrayList<T>retAgentList, int index){
        T agent= grid[index];
        if(agent!=null){
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int GetPop(){
        //gets population
        return agents.pop;
    }

    @Override
    public Iterator<T> iterator() {
            return agents.iterator();
    }
}
