package HAL.GridsAndAgents;

import HAL.Interfaces.IndexIntAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

/*
TODO:
 1) Extend move functions to other grid types
 */

public class PopulationGridBase implements Iterable<Integer>{
    //use live indices to iterate over agents when the number is low
    private final int[]agents;
    private final int[]deltas;
    private int occupiedArea;
    private long pop;
    private int updateCt;
    public final int length;

    public boolean usingSparseIndices;
    private int firstLiveIndex;
    private int[]nextLiveIndex;
    private int[]prevLiveIndex;
    private int[] nextLiveDelta;
    private int firstLiveDelta;
    private final static int UNLINKED =-1;
    private final static int LINKED_TO =-2;
    private final static int START_ITER =-3;
    private ArrayList<OccupiedIterator> usedIters=new ArrayList<>();

    public PopulationGridBase(int length) {
        this.agents=new int[length];
        this.deltas=new int[length];
        this.length=length;
    }
    public void Add(int i,int val){
        if(val!=0) {
            deltas[i] += val;
            if (usingSparseIndices && nextLiveDelta[i] == UNLINKED) {
                if (firstLiveDelta == UNLINKED) {
                    nextLiveDelta[i] = LINKED_TO;
                    firstLiveDelta = i;
                } else {
                    nextLiveDelta[i] = firstLiveDelta;
                    firstLiveDelta = i;
                }
            }
        }
    }
    public void Set(int i,int val){
        Add(i,val-agents[i]);
    }
    public void SetAll(int val){
        for (int i = 0; i < length; i++) {
            Add(i,val-agents[i]);
        }
    }
    public long Pop(){
        return pop;
    }
    public int OccupiedArea(){
        return occupiedArea;
    }
    public int UpdateCt(){
        return updateCt;
    }
    public int Get(int i){
        return agents[i];
    }
    public void Move(int iFrom,int iTo,int val){
        Add(iFrom,-val);
        Add(iTo,val);
    }
    public <T extends PopulationGridBase> void Move(int iFrom,int iTo,T gridTo,int val){
        Add(iFrom,-val);
        gridTo.Add(iTo,val);
    }
    public void ApplyOccupied(IndexIntAction Action){
        int updateID=updateCt;
        if(usingSparseIndices){
            int i=firstLiveIndex;
            while (i>=0){
                Action.Action(i,agents[i]);
                i=nextLiveIndex[i];
            }
        }
        else{
            for (int i = 0; i < length; i++) {
                if(agents[i]!=0){
                    Action.Action(i,agents[i]);
                }
            }
        }
        if(updateID!=updateCt){
            throw new IllegalStateException("update during apply iteration not permitted!");
        }
    }

    private void AddPopIndex(int i){
        occupiedArea++;
        if(firstLiveIndex!=UNLINKED){
            prevLiveIndex[firstLiveIndex]=i;
            nextLiveIndex[i]=firstLiveIndex;
        }
        firstLiveIndex=i;
    }

    private void RemovePopIndex(int i){
        occupiedArea--;
        //pop live index
        if(firstLiveIndex==i){
            //need to reassign firstLiveIndex
            firstLiveIndex=nextLiveIndex[i];
        }
        if(prevLiveIndex[i]>=0){
            nextLiveIndex[prevLiveIndex[i]]=nextLiveIndex[i];
        }
        if(nextLiveIndex[i]>=0){
            prevLiveIndex[nextLiveIndex[i]]=prevLiveIndex[i];
        }
        prevLiveIndex[i]=UNLINKED;
        nextLiveIndex[i]=UNLINKED;
    }

    public void Update(){
        updateCt++;
        if(usingSparseIndices){
            int i= firstLiveDelta;
            firstLiveDelta=UNLINKED;
            while(i>=0) {
                int prev = agents[i];
                agents[i] += deltas[i];
                pop+=deltas[i];
                deltas[i]=0;
                if (prev == 0 && agents[i] != 0) {
                    AddPopIndex(i);
                } else if (prev != 0 && agents[i] == 0) {
                    RemovePopIndex(i);
                }
                if (agents[i] < 0) {
                    throw new IllegalStateException("number of agents is below zero, could be overflow or underflow! index:" + i + " val:" + agents[i]+"consider using the PopulationGridLong if this is due to population overflow");
                }
                int j=nextLiveDelta[i];
                nextLiveDelta[i]=UNLINKED;
                i=j;
            }
        }
        else {
            for (int i = 0; i < agents.length; i++) {
                int prev = agents[i];
                agents[i] += deltas[i];
                pop+=deltas[i];
                deltas[i]=0;
                if (prev == 0 && agents[i] != 0) {
                    occupiedArea++;
                } else if (prev != 0 && agents[i] == 0) {
                    occupiedArea--;
                }
                if (agents[i] < 0) {
                    throw new IllegalStateException("number of agents is below zero, could be overflow or underflow! index:" + i + " val:" + agents[i]);
                }
            }
        }
        if(!usingSparseIndices && occupiedArea<=length*0.001){
            usingSparseIndices=true;
            SetupSparseIndices();
        }
        if(usingSparseIndices && occupiedArea>=length*0.01){
            usingSparseIndices=false;
        }
    }

    public void SetupSparseIndices(){
        //called when occupiedArea reaches sparse threshold at end of Update()
        //set usingSparseIndices to false when occupiedArea reaches non-sparse threshold

        //setup indices arrays
        if(nextLiveIndex==null){
            nextLiveIndex=new int[agents.length];
            prevLiveIndex=new int[agents.length];
            nextLiveDelta =new int[agents.length];
        }
        Arrays.fill(nextLiveIndex, UNLINKED);
        Arrays.fill(prevLiveIndex, UNLINKED);
        Arrays.fill(nextLiveDelta, UNLINKED);
        firstLiveIndex=UNLINKED;
        firstLiveDelta=UNLINKED;

        //link indices
        for (int i = 0; i < length; i++) {
            if(agents[i]>0){
                AddPopIndex(i);
            }
        }
    }
    public Iterator<Integer> iterator(){
        OccupiedIterator ret;
        if(usedIters.size()>0){
            ret=usedIters.remove(usedIters.size()-1);
        }
        else {
            ret=new OccupiedIterator();
        }
        ret.Setup();
        return ret;
    }
    public void AddTo(int[] dest){
        if(usingSparseIndices){
            ApplyOccupied((i,ct)->{
                dest[i]+=ct;
            });
        }
        for (int i = 0; i < length; i++) {
            dest[i]+=agents[i];
        }
    }

    public void AddTo(Grid2Dint dest){
        AddTo(dest.GetField());

    }

    public void CopyTo(int[] dest){
        System.arraycopy(agents,0,dest,0,length);
    }

    private class OccupiedIterator implements Iterator<Integer>, Iterable<Integer>, Serializable {
        int iCurr;
        int updateID;

        OccupiedIterator() {
        }

        public void Setup() {
            iCurr=START_ITER;
            updateID=updateCt;
        }

        @Override
        public boolean hasNext() {
            if(usingSparseIndices){
                if(iCurr==START_ITER){
                    iCurr=firstLiveIndex;
                }
                else{
                    iCurr=nextLiveIndex[iCurr];
                }
                if(iCurr<0){
                    return false;
                }
                return true;
            }
            if(iCurr==START_ITER) {
                iCurr=0;
            }
            else{
                iCurr++;
            }
            while(iCurr<length){
                if(agents[iCurr]>0){
                    return true;
                }
                iCurr++;
            }
            return false;
        }

        @Override
        public Integer next() {
            return iCurr;
        }

        @Override
        public Iterator<Integer> iterator() {
            return this;
        }
    }
}

