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
public class AgentSQ3Dunstackable<T extends AgentGrid3D> extends AgentBaseSpatial<T>{
    int xSq;
    int ySq;
    int zSq;

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

    public void SwapPosition(AgentBaseSpatial other){
        if(this.Isq()==other.Isq()){
            return;
        }
        if(!alive||!other.alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        if(other.myGrid!=myGrid){
            throw new IllegalStateException("can't swap positions between agents on different grids!");
        }
        int iNew=other.Isq();
        int iNewOther=Isq();
        other.RemSQ();
        this.RemSQ();
        other.Setup(iNewOther);
        this.Setup(iNew);
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
    void AddSQ(int i){
        if(myGrid.grid[iSq]!=null){
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        myGrid.grid[iSq]=this;
    }


    void RemSQ(){
        myGrid.grid[iSq]=null;
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
        putHere.add(this);
    }

    @Override
    public void MoveSQ(int i) {
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        RemSQ();
        xSq=myGrid.ItoX(i);
        ySq=myGrid.ItoY(i);
        zSq=myGrid.ItoZ(i);
        iSq=i;
        AddSQ(i);
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

    }

    @Override
    void Setup(double x, double y) {

    }
}
