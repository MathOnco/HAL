package Framework.GridsAndAgents;

import Framework.Interfaces.*;
import Framework.Rand;

public interface Agent2DBase{
    int Xsq();
    int Ysq();
    double Xpt();
    double Ypt();
    int Isq();
    AgentGrid2D G();
    default int ConvXsq(GridBase2D other){
        return Xsq()*other.xDim/G().xDim;
    }
    default int ConvYsq(GridBase2D other){
        return Ysq()*other.yDim/G().yDim;
    }
    default int ConvI(GridBase2D other){
        return other.I(ConvXsq(other),ConvYsq(other));
    }
    default double ConvXpt(GridBase2D other){
        return Xpt()*other.xDim/G().xDim;
    }
    default double ConvYpt(GridBase2D other){
        return Ypt()*other.yDim/G().yDim;
    }
    default int MapHood(int[] hood){
        return G().MapHood(hood,Xsq(),Ysq());
    }
    default int MapEmptyHood(int[] hood){
        return G().MapEmptyHood(hood,Xsq(),Ysq());
    }
    default int MapOccupiedHood(int[] hood){
        return G().MapOccupiedHood(hood,Xsq(),Ysq());
    }
}
