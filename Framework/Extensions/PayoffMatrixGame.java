package Framework.Extensions;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Rand;

import static Framework.Util.*;

/**
 * Created by rafael on 8/27/17.
 */
public abstract class PayoffMatrixGame extends SpatialGameCanonical {
    PDEGrid2D types;
    public double[]payoffs;
    public int nTypes;
    public final boolean singleUpdate;
    public PayoffMatrixGame(int x, int y, double[]payoffs, int maxHoodSize,boolean singleUpdate,boolean wrapX,boolean wrapY) {
        super(x, y,maxHoodSize,wrapX,wrapY);
        this.singleUpdate=singleUpdate;
        this.types=new PDEGrid2D(x,y,wrapX,wrapY);
        this.nTypes=(int)Math.sqrt(payoffs.length);
        this.payoffs=payoffs;
        if(nTypes*nTypes!=payoffs.length){
            throw new IllegalArgumentException("payoff matrix has incorrect dimensions");
        }
    }
    @Override
    public double GetFitness(int idTo, int idOther) {
        return payoffs[((int)types.Get((idTo))*nTypes+((int)types.Get(idOther)))];
    }
    @Override
    public void ChangeState(int idTo, int idFrom) {
        if(singleUpdate){ types.Set(idTo,types.Get(idFrom)); }
        else { types.SetSwap(idTo, types.Get(idFrom)); }
    }
    public void DrawTypes(GuiGrid vis, int[]colors){
        if(colors.length!=nTypes){
            throw new IllegalArgumentException("colors array has incorrect length");
        }
        vis.DrawGridDiff(types,(v)->{return colors[(int)v];});
    }

    public void SetupType(int id){
        for (int i = 0; i < length; i++) {
            types.Set(i,id);
        }
    }
    public int GetType(int i){
        return (int)types.Get(i);
    }
    public void SetType(int i,int type){
        types.Set(i,type);
    }
    public int GetType(int x,int y){
        return (int)types.Get(x,y);
    }
    public void SetType(int x,int y,int type){
        types.Set(x,y,type);
    }

    public void GetPopCounts(int[]ret){
        if(ret.length!=nTypes){
            throw new IllegalArgumentException("pop count array has incorrect length");
        }
        for (int i = 0; i < ret.length; i++) {
            ret[i]=0;
        }
        for (int i = 0; i < types.length; i++) {
            ret[(int)types.Get(i)]+=1;
        }
    }
    public String GetPayoffMat(String[]names){
        String out="X,"+ArrToString(names,",")+"\n";
        for (int i = 0; i < nTypes; i++) {
            out+=names[i]+",";
            for (int j = nTypes*i; j < nTypes*(i+1)-1; j++) {
                out+=payoffs[j]+",";
            }
            out+=payoffs[nTypes*(i+1)-1]+"\n";
        }
        return out;
    }

    public void SetupRandom(Rand rn, double[] probs){
        if(probs.length!=nTypes){
            throw new IllegalArgumentException("probs array has incorrect length");
        }
        SumTo1(probs);
        for (int i = 0; i < length; i++) {
            types.Set(i, rn.RandomVariable(probs));
        }
    }
    public void DefaultStep(Rand rn){
        if(singleUpdate){
            StepOne();
            IncTick();
        }
        else{
            StepAll();
            types.SwapInc();
        }
    }
}
