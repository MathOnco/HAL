package Framework.GridsAndAgents;

import Framework.Interfaces.Coords1DBool;

public abstract class Agent1DBase<T extends AgentGrid1D> extends AgentBaseSpatial<T>{
    public abstract int Xsq();
    public abstract double Xpt();
    public int ConvXsq(GridBase1D other){
        return (int)(((Xsq()+0.5)*other.xDim)/ G.xDim);
    }
    public int ConvI(GridBase1D other){
        return ConvXsq(other);
    }
    public double ConvXpt(GridBase2D other){
        return Xpt()*other.xDim/ G.xDim;
    }
    public int MapHood(int[] hood){
        return G.MapHood(hood,Xsq());
    }
    public int MapEmptyHood(int[] hood){
        return G.MapEmptyHood(hood,Xsq());
    }
    public int MapOccupiedHood(int[] hood){
        return G.MapOccupiedHood(hood,Xsq());
    }
    public int MapHood(int[] hood,Coords1DBool Eval){
        return G.MapHood(hood,Xsq(),Eval);
    }

}

