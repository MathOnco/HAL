package Framework.GridsAndAgents;

import Framework.Rand;

import java.util.*;

/**
 * Extend the Grid2unstackable class if you want a 2D lattice with at most one agent per typeGrid square
 * @param <T> the AgentSQ2Dunstackable extending agent class that will inhabit the typeGrid
 */
public class AgentGrid0D<T extends Agent0D> implements Iterable<T>{
    InternalGridAgentList<T> agents;
    int tick;

    /**
     * @param agentClass pass T.class, used to instantiate agent instances within the typeGrid as needed
     */
    public AgentGrid0D(Class<T> agentClass){
        agents=new InternalGridAgentList<T>(agentClass,this);
    }

    /**
     * returns an uninitialized agent at the specified coordinates
     */
    public T NewAgent(){
        T newAgent=agents.GetNewAgent(tick);
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

    public void CleanShuffle(Rand rn){
        agents.CleanAgents();
        agents.ShuffleAgents(rn);
    }

    /**
     * returns an umodifiable copy of the complete agentlist, including dead and just born agents
     */
    public ArrayList<T> AllAgents(){return (ArrayList<T>)this.agents.GetAllAgents();}


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
    /**
     * calls dispose on all agents in the typeGrid
     */
    public void Reset(){
        List<T> AllAgents=this.agents.GetAllAgents();
        AllAgents.stream().filter(curr -> curr.alive).forEach(Agent0D::Dispose);
        ResetTick();
    }

    public T RandomAgent(Rand rn){
        CleanAgents();
        if(GetPop()==0){
            return null;
        }
        return agents.agents.get(rn.Int(GetPop()));
    }

    public int GetTick(){
        return tick;
    }
    public void IncTick(){
        tick++;
    }
    public void ResetTick(){tick=0;}
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
