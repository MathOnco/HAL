package HAL.GridsAndAgents;

import HAL.Rand;

import java.io.Serializable;
import java.util.*;

/**
 * AgentLists can hold any type of agent
 * @param <T> the type of agent that the AgentList will hold
 */

public class AgentList<T> implements Iterable<T>,Serializable{
    ArrayList<AgentListNode<T>> nodes;
    ArrayList<AgentListNode<T>> deads;
    ArrayList<myIter> usedIters=new ArrayList<>();
    int iLastAlive;
    int pop;
    long stateID;
    static final long DEAD_ID=Long.MAX_VALUE;

    /**
     * creates an empty AgentList
     */
    public AgentList(){
        this.nodes =new ArrayList<>();
        this.deads=new ArrayList<>();
        //this.map=new HashMap<>();
        this.iLastAlive=-1;
        this.pop=0;
    }

    /**
     * returns whether a given agent is already in the AgentList
     */
    public boolean InList(T agent){
        AgentListNode<T> n=((AgentBase)agent).myNodes;
        while(n!=null){
            if(n.mySet==this){
                return true;
            }
            n=n.next;
        }
        return false;
        //return map.containsKey(agent);
    }


    /**
     * returns the size of the AgentList, duplicate agents will be counted multiple times
     */
    public int GetPop(){
        return pop;
    }

    /**
     * adds an agent to the AgentList, agents can be added multiple times.
     */
    public void AddAgent(T agent){
        if(!((AgentBase)agent).alive){
            throw new IllegalStateException("add dead Agent!");
        }
        AgentListNode<T> node;
        if(deads.size()>0){
            node=deads.remove(deads.size()-1);
        }
        else if(iLastAlive+1< nodes.size()) {
            iLastAlive++;
            node= nodes.get(iLastAlive);
        }
        else{
            node=new AgentListNode<>(this);
            nodes.add(node);
            iLastAlive++;
            node.i=iLastAlive;
        }
        pop++;
        node.stateID=stateID;
        node.SetAgent(agent);
        //map.put(agent,node);
    }

    /**
     * removes all instances of the agent from the AgentList, returns the number of instances removed
     */
    public int RemoveAgent(T agent) {
        AgentListNode<T> n=((AgentBase)agent).myNodes;
        int ct=0;
        while(n!=null){
            if(n.mySet==this){
                n.stateID=DEAD_ID;
                n.PopNode();
                deads.add(n);
                pop--;
                ct++;
            }
            n=n.next;
        }
        return ct;
    }
    public void CleanShuffle(Rand rn){
        CleanAgents();
        ShuffleAgents(rn);
    }

    /**
     * Shuffles the agentlist order (Don't run during agent iteration)
     */
    public void ShuffleAgents(Rand rn){
        stateID++;
        for(int iSwap1 = iLastAlive; iSwap1>0; iSwap1--){
            int iSwap2=rn.Int(iSwap1+1);
            AgentListNode<T> swap1= nodes.get(iSwap1);
            AgentListNode<T> swap2= nodes.get(iSwap2);
            swap1.i = iSwap2;
            swap2.i = iSwap1;
            nodes.set(iSwap2,swap1);
            nodes.set(iSwap1,swap2);
        }
    }

    /**
     * may speed up AgentList iteration if many agents from the AgentList have died recently
     */
    public void CleanAgents(){
        stateID++;
        int iSwap=iLastAlive;
        iLastAlive=pop-1;
        while(deads.size()>0&&iSwap>iLastAlive){
            AgentListNode<T> dead=deads.remove(deads.size()-1);
            int iDead=dead.i;
            if(iDead<=iLastAlive){
                AgentListNode<T> swap= nodes.get(iSwap);
                while((swap.stateID ==DEAD_ID)){
                    iSwap--;
                    if(iSwap<=iLastAlive){
                        deads.clear();
                        return;
                    }
                    swap= nodes.get(iSwap);
                }
                swap.i=iDead;
                dead.i=iSwap;
                nodes.set(iDead,swap);
                nodes.set(iSwap,dead);
                iSwap--;
            }
        }
        deads.clear();
    }

    /**
     * returns a random agent from the AgentList
     */
    public T RandomAgent(Rand rn){
        CleanAgents();
        if(pop==0){
            return null;
        }
        return nodes.get(rn.Int(pop)).agent;
    }
    void RemoveNode(AgentListNode<T> node) {
        node.stateID=DEAD_ID;
        deads.add(node);
        //map.remove(node.agent);
        pop--;
    }

    @Override
    public Iterator<T> iterator() {
        myIter ret;
        if(usedIters.size()>0){
            ret=usedIters.remove(usedIters.size()-1);
        }
        else{
            ret=new myIter(this);
        }
        ret.Setup(stateID);
        return new myIter(this);
    }
    private class myIter implements Iterator<T>,Serializable{
        long stateID;
        final AgentList<T> myList;
        int iNode;
        T ret;

        myIter(AgentList<T> myList){
            this.myList=myList;
        }
        void Setup(long stateID){
            this.stateID = stateID;
            this.iNode =0;
            this.ret=null;
        }
        @Override
        public boolean hasNext() {
            while(iNode <=iLastAlive) {
                AgentListNode<T> possibleRet= nodes.get(iNode);
                iNode += 1;
                if (possibleRet != null && possibleRet.stateID<=stateID) {
                    ret=possibleRet.agent;
                    return true;
                }
            }
            ret=null;
            usedIters.add(this);
            return false;
        }

        @Override
        public T next() {
            if(stateID!=myList.stateID){
                throw new IllegalStateException("shuffle or clean or randomagent called while in the middle of iteration! this is not permitted!");
            }
            return ret;
        }
    }
}

class AgentListNode<T> implements Serializable{
    T agent;
    int i;
    long stateID;
    AgentListNode<T> next;
    AgentListNode<T> prev;
    final AgentList<T> mySet;

    AgentListNode(AgentList<T> mySet) {
        this.mySet = mySet;
    }
    void SetAgent(T agent){
        this.agent=agent;
        this.next=((AgentBase)agent).myNodes;
        ((AgentBase)agent).myNodes=this;
        this.prev=null;

    }
    void PopNode(){
        if(((AgentBase)agent).myNodes==this){
            ((AgentBase)agent).myNodes=this.next;
        }
        if(this.next!=null){
            this.next.prev=this.prev;
        }
        if(this.prev!=null){
            this.prev.next=this.next;
        }
    }
    void DisposeAll(){
        AgentListNode remMe=this;
        while(remMe!=null){
            remMe.mySet.RemoveNode(remMe);
            remMe=remMe.next;
        }
        ((AgentBase)agent).myNodes=null;
    }
}

