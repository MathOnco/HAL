package HAL.Tools.Internal;

import HAL.GridsAndAgents.*;
import HAL.Interfaces.*;
import HAL.Interfaces.Coords3DInt;
import HAL.Tools.MultinomialCalc;
import HAL.Tools.MultinomialCalcLong;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

public class PopulationGridPDEequations {

    //quantifying analysis?
    public static void Diffusion1(int pop, int x, PopulationGrid1D grid, double prob, int xDim, boolean wrapX, Coords1DInt AgentBC, MultinomialCalc mn) {
        mn.Setup(pop);
        Diffusion1D(grid, x + 1,x, xDim, mn, AgentBC, (xBC) -> prob, wrapX, prob);
        Diffusion1D(grid, x - 1,x, xDim, mn, AgentBC, (xBC) -> prob, wrapX, prob);
    }
    public static void Diffusion2(int pop, int x, int y, int i, PopulationGrid2D grid, double prob, int xDim, int yDim, boolean wrapX, boolean wrapY, Coords2DInt AgentBC, MultinomialCalc mn){
                mn.Setup(pop);
                DiffusionX2D(grid, x + 1, y, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapX, prob);
                DiffusionX2D(grid, x - 1, y, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapX, prob);
                DiffusionY2D(grid, x, y + 1, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapY, prob);
                DiffusionY2D(grid, x, y - 1, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapY, prob);
    }
    public static void Diffusion3(int pop, int x, int y, int z, int i, PopulationGrid3D grid, double prob, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DInt AgentBC, MultinomialCalc mn){
        mn.Setup(pop);
        DiffusionX3D(grid, x + 1, y,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapX, prob);
        DiffusionX3D(grid, x - 1, y,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapX, prob);
        DiffusionY3D(grid, x, y + 1,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapY, prob);
        DiffusionY3D(grid, x, y - 1,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapY, prob);
        DiffusionZ3D(grid, x, y,z+1, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapZ, prob);
        DiffusionZ3D(grid, x, y,z-1, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapZ, prob);
    }
    public static void Diffusion1D(PopulationGrid1D grid, int x, int centerX, int xDim, MultinomialCalc mn, Coords1DInt AgentBC, Coords1DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerX,-ct);
            grid.Add(x,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            int ct=mn.Sample(prob);
            grid.Add(centerX,-ct);
            grid.Add(x,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x),ProbBC.GenDouble(x));
            grid.Add(centerX,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionX2D(PopulationGrid2D grid, int x, int y, int centerI, int xDim, int yDim, MultinomialCalc mn, Coords2DInt AgentBC, Coords2DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y),ProbBC.GenDouble(x,y));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionY2D(PopulationGrid2D grid, int x, int y, int centerI, int xDim, int yDim, MultinomialCalc mn, Coords2DInt AgentBC, Coords2DDouble ProbBC, boolean WrapY, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(y,yDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        else if(WrapY){
            y=Wrap(y,yDim);
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y),ProbBC.GenDouble(x,y));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionX3D(PopulationGrid3D grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalc mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionY3D(PopulationGrid3D grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalc mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapY, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(y,yDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapY){
            x=Wrap(y,yDim);
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionZ3D(PopulationGrid3D grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalc mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapZ, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(z,zDim)){
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapZ){
            x=Wrap(z,zDim);
            int ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            int ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }

    public static void Diffusion1L(long pop, int x, PopulationGrid1DLong grid, double prob, int xDim, boolean wrapX, Coords1DInt AgentBC, MultinomialCalcLong mn) {
        mn.Setup(pop);
        Diffusion1DL(grid, x + 1,x, xDim, mn, AgentBC, (xBC) -> prob, wrapX, prob);
        Diffusion1DL(grid, x - 1,x, xDim, mn, AgentBC, (xBC) -> prob, wrapX, prob);
    }
    public static void Diffusion2L(long pop, int x, int y, int i, PopulationGrid2DLong grid, double prob, int xDim, int yDim, boolean wrapX, boolean wrapY, Coords2DInt AgentBC, MultinomialCalcLong mn){
        mn.Setup(pop);
        DiffusionX2DL(grid, x + 1, y, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapX, prob);
        DiffusionX2DL(grid, x - 1, y, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapX, prob);
        DiffusionY2DL(grid, x, y + 1, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapY, prob);
        DiffusionY2DL(grid, x, y - 1, i, xDim, yDim, mn, AgentBC, (xBC, yBC) -> prob, wrapY, prob);
    }
    public static void Diffusion3L(long pop, int x, int y, int z, int i, PopulationGrid3DLong grid, double prob, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DInt AgentBC, MultinomialCalcLong mn){
        mn.Setup(pop);
        DiffusionX3DL(grid, x + 1, y,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapX, prob);
        DiffusionX3DL(grid, x - 1, y,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapX, prob);
        DiffusionY3DL(grid, x, y + 1,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapY, prob);
        DiffusionY3DL(grid, x, y - 1,z, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapY, prob);
        DiffusionZ3DL(grid, x, y,z+1, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapZ, prob);
        DiffusionZ3DL(grid, x, y,z-1, i, xDim, yDim,zDim, mn, AgentBC, (xBC, yBC, zBC) -> prob, wrapZ, prob);
    }
    public static void Diffusion1DL(PopulationGrid1DLong grid, int x, int centerX, int xDim, MultinomialCalcLong mn, Coords1DInt AgentBC, Coords1DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerX,-ct);
            grid.Add(x,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            long ct=mn.Sample(prob);
            grid.Add(centerX,-ct);
            grid.Add(x,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x),ProbBC.GenDouble(x));
            grid.Add(centerX,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionX2DL(PopulationGrid2DLong grid, int x, int y, int centerI, int xDim, int yDim, MultinomialCalcLong mn, Coords2DInt AgentBC, Coords2DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y),ProbBC.GenDouble(x,y));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionY2DL(PopulationGrid2DLong grid, int x, int y, int centerI, int xDim, int yDim, MultinomialCalcLong mn, Coords2DInt AgentBC, Coords2DDouble ProbBC, boolean WrapY, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(y,yDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        else if(WrapY){
            y=Wrap(y,yDim);
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim+y,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y),ProbBC.GenDouble(x,y));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionX3DL(PopulationGrid3DLong grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalcLong mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapX, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(x,xDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapX){
            x=Wrap(x,xDim);
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionY3DL(PopulationGrid3DLong grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalcLong mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapY, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(y,yDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapY){
            x=Wrap(y,yDim);
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }
    public static void DiffusionZ3DL(PopulationGrid3DLong grid, int x, int y, int z, int centerI, int xDim, int yDim, int zDim, MultinomialCalcLong mn, Coords3DInt AgentBC, Coords3DDouble ProbBC, boolean WrapZ, double prob){
        //3 possibilities:
        //location exists and should be moved onto (or wrap)
        if(InDim(z,zDim)){
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        else if(WrapZ){
            x=Wrap(z,zDim);
            long ct=mn.Sample(prob);
            grid.Add(centerI,-ct);
            grid.Add(x*yDim*zDim+y*zDim+z,ct);
        }
        //location is BC, and value should be subtracted and added using binomial
        else if(AgentBC!=null){
            long ct=mn.Sample(prob);
            ct-=mn.Binomial(AgentBC.GenInt(x,y,z),ProbBC.GenDouble(x,y,z));
            grid.Add(centerI,-ct);
        }
        //location is zero-flux boundary, and movement does not need to happen
    }













    public static void DiffusionOperator2D(int[]field,int[]deltas,int centerX,int centerY,double prob,int xDim,int yDim,MultinomialCalc MN,Coords2DInt AgentBC){
        int centerI=centerX*yDim+centerY;
        int centerPop=field[centerI];
        MN.Setup(centerPop);
        //right
        //left
        //up
        //down
    }
    public static boolean AddDisplacedY2D(int[] deltas,int x,int y,int xDim,int yDim,boolean wrapY,Coords1DInt BoundaryCond,int yFrom,int val){
        if(InDim(x,xDim)){
            deltas[x*yDim+y]+=val;
            return true;
        }
        else if (wrapY){
            deltas[x*yDim+Wrap(y,yDim)]+=val;
            return true;
        }
        return false;
    }

    public static int Displaced1D(int[] vals,int x, int xDim, boolean wrapX, Coords1DInt BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenInt(x);
        }
        return 0;//default
    }
    public static int DisplacedX2D(int[] vals,int x,int y, int xDim,int yDim, boolean wrapX, Coords2DInt BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim+y];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim+y];
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y));//Derichlet boundary conds.
        }
        return 0;
    }
    public static int DisplacedY2D(int[] vals,int x,int y, int xDim,int yDim, boolean wrapY, Coords2DInt BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim+y];
        }
        else if(wrapY){
            return vals[x*yDim+ Wrap(y,yDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenInt(x,y);
        }
        return 0;
    }
    public static int DisplacedX3D(int[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapX, Coords3DInt BoundaryCond) {
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim*zDim+y*zDim+z];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenInt(x,y,z);
        }
        return 0;
    }
    public static int DisplacedY3D(int[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapY, Coords3DInt BoundaryCond) {
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapY){
            return vals[x*yDim*zDim+Wrap(y,yDim)*zDim+z];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenInt(x,y,z);
        }
        return 0;//Zero flux
    }
    public static int DisplacedZ3D(int[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapZ, Coords3DInt BoundaryCond) {
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapZ){
            return vals[x*yDim*zDim+y*zDim+Wrap(z,zDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenInt(x,y,z);//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }

    public static int Delta1D(int[]vals,int centerVal,int x,int xDim,boolean wrapX,Coords1DInt BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static int DeltaX2D(int[]vals,int centerVal,int x,int y,int xDim,int yDim,boolean wrapX,Coords2DInt BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim+y]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static int DeltaY2D(int[]vals,int centerVal,int x,int y,int xDim,int yDim,boolean wrapY,Coords2DInt BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim+ Wrap(y,yDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static int DeltaX3D(int[]vals,int centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapX,Coords3DInt BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static int DeltaY3D(int[]vals,int centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapY,Coords3DInt BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim*zDim+Wrap(y,yDim)*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static int DeltaZ3D(int[]vals,int centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapZ,Coords3DInt BoundaryCond){
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapZ){
            return vals[x*yDim*zDim+y*zDim+Wrap(z,zDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenInt(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
}
