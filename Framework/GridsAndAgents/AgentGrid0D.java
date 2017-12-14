package Framework.GridsAndAgents;

import Framework.Interfaces.AgentStepFunction;
import Framework.Rand;
import Framework.Util;

import java.util.*;

/**
 * Extend the Grid2unstackable class if you want a 2D lattice with at most one agent per typeGrid square
 * @param <T> the AgentSQ2Dunstackable extending agent class that will inhabit the typeGrid
 */
public class AgentGrid0D<T extends Agent0D> extends GridBase implements Iterable<T>{
    AgentList<T> agents;

    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid0D(Class<T> agentClass){
        agents=new AgentList<T>(agentClass,this);
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent(){
        T newAgent=agents.GetNewAgent();
        newAgent.alive=true;
        return newAgent;
    }

    void RemoveAgent(T agent){
        //internal function, removes agent from world
        agents.RemoveAgent(agent);
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

    /**
     * calls CleanAgents, then SuffleAgents, then IncTick. useful to call at the end of a tick
     * do not call this while in the middle of iteration
     * @param rn the Random number generator to be used
     */
    public void CleanShuffInc(Rand rn){
        agents.CleanAgents();
        agents.ShuffleAgents(rn);
        IncTick();
    }

    public void ShuffInc(Rand rn){
        agents.ShuffleAgents(rn);
        IncTick();
    }
    public void CleanInc(){
        agents.CleanAgents();
        IncTick();
    }
    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public ArrayList<T> AllAgents(){return (ArrayList<T>)this.agents.GetAllAgents();}


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
     * calls dispose on all agents in the typeGrid
     */
    public void Reset(){
        List<T> AllAgents=this.agents.GetAllAgents();
        AllAgents.stream().filter(curr -> curr.alive).forEach(Agent0D::Dispose);
        tick=0;
    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int GetPop(){
        //gets population
        return agents.pop;
    }

    @Override
    public Iterator<T> iterator(){
        return agents.iterator();
    }

}
