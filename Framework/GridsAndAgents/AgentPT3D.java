package Framework.GridsAndAgents;

import Framework.Interfaces.Coords3DToAction;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * extend the AgentPT3D class if you want agents that exist on a 3D continuous lattice
 * with the possibility of stacking multiple agents on the same typeGrid square
 * @param <T> the extended AgentGrid3D class that the agents will live in
 * Created by rafael on 11/18/16.
 */
public class AgentPT3D<T extends AgentGrid3D> extends AgentBaseSpatial<T> {
    private double xPos;
    private double yPos;
    private double zPos;
    AgentPT3D nextSq;
    AgentPT3D prevSq;

    @Override
    public void MoveSQ(int iNext) {
        if(!alive){
            throw new RuntimeException("attempting to move dead agent");
        }
        RemSQ();
        xPos=myGrid.ItoX(iNext)+0.5;
        yPos=myGrid.ItoY(iNext)+0.5;
        zPos=myGrid.ItoZ(iNext)+0.5;
        iSq=iNext;
        AddSQ(iNext);
    }

    @Override
    void Setup(double i) {
        Setup((int)i);
    }

    @Override
    void Setup(double x, double y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }

    void Setup(double xPos, double yPos, double zPos){
        this.xPos=xPos;
        this.yPos=yPos;
        this.zPos=zPos;
        iSq=myGrid.I(xPos,yPos,zPos);
        AddSQ(iSq);
    }

    @Override
    void Setup(int i) {
        this.xPos=myGrid.ItoX(i)+0.5;
        this.yPos=myGrid.ItoY(i)+0.5;
        this.zPos=myGrid.ItoZ(i)+0.5;
        iSq=myGrid.I(xPos,yPos,zPos);
        AddSQ(iSq);
    }

    @Override
    void Setup(int x, int y) {
        throw new IllegalStateException("shouldn't be adding 3D agent to 2D typeGrid");
    }

    void Setup(int xPos,int yPos,int zPos){
        this.xPos=xPos+0.5;
        this.yPos=yPos+0.5;
        this.zPos=zPos+0.5;
        iSq=myGrid.I(xPos,yPos,zPos);
        AddSQ(iSq);
    }
    void AddSQ(int i){
        if(myGrid.grid[i]!=null){
            ((AgentPT3D)myGrid.grid[i]).prevSq=this;
            this.nextSq=(AgentPT3D)myGrid.grid[i];
        }
        myGrid.grid[i]=this;
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
    public void MoveSQ(int newX, int newY, int newZ){
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        int oldX=(int)xPos;
        int oldY=(int)yPos;
        int oldZ=(int)zPos;
        RemSQ();
        iSq=myGrid.I(newX,newY,newZ);
        AddSQ(iSq);
        this.xPos=newX+0.5;
        this.yPos=newY+0.5;
        this.zPos=newZ+0.5;
    }

    /**
     * Moves the agent to the specified coordinates
     */
    public void MovePT(double newX, double newY, double newZ){
        int xIntNew=(int)newX;
        int yIntNew=(int)newY;
        int zIntNew=(int)newZ;
        int xIntOld=(int)xPos;
        int yIntOld=(int)yPos;
        int zIntOld=(int)zPos;
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if(xIntNew!=xIntOld||yIntNew!=yIntOld||zIntNew!=zIntOld) {
            RemSQ();
            iSq=myGrid.I(xIntNew,yIntNew,zIntNew);
            AddSQ(iSq);
        }
        xPos=newX;
        yPos=newY;
        zPos=newZ;
    }


    public void MoveSafePT(double newX, double newY, double newZ, boolean wrapX, boolean wrapY, boolean wrapZ) {
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if (G().In(newX, newY, newZ)) {
            MovePT(newX, newY, newZ);
            return;
        }
        if (wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xpt();
        }
        if (wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY)) {
            newY = Ypt();
        }
        if (wrapZ) {
            newZ = ModWrap(newZ, G().zDim);
        } else if (!InDim(G().zDim, newZ)) {
            newZ = Zpt();
        }
        MovePT(newX,newY,newZ);
    }
    public void MoveSafePT(double newX, double newY, double newZ) {
        if(!alive){
            throw new RuntimeException("Attempting to move dead agent!");
        }
        if (G().In(newX, newY, newZ)) {
            MovePT(newX, newY, newZ);
            return;
        }
        if (G().wrapX) {
            newX = ModWrap(newX, G().xDim);
        } else if (!InDim(G().xDim, newX)) {
            newX = Xpt();
        }
        if (G().wrapY) {
            newY = ModWrap(newY, G().yDim);
        } else if (!InDim(G().yDim, newY)) {
            newY = Ypt();
        }
        if (G().wrapZ) {
            newZ = ModWrap(newZ, G().zDim);
        } else if (!InDim(G().zDim, newZ)) {
            newZ = Zpt();
        }
        MovePT(newX,newY,newZ);
    }
    /**
     * gets the xDim coordinate of the agent
     */
    public double Xpt(){
        return xPos;
    }

    /**
     * gets the yDim coordinate of the agent
     */
    public double Ypt(){
        return yPos;
    }

    /**
     * gets the z coordinate of the agent
     */
    public double Zpt(){
        return zPos;
    }

    /**
     * gets the xDim coordinate of the square that the agent occupies
     */
    public int Xsq(){
        return (int)xPos;
    }

    /**
     * gets the yDim coordinate of the square that the agent occupies
     */
    public int Ysq(){
        return (int)yPos;
    }

    /**
     * gets the z coordinate of the square that the agent occupies
     */
    public int Zsq(){ return (int)zPos; }

    public void Dispose(){
        if(!alive){
            throw new RuntimeException("attepting to dispose already dead agent");
        }
        RemSQ();
        myGrid.agents.RemoveAgent(this);
    }

    @Override
    void GetAllOnSquare(ArrayList<AgentBaseSpatial> putHere) {
        AgentPT3D toList=this;
        while (toList!=null){
            putHere.add(toList);
            toList=toList.nextSq;
        }
    }

    public<T extends AgentPT3D> double Xdisp(T other, boolean wrapX){
        return wrapX? DistWrap(other.Xpt(),Xpt(),G().xDim):Xpt()-other.Xpt();
    }
    public <T extends AgentPT3D> double Ydisp(T other, boolean wrapY){
        return wrapY? DistWrap(other.Ypt(),Ypt(),G().yDim):Ypt()-other.Ypt();
    }
    public <T extends AgentPT3D> double Zdisp(T other, boolean wrapY){
        return wrapY? DistWrap(other.Zpt(),Zpt(),G().zDim):Zpt()-other.Zpt();
    }

    public <T extends AgentPT3D> double disp(T other, boolean wrap){
        double dx = Xdisp(other, wrap);
        double dy = Ydisp(other, wrap);
        double dz = Zdisp(other, wrap);
        return Norm(dx, dy, dz);
    }
}
