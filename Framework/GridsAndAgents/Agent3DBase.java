package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;

public interface Agent3DBase{
    int Xsq();
    int Ysq();
    int Zsq();
    double Xpt();
    double Ypt();
    double Zpt();
    int Isq();
    AgentGrid3D G();
    default int ConvXsq(GridBase3D other){
        return Xsq()*other.xDim/G().xDim;
    }
    default int ConvYsq(GridBase3D other){
        return Ysq()*other.yDim/G().yDim;
    }
    default int ConvZsq(GridBase3D other){
        return Zsq()*other.zDim/G().zDim;
    }
    default int ConvI(GridBase3D other){
        return other.I(ConvXsq(other),ConvYsq(other),ConvZsq(other));
    }
    default double ConvXpt(GridBase3D other){
        return Xpt()*other.xDim/G().xDim;
    }
    default double ConvYpt(GridBase3D other){
        return Ypt()*other.yDim/G().yDim;
    }
    default double ConvZpt(GridBase3D other){
        return Ypt()*other.zDim/G().zDim;
    }
    default int MapHood(int[] hood){
        return G().MapHood(hood,Xsq(),Ysq(),Zsq());
    }
    default int MapEmptyHood(int[] hood){
        return G().MapEmptyHood(hood,Xsq(),Ysq(),Zsq());
    }
    default int MapOccupiedHood(int[] hood){
        return G().MapOccupiedHood(hood,Xsq(),Ysq(),Zsq());
    }
    default int HoodToAction(int[]neighborhood, Coords3DToAction Action){
        return G().HoodToAction(neighborhood,Xsq(),Ysq(),Zsq(),Action);
    }

    default int HoodToIs(int[]neighborhood,int[]retIs){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    default int HoodToEmptyIs(int[]neighborhood,int[]retIs){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    default int HoodToOccupiedIs(int[]neighborhood,int[]retIs){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq());
    }
    default int HoodToIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
    default int HoodToEmptyIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToEmptyIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
    default int HoodToOccupiedIs(int[]neighborhood,int[]retIs,boolean wrapX,boolean wrapY,boolean wrapZ){
        return G().HoodToOccupiedIs(neighborhood,retIs,this.Xsq(),this.Ysq(),this.Zsq(),wrapX,wrapY,wrapZ);
    }
}