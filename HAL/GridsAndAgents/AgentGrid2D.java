package HAL.GridsAndAgents;

import HAL.Interfaces.*;
import HAL.Rand;
import HAL.Util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static HAL.Util.Norm;

/**
 * AgentGrid2Ds can hold any type of 2D Agent
 * @param <T> the type of agent that the AgentGrid2D will hold
 */
public class AgentGrid2D<T extends AgentBaseSpatial> implements Grid2D,Iterable<T>,Serializable {
    final public int xDim;
    final public int yDim;
    final public int length;
    public boolean wrapX;
    public boolean wrapY;
    int tick;
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches = new ArrayList<>();
    ArrayList<AgentsIterator2D> usedIterIs = new ArrayList<>();
    int[] counts;
    final double moveSafeXdim;
    final double moveSafeYdim;


    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class. the wrap booleans specify whether to domain should use wraparound or stop at the
     * boundary
     */
    public AgentGrid2D(int x, int y, Class<T> agentClass, boolean wrapX, boolean wrapY) {
        this.xDim=x;
        this.yDim=y;
        this.length=xDim*yDim;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.tick=0;
        //creates a new typeGrid with given dimensions
        agents = new InternalGridAgentList<T>(agentClass, this);
        grid = (T[]) new AgentBaseSpatial[length];
        counts = new int[length];
        moveSafeXdim = Math.nextAfter(xDim, 0);
        moveSafeYdim = Math.nextAfter(yDim, 0);
    }

    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class.
     */
    public AgentGrid2D(int x, int y, Class<T> agentClass) {
        this(x, y, agentClass, false, false);

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
    public T GetAgent(int x, int y) {
        return grid[I(x, y)];
    }

    /**
     * Gets a single agent at the specified grid square, beware using this function with stackable agents, as it will
     * only return one of the stack of agents. This function is recommended for the Unstackable Agents, as it tends to
     * perform better than the other methods for single agent accesses.
     */
    public T GetAgent(int index) {
        return grid[index];
    }


    /**
     * Same as GetAgent above, but if x or y are outside the domain, it will apply wrap around if wrapping is enabled,
     * or return null.
     */
    public T GetAgentSafe(int x, int y) {
        if(!In(x,y)) {
            if (wrapX) {
                x = Util.Wrap(x, xDim);
            } else if (!Util.InDim(x, xDim)) {
                return null;
            }
            if (wrapY) {
                y = Util.Wrap(y, yDim);
            } else if (!Util.InDim(y, yDim)) {
                return null;
            }
        }
        return grid[I(x, y)];
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x, int y) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x, y);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x, double y) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x, y);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified index
     */
    public T NewAgentSQ(int index) {
        T newAgent = GetNewAgent();
        newAgent.Setup(index);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates, will apply wraparound if the coordinates are outside
     * the domain
     */
    public T NewAgentPTSafe(double newX, double newY) {
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            return null;
        }
        if (wrapY) {
            newY = Util.Wrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim)) {
            return null;
        }
        return NewAgentPT(newX, newY);
    }

    /**
     * returns an uninitialized agent at the specified coordinates, will apply wraparound or use the fallback if the
     * coordinates are outside the domain
     */

    public T NewAgentPTSafe(double newX, double newY, double fallbackX, double fallbackY) {
        if (In(newX, newY)) {
            return NewAgentPT(newX, newY);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.Wrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim)) {
            newY = fallbackY;
        }
        return NewAgentPT(newX, newY);
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
    public void GetAgents(ArrayList<T> putHere, int x, int y) {
        T agent = GetAgent(x,y);
        if (agent != null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates
     */
    public void GetAgents(ArrayList<T> putHere, int i) {
        T agent = grid[i];
        if (agent != null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> putHere, int x, int y, AgentToBool<T> EvalAgent) {
        T agent = GetAgent(x,y);
        if (agent != null) {
            agent.GetAllOnSquare(putHere, EvalAgent);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> putHere, int i, AgentToBool<T> EvalAgent) {
        T agent = GetAgent(i);
        if (agent != null) {
            agent.GetAllOnSquare(putHere, EvalAgent);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will apply wraparound if
     * the coordinates are outside the domain
     */
    public void GetAgentsSafe(ArrayList<T> putHere, int x, int y) {
        if (wrapX) {
            x = Util.Wrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        if (wrapY) {
            y = Util.Wrap(y, yDim);
        } else if (!Util.InDim(y, yDim)) {
            return;
        }
        T agent = grid[I(x, y)];
        if (agent != null) {
            agent.GetAllOnSquare(putHere);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates forwhich EvalAgent
     * returns true, will apply wraparound if the coordinates are outside the domain
     */
    public void GetAgentsSafe(ArrayList<T> putHere, int x, int y, AgentToBool<T> EvalAgent) {
        if (wrapX) {
            x = Util.Wrap(x, xDim);
        } else if (!Util.InDim(x, xDim)) {
            return;
        }
        if (wrapY) {
            y = Util.Wrap(y, yDim);
        } else if (!Util.InDim(y, yDim)) {
            return;
        }
        T agent = grid[I(x, y)];
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
    void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double rad) {
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY);
            }
        }
    }

    /**
     * quickly gets all agents that are within rad, but also includes some that are further away than rad, an additional
     * distance check should be used to properly subset this group. only agents forwhich EvalAgent returns true will be
     * added
     */
    void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double rad, AgentToBool<T> EvalAgent) {
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY, EvalAgent);
            }
        }
    }

    /**
     * gets all agents that are within rad, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double rad) {
        int nAgents;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY, (agent) -> {
                    Agent2DBase a = (Agent2DBase) agent;
                    return Util.DistSquared(a.Xpt(), a.Ypt(), x, y, xDim, yDim, wrapX, wrapY) < radSq;
                });
            }
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY, (agent) -> {
                    Agent2DBase a = (Agent2DBase) agent;
                    return Util.DistSquared(a.Xpt(), a.Ypt(), x, y, xDim, yDim, wrapX, wrapY) < radSq && EvalAgent.EvalAgent(agent);
                });
            }
        }
    }
    /**
     * gets all agents that are within rad, and adds them to the ArrayList, displacementInfo contains the following info for each agent: [dist from point,x disp,y disp]
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList,final ArrayList<double[]> displacementInfo, final double x, final double y, final double rad) {
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY, (agent) -> {
                    double dispX=DispX(x,((Agent2DBase) agent).Xpt());
                    double dispY=DispY(y,((Agent2DBase) agent).Ypt());
                    double dist=Norm(dispX,dispY);
                    if(dist<rad) {
                        for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                            displacementInfo.add(new double[3]);
                        }
                        double[] info = displacementInfo.get(retAgentList.size());
                        info[0]=dist;
                        info[1]=dispX;
                        info[2]=dispY;
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList,final ArrayList<double[]> displacementInfo, final double x, final double y, final double rad, AgentToBool<T> EvalAgent) {
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(retAgentList, retX, retY, (agent) -> {
                    double dispX=DispX(x,((Agent2DBase) agent).Xpt());
                    double dispY=DispY(y,((Agent2DBase) agent).Ypt());
                    double dist=Norm(dispX,dispY);
                    if(dist<rad&&EvalAgent.EvalAgent(agent)) {
                        for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                            displacementInfo.add(new double[3]);
                        }
                        double[] info = displacementInfo.get(retAgentList.size());
                        info[0]=dist;
                        info[1]=dispX;
                        info[2]=dispY;
                        return true;
                    }
                    return false;
                });
            }
        }
    }

    /**
     * gets all agents within the rectangle argument defined by the bottom corner and the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y, int width, int height) {
        int xEnd = x + width;
        int yEnd = y + height;
        int xWrap;
        int yWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX = Util.InDim(xi, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            xWrap = xi;
            if (wrapX && !inX) {
                xWrap = Util.Wrap(xi, xDim);
            }
            for (int yi = y; yi <= yEnd; yi++) {
                boolean inY = Util.InDim(yi, yDim);
                if ((!wrapY && !inY)) {
                    continue;
                }
                yWrap = yi;
                if (wrapY && !inY) {
                    yWrap = Util.Wrap(yi, yDim);
                }
                GetAgents(retAgentList, xWrap, yWrap);
            }
        }
    }

    /**
     * gets all agents forwhich evalAgent returns true within the rectangle argument defined by the bottom corner and
     * the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y, int width, int height, AgentToBool<T> EvalAgent) {
        int xEnd = x + width;
        int yEnd = y + height;
        int xWrap;
        int yWrap;
        for (int xi = x; xi <= xEnd; xi++) {
            boolean inX = Util.InDim(xi, xDim);
            if ((!wrapX && !inX)) {
                continue;
            }
            xWrap = xi;
            if (wrapX && !inX) {
                xWrap = Util.Wrap(xi, xDim);
            }
            for (int yi = y; yi <= yEnd; yi++) {
                boolean inY = Util.InDim(yi, yDim);
                if ((!wrapY && !inY)) {
                    continue;
                }
                yWrap = yi;
                if (wrapY && !inY) {
                    yWrap = Util.Wrap(yi, yDim);
                }
                GetAgents(retAgentList, xWrap, yWrap, EvalAgent);
            }
        }
    }

    /**
     * gets all agents within the given neighborhood and adds them to the ArrayList argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX, int centerY) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y);
        }

    }

    /**
     * gets all agents within the given neighborhood forwhich EvalAgent returns true and adds them to the ArrayList
     * argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX, int centerY, AgentToBool<T> EvalAgent) {
        int iStart = hood.length / 3;
        for (int i = iStart; i < hood.length; i += 2) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            if (!Util.InDim(x, xDim)) {
                if (wrapX) {
                    x = Util.Wrap(x, xDim);
                } else {
                    continue;
                }
            }
            if (!Util.InDim(y, yDim)) {
                if (wrapY) {
                    y = Util.Wrap(y, yDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y, EvalAgent);
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
     * gets all agents in the provided neighborhood, assumes that the neighborhood has already been mapped to a
     * location
     */
    public void GetAgentsHoodMapped(ArrayList<T> retAgentList, int[] hood, int hoodLen, AgentToBool<T> EvalAgent) {
        for (int i = 0; i < hoodLen; i++) {
            GetAgents(retAgentList, hood[i], EvalAgent);
        }
    }

    /**
     * calls dispose on all agents in the typeGrid, resets the tick timer to 0.
     */
    public void Reset() {
        for (T a : this) {
            a.Dispose();
        }
        if (Pop() > 0) {
            throw new IllegalStateException("Something is wrong with Reset, tell Rafael Bravo to fix this!");
        }
        CleanAgents();
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
        this.ResetTick();
    }

    /**
     * gets the population at a specific location
     */
    public int PopAt(int i) {
        T agent = grid[i];
        if (agent == null) {
            return 0;
        }
        return agent.GetCountOnSquare();
    }

    /**
     * gets the population at a specific location
     */
    public int PopAt(int x, int y) {
        return PopAt(I(x, y));
    }


    /**
     * gets the population at a specific location, subsetting to only those for which EvalAgent returns true
     */
    public int PopAt(int i, AgentToBool EvalAgent) {
        T agent = grid[i];
        if (agent == null) {
            return 0;
        }
        return agent.GetCountOnSquare(EvalAgent);
    }

    /**
     * gets the population at a specific location, subsetting to only those for which EvalAgent returns true
     */
    public int PopAt(int x, int y, AgentToBool EvalAgent) {
        return PopAt(I(x, y), EvalAgent);
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
    public int ApplyAgentsRad(double rad, double x, double y, AgentRadDispToAction2D<T> action) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        int nAgents = 0;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                int retX = xSq;
                int retY = ySq;
                boolean inX = Util.InDim(retX, xDim);
                boolean inY = Util.InDim(retY, yDim);
                if ((!wrapX && !inX) || (!wrapY && !inY)) {
                    continue;
                }
                if (wrapX && !inX) {
                    retX = Util.Wrap(retX, xDim);
                }
                if (wrapY && !inY) {
                    retY = Util.Wrap(retY, yDim);
                }
                GetAgents(agents, retX, retY);
                for (int i = 0; i < agents.size(); i++) {
                    T agent = agents.get(i);
                    double xDisp = ((Agent2DBase) (agent)).Xpt() - x;
                    double yDisp = ((Agent2DBase) (agent)).Ypt() - y;
                    double distSq = xDisp * xDisp + yDisp * yDisp;
                    if (distSq <= radSq) {
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

    /**
     * returns the number of agents that are alive in the grid
     */
    public int Pop() {
        return agents.pop;
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are empty
     */
    public int MapEmptyHood(int[] hood, int centerX, int centerY) {
        return MapHood(hood, centerX, centerY, (i, x, y) -> GetAgent(i) == null);
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are empty
     */
    public int MapEmptyHood(int[] hood, int centerI) {
        return MapEmptyHood(hood, ItoX(centerI), ItoY(centerI));
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are occupied
     */
    public int MapOccupiedHood(int[] hood, int centerX, int centerY) {
        return MapHood(hood, centerX, centerY, (i, x, y) -> GetAgent(i) != null);
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are occupied
     */
    public int MapOccupiedHood(int[] hood, int centerI) {
        return MapOccupiedHood(hood, ItoX(centerI), ItoY(centerI));
    }

    /**
     * Same as IterAgents above, but will apply wraparound if x,y fall outside the grid dimensions.
     */
    public Iterable<T> IterAgentsSafe(int x, int y) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents at position
     */
    public Iterable<T> IterAgents(int x, int y) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, x, y);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents at position
     */
    public Iterable<T> IterAgents(int i) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, i);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents within radius, will include some over rad as well, use a second distance check to filter
     * these
     */
    Iterable<T> IterAgentsRadApprox(double x, double y, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents, x, y, rad);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents within radius
     */
    public Iterable<T> IterAgentsRad(double x, double y, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, rad);
        return agents;
    }

    /**
     * iterates over all agents in the rectangle defined by (x,y) as the lower left corner, and (x+width,y+height) as
     * the top right corner.
     */
    public Iterable<T> IterAgentsRect(int x, int y, int width, int height) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, width, height);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents found in a neighborhood after mapping
     */
    public Iterable<T> IterAgentsHood(int[] hood, int centerX, int centerY) {
        ArrayList<T> myAgents = GetFreshAgentSearchArr();
        GetAgentsHood(myAgents, hood, centerX, centerY);
        return GetFreshAgentsIterator(myAgents);
    }

    /**
     * iterates over all agents found in a neighborhood after mapping
     */
    public Iterable<T> IterAgentsHood(int[] hood, int centerI) {
        return IterAgentsHood(hood, ItoX(centerI), ItoY(centerI));
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
    public T RandomAgentSafe(int x, int y, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (agents.size() > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent, will apply wraparound if the coordiantes are outside of the grid
     */
    public T RandomAgentSafe(int x, int y, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the provided coordinates
     */
    public T RandomAgent(int x, int y, Rand rn) {
        return RandomAgent(I(x, y), rn);
    }

    /**
     * returns a single random agent from the provided coordinates
     */
    public T RandomAgent(int i, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }


    /**
     * returns a single random agent from the provided coordinates forwhich EvalAgent returns true
     */
    public T RandomAgent(int i, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, i, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the provided coordinates forwhich EvalAgent returns true
     */
    public T RandomAgent(int x, int y, Rand rn, AgentToBool<T> EvalAgent) {
        return RandomAgent(I(x, y), rn, EvalAgent);
    }

    /**
     * returns a single random agent from the set of all agents within the specified radius
     */
    public T RandomAgentRad(double x, double y, double rad, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, rad);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified radius forwhich EvalAgent returns
     * true
     */
    public T RandomAgentRad(double x, double y, double rad, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, rad, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified rectangle
     */
    public T RandomAgentRect(int x, int y, int width, int height, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, width, height);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified rectangle forwhich EvalAgent
     * returns true
     */
    public T RandomAgentRect(int x, int y, int width, int height, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, width, height, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood
     */
    public T RandomAgentHood(int[] hood, int x, int y, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x, y);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified neighborhood forwhich EvalAgent
     * returns true
     */
    public T RandomAgentHood(int[] hood, int x, int y, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x, y, EvalAgent);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
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
        usedAgentSearches.add(agents);
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
        usedAgentSearches.add(agents);
        return ret;
    }

    public Iterator<T> iterator() {
        return agents.iterator();
    }

    AgentsIterator2D GetFreshAgentsIterator(ArrayList<T> agents) {
        AgentsIterator2D ret;
        if (usedIterIs.size() > 0) {
            ret = usedIterIs.remove(usedIterIs.size() - 1);
        } else {
            ret = new AgentsIterator2D(this);
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

    T GetNewAgent() {
        return agents.GetNewAgent(tick);
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return wrapX;
    }

    @Override
    public boolean IsWrapY() {
        return wrapY;
    }


    private class AgentsIterator2D implements Iterator<T>, Iterable<T>, Serializable {
        final AgentGrid2D<T> myGrid;
        ArrayList<T> myAgents;
        int numAgents;
        int iCount;

        AgentsIterator2D(AgentGrid2D<T> grid) {
            myGrid = grid;
        }

        public void Setup(ArrayList<T> myAgents) {
            this.myAgents = myAgents;
            iCount = 0;
            numAgents = myAgents.size();
        }

        @Override
        public boolean hasNext() {
            if (iCount == numAgents) {
                myGrid.usedAgentSearches.add(myAgents);
                myGrid.usedIterIs.add(this);
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
//        foreignAgent.Reset(newX,newY);
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
//        foreignAgent.Reset(newX,newY);
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
//        foreignAgent.Reset(newI);
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
//                    x = Util.Wrap(x, xDim);
//                } else {
//                    continue;
//                }
//            }
//            if (!Util.InDim(yDim, y)) {
//                if (wrapY) {
//                    y = Util.Wrap(y, yDim);
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
}
