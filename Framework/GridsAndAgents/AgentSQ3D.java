package Framework.GridsAndAgents;

import Framework.Interfaces.Coords3DToAction;

import java.util.ArrayList;

import static Framework.Util.InDim;
import static Framework.Util.ModWrap;

/**
 * extend the AgentSQ3D class if you want agents that exist on a 3D discrete lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid3D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentSQ3D<T extends AgentGrid3D> extends AgentBaseSpatial<T>{
    int xSq;
    int ySq;
    int zSq;
    AgentSQ3D nextSq;
    AgentSQ3D prevSq;

     void Setup(int xSq,int ySq,int zSq){
        this.xSq=xSq;
        this.ySq=ySq;
        this.zSq=zSq;
        this.iSq=myGrid.I(xSq,ySq,zSq);
        AddSQ(iSq);
    }
    void Setup(double xPos,double yPos,double zPos){
        Setup((int)xPos,(int)yPos,(int)zPos);
    }

    @Override
    void Setup(int i) {
        this.iSq=i;
        this.xSq=myGrid.ItoX(i);
        this.ySq=myGrid.ItoY(i);
        this.zSq=myGrid.ItoZ(i);
        AddSQ(iSq);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }
    void AddSQ(int i){
        if(myGrid.grid[i]!=null){
            ((AgentSQ3D)myGrid.grid[i]).prevSq=this;
            this.nextSq=(AgentSQ3D)myGrid.grid[i];
        }
        myGrid.grid[i]=this;
    }

    void RemSQ(){
        if(myGrid.grid[iSq]==this){
            myGrid.grid[iSq]=this.nextSq;
        }
        if(nextSq!=null){
            nextSq.prevSq=prevSq;
        }
        if(prevSq!=null){
            prevSq.nextSq=nextSq;
        }
        prevSq=null;
        nextSq=null;
    }
    /**
     * Moves the agent to the specified coordinates
     */
    public void MoveSQ(int x, int y, int z){
        //moves agent discretely
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        int iNewPos=myGrid.I(x,y,z);
        RemSQ();
        this.xSq=x;
        this.ySq=y;
        this.zSq=z;
        this.iSq=iNewPos;
        AddSQ(iNewPos);
    }
    public int HoodToAction(int[]neighborhood, Coords3DToAction Action){
        return G().HoodToAction(neighborhood,Xsq(),Ysq(),Zsq(),Action);
    }

    public int HoodToIs(int[]neighborhood,int[]retIs){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    public int HoodToIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return xSq;
    }

    /**
     * gets the yDim coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return ySq;
    }

    /**
     * gets the z coordinate of the square that the agent occupies
     */
    public int Zsq(){
        return zSq;
    }

    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return xSq+0.5;
    }

    /**
     * gets the yDim coordinate of the agent
     */
    public double Ypt(){
        return ySq+0.5;
    }

    /**
     * gets the z coordinate of the agent
     */
    public double Zpt(){ return zSq+0.5;}
    /**
     * deletes the agent
     */
    public void Dispose(){
        //kills agent
        if(!alive){
            throw new RuntimeException("attempting to dispose already dead agent");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentSQ3D toList=this;
        while (toList!=null){
            putHere.add(toList);
            toList=toList.nextSq;
        }
    }

    @Override
    public void MoveSQ(int iNext) {
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        xSq=myGrid.ItoX(iNext);
        ySq=myGrid.ItoY(iNext);
        zSq=myGrid.ItoZ(iNext);
        iSq=iNext;
        AddSQ(iNext);
    }
    public void MoveSafeSQ(int newX, int newY, int newZ, boolean wrapX, boolean wrapY, boolean wrapZ) {
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if (G().In(newX, newY, newZ)) {
            MoveSQ(newX, newY, newZ);
            return;
        }
        if (wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        if (wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY)) {
            newY = Ysq();
        }
        if (wrapZ) {
            newZ = ModWrap(newZ, G().zDim);
        } else if (!InDim(G().zDim, newZ)) {
            newZ = Zsq();
        }
        MoveSQ(newX,newY,newZ);
    }
    public void MoveSafeSQ(int newX, int newY, int newZ) {
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if (G().In(newX, newY, newZ)) {
            MoveSQ(newX, newY, newZ);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        if (G().wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY)) {
            newY = Ysq();
        }
        if (G().wrapZ) {
            newZ = ModWrap(newZ, G().zDim);
        } else if (!InDim(G().zDim, newZ)) {
            newZ = Zsq();
        }
        MoveSQ(newX,newY,newZ);
    }

    @Override
    void Setup(double i) {
        Setup((int)i);
    }

    @Override
    void Setup(double x, double y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }
    //addCoords
}
