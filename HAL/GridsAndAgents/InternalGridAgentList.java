package HAL.GridsAndAgents;

import HAL.Interfaces.AgentToString;
import HAL.Tools.FileIO;
import HAL.Rand;


import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

/**
 * Created by rafael on 2/17/17.
 */
class InternalGridAgentList<T extends AgentBase> implements Iterable<T>,Serializable{
    ArrayList<T> agents;
    ArrayList<T> deads;
    ArrayList<AgentListIterator> usedIters=new ArrayList<>();
    transient Constructor<?> builder;
    transient Field gridField;
    int iLastAlive;
    int pop;
    final Object myGrid;
    long stateID;
    AgentListIterator outerIter;

    InternalGridAgentList(Class<T> type, Object myGrid){
        SetupConstructor(type);
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
        stateID++;
    }
    void SetupConstructor(Class<T> type){
        this.builder=type.getDeclaredConstructors()[0];
        this.builder.setAccessible(true);
        try {
            gridField=type.getField("G");
            gridField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
    T GetNewAgent(int birthTick){
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
            gridField.set(newAgent,this.myGrid);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new RuntimeException("Could not instantiate");
        }
        agents.add(newAgent);
        //newAgent.myGrid=this.myGrid;
        iLastAlive++;
        newAgent.iList=iLastAlive;
        //agent.iList= iLastAlive;
    }
    newAgent.alive=true;
    newAgent.stateID=this.stateID;
    newAgent.birthTick=birthTick;
    pop++;
    return newAgent;
    }
    void AddAgent(T newAgent){
        if(!newAgent.alive){
            throw new IllegalStateException("can't transplant dead agent between grids!");
        }
        if(iLastAlive+1<agents.size()) {
            agents.add(agents.get(iLastAlive + 1));
            agents.set(iLastAlive+1,newAgent);
            newAgent.iList=iLastAlive+1;
        }
        else {
            agents.add(newAgent);
            newAgent.iList=agents.size()-1;
        }
        //newAgent.myGrid=this.myGrid;
        iLastAlive++;
        pop++;
        newAgent.stateID=stateID;
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
        if(outerIter==null) {
            stateID++;
        }
        AgentListIterator ret;
        if(usedIters.size()>0){
            ret=usedIters.remove(usedIters.size()-1);
            ret.stateID=stateID;
            ret.iAgent=0;
            ret.ret=null;
        }else {
            ret=new AgentListIterator(stateID, this);
        }
        if(outerIter==null){
            outerIter=ret;
        }
        return ret;
    }

    private void ReturnIter(AgentListIterator iter){
        if(usedIters.size()<=5){
            usedIters.add(iter);
        }
    }

    private class AgentListIterator implements Iterator<T>,Serializable{
        long stateID;
        InternalGridAgentList<T> myList;
        int iAgent;
        T ret;

        AgentListIterator(long stateID, InternalGridAgentList<T> myList){
            this.stateID = stateID;
            this.myList=myList;
            this.iAgent=0;
            this.ret=null;
        }
        @Override
        public boolean hasNext() {
            while(iAgent<=iLastAlive) {
                T possibleRet=agents.get(iAgent);
                iAgent += 1;
                if (possibleRet.alive && possibleRet.stateID < stateID) {
                    ret=possibleRet;
                    return true;
                }
            }
            ret=null;
            ReturnIter(this);
            if(outerIter==this){
                outerIter=null;
            }
            return false;
        }

        @Override
        public T next() {
            if(stateID!=myList.stateID){
                throw new IllegalStateException("shuffle or clean or randomagent or incTick called while in the middle of iteration! this is not permitted!");
            }
            return ret;
        }
    }
    public void ShuffleAgents(Rand rn){
        stateID++;
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
    public void CleanAgents() {
        stateID++;
        //deal with cleaning agent list second
        int iSwap = iLastAlive;
        iLastAlive = pop - 1;
        while (deads.size() > 0 && iSwap > iLastAlive) {
            T dead = deads.remove(deads.size() - 1);
            int iDead = dead.iList;
            if (iDead <= iLastAlive) {
                T swap = agents.get(iSwap);
                while (!swap.alive) {
                    iSwap--;
                    if (iSwap <= iLastAlive) {
                        deads.clear();
                        return;
                    }
                    swap = agents.get(iSwap);
                }
                swap.iList = iDead;
                dead.iList = iSwap;
                agents.set(iDead, swap);
                agents.set(iSwap, dead);
                iSwap--;
            }
        }
        deads.clear();
    }
}
