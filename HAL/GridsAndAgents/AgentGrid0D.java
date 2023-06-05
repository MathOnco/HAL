package HAL.GridsAndAgents;

import HAL.Rand;

import java.io.Serializable;
import java.util.*;

/**
 * AgentGrid0Ds can only hold Agent0Ds
 * @param <T> the type of agent that the grid will hold
 */
public class AgentGrid0D<T extends Agent0D> implements Iterable<T>,Serializable {
    InternalGridAgentList<T> agents;
    int tick;

    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid0D(Class<T> agentClass) {
        agents = new InternalGridAgentList<T>(agentClass, this);
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent() {
        T newAgent = agents.GetNewAgent(tick);
        newAgent.alive = true;
        return newAgent;
    }


    /**
     * shuffles the agent list to randomize iteration do not call this while in the middle of iteration
     *
     * @param rn the Random number generator to be used
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
     * calls CleanAgents, then ShuffleAgents
     */
    public void CleanShuffle(Rand rn) {
        agents.CleanAgents();
        agents.ShuffleAgents(rn);
    }

    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public List<T> AllAgents() {
        return (List<T>) this.agents.GetAllAgents();
    }

    /**
     * calls dispose on all agents in the typeGrid
     */
    public void Reset() {
        List<T> AllAgents = this.agents.GetAllAgents();
        AllAgents.stream().filter(curr -> curr.alive).forEach(Agent0D::Dispose);
        ResetTick();
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

    public int GetTick() {
        return tick;
    }

    public void IncTick() {
        tick++;
    }

    public void ResetTick() {
        tick = 0;
    }

    /**
     * returns the number of agents that are alive in the typeGrid
     */
    public int Pop() {
        //gets population
        return agents.pop;
    }

    @Override
    public Iterator<T> iterator() {
        return agents.iterator();
    }

    void RemoveAgent(T agent) {
        //internal function, removes agent from world
        agents.RemoveAgent(agent);
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
}
