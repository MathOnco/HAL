package Framework.GridsAndAgents;

import Framework.Interfaces.Coords1DToBool;
import Framework.Interfaces.Coords2DToBool;

public abstract class Agent1DBase<T extends AgentGrid1D> extends AgentBaseSpatial<T>{
    public abstract int Xsq();
    public abstract double Xpt();
    public int ConvXsq(GridBase1D other){
        return (int)(((Xsq()+0.5)*other.xDim)/G().xDim);
    }
    public int ConvI(GridBase1D other){
        return ConvXsq(other);
    }
    public double ConvXpt(GridBase2D other){
        return Xpt()*other.xDim/G().xDim;
    }
    public int MapHood(int[] hood){
        return G().MapHood(hood,Xsq());
    }
    public int MapEmptyHood(int[] hood){
        return G().MapEmptyHood(hood,Xsq());
    }
    public int MapOccupiedHood(int[] hood){
        return G().MapOccupiedHood(hood,Xsq());
    }
    public int MapHood(int[] hood,Coords1DToBool Eval){
        return G().MapHood(hood,Xsq(),Eval);
    }
    public int HoodToIs(int[]neighborhood,int[]retIs){
        return G().HoodToIs(neighborhood,retIs,this.Xsq());
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq());
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq());
    }
    public int HoodToIs(int[]neighborhood,int[]retIs,boolean wrapX){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),wrapX);
    }
    public int HoodToEmptyIs(int[]neighborhood,int[]retIs,boolean wrapX){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),wrapX);
    }
    public int HoodToOccupiedIs(int[]neighborhood,int[]retIs,boolean wrapX){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),wrapX);
    }

}

