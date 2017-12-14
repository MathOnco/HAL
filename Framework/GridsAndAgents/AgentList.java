package Framework.GridsAndAgents;

import Framework.Interfaces.AgentToString;
import Framework.Tools.FileIO;
import Framework.Rand;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.util.*;

/**
 * Created by rafael on 2/17/17.
 */
class AgentList <T extends AgentBase> implements Iterable<T>,Serializable{
    ArrayList<T> agents;
    ArrayList<T> deads;
    transient Constructor<?> builder;
    int iLastAlive;
    int pop;
    final GridBase myGrid;

    AgentList(Class<T> type, GridBase myGrid){
        this.builder=type.getDeclaredConstructors()[0];
        this.builder.setAccessible(true);
        this.agents=new ArrayList<>();
        this.deads=new ArrayList<>();
        this.iLastAlive=-1;
        this.pop=0;
        this.myGrid=myGrid;
    }
    void Reset(){
        this.agents.clear();
        this.deads.clear();
        this.iLastAlive=-1;
        this.pop=0;
    }
    void SetupConstructor(Class<T> type){
        this.builder=type.getDeclaredConstructors()[0];
        this.builder.setAccessible(true);
    }
    T GetNewAgent(){
    T newAgent;
    //internal function, inserts agent into AgentGridMin.AgentGrid2_5
    if(deads.size()>0){
        newAgent=deads.remove(deads.size()-1);
    }
    else if(agents.size()>iLastAlive+1){
        iLastAlive++;
        newAgent=agents.get(iLastAlive);
    }
    else {
        try {
            newAgent = (T)builder.newInstance();
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Could not instantiate");
        }
        agents.add(newAgent);
        newAgent.myGrid=this.myGrid;
        iLastAlive++;
        newAgent.iList=iLastAlive;
        //agent.iList= iLastAlive;
    }
    newAgent.alive=true;
    newAgent.birthTick=this.myGrid.tick;
    pop++;
    return newAgent;
    }
    void AddAgent(T newAgent){
        if(!newAgent.alive){
            throw new IllegalStateException("can't transplant dead agent between grids!");
        }
        if(iLastAlive+1<agents.size()) {
            agents.add(agents.get(iLastAlive + 1));
        }
        agents.add(iLastAlive+1,newAgent);
        iLastAlive++;
        pop++;
        newAgent.birthTick=myGrid.tick;
    }
    void RemoveAgent(T agent) {
        agent.alive = false;
        deads.add(agent);
        pop--;
    }
    List<T> GetAllAgents(){
        return Collections.unmodifiableList(this.agents);//will contain dead agents and newly born agents
    }
    List<T> GetAllDeads(){
        return Collections.unmodifiableList(this.deads);//will contain dead agents and newly born agents
    }

    public void PopToCSV(FileIO out, AgentToString strFn){
        for (T agent : this) {
            out.Write(strFn.AtoS(agent)+"\n");
        }
    }
    @Override
    public Iterator<T> iterator() {
        return new myIter(this);
    }
    private class myIter implements Iterator<T>{
        AgentList<T> myList;
        int iAgent;
        T ret;

        T NextAgent(){
            //use within a while loop that exits when the returned agent is null to iterate over all agents (advances Age of agents)
            while(iAgent<=iLastAlive) {
                T possibleRet=agents.get(iAgent);
                iAgent += 1;
                if (possibleRet != null && possibleRet.alive && possibleRet.birthTick != myList.myGrid.tick) {
                    return possibleRet;
                }
            }
            return null;
        }
        myIter(AgentList<T> myList){
            this.myList=myList;
            this.iAgent=0;
            this.ret=null;
        }
        @Override
        public boolean hasNext() {
            while(iAgent<=iLastAlive) {
                T possibleRet=agents.get(iAgent);
                iAgent += 1;
                if (possibleRet != null && possibleRet.alive && possibleRet.birthTick != myList.myGrid.tick) {
                    ret=possibleRet;
                    return true;
                }
            }
            ret=null;
            return false;
        }

        @Override
        public T next() {
            return ret;
        }
    }
    public void ShuffleAgents(Rand rn){
        //shuffles the agents list (Don't run during agent iteration)
        for(int iSwap1 = iLastAlive; iSwap1>0; iSwap1--){
            int iSwap2=rn.Int(iSwap1+1);
            T swap1=agents.get(iSwap1);
            T swap2=agents.get(iSwap2);
            swap1.iList = iSwap2;
            swap2.iList = iSwap1;
            agents.set(iSwap2,swap1);
            agents.set(iSwap1,swap2);
        }
    }
    public void CleanAgents(){
        int iSwap=iLastAlive;
        iLastAlive=pop-1;
        while(deads.size()>0&&iSwap>iLastAlive){
            T dead=deads.remove(deads.size()-1);
            int iDead=dead.iList;
            if(iDead<=iLastAlive){
                T swap=agents.get(iSwap);
                while(!swap.alive){
                    iSwap--;
                    if(iSwap<=iLastAlive){
                        deads.clear();
                        return;
                    }
                    swap=agents.get(iSwap);
                }
                swap.iList=iDead;
                dead.iList=iSwap;
                agents.set(iDead,swap);
                agents.set(iSwap,dead);
                iSwap--;
            }
        }
        deads.clear();
    }
}
