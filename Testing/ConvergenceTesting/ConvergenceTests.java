package Testing.ConvergenceTesting;


import Framework.GridsAndAgents.PDEGrid1D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Gui.PlotLine;
import Framework.Gui.PlotWindow;
import Framework.Gui.UIGrid;
import Framework.Interfaces.DoubleArrayToDouble;
import Framework.Interfaces.DoubleToColor;
import Framework.Util;

import static Framework.Util.RED;


public class ConvergenceTests {
    public final static int SPACE_FACTOR=0,TIME_FACTOR=1,ERROR=2;


    public static double NoRxn1D(double x, int t, double v, double dx){
        return v;
    }
    public static void Diffusion1DBC(PDEGrid1D grid,double[]consts){
        grid.Diffusion(consts[0],(x)->{
            if(x==-1){
                return 1;
            }
            else{
                return 0;
            }
        });
    }
    public static double Linfinity(double[]errors){
        double max=0;
        for (int i = 0; i < errors.length; i++) {
            max=Math.max(max,errors[i]);
        }
        return max;
    }


    public static void main(String[] args) {
        double[][]errors=ConvergenceTest1D(ConvergenceTests::NoRxn1D,ConvergenceTests::Diffusion1DBC,ConvergenceTests::Linfinity,4,2000,new double[]{0.0005},1,0.5,new double[]{2},new int[]{0,1,2,3,4},new GridWindow(500,500,1),0, Util::HeatMapRGB);

        PlotWindow plot=new PlotWindow(500,500);
        PlotLine line= new PlotLine(plot,RED);
        for (int j = 0; j < errors[ERROR].length; j++) {
            line.AddSegment(errors[SPACE_FACTOR][0],errors[ERROR][j]);
        }
    }

    public static double[][] ConvergenceTest1D(ConvergenceReaction1D RxnFn, ConvergenceDiffusionAdvection1D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int numTimeSteps, double[]rateConstants,  double spaceScalingFactor,double timeScalingFactor, double[]rateConstantScalingFactors, int[]scalesToCompare){
        return ConvergenceTest1D(RxnFn,DifAdvFn,ErrorFn,xDim,numTimeSteps,rateConstants,timeScalingFactor,spaceScalingFactor,rateConstantScalingFactors,scalesToCompare,null,0,null);
    }
    public static double[][] ConvergenceTest1D(ConvergenceReaction1D RxnFn, ConvergenceDiffusionAdvection1D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int numTimeSteps, double[]rateConstants,  double spaceScalingFactor,double timeScalingFactor, double[]rateConstantScalingFactors, int[]scalesToCompare, UIGrid vis, int pauseMS, DoubleToColor ColorFn){
        int xDimOrig=xDim;
        int numStepsOrig=numTimeSteps;
        double[]constants=rateConstants.clone();
        PDEGrid1D prev=null;
        PDEGrid1D curr=null;
        double spaceScalar=1;
        double prevSpaceScalar;
        int currIteration=0;
        double[][]ret=new double[3][];
        ret[0]=new double[scalesToCompare.length];//time scales
        ret[1]=new double[scalesToCompare.length];//space scales
        ret[2]=new double[scalesToCompare.length-1];//errors

        for (int i = 0; i < scalesToCompare.length; i++) {
            //scale constants
            prevSpaceScalar=spaceScalar;
            spaceScalar=Math.pow(spaceScalingFactor,scalesToCompare[i]);
            double timeScalar=Math.pow(timeScalingFactor,scalesToCompare[i]);
            for (int j = 0; j < constants.length; j++) {
                constants[j] = rateConstants[j] * Math.pow(rateConstantScalingFactors[j],scalesToCompare[i]);
            }

            //check proper scaling
            if((xDimOrig*1.0*spaceScalar)%1!=0){
                throw new IllegalStateException("space dim "+xDimOrig+" does not evenly multiply by spaceScalar "+spaceScalar);
            }
            if((numStepsOrig*1.0*timeScalar)%1!=0){
                throw new IllegalStateException("time step "+numStepsOrig+" does not evenly multiply by timeScalar "+timeScalar);
            }
            xDim=(int)(xDimOrig*spaceScalar);
            numTimeSteps=(int)(numStepsOrig*timeScalar);

            ret[0][i]=spaceScalar;
            ret[1][i]=timeScalar;
            prev=curr;

            curr=new PDEGrid1D(xDim);
            //todo: confirm inital condition
            //set initial condition
            for (int x = 0; x < xDim; x++) {
                curr.Set(x,RxnFn.React((x+0.5)*(1.0/spaceScalar),-1,curr.Get(x),spaceScalar));
                curr.Update();
            }

            //run rxn diffusion
            for (int t = 0; t < numTimeSteps; t++) {
                if(vis!=null){
                    for (int x = 0; x < vis.xDim; x++) {
                        int drawColor= ColorFn.GenColor(curr.Get((int)((x+0.5)*(curr.xDim*1.0/vis.xDim))));
                        for (int y = 0; y < vis.yDim; y++) {
                            vis.SetPix(x,y,drawColor);
                        }
                    }
                    vis.TickPause(pauseMS);
                }
                for (int x = 0; x < xDim; x++) {
                    curr.Set(x,RxnFn.React((x+0.5)*(1.0/spaceScalar),t,curr.Get(x),spaceScalar));
                }
                DifAdvFn.DiffusionAdvection1D(curr,constants);
                curr.Update();
            }

//            //todo: confirm scaling issue
//            if(prev!=null){
//                PDEGrid1D smaller;
//                PDEGrid1D larger;
//                if(prevSpaceScalar>spaceScalar){
//                    larger=prev;
//                    smaller=curr;
//                }else{
//                    larger=curr;
//                    smaller=prev;
//                }
//                if((larger.xDim*1.0/(smaller.xDim*3.0))%1!=0){
//                    throw new IllegalStateException("subsequent grid dimensions not evenly divisible by a factor of 3! larger:"+larger.xDim+" smaller:"+smaller.xDim);
//                }
//                int posScalingFactor=larger.xDim/smaller.xDim;
//                double[] differences=new double[smaller.length];
//                for (int x = 0; x < smaller.xDim; x++) {
//                    differences[x]=Math.abs(smaller.Get((int)(x+0.5))-larger.Get((int)(x+0.5)*posScalingFactor));
//                }
//                ret[2][i-1]=ErrorFn.Eval(differences);
//            }
            if(prev!=null){
//                if((prev.xDim*1.0/(curr.xDim*3.0))%1!=0&&(curr.xDim*1.0/(prev.xDim*3.0))%1!=0){
//                    throw new IllegalStateException("subsequent grid dimensions not evenly divisible by a factor of 3! prev:"+prev.xDim+" curr:"+curr.xDim);
//                }
                double[] differences=new double[xDimOrig];
                for (int x = 0; x < xDimOrig; x++) {
                    differences[x]=Math.abs(prev.Get((int)((x+0.5)*prevSpaceScalar))-curr.Get((int)((x+0.5)*spaceScalar)));
                }
                ret[2][i-1]=ErrorFn.Eval(differences);
                System.out.println(Util.ArrToString(differences,","));
            }
        }
        return ret;
    }

    public static double[][] ConvergenceTest2D(ConvergenceReaction2D RxnFn, ConvergenceDiffusionAdvection2D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim,int yDim, int numTimeSteps, double[]rateConstants, double timeScalingFactor, double spaceScalingFactor, double[]rateConstantScalingFactors, int[]scalesToCompare){
        return ConvergenceTest2D(RxnFn,DifAdvFn,ErrorFn,xDim,yDim,numTimeSteps,rateConstants,timeScalingFactor,spaceScalingFactor,rateConstantScalingFactors,scalesToCompare,null,0,null);
    }
    public static double[][] ConvergenceTest2D(ConvergenceReaction2D RxnFn, ConvergenceDiffusionAdvection2D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim,int yDim, int numTimeSteps, double[]rateConstants, double timeScalingFactor, double spaceScalingFactor, double[]rateConstantScalingFactors, int[]scalesToCompare, UIGrid vis, int pauseMS, DoubleToColor ColorFn){
        int xDimOrig=xDim;
        int yDimOrig=yDim;
        int numStepsOrig=numTimeSteps;
        double[]constants=rateConstants.clone();
        PDEGrid2D prev=null;
        PDEGrid2D curr=null;
        double spaceScalar=1;
        double prevSpaceScalar;
        int currIteration=0;
        double[][]ret=new double[3][];
        ret[TIME_FACTOR]=new double[scalesToCompare.length];//time scales
        ret[SPACE_FACTOR]=new double[scalesToCompare.length];//space scales
        ret[ERROR]=new double[scalesToCompare.length-1];//errors

        for (int i = 0; i < scalesToCompare.length; i++) {
            //scale constants
            prevSpaceScalar=spaceScalar;
            spaceScalar=Math.pow(spaceScalingFactor,scalesToCompare[i]);
            double timeScalar=Math.pow(timeScalingFactor,scalesToCompare[i]);
            for (int j = 0; j < constants.length; j++) {
                constants[j] = rateConstants[j] * Math.pow(rateConstantScalingFactors[j],scalesToCompare[i]);
            }

            //check proper scaling
            if((xDimOrig*1.0*spaceScalar)%1!=0){
                throw new IllegalStateException("space dim "+xDimOrig+" does not evenly multiply by spaceScalar "+spaceScalar);
            }
            if((yDimOrig*1.0*spaceScalar)%1!=0){
                throw new IllegalStateException("space dim "+yDimOrig+" does not evenly multiply by spaceScalar "+spaceScalar);
            }
            if((numStepsOrig*1.0*timeScalar)%1!=0){
                throw new IllegalStateException("time step "+numStepsOrig+" does not evenly multiply by timeScalar "+timeScalar);
            }
            xDim=(int)(xDimOrig*spaceScalar);
            numTimeSteps=(int)(numStepsOrig*timeScalar);

            ret[TIME_FACTOR][i]=spaceScalar;
            ret[SPACE_FACTOR][i]=timeScalar;
            prev=curr;

            curr=new PDEGrid2D(xDim,yDim);
            //todo: confirm inital condition
            //set initial condition
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < xDim; y++) {
                    curr.Set(x, RxnFn.React((x + 0.5) * (1.0 / spaceScalar),(y + 0.5) * (1.0 / spaceScalar), -1, curr.Get(x), spaceScalar));
                    curr.Update();
                }
            }

            //run rxn diffusion
            for (int t = 0; t < numTimeSteps; t++) {
                if(vis!=null){
                    for (int x = 0; x < vis.xDim; x++) {
                        int drawColor= ColorFn.GenColor(curr.Get((int)((x+0.5)*(curr.xDim*1.0/vis.xDim))));
                        for (int y = 0; y < vis.yDim; y++) {
                            vis.SetPix(x,y,drawColor);
                        }
                    }
                    vis.TickPause(pauseMS);
                }
                for (int x = 0; x < xDim; x++) {
                  //  curr.Set(x,RxnFn.React((x+0.5)*(1.0/spaceScalar),t,curr.Get(x),spaceScalar));
                }
                //DifAdvFn.DiffusionAdvection1D(curr,constants);
                curr.Update();
            }

//            //todo: confirm scaling issue
//            if(prev!=null){
//                PDEGrid1D smaller;
//                PDEGrid1D larger;
//                if(prevSpaceScalar>spaceScalar){
//                    larger=prev;
//                    smaller=curr;
//                }else{
//                    larger=curr;
//                    smaller=prev;
//                }
//                if((larger.xDim*1.0/(smaller.xDim*3.0))%1!=0){
//                    throw new IllegalStateException("subsequent grid dimensions not evenly divisible by a factor of 3! larger:"+larger.xDim+" smaller:"+smaller.xDim);
//                }
//                int posScalingFactor=larger.xDim/smaller.xDim;
//                double[] differences=new double[smaller.length];
//                for (int x = 0; x < smaller.xDim; x++) {
//                    differences[x]=Math.abs(smaller.Get((int)(x+0.5))-larger.Get((int)(x+0.5)*posScalingFactor));
//                }
//                ret[2][i-1]=ErrorFn.Eval(differences);
//            }
            if(prev!=null){
                if((prev.xDim*1.0/(curr.xDim*3.0))%1!=0&&(curr.xDim*1.0/(prev.xDim*3.0))%1!=0){
                    throw new IllegalStateException("subsequent grid dimensions not evenly divisible by a factor of 3! prev:"+prev.xDim+" curr:"+curr.xDim);
                }
                double[] differences=new double[xDimOrig];
                for (int x = 0; x < xDimOrig; x++) {
                    differences[x]=Math.abs(prev.Get((int)((x+0.5)*prevSpaceScalar))-curr.Get((int)((x+0.5)*spaceScalar)));
                }
                ret[ERROR][i-1]=ErrorFn.Eval(differences);
            }
        }
        return ret;
    }
}
