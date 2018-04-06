package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;

public abstract class Agent2DBase<T extends AgentGrid2D> extends AgentBaseSpatial<T>{
    public abstract int Xsq();
    public abstract int Ysq();
    public abstract double Xpt();
    public abstract double Ypt();
    public int ConvXsq(GridBase2D other){
        return (int)(((Xsq()+0.5)*other.xDim)/G().xDim);
    }
    public int ConvYsq(GridBase2D other){
        return (int)(((Ysq()+0.5)*other.yDim)/G().yDim);
    }
    public int ConvI(GridBase2D other){
        return other.I(ConvXsq(other),ConvYsq(other));
    }
    public double ConvXpt(GridBase2D other){
        return Xpt()*other.xDim/G().xDim;
    }
    public double ConvYpt(GridBase2D other){
        return Ypt()*other.yDim/G().yDim;
    }
    public int MapHood(int[] hood){
        return G().MapHood(hood,Xsq(),Ysq());
    }
    public int MapEmptyHood(int[] hood){
        return G().MapEmptyHood(hood,Xsq(),Ysq());
    }
    public int MapOccupiedHood(int[] hood){
        return G().MapOccupiedHood(hood,Xsq(),Ysq());
    }
    public int MapHood(int[] hood,Coords2DToBool Eval){
        return G().MapHood(hood,Xsq(),Ysq(),Eval);
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

}

