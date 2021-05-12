package HAL.GridsAndAgents;

import HAL.Interfaces.AgentRadDispToAction1D;
import HAL.Interfaces.AgentToBool;
import HAL.Interfaces.Grid1D;
import HAL.Rand;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * AgentGrid1Ds can hold any type of 1D agent
 * @param <T> the type of agent that the AgentGrid1D will hold
 */
public class AgentGrid1D<T extends AgentBaseSpatial> implements Grid1D,Iterable<T>,Serializable {
    final public int xDim;
    final public int length;
    public boolean wrapX;
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches = new ArrayList<>();
    ArrayList<AgentsIterator1D> usedIterIs = new ArrayList<>();
    final int[] counts;
    final double moveSafeXdim;
    int tick;

    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class. the wrap booleans specify whether to domain should use wraparound or stop at the
     * boundary
     */
    public AgentGrid1D(int x, Class<T> agentClass, boolean wrapX) {
        //creates a new typeGrid with given dimensions
        this.xDim=x;
        this.length=x;
        this.wrapX=wrapX;
        this.tick=0;
        agents = new InternalGridAgentList<T>(agentClass, this);
        grid = (T[]) new AgentBaseSpatial[length];
        counts = new int[length];
        moveSafeXdim = Math.nextAfter(xDim, 0);
    }

    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class.
     */
    public AgentGrid1D(int x, Class<T> agentClass) {
        this(x, agentClass, false);
    }


    /**
     * meant to be used specifically in conjunction with the LoadState Utils function. LoadState won't by default setup
     * the grid constructor, so this function must be called as well for the loaded grid to be able to create more
     * agents. pass in the same class argument as is normally used by the grid constructor
     */
    public void _PassAgentConstructor(Class<T> agentClass) {
        agents.SetupConstructor(agentClass);
    }

    /**
     * Gets a single agent at the specified grid square, beware using this function with stackable agents, as it will
     * only return one of the stack of agents. This function is recommended for the Unstackable Agents, as it tends to
     * perform better than the other methods for single agent accesses.
     */
    public T GetAgent(int x) {
        return grid[x];
    }

    /**
     * Same as GetAgent above, but if x or y are outside the domain, it will apply wrap around if wrapping is enabled,
     * or return null.
     */
    public T GetAgentSafe(int x) {
        if (wrapX) {
            x = Util.Wrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return null;
        }
        return grid[x];
    }


    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates, will apply wraparound if the coordinates are outside
     * the domain
     */
    public T NewAgentPTSafe(double newX) {
        if (In(newX)) {
            return NewAgentPT(newX);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            return null;
        }
        return NewAgentPT(newX);
    }

    /**
     * returns an uninitialized agent at the specified coordinates, will apply wraparound or use the fallback if the
     * coordinates are outside the domain
     */
    public T NewAgentPTSafe(double newX, double fallbackX) {
        if (In(newX)) {
            return NewAgentPT(newX);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            newX = fallbackX;
        }
        return NewAgentPT(newX);
    }

    /**
     * calls CleanAgents, then ShuffleAgents
     */
    public void CleanShuffle(Rand rn) {
        CleanAgents();
        ShuffleAgents(rn);
    }

    /**
     * shuffles the agent list to randomize iteration. do not call this while in the middle of iteration
     */
    public void ShuffleAgents(Rand rn) {
        agents.ShuffleAgents(rn);
    }

    /**
     * cleans the list of agents, removing dead ones, may improve the efficiency of the agent iteration if many agents
     * have died do not call this while in the middle of iteration
     */
    public void CleanAgents() {
        agents.CleanAgents();
    }

    /**
     * returns the list of all agents as an unmodifiable list
     */
    public List<T> AllAgents() {
        return Collections.unmodifiableList(this.agents.GetAllAgents());
    }

    /**
     * returns the list of all dead agents as an unmodifiable list
     */
    public List<T> AllDeads() {
        return Collections.unmodifiableList(this.agents.GetAllDeads());
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     */
    public void GetAgents(ArrayList<T> putHere, int x) {
        T agent = grid[x];
        if (agent != null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> putHere, int x, AgentToBool<T> EvalAgent) {
        T agent = grid[x];
        if (agent != null) {
            agent.GetAllOnSquare(putHere, EvalAgent);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will apply wraparound if
     * the coordinates are outside the domain
     */
    public void GetAgentsSafe(ArrayList<T> putHere, int x) {
        if (wrapX) {
            x = Util.Wrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        T agent = grid[x];
        if (agent != null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will apply wraparound if
     * the coordinates are outside the domain, will subset only agents for which EvalAgent returns true
     */
    public void GetAgentsSafe(ArrayList<T> putHere, int x, AgentToBool<T> EvalAgent) {
        if (wrapX) {
            x = Util.Wrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        T agent = grid[x];
        if (agent != null) {
            agent.GetAllOnSquare(putHere, EvalAgent);
        }
    }


    /**
     * subsets only agents for which EvalAgent returns true in the provided ArrayList
     */
    int SubsetAgents(ArrayList<T> agents, AgentToBool<T> EvalAgent) {
        int len = agents.size();
        int ret = 0;
        for (int i = 0; i < len; i++) {
            T agent = agents.get(i);
            if (EvalAgent.EvalAgent(agent)) {
                agents.set(ret, agent);
                ret++;
            }
        }
        return ret;
    }

    /**
     * quickly gets all agents that are within rad, but also includes some that are further away than rad, an additional
     * distance check should be used to properly subset this group
     */
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double rad) {
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX);
        }
    }

    /**
     * quickly gets all agents that are within rad, but also includes some that are further away than rad, an additional
     * distance check should be used to properly subset this group. only agents forwhich EvalAgent returns true will be
     * added
     */
    public void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX, EvalAgent);
        }
    }
    /**
     * gets all agents that are within rad, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList,final ArrayList<double[]> displacementInfo, final double x, final double rad) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX, (agent) -> {
                Agent1DBase a = (Agent1DBase) agent;
                double disp=DispX(x,((Agent1DBase) agent).Xpt());
                double dist=Math.abs(disp);
                if(dist<rad) {
                    for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                        displacementInfo.add(new double[2]);
                    }
                    double[] info = displacementInfo.get(retAgentList.size());
                    info[0]=dist;
                    info[1]=disp;
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final ArrayList<double[]> displacementInfo, final double x, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX, (agent) -> {
                Agent1DBase a = (Agent1DBase) agent;
                double disp=DispX(x,((Agent1DBase) agent).Xpt());
                double dist=Math.abs(disp);
                if(dist<rad&&EvalAgent.EvalAgent(agent)) {
                    for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                        displacementInfo.add(new double[2]);
                    }
                    double[] info = displacementInfo.get(retAgentList.size());
                    info[0]=dist;
                    info[1]=disp;
                    return true;
                }
                return false;
            });
        }
    }

    /**
     * gets all agents that are within rad, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double rad) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX, (agent) -> {
                Agent1DBase a = (Agent1DBase) agent;
                return Dist(a.Xpt(), x) < rad;
            });
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            int retX = xSq;
            boolean inX = Util.InDim(retX, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            if (wrapX && !inX) {
                retX = Util.Wrap(retX, xDim);
            }
            GetAgents(retAgentList, retX, (agent) -> {
                Agent1DBase a = (Agent1DBase) agent;
                return Dist(a.Xpt(), x) < rad && EvalAgent.EvalAgent(agent);
            });
        }
    }

    /**
     * gets all agents within the rectangle argument defined by the bottom corner and the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int width) {
        int xEnd = x + width;
        int xWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX = Util.InDim(xi, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            xWrap = xi;
            if (wrapX && !inX) {
                xWrap = Util.Wrap(xi, xDim);
            }
            GetAgents(retAgentList, xWrap);
        }
    }

    /**
     * gets all agents forwhich evalAgent returns true within the rectangle argument defined by the bottom corner and
     * the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int width, AgentToBool<T> EvalAgent) {
        int xEnd = x + width;
        int xWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX = Util.InDim(xi, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            xWrap = xi;
            if (wrapX && !inX) {
                xWrap = Util.Wrap(xi, xDim);
            }
            GetAgents(retAgentList, xWrap, EvalAgent);
        }
    }

    /**
     * gets all agents within the given neighborhood and adds them to the ArrayList argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x);
        }
    }

    /**
     * gets all agents within the given neighborhood forwhich EvalAgent returns true and adds them to the ArrayList
     * argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX, AgentToBool<T> EvalAgent) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, EvalAgent);
        }

    }

    /**
     * gets all agents in the provided neighborhood, assumes that the neighborhood has already been mapped to a
     * location
     */
    public void GetAgentsHoodMapped(ArrayList<T> retAgentList, int[] hood, int hoodLen) {
        for (int i = 0; i < hoodLen; i++) {
            GetAgents(retAgentList, hood[i]);
        }
    }

    /**
     * gets all agents in the provided neighborhood forwhich EvalAgent returns true, assumes that the neighborhood has
     * already been mapped to a location
     */
    public void GetAgentsHoodMapped(ArrayList<T> retAgentList, int[] hood, int hoodLen, AgentToBool<T> EvalAgent) {
        for (int i = 0; i < hoodLen; i++) {
            GetAgents(retAgentList, hood[i]);
        }
    }


    /**
     * calls dispose on all agents, resets the tick timer to 0.
     */
    public void Reset() {
        for (T a : this) {
            a.Dispose();
        }
        if (Pop() > 0) {
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        ResetTick();
    }

    /**
     * calls dispose on all agents and completely resets the internal agentlist, also resets the tick timer to 0.
     */
    public void ResetHard() {
        for (T a : this) {
            a.Dispose();
        }
        if (Pop() > 0) {
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        this.agents.Reset();
        ResetTick();
    }

    /**
     * gets the population at a specific location
     */
    public int PopAt(int x) {
        T agent = grid[x];
        if (agent == null) {
            return 0;
        }
        return agent.GetCountOnSquare();
    }

    /**
     * gets the population at a specific location, subsetting to only those for which EvalAgent returns true
     */
    public int PopAt(int x, AgentToBool EvalAgent) {
        T agent = grid[x];
        if (agent == null) {
            return 0;
        }
        return agent.GetCountOnSquare(EvalAgent);
    }

    /**
     * gets a random agent from the grid, be careful not to use this during iteration over the grid
     */
    public T RandomAgent(Rand rn) {
        CleanAgents();
        if (Pop() == 0) {
            return null;
        }
        return agents.agents.get(rn.Int(Pop()));
    }

    /**
     * applies AgentRadDispToAction2D function to all agents within radius
     */
    public int ApplyAgentsRad(double rad, double x, AgentRadDispToAction1D<T> action) {
        int nAgents = 0;
        for (T t : this.IterAgentsRect((int) (x - rad), (int) (rad * 2 + 1))) {
            double xDisp = DispX(x, ((Agent2DBase) t).Xpt());
            if (Math.abs(xDisp) <= rad) {
                action.Action(t, xDisp, xDisp * xDisp);
                nAgents++;
            }
        }
        return nAgents;
    }

    /**
     * returns the number of agents that are alive in the grid
     */
    public int Pop() {
        //gets population
        return agents.pop;
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are empty
     */
    public int MapEmptyHood(int[] hood, int centerX) {
        return MapHood(hood, centerX, (i) -> GetAgent(i) == null);
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are occupied
     */
    public int MapOccupiedHood(int[] hood, int centerX) {
        return MapHood(hood, centerX, (i) -> GetAgent(i) != null);
    }

    /**
     * Same as IterAgents above, but will apply wraparound if x,y fall outside the grid dimensions.
     */
    public Iterable<T> IterAgentsSafe(int x) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents at position
     */
    public Iterable<T> IterAgents(int x) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, x);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents within radius, will include some over rad as well, use a second distance check to filter
     * these
     */
    public Iterable<T> IterAgentsRadApprox(double x, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents, x, rad);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents within radius
     */
    public Iterable<T> IterAgentsRad(double x, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, rad);
        return agents;
    }

    /**
     * iterates over all agents in the rectangle defined by (x,y) as the lower left corner, and (x+width,y+height) as
     * the top right corner.
     */
    public Iterable<T> IterAgentsRect(int x, int width) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, width);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents found in a neighborhood after mapping
     */
    public Iterable<T> IterAgentsHood(int[] hood, int centerX) {
        ArrayList<T> myAgents = GetFreshAgentSearchArr();
        GetAgentsHood(myAgents, hood, centerX);
        return GetFreshAgentsIterator(myAgents);
    }

    /**
     * iterates over all agents found in an already mapped neighborhood
     */
    public Iterable<T> IterAgentsHoodMapped(int[] hood, int hoodLen) {
        ArrayList<T> myAgents = GetFreshAgentSearchArr();
        GetAgentsHoodMapped(myAgents, hood, hoodLen);
        return GetFreshAgentsIterator(myAgents);
    }

    /**
     * returns a single random agent that satisfies EvalAgent, will apply wraparound if the coordiantes are outside of
     * the grid
     */
    public T RandomAgentSafe(int x, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (agents.size() > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent, will apply wraparound if the coordiantes are outside of the grid
     */
    public T RandomAgentSafe(int x, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x);
        T ret = null;
        int ct = agents.size();
        if (agents.size() > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the provided coordinates
     */
    public T RandomAgent(int x, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, x);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the provided coordinates forwhich EvalAgent returns true
     */
    public T RandomAgent(int x, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, x, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified radius
     */
    public T RandomAgentRad(double x, double rad, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, rad);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified radius forwhich EvalAgent returns
     * true
     */
    public T RandomAgentRad(double x, double rad, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, rad, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified rectangle
     */
    public T RandomAgentRect(int x, int width, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, width);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified rectangle forwhich EvalAgent
     * returns true
     */
    public T RandomAgentRect(int x, int width, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, width, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood
     */
    public T RandomAgentHood(int[] hood, int x, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood forwhich EvalAgent
     * returns true
     */
    public T RandomAgentHood(int[] hood, int x, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood, assumes the
     * neighborhood has already been mapped
     */
    public T RandomAgentHoodMapped(int[] hood, int hoodLen, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents, hood, hoodLen);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood forwhich EvalAgent
     * returns true, assumes the neighborhood has already been mapped
     */
    public T RandomAgentHoodMapped(int[] hood, int hoodLen, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHoodMapped(agents, hood, hoodLen, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        ReturnAgentSearchArr(agents);
        return ret;
    }

    public Iterator<T> iterator() {
        return agents.iterator();
    }


    AgentsIterator1D GetFreshAgentsIterator(ArrayList<T> agents) {
        AgentsIterator1D ret;
        if (usedIterIs.size() > 0) {
            ret = usedIterIs.remove(usedIterIs.size() - 1);
        } else {
            ret = new AgentsIterator1D(this);
        }
        ret.Setup(agents);
        return ret;
    }

    ArrayList<T> GetFreshAgentSearchArr() {
        ArrayList<T> agents;
        if (usedAgentSearches.size() > 0) {
            agents = usedAgentSearches.remove(usedAgentSearches.size() - 1);
            agents.clear();
            return agents;
        }
        return new ArrayList<T>();
    }

    private void ReturnAgentSearchArr(ArrayList<T> arr){
        if(usedAgentSearches.size()<=5){
            usedAgentSearches.add(arr);
        }
    }
    private void ReturnAgentsIterator(AgentsIterator1D ret){
        if(usedIterIs.size()<=5){
            usedIterIs.add(ret);
        }
    }



    T GetNewAgent() {
        return agents.GetNewAgent(tick);
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }

    private class AgentsIterator1D implements Iterator<T>, Iterable<T>, Serializable {
        final AgentGrid1D<T> myGrid;
        ArrayList<T> myAgents;
        int numAgents;
        int iCount;

        AgentsIterator1D(AgentGrid1D<T> grid) {
            myGrid = grid;
        }

        void Setup(ArrayList<T> myAgents) {
            this.myAgents = myAgents;
            iCount = 0;
            numAgents = myAgents.size();
        }

        @Override
        public boolean hasNext() {
            if (iCount == numAgents) {
                ReturnAgentSearchArr(myAgents);
                ReturnAgentsIterator(this);
                return false;
            }
            return true;
        }

        @Override
        public T next() {
            T ret = myAgents.get(iCount);
            iCount++;
            return ret;
        }

        @Override
        public Iterator<T> iterator() {
            return this;
        }
    }

    /**
     * increments the internal grid tick counter by 1, used with the Age() and BirthTick() functions to get age
     * information about the agents on an AgentGrid. can otherwise be used as a counter with the other grid types.
     */
    public void IncTick() {
        tick++;
    }

    /**
     * gets the current grid timestep.
     */
    public int GetTick() {
        return tick;
    }

    /**
     * sets the tick to 0.
     */
    public void ResetTick() {
        tick = 0;
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

}
