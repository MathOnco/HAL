package Framework.GridsAndAgents;

import Framework.Interfaces.CoordsAction;
import Framework.Interfaces.IndexAction;

import java.util.ArrayList;

import static Framework.Util.InDim;
import static Framework.Util.ModWrap;

/**
 * extend the AgentSQ2Dunstackable class if you want agents that exist on a 2D discrete lattice
 * without the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended Grid2unstackable class that the agents will live in
 * Created by rafael on 11/18/16.
 */

public class AgentSQ2Dunstackable<T extends AgentGrid2D> extends AgentBaseSpatial <T>{
    int xSq;
    int ySq;

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
    void Setup(double i){
        Setup((int)i);
    }
    void Setup(double xSq,double ySq){
        Setup((int)xSq,(int)ySq);
    }
    void Setup(double xSq,double ySq,double zSq){
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    @Override
    void Setup(int i) {
        xSq=myGrid.ItoX(i);
        ySq=myGrid.ItoY(i);
        iSq=i;
        AddSQ(i);
    }

    @Override
    void Setup(int x, int y) {
        this.xSq=x;
        this.ySq=y;
        iSq=myGrid.I(xSq,ySq);
        AddSQ(iSq);
    }

    @Override
    void Setup(int x, int y, int z) {
        throw new IllegalStateException("shouldn't be adding 2D agent to 3D typeGrid");
    }

    /**
     * Moves the agent to the square with the specified index
     */
    public void MoveSQ(int i){
        //moves agent discretely
        if(!this.alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        this.xSq=myGrid.ItoX(i);
        this.ySq=myGrid.ItoY(i);
        myGrid.grid[iSq]=null;
        iSq=i;
        AddSQ(i);
    }
    void AddSQ(int i){
        if(myGrid.grid[iSq]!=null){
            throw new RuntimeException("Adding multiple unstackable agents to the same square!");
        }
        myGrid.grid[iSq]=this;
    }
    public int HoodToIs(int[]neighborhood,int[]retIs){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq());
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq());
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq());
    }
    public int HoodToIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq(),wrapX,wrapY);
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq(),wrapX,wrapY);
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq(),wrapX,wrapY);
    }
    void RemSQ(){
        myGrid.grid[iSq]=null;
    }

    /**
     * Moves the agent to the square at the specified coordinates
     */
    public void MoveSQ(int x, int y){
        if(!this.alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        int iNewPos=myGrid.I(x,y);
        RemSQ();
        AddSQ(iNewPos);
        this.xSq=x;
        this.ySq=y;
        this.iSq=iNewPos;
    }

    public void MoveSafeSQ(int newX,int newY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX, newY)) {
            MoveSQ(newX, newY);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        if (G().wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY))
            newY = Ysq();
        MoveSQ(newX,newY);
    }
    public void MoveSafeSQ(int newX,int newY,boolean wrapX,boolean wrapY){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent");
        }
        if (G().In(newX, newY)) {
            MoveSQ(newX, newY);
            return;
        }
        if (wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xsq();
        }
        if (wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY))
            newY = Ysq();
        MoveSQ(newX,newY);
    }
    /**
     * Gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return xSq;
    }
    /**
     * Gets the yDim coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return ySq;
    }
    /**
     * Gets the xDim coordinate agent
     */
    public double Xpt(){
        return xSq+0.5;
    }
    /**
     * Gets the yDim coordinate agent
     */
    public double Ypt(){
        return ySq+0.5;
    }

    /**
     * Deletes the agent
     */
    public void Dispose(){
        if(!this.alive){
            throw new RuntimeException("Attempting to dispose already dead agent!");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
    }
    public void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere){
        putHere.add(this);
    }

    /**
     * Gets the index of the square that the agent occupies
     */
    public int Isq(){
        return iSq;
    }
}