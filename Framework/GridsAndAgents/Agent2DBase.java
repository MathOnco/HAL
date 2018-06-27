package Framework.GridsAndAgents;

import Framework.Interfaces.*;

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
    public int MapHood(int[] hood,IndexCoords2DBool Eval){
        return G().MapHood(hood,Xsq(),Ysq(),Eval);
    }
}

