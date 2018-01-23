package Framework.Extensions;

import Framework.GridsAndAgents.PDEGrid2D;

import java.io.Serializable;

//TODO add GetInterp function
public class PDEGrid2DCoarse implements Serializable{
    final public int spaceFactor;
    public final PDEGrid2D grid;
    final private int spaceFactorSq;
    final private double denom;
    private double nSetsPrev;
    private double setPartialMulPrev;
//    private double mulPrevIn;
//    private double mulPrevOut;
    public int ItoX(int i){
        return (i/(grid.xDim*spaceFactor))/spaceFactor;
    }
    public int ItoY(int i){
        return (i%(grid.yDim*spaceFactor))/spaceFactor;
    }
    public PDEGrid2DCoarse(int xDim, int yDim,int spaceFactor,boolean wrapX,boolean wrapY) {
        grid=new PDEGrid2D(xDim, yDim,wrapX,wrapY);
        this.spaceFactor=spaceFactor;
        this.spaceFactorSq=spaceFactor*spaceFactor;
        this.denom=1.0/spaceFactorSq;
    }
    public PDEGrid2DCoarse(int xDim, int yDim,int spaceFactor) {
        this(xDim,yDim,spaceFactor,false,false);
    }
    public double Get(int x,int y){
        return grid.Get(x/spaceFactor,y/spaceFactor);
    }
    public void Set(int x,int y,double val){
        grid.Set(x/spaceFactor,y/spaceFactor,val);
    }
    public void SetPartial(int x,int y,double val,double nSets){
        int xc=x/spaceFactor;
        int yc=y/spaceFactor;
        double mul;
        if(nSets==nSetsPrev){
            mul=setPartialMulPrev;
        }else {
            mul = 1.0 - Math.pow(1.0 - 1.0 *denom, nSets);
            setPartialMulPrev=mul;
            nSetsPrev=nSets;
        }
        double oldVal=grid.Get(xc,yc);
        grid.Add(xc,yc,(val-oldVal)*mul);
    }
    public void SetPartialCurrToSwap(int x,int y,double val,double nSets){
        int xc=x/spaceFactor;
        int yc=y/spaceFactor;
        double mul;
        if(nSets==nSetsPrev){
            mul=setPartialMulPrev;
        }else {
            mul = 1.0 - Math.pow(1.0 - 1.0 *denom, nSets);
            setPartialMulPrev=mul;
            nSetsPrev=nSets;
        }
        double oldVal=grid.Get(xc,yc);
        grid.AddSwap(xc,yc,(val-oldVal)*mul);
    }
    public void SetPartialSwapToSwap(int x,int y,double val,double nSets){
        int xc=x/spaceFactor;
        int yc=y/spaceFactor;
        double mul;
        if(nSets==nSetsPrev){
            mul=setPartialMulPrev;
        }else {
            mul = 1.0 - Math.pow(1.0 - 1.0 *denom, nSets);
            setPartialMulPrev=mul;
            nSetsPrev=nSets;
        }
        double oldVal=grid.GetSwap(xc,yc);
        grid.AddSwap(xc,yc,(val-oldVal)*mul);
    }
    public void Mul(int x,int y,double val){
//        if(val!=mulPrevIn){
//            mulPrevOut=Math.pow(val,denom);
//            mulPrevIn=val;
//        }
        grid.Mul(x/spaceFactor,y/spaceFactor,Math.pow(val,denom));
    }
    public void Add(int x,int y,double val){
        grid.Add(x/spaceFactor,y/spaceFactor,val*denom);
    }

    public void DiffusionADIChangeOrder(double diffRate) {
        grid.DiffusionADIChangeOrder(diffRate*denom);
    }

    public void DiffusionADIChangeOrder(double diffRate, double boundaryValue) {
        grid.DiffusionADIChangeOrder(diffRate*denom,boundaryValue);
    }
    public void DiffusionADI(double diffRate) {
        grid.DiffusionADI(diffRate*denom);
    }

    public void DiffusionADI(double diffRate, double boundaryValue) {
        grid.DiffusionADI(diffRate*denom,boundaryValue);
    }

    public void DiffusionADIHalf(double diffRate) {
        grid.DiffusionADIHalf(diffRate*denom);
    }

    public void DiffusionADIHalfX(double diffRate, boolean boundaryCond, double boundaryValue) {
        grid.DiffusionADIHalfX(diffRate*denom, boundaryCond, boundaryValue);
    }

    public void DiffusionADIHalfY(double diffRate, boolean boundaryCond, double boundaryValue) {
        grid.DiffusionADIHalfY(diffRate*denom, boundaryCond, boundaryValue);
    }

    public void DiffusionADIHalf(double diffRate, double boundaryValue) {
        grid.DiffusionADIHalf(diffRate*denom, boundaryValue);
    }

    public double GetSwap(int x,int y){
        return grid.GetSwap(x/spaceFactor,y/spaceFactor);
    }
    public void SetSwap(int x,int y,double val){
        grid.SetSwap(x/spaceFactor,y/spaceFactor,val);
    }

    public void AddSwap(int x,int y,double val){
        grid.AddSwap(x/spaceFactor,y/spaceFactor,val*denom);
    }
    public void Diffusion(double diffRate){
        grid.Diffusion(diffRate*denom);
    }
    public void Diffusion(double diffRate,double boundaryValue){
        grid.Diffusion(diffRate*denom,boundaryValue);
    }
    public void Diffusion(double diffRate,boolean wrapX,boolean wrapY){
        grid.Diffusion(diffRate*denom,wrapX,wrapY);
    }
    public void Diffusion(double diffRate,double boundaryValue,boolean wrapX,boolean wrapY){
        grid.Diffusion(diffRate*denom,boundaryValue,wrapX,wrapY);
    }
    public double GradientX(int x,int y){
        return grid.GradientX(x/spaceFactor,y/spaceFactor);
    }
    public double GradientY(int x, int y){
        return grid.GradientY(x/spaceFactor,y/spaceFactor);
    }
}
