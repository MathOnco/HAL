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
 * AgentGrid3Ds can hold any type of 3D Agent
 * @param <T> the type of agent that the AgentGrid3D will hold
 */
public class AgentGrid3D<T extends AgentBaseSpatial> implements Grid3D,Iterable<T>,Serializable {
    public final int xDim;
    public final int yDim;
    public final int zDim;
    public final int length;
    public boolean wrapX;
    public boolean wrapY;
    public boolean wrapZ;
    int tick;
    InternalGridAgentList<T> agents;
    T[] grid;
    ArrayList<ArrayList<T>> usedAgentSearches = new ArrayList<>();
    ArrayList<AgentsIterator3D> usedIterIs = new ArrayList<>();
    int iagentSearch = 0;
    int[] counts;
    final double moveSafeXdim;
    final double moveSafeYdim;
    final double moveSafeZdim;

    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class. the wrap booleans specify whether to domain should use wraparound or stop at the
     * boundary
     */
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass, boolean wrapX, boolean wrapY, boolean wrapZ) {
        this.xDim=x;
        this.yDim=y;
        this.zDim=z;
        this.length=x*y*z;
        this.wrapX=wrapX;
        this.wrapY=wrapY;
        this.wrapZ=wrapZ;
        this.tick=0;
        agents = new InternalGridAgentList<T>(agentClass, this);
        grid = (T[]) new AgentBaseSpatial[length];
        counts = new int[length];
        moveSafeXdim = Math.nextAfter(xDim, 0);
        moveSafeYdim = Math.nextAfter(yDim, 0);
        moveSafeZdim = Math.nextAfter(zDim, 0);
    }

    /**
     * pass to the constructor the dimensions of the grid and the agent class type, written T.type where T is the name
     * of the occupying agent class.
     */
    public AgentGrid3D(int x, int y, int z, Class<T> agentClass) {
        this(x, y, z, agentClass, false, false, false);
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
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentSQ(int x, int y, int z) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x, y, z);
        return newAgent;
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgentPT(double x, double y, double z) {
        T newAgent = GetNewAgent();
        newAgent.Setup(x, y, z);
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
    public T NewAgentPTSafe(double newX, double newY, double newZ) {
        if (In(newX, newY, newZ)) {
            return NewAgentPT(newX, newY, newZ);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            return null;
        }
        if (wrapY) {
            newY = Util.Wrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim))
            return null;
        if (wrapZ) {
            newZ = Util.Wrap(newZ, zDim);
        } else if (!Util.InDim(newZ, zDim))
            return null;
        return NewAgentPT(newX, newY, newZ);
    }

    /**
     * returns an uninitialized agent at the specified coordinates, will apply wraparound or use the fallback if the
     * coordinates are outside the domain
     */
    public T NewAgentPTSafe(double newX, double newY, double newZ, double fallbackX, double fallbackY, double fallbackZ) {
        if (In(newX, newY, newZ)) {
            return NewAgentPT(newX, newY, newZ);
        }
        if (wrapX) {
            newX = Util.Wrap(newX, xDim);
        } else if (!Util.InDim(newX, xDim)) {
            newX = fallbackX;
        }
        if (wrapY) {
            newY = Util.Wrap(newY, yDim);
        } else if (!Util.InDim(newY, yDim))
            newY = fallbackY;
        if (wrapZ) {
            newZ = Util.Wrap(newZ, zDim);
        } else if (!Util.InDim(newZ, zDim))
            newZ = fallbackZ;
        return NewAgentPT(newX, newY, newZ);
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
     * similar to the MapHood function, but will only include indices of locations that are empty
     */
    public int MapEmptyHood(int[] hood, int centerX, int centerY, int centerZ) {
        return MapHood(hood, centerX, centerY, centerZ, (i, x, y, z) -> GetAgent(i) == null);
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are empty
     */
    public int MapEmptyHood(int[] hood, int centerI) {
        return MapEmptyHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI));
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are occupied
     */
    public int MapOccupiedHood(int[] hood, int centerX, int centerY, int centerZ) {
        return MapHood(hood, centerX, centerY, centerZ, (i, x, y, z) -> GetAgent(i) != null);
    }

    /**
     * similar to the MapHood function, but will only include indices of locations that are occupied
     */
    public int MapOccupiedHood(int[] hood, int centerI) {
        return MapOccupiedHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI));
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
     * Gets a single agent at the specified grid square, beware using this function with stackable agents, as it will
     * only return one of the stack of agents. This function is recommended for the Unstackable Agents, as it tends to
     * perform better than the other methods for single agent accesses.
     */
    public T GetAgent(int x, int y, int z) {
        return grid[I(x, y, z)];
    }

    /**
     * Same as GetAgent above, but if x or y are outside the domain, it will apply wrap around if wrapping is enabled,
     * or return null.
     */
    public T GetAgentSafe(int x, int y, int z) {
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
        if (wrapZ) {
            z = Util.Wrap(z, yDim);
        } else if (!Util.InDim(z, yDim)) {
            return null;
        }
        return grid[I(x, y, z)];
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
     * gets all agents within the rectangle argument defined by the bottom corner and the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y, int z, int width, int height, int depth) {
        int xEnd = x + width;
        int yEnd = y + height;
        int zEnd = z + depth;
        int xWrap;
        int yWrap;
        int zWrap;
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
                for (int zi = z; zi <= zEnd; zi++) {
                    boolean inZ = Util.InDim(zi, zDim);
                    if ((!wrapZ && !inZ)) {
                        continue;
                    }
                    zWrap = zi;
                    if (wrapZ && !inZ) {
                        zWrap = Util.Wrap(zi, zDim);
                    }
                    GetAgents(retAgentList, xWrap, yWrap, zWrap);
                }
            }
        }
    }

    /**
     * gets all agents forwhich evalAgent returns true within the rectangle argument defined by the bottom corner and
     * the dimension arguments
     */
    public void GetAgentsRect(ArrayList<T> retAgentList, int x, int y, int z, int width, int height, int depth, AgentToBool<T> EvalAgent) {
        int xEnd = x + width;
        int yEnd = y + height;
        int zEnd = z + depth;
        int xWrap;
        int yWrap;
        int zWrap;
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
                for (int zi = z; zi <= zEnd; zi++) {
                    boolean inZ = Util.InDim(zi, zDim);
                    if ((!wrapZ && !inZ)) {
                        continue;
                    }
                    zWrap = zi;
                    if (wrapZ && !inZ) {
                        zWrap = Util.Wrap(zi, zDim);
                    }
                    GetAgents(retAgentList, xWrap, yWrap, zWrap, EvalAgent);
                }
            }
        }
    }

    /**
     * gets all agents within the given neighborhood and adds them to the ArrayList argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX, int centerY, int centerZ) {
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y, z);
        }

    }

    /**
     * gets all agents within the given neighborhood forwhich EvalAgent returns true and adds them to the ArrayList
     * argument
     */
    public void GetAgentsHood(ArrayList<T> retAgentList, int[] hood, int centerX, int centerY, int centerZ, AgentToBool<T> EvalAgent) {
        int iStart = hood.length / 4;
        for (int i = iStart; i < hood.length; i += 3) {
            int x = hood[i] + centerX;
            int y = hood[i + 1] + centerY;
            int z = hood[i + 2] + centerZ;
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
            if (!Util.InDim(z, zDim)) {
                if (wrapZ) {
                    z = Util.Wrap(z, zDim);
                } else {
                    continue;
                }
            }
            GetAgents(retAgentList, x, y, z, EvalAgent);
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
     * quickly gets all agents that are within rad, but also includes some that are further away than rad, an additional
     * distance check should be used to properly subset this group
     */
    void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, I(retX, retY, retZ));
                }
            }
        }
    }

    /**
     * quickly gets all agents that are within rad, but also includes some that are further away than rad, an additional
     * distance check should be used to properly subset this group. only agents forwhich EvalAgent returns true will be
     * added
     */
    void GetAgentsRadApprox(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, I(retX, retY, retZ), EvalAgent);
                }
            }
        }
    }

    /**
     * gets all agents that are within rad, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad) {
        int nAgents;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, xSq, ySq, zSq, (agent) -> {
                        Agent3DBase a = (Agent3DBase) agent;
                        return Util.DistSquared(a.Xpt(), a.Ypt(), a.Zpt(), x, y, z, xDim, yDim, zDim, wrapX, wrapY, wrapZ) < radSq;
                    });
                }
            }
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final double x, final double y, final double z, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, xSq, ySq, zSq, (agent) -> {
                        Agent3DBase a = (Agent3DBase) agent;
                        return Util.DistSquared(a.Xpt(), a.Ypt(), a.Zpt(), x, y, z, xDim, yDim, zDim, wrapX, wrapY, wrapZ) < radSq && EvalAgent.EvalAgent(agent);
                    });
                }
            }
        }
    }

    /**
     * gets all agents that are within rad, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList, final ArrayList<double[]> displacementInfo, final double x, final double y, final double z, final double rad) {
        int nAgents;
        double radSq = rad * rad;
        for (int retX = (int) Math.floor(x - rad); retX < (int) Math.ceil(x + rad); retX++) {
            for (int retY = (int) Math.floor(y - rad); retY < (int) Math.ceil(y + rad); retY++) {
                for (int retZ = (int) Math.floor(z - rad); retZ < (int) Math.ceil(z + rad); retZ++) {
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, retX, retY, retZ, (agent) -> {
                        Agent3DBase a = (Agent3DBase) agent;
                        double dispX = DispX(x, ((Agent3DBase) agent).Xpt());
                        double dispY = DispY(y, ((Agent3DBase) agent).Ypt());
                        double dispZ = DispZ(z, ((Agent3DBase) agent).Zpt());
                        double dist = Norm(dispX, dispY,dispZ);
                        if (dist < rad) {
                            for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                                displacementInfo.add(new double[4]);
                            }
                            double[] info = displacementInfo.get(retAgentList.size());
                            info[0] = dist;
                            info[1] = dispX;
                            info[2] = dispY;
                            info[3] = dispZ;
                            return true;
                        }
                        return false;
                    });
                }
            }
        }
    }

    /**
     * gets all agents that are within rad forwhich EvalAgent returns true, and adds them to the ArrayList
     */
    public void GetAgentsRad(final ArrayList<T> retAgentList,final ArrayList<double[]>displacementInfo, final double x, final double y, final double z, final double rad, AgentToBool<T> EvalAgent) {
        int nAgents;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(retAgentList, xSq, ySq, zSq, (agent) -> {
                        Agent3DBase a = (Agent3DBase) agent;
                        double dispX = DispX(x, ((Agent3DBase) agent).Xpt());
                        double dispY = DispY(y, ((Agent3DBase) agent).Ypt());
                        double dispZ = DispZ(z, ((Agent3DBase) agent).Zpt());
                        double dist = Norm(dispX, dispY,dispZ);
                        if (dist < rad&&EvalAgent.EvalAgent(agent)) {
                            for (int i = displacementInfo.size(); i <= retAgentList.size(); i++) {
                                displacementInfo.add(new double[4]);
                            }
                            double[] info = displacementInfo.get(retAgentList.size());
                            info[0] = dist;
                            info[1] = dispX;
                            info[2] = dispY;
                            info[3] = dispZ;
                            return true;
                        }
                        return false;
                    });
                }
            }
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
    public int PopAt(int x, int y, int z) {
        return PopAt(I(x, y, z));
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
    public int PopAt(int x, int y, int z, AgentToBool EvalAgent) {
        return PopAt(I(x, y, z), EvalAgent);
    }

    /**
     * applies AgentRadDispToAction2D function to all agents within radius
     */
    public int ApplyAgentsRad(double rad, double x, double y, double z, AgentRadDispToAction3D<T> action) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        iagentSearch++;
        int nAgents = 0;
        double radSq = rad * rad;
        for (int xSq = (int) Math.floor(x - rad); xSq < (int) Math.ceil(x + rad); xSq++) {
            for (int ySq = (int) Math.floor(y - rad); ySq < (int) Math.ceil(y + rad); ySq++) {
                for (int zSq = (int) Math.floor(z - rad); zSq < (int) Math.ceil(z + rad); zSq++) {
                    int retX = xSq;
                    int retY = ySq;
                    int retZ = zSq;
                    boolean inX = Util.InDim(retX, xDim);
                    boolean inY = Util.InDim(retY, yDim);
                    boolean inZ = Util.InDim(retZ, zDim);
                    if ((!wrapX && !inX) || (!wrapY && !inY) || (!wrapZ && !inZ)) {
                        continue;
                    }
                    if (wrapX && !inX) {
                        retX = Util.Wrap(retX, xDim);
                    }
                    if (wrapY && !inY) {
                        retY = Util.Wrap(retY, yDim);
                    }
                    if (wrapZ && !inZ) {
                        retZ = Util.Wrap(retZ, zDim);
                    }
                    GetAgents(agents, retX, retY, retZ);
                    for (int i = 0; i < agents.size(); i++) {
                        T agent = agents.get(i);
                        double xDisp = ((Agent3DBase) (agent)).Xpt() - x;
                        double yDisp = ((Agent3DBase) (agent)).Ypt() - y;
                        double zDisp = ((Agent3DBase) (agent)).Zpt() - z;
                        double distSq = xDisp * xDisp + yDisp * yDisp + zDisp * zDisp;
                        if (distSq <= radSq) {
                            action.Action(agent, xDisp, yDisp, zDisp, distSq);
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
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> retAgentList, int x, int y, int z) {
        T agent = GetAgent(x, y, z);
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> retAgentList, int index) {
        T agent = grid[index];
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> retAgentList, int x, int y, int z, AgentToBool<T> evalAgent) {
        T agent = GetAgent(x, y, z);
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList, evalAgent);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will subset only agents
     * for which EvalAgent returns true
     */
    public void GetAgents(ArrayList<T> retAgentList, int i, AgentToBool<T> evalAgent) {
        T agent = GetAgent(i);
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList, evalAgent);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates, will apply wraparound if
     * the coordinates are outside the domain
     */
    public void GetAgentsSafe(ArrayList<T> retAgentList, int x, int y, int z) {
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
        if (wrapZ) {
            z = Util.Wrap(z, yDim);
        } else if (!Util.InDim(z, yDim)) {
            return;
        }
        T agent = GetAgent(x, y, z);
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList);
        }
    }

    /**
     * appends to the provided arraylist all agents on the square at the specified coordinates forwhich EvalAgent
     * returns true, will apply wraparound if the coordinates are outside the domain
     */
    public void GetAgentsSafe(ArrayList<T> retAgentList, int x, int y, int z, AgentToBool<T> EvalAgent) {
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
        if (wrapZ) {
            z = Util.Wrap(z, yDim);
        } else if (!Util.InDim(z, yDim)) {
            return;
        }
        T agent = GetAgent(x, y, z);
        if (agent != null) {
            agent.GetAllOnSquare(retAgentList, EvalAgent);
        }
    }

    /**
     * returns the number of agents that are alive in the grid
     */
    public int Pop() {
        return agents.pop;
    }

    /**
     * iterates over all agents at position
     */
    public Iterable<T> IterAgents(int x, int y, int z) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, x, y, z);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * Same as IterAgents above, but will apply wraparound if x,y fall outside the grid dimensions.
     */
    public Iterable<T> IterAgentsSafe(int x, int y, int z) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y, z);
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
    public Iterable<T> IterAgentsRadApprox(double x, double y, double z, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRadApprox(agents, x, y, z, rad);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents within radius
     */
    public Iterable<T> IterAgentsRad(double x, double y, double z, double rad) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, z, rad);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents in the rectangle defined by (x,y) as the lower left corner, and (x+width,y+height) as
     * the top right corner.
     */
    public Iterable<T> IterAgentsRect(int x, int y, int z, int width, int height, int depth) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, z, width, height, depth);
        return GetFreshAgentsIterator(agents);
    }

    /**
     * iterates over all agents found in a neighborhood after mapping
     */
    public Iterable<T> IterAgentsHood(int[] hood, int centerX, int centerY, int centerZ) {
        ArrayList<T> myAgents = GetFreshAgentSearchArr();
        GetAgentsHood(myAgents, hood, centerX, centerY, centerZ);
        return GetFreshAgentsIterator(myAgents);
    }

    /**
     * iterates over all agents found in a neighborhood after mapping
     */
    public Iterable<T> IterAgentsHood(int[] hood, int centerI) {
        return IterAgentsHood(hood, ItoX(centerI), ItoY(centerI), ItoZ(centerI));
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
     * returns a single random agent, will apply wraparound if the coordiantes are outside of the grid
     */
    public T RandomAgentSafe(int x, int y, int z, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y, z);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent that satisfies EvalAgent, will apply wraparound if the coordiantes are outside of
     * the grid
     */
    public T RandomAgentSafe(int x, int y, int z, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsSafe(agents, x, y, z, EvalAgent);
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
    public T RandomAgent(int x, int y, int z, Rand rn) {
        return RandomAgent(I(x, y, z), rn);
    }

    /**
     * returns a single random agent from the provided coordinates forwhich EvalAgent returns true
     */
    public T RandomAgent(int x, int y, int z, Rand rn, AgentToBool<T> EvalAgent) {
        return RandomAgent(I(x, y, z), rn, EvalAgent);
    }

    /**
     * returns a single random agent from the provided coordinates
     */
    public T RandomAgent(int i, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgents(agents, i);
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
     * returns a single random agent from the set of all agents within the specified radius
     */
    public T RandomAgentRad(double x, double y, double z, double rad, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, z, rad);
        T ret = null;
        int ct = agents.size();
        if (ct > 0) {
            ret = agents.get(rn.Int(ct));
        }
        usedAgentSearches.add(agents);
        return ret;
    }

    /**
     * returns a single random agent from the set of all agents within the specified radius
     */
    public T RandomAgentRad(double x, double y, double z, double rad, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRad(agents, x, y, z, rad, EvalAgent);
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
    public T RandomAgentRect(int x, int y, int z, int width, int height, int depth, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, z, width, height, depth);
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
    public T RandomAgentRect(int x, int y, int z, int width, int height, int depth, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsRect(agents, x, y, z, width, height, depth, EvalAgent);
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
    public T RandomAgentHood(int[] hood, int x, int y, int z, Rand rn) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x, y, z);
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
    public T RandomAgentHood(int[] hood, int x, int y, int z, Rand rn, AgentToBool<T> EvalAgent) {
        ArrayList<T> agents = GetFreshAgentSearchArr();
        GetAgentsHood(agents, hood, x, y, z, EvalAgent);
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

    @Override
    public Iterator<T> iterator() {
        return agents.iterator();
    }


    T GetNewAgent() {
        return agents.GetNewAgent(tick);
    }

    AgentsIterator3D GetFreshAgentsIterator(ArrayList<T> agents) {
        AgentsIterator3D ret;
        if (usedIterIs.size() > 0) {
            ret = usedIterIs.remove(usedIterIs.size() - 1);
        } else {
            ret = new AgentsIterator3D(this);
        }
        ret.Setup(agents);
        return ret;
    }

    ArrayList<T> GetFreshAgentSearchArr() {
        ArrayList<T> agents;
        if (iagentSearch >= usedAgentSearches.size()) {
            agents = new ArrayList<T>();
            usedAgentSearches.add(agents);
        } else {
            agents = usedAgentSearches.get(iagentSearch);
            agents.clear();
        }
        return agents;
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
    public int Zdim() {
        return zDim;
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

    @Override
    public boolean IsWrapZ() {
        return wrapZ;
    }


    private class AgentsIterator3D implements Iterator<T>, Iterable<T>, Serializable {
        final AgentGrid3D<T> G;
        ArrayList<T> myAgents;
        int numAgents;
        int iCount;

        AgentsIterator3D(AgentGrid3D<T> grid) {
            G = grid;
        }

        public void Setup(ArrayList<T> myAgents) {
            this.myAgents = myAgents;
            iCount = 0;
            numAgents = myAgents.size();
        }

        @Override
        public boolean hasNext() {
            if (iCount == numAgents) {
                G.usedAgentSearches.add(myAgents);
                G.usedIterIs.add(this);
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
}
