package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;

public abstract class Agent3DBase<T extends AgentGrid3D> extends AgentBaseSpatial<T>{
    public abstract int Xsq();
    public abstract int Ysq();
    public abstract int Zsq();
    public abstract double Xpt();
    public abstract double Ypt();
    public abstract double Zpt();
    public int ConvXsq(GridBase3D other){
        return (int)(((Xsq()+0.5)*other.xDim)/G().xDim);
    }
    public int ConvYsq(GridBase3D other){
        return (int)(((Ysq()+0.5)*other.yDim)/G().yDim);
    }
    public int ConvZsq(GridBase3D other){
        return (int)(((Zsq()+0.5)*other.zDim)/G().zDim);
    }
    public int ConvI(GridBase3D other){
        return other.I(ConvXsq(other),ConvYsq(other),ConvZsq(other));
    }
    public double ConvXpt(GridBase3D other){
        return Xpt()*other.xDim/G().xDim;
    }
    public double ConvYpt(GridBase3D other){
        return Ypt()*other.yDim/G().yDim;
    }
    public double ConvZpt(GridBase3D other){
        return Ypt()*other.zDim/G().zDim;
    }
    public int MapHood(int[] hood){
        return G().MapHood(hood,Xsq(),Ysq(),Zsq());
    }
    public int MapHood(int[] hood,Coords3DToBool Eval){
        return G().MapHood(hood,Xsq(),Ysq(),Zsq(),Eval);
    }
    public int MapEmptyHood(int[] hood){
        return G().MapEmptyHood(hood,Xsq(),Ysq(),Zsq());
    }
    public int MapOccupiedHood(int[] hood){
        return G().MapOccupiedHood(hood,Xsq(),Ysq(),Zsq());
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
}
