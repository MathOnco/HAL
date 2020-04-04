package Testing.ConvergenceTesting;

// txt reading/writing
import java.io.PrintWriter;
import java.io.File;

import HAL.GridsAndAgents.PDEGrid1D;
import HAL.GridsAndAgents.PDEGrid2D;
import HAL.GridsAndAgents.PDEGrid3D;
import HAL.Gui.GridWindow;
import HAL.Gui.PlotLine;
import HAL.Gui.PlotWindow;
import HAL.Gui.UIGrid;
import HAL.Interfaces.DoubleArrayToDouble;
import HAL.Interfaces.DoubleToColor;
import HAL.Util;

import static HAL.Util.RED;


public class ConvergenceTests {
    public final static int SPACE_FACTOR = 0, TIME_FACTOR = 1, ERROR = 2;


    public static double NoRxn1D(double x, int t, double v, double dx) {
        return v;
    }

    public static void writeResults(String path_to_file,double[] Results){
        try
        {
            File file = new File(path_to_file);
            PrintWriter printWriter = new PrintWriter(file);
            String line = "";
            printWriter.print(line);
            int cnt = 0;

            int type = 10;
            line=Util.ArrToString(Results, ",");

            printWriter.print(line);
            printWriter.close();
        } // end try block
        catch (Exception e) {
            System.out.println(e.getClass());
        }
    }

    public static void Diffusion1DBC(PDEGrid1D grid, double[] consts) {
        //Diffusion with 1 and 0 Dirichlet Boundary conditions
        grid.DiffusionCrank(consts[0], (x) -> {
            if (x == -1) {
                return 0;
            } else {
                return 0;
            }
        });

        //grid.DiffusionCrank(consts[0]);
    }


    public static void Advection1DBC(PDEGrid1D grid, double[] consts) {

        //Finite volumes advection with inhomogeneous veocity field
//        double [] v= VelocityField(grid);
//        grid.Advection(consts, (x) -> {
//            if (x == -1) {
//                return 0;
//            } else {
//                return 0;
//            }
//        },(x) -> {
//            if (x == -1) {
//                return v[0];
//            } else {
//                return v[grid.length];
//            }
//        });

        //Finite differences advection with homogeneous velocity field
        grid.Advection(consts[0], (x) -> {
            if (x == -1) {
                return 1;
            } else {
                return 0;
            }
        });
    }


    public static void Advection2DBC(PDEGrid2D grid, double[] consts) {

        //Finite volumes advection with inhomogeneous veocity field
//        double [] v= VelocityField2D(grid);
//        grid.Advection(consts, (x) -> {
//            if (x == -1) {
//                return 0;
//            } else {
//                return 0;
//            }
//        },(x) -> {
//            if (x == -1) {
//                return v[0];
//            } else {
//                return v[grid.length];
//            }
//        });

        //Finite differences advection with homogeneous velocity field
        grid.Advection(consts[0], 0, 1);
    }


//    public static void Advection1DPeriodic(PDEGrid1D grid, double[] consts) {
//
//        //Advection with periodic boundary conditions
//        grid.Advection(consts[0],);
//    }


    public static double Linfinity(double[] errors) {
        double max = 0;
        for (int i = 0; i < errors.length; i++) {
            max = Math.max(max, errors[i]);
        }
        return max;
    }

    public static double L2(double[] errors) {
        double errorL2 = 0;
        for (int i = 0; i < errors.length; i++) {
            errorL2 = errorL2+errors[i]*errors[i];
        }
        errorL2=Math.sqrt(errorL2);
        return errorL2;
    }

    public static double L1(double[] errors) {
        double errorL1 = 0;
        for (int i = 0; i < errors.length; i++) {
            errorL1 = errorL1+Math.abs(errors[i]);
        }
        return errorL1;
    }


    public static double[] VelocityField(PDEGrid1D grid) {
        double[] v=new double[1+grid.length];
        for (int i = 0; i < 1+grid.length; i++) {
            //v[i]=0.1; //Constant velocity field
            v[i]=0.4*Math.sin(Math.PI*(grid.xDim-i)/(grid.xDim)/2); //continuous velocity tending to zero
        }
        return v;
    }

//
//    public static double[] VelocityField2D(PDEGrid2D grid) {
//         vx=new double[1+grid.length];
//        double[] vy=new double[1+grid.length];
//        for (int i = 0; i < 1+grid.length; i++) {
//            v[i]=0.1; //Constant velocity field
//            v[i]=0.1; //Constant velocity field
//            //v[i]=0.4*Math.sin(Math.PI*(grid.xDim-i)/(grid.xDim)/2); //continuous velocity tending to zero
//        }
//        return v;
//    }

    public static double NoRxn2D(double x,double y, int t, double v, double dx) {
        return v;
    }

    public static void Diffusion2DBC(PDEGrid2D grid, double[] consts) {
        grid.Diffusion(consts[0],0);
    }
    static double[]CONSTS=new double[]{0};
    static double[]CONSTS2=new double[]{0};
    static double[] CONSTS3=new double[]{0};

    public static void Diffusion2DInh(PDEGrid2D grid, double[] consts) {

        if(CONSTS.length!=grid.length){
            CONSTS=new double[grid.length];
            CONSTS2=new double[grid.length];
        }
//        if(CONSTS[0]!=consts[0]){
            for (int i = 0; i < CONSTS.length; i++) {
//
                    CONSTS[i]=consts[0]+0.1*consts[0]*grid.ItoX(i)*1.0/grid.xDim;
                    CONSTS2[i]=consts[0];
//



//                CONSTS[i]=consts[0]+consts[0]*Math.sin(2*Math.PI*(grid.ItoX(i)+0.5)/(grid.xDim)); // Centered sinus function as initial condition
//                CONSTS2[i]=consts[0];

//                if(i>grid.xDim/2.0) {
//                    CONSTS[i] = consts[0] + consts[0] * ((grid.xDim-1)-grid.ItoX(i)) * 1.0 / grid.xDim;
//                }
//                else{
//                    CONSTS[i]=consts[0]+consts[0]*grid.ItoX(i)*1.0/grid.xDim;
//                }


            }

        grid.Diffusion(CONSTS,CONSTS2);
    }

    public static double NoRxn3D(double x,double y,double z, int t, double v, double dx) {
        return v;
    }

    public static void Diffusion3DBC(PDEGrid3D grid, double[] consts) {
        grid.DiffusionADI(consts[0],0);
    }


    public static void Diffusion3DInh(PDEGrid3D grid, double[] consts) {

        if(CONSTS.length!=grid.length){
            CONSTS=new double[grid.length];
            CONSTS2=new double[grid.length];
            CONSTS3=new double[grid.length];
        }
//        if(CONSTS[0]!=consts[0]){
        for (int i = 0; i < CONSTS.length; i++) {
//
            CONSTS[i]=consts[0]+0.1*consts[0]*grid.ItoX(i)*1.0/grid.xDim;
            CONSTS2[i]=consts[0];
            CONSTS3[i]=consts[0];
//



//                CONSTS[i]=consts[0]+consts[0]*Math.sin(2*Math.PI*(grid.ItoX(i)+0.5)/(grid.xDim)); // Centered sinus function as initial condition
//                CONSTS2[i]=consts[0];

//                if(i>grid.xDim/2.0) {
//                    CONSTS[i] = consts[0] + consts[0] * ((grid.xDim-1)-grid.ItoX(i)) * 1.0 / grid.xDim;
//                }
//                else{
//                    CONSTS[i]=consts[0]+consts[0]*grid.ItoX(i)*1.0/grid.xDim;
//                }


        }

        grid.Diffusion(CONSTS,CONSTS2,CONSTS3);
    }



    public static void Advection3DBC(PDEGrid3D grid, double[] consts) {
        grid.Advection(consts[0],0,0,1);
    }


    public static void ConvergenceEx1D(){
        double[][] errors = ConvergenceTest1D(ConvergenceTests::NoRxn1D, ConvergenceTests::Diffusion1DBC, ConvergenceTests::Linfinity, 100,true, 50, new double[]{0.1}, 3, 1, new double[]{9}, new int[]{0,1,2, 3,4}, new GridWindow(500, 500, 1), 0, Util::HeatMapRGB);

        PlotWindow plot = new PlotWindow(500, 500);
        PlotLine line = new PlotLine(plot, RED);
        for (int j = 0; j < errors[ERROR].length; j++) {
            line.AddSegment(Math.log(errors[SPACE_FACTOR][j]), Math.log(errors[ERROR][j]));
        }

        writeResults("./Testing/ConvergenceTesting/Linf.txt",errors[ERROR]);
        writeResults("./Testing/ConvergenceTesting/Dx.txt",errors[SPACE_FACTOR]);
    }

    public static  void ConvergenceEx2D(){

        double[][] errors = ConvergenceTest2D(ConvergenceTests::NoRxn2D, ConvergenceTests::Diffusion2DInh, ConvergenceTests::L2, 20,20,false,false, 500, new double[]{0.0001}, 3, 1, new double[]{9}, new int[]{0,1,2,3}, new GridWindow(500, 500, 1), 0, Util::HeatMapRGB);

        PlotWindow plot = new PlotWindow(500, 500);
        PlotLine line = new PlotLine(plot, RED);
        for (int j = 0; j < errors[ERROR].length; j++) {
            line.AddSegment(Math.log(errors[SPACE_FACTOR][j]), Math.log(errors[ERROR][j]));
        }
        writeResults("./Testing/ConvergenceTesting/L2.txt",errors[ERROR]);
        writeResults("./Testing/ConvergenceTesting/Dx.txt",errors[SPACE_FACTOR]);
    }
    public static  void ConvergenceEx3D(){
        double[][] errors = ConvergenceTest3D(ConvergenceTests::NoRxn3D, ConvergenceTests::Diffusion3DInh, ConvergenceTests::Linfinity, 10,10,10,false,false,false, 10, new double[]{0.001}, 3, 1, new double[]{9}, new int[]{0,1,2,3}, new GridWindow(500, 500, 1), 0, Util::HeatMapRGB);

        PlotWindow plot = new PlotWindow(500, 500);
        PlotLine line = new PlotLine(plot, RED);
        for (int j = 0; j < errors[ERROR].length; j++) {
            line.AddSegment(errors[SPACE_FACTOR][j], errors[ERROR][j]);
        }
        writeResults("./Testing/ConvergenceTesting/Linf.txt",errors[ERROR]);
        writeResults("./Testing/ConvergenceTesting/Dx.txt",errors[SPACE_FACTOR]);
    }
    public static double[][] ConvergenceTest1D(ConvergenceReaction1D RxnFn, ConvergenceDiffusionAdvection1D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim,boolean wrapX, int numTimeSteps, double[] rateConstants, double spaceScalingFactor, double timeScalingFactor, double[] rateConstantScalingFactors, int[] scalesToCompare) {
        return ConvergenceTest1D(RxnFn, DifAdvFn, ErrorFn, xDim,wrapX, numTimeSteps, rateConstants, timeScalingFactor, spaceScalingFactor, rateConstantScalingFactors, scalesToCompare, null, 0, null);
    }

    public static double[][] ConvergenceTest1D(ConvergenceReaction1D RxnFn, ConvergenceDiffusionAdvection1D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim,boolean wrapX, int numTimeSteps, double[] rateConstants, double spaceScalingFactor, double timeScalingFactor, double[] rateConstantScalingFactors, int[] scalesToCompare, UIGrid vis, int pauseMS, DoubleToColor ColorFn) {
        int xDimOrig = xDim;
        int numStepsOrig = numTimeSteps;
        double[] constants = rateConstants.clone();
        PDEGrid1D prev = null;
        PDEGrid1D curr = null;
        double spaceScalar = 1;
        double prevSpaceScalar;
        double[][] ret = new double[5][];
        ret[SPACE_FACTOR] = new double[scalesToCompare.length];//time scales
        ret[TIME_FACTOR] = new double[scalesToCompare.length];//space scales
        ret[ERROR] = new double[scalesToCompare.length - 1];//errors
        double Mass=0;
        double[] MassIni=new double[scalesToCompare.length];
        double[] MassFinal=new double[scalesToCompare.length];


        for (int i = 0; i < scalesToCompare.length; i++) {
            //scale constants
            prevSpaceScalar = spaceScalar;
            spaceScalar = Math.pow(spaceScalingFactor, scalesToCompare[i]);
            double timeScalar = Math.pow(timeScalingFactor, scalesToCompare[i]);
            for (int j = 0; j < constants.length; j++) {
                constants[j] = rateConstants[j] * Math.pow(rateConstantScalingFactors[j], scalesToCompare[i]);
            }

            //check proper scaling
            if ((xDimOrig * 1.0 * spaceScalar) % 1 != 0) {
                throw new IllegalStateException("space dim " + xDimOrig + " does not evenly multiply by spaceScalar " + spaceScalar);
            }
            if ((numStepsOrig * 1.0 * timeScalar) % 1 != 0) {
                throw new IllegalStateException("time step " + numStepsOrig + " does not evenly multiply by timeScalar " + timeScalar);
            }
            xDim = (int) (xDimOrig * spaceScalar);
            numTimeSteps = (int) (numStepsOrig * timeScalar);

            ret[SPACE_FACTOR][i] = spaceScalar;
            ret[TIME_FACTOR][i] = timeScalar;
            prev = curr;

            curr = new PDEGrid1D(xDim,wrapX);

            //set initial condition
            Mass=0;
            for (int x = 0; x < xDim; x++) {
                //curr.Set(x, Math.sin(Math.PI*(xDim-x-0.5)/(xDim)/2)); //Decreasing sinus function as initial condition
                //curr.Set(x, Math.sin(Math.PI*(x+0.5)/(xDim))); // Centered sinus function as initial condition
                //curr.Set(x, 1+Math.sin(2*Math.PI*(x+0.5)/(xDim))); // Centered sinus function as initial condition
                //Step function as initial condition
//                if (x > xDim/4) {
//                    curr.Set(x,1);
//                }
//                if (x>3*xDim/4) {
//                    curr.Set(x,0);
//                }
                //curr.Set(x, (xDim-x-0.5)/xDim); //Linear function as initial condition
                //curr.Set(x, Math.exp(-(x + 0.5) * (1.0 / spaceScalar)*(x + 0.5) * (1.0 / spaceScalar)/(2))); //Normal distribution as initial condition
                curr.Set(x, Math.exp(-(x-(double)(xDim)/2 + 0.5) * (1.0 / spaceScalar)*(x-(double)(xDim)/2 + 0.5) * (1.0 / spaceScalar)/(1000)));
                //curr.Set(x, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), -1, curr.Get(x), spaceScalar));
            }
            curr.Update();

            //Calculation of the initial total mass
            for (int x = 0; x < xDim; x++) {
                Mass=Mass+curr.Get(x);
            }
            MassIni[i]=Mass;
            Mass=0;

            //run rxn diffusion
            for (int t = 0; t < numTimeSteps; t++) {
                if (vis != null) {
                    for (int x = 0; x < vis.xDim; x++) {
                        int drawColor = ColorFn.GenColor(curr.Get((int) ((x + 0.5) * (curr.xDim * 1.0 / vis.xDim))));
                        for (int y = 0; y < vis.yDim; y++) {
                            vis.SetPix(x, y, drawColor);
                        }
                    }

                    vis.TickPause(0);
                }
                for (int x = 0; x < xDim; x++) {
                    curr.Set(x, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), t, curr.Get(x), spaceScalar));
                }
                DifAdvFn.DiffusionAdvection1D(curr, constants);
                curr.Update();
            }

            //Calculation of the final total mass
            Mass=0;
            for (int x = 0; x < xDim; x++) {
                Mass=Mass+curr.Get(x);
            }
            MassFinal[i]=Mass;


            if (prev != null) {
                double[] differences = new double[xDimOrig];
                for (int x = 0; x < xDimOrig; x++) {
                    differences[x] = Math.abs(prev.Get((int) ((x + 0.5) * prevSpaceScalar)) - curr.Get((int) ((x + 0.5) * spaceScalar)));
                }
                ret[2][i - 1] = ErrorFn.Eval(differences);
                System.out.println(Util.ArrToString(differences, ","));
            }
        }
        return ret;
    }

    public static double[][] ConvergenceTest2D(ConvergenceReaction2D RxnFn, ConvergenceDiffusionAdvection2D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int yDim,boolean wrapX,boolean wrapY, int numTimeSteps, double[] rateConstants, double spaceScalingFactor,double timeScalingFactor,  double[] rateConstantScalingFactors, int[] scalesToCompare) {
        return ConvergenceTest2D(RxnFn, DifAdvFn, ErrorFn, xDim, yDim,wrapX,wrapY, numTimeSteps, rateConstants, timeScalingFactor, spaceScalingFactor, rateConstantScalingFactors, scalesToCompare, null, 0, null);
    }

    public static double[][] ConvergenceTest2D(ConvergenceReaction2D RxnFn, ConvergenceDiffusionAdvection2D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int yDim,boolean wrapX,boolean wrapY, int numTimeSteps, double[] rateConstants, double spaceScalingFactor,double timeScalingFactor,  double[] rateConstantScalingFactors, int[] scalesToCompare, UIGrid vis, int pauseMS, DoubleToColor ColorFn) {
        int xDimOrig = xDim;
        int yDimOrig = yDim;
        int numStepsOrig = numTimeSteps;
        double[] constants = rateConstants.clone();
        PDEGrid2D prev = null;
        PDEGrid2D curr = null;
        double spaceScalar = 1;
        double prevSpaceScalar;
        double[][] ret = new double[3][];
        ret[SPACE_FACTOR] = new double[scalesToCompare.length];//time scales
        ret[TIME_FACTOR] = new double[scalesToCompare.length];//space scales
        ret[ERROR] = new double[scalesToCompare.length - 1];//errors

        for (int i = 0; i < scalesToCompare.length; i++) {
            //scale constants
            prevSpaceScalar = spaceScalar;
            spaceScalar = Math.pow(spaceScalingFactor, scalesToCompare[i]);
            double timeScalar = Math.pow(timeScalingFactor, scalesToCompare[i]);
            for (int j = 0; j < constants.length; j++) {
                constants[j] = rateConstants[j] * Math.pow(rateConstantScalingFactors[j], scalesToCompare[i]);
            }

            //check proper scaling
            if ((xDimOrig * 1.0 * spaceScalar) % 1 != 0 || (yDimOrig * 1.0 * spaceScalar) % 1 != 0) {
                throw new IllegalStateException("space dims " + xDimOrig + ", " + yDimOrig + " do not evenly multiply by spaceScalar " + spaceScalar);
            }
            if ((numStepsOrig * 1.0 * timeScalar) % 1 != 0) {
                throw new IllegalStateException("time step " + numStepsOrig + " does not evenly multiply by timeScalar " + timeScalar);
            }
            xDim = (int) (xDimOrig * spaceScalar);
            yDim = (int) (yDimOrig * spaceScalar);
            numTimeSteps = (int) (numStepsOrig * timeScalar);

            ret[SPACE_FACTOR][i] = spaceScalar;
            ret[TIME_FACTOR][i] = timeScalar;
            prev = curr;

            curr = new PDEGrid2D(xDim, yDim,wrapX,wrapY);
            //set initial condition
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
//                    if (Math.sqrt((x+0.5-xDim/2)*(x+0.5-xDim/2)+(y+0.5-yDim/2)*(y+0.5-yDim/2)) < xDim/4) {
//                        curr.Set(x,y,1);
//                    }
                    //curr.Set(x,y,Math.exp(-((x + 0.5) * (1.0 / spaceScalar)*(x + 0.5) * (1.0 / spaceScalar)/2000))); //Gaussian from left
                    curr.Set(x,y,Math.exp(-((x + 0.5-(double)(xDim)/2) * (1.0 / spaceScalar)*(x + 0.5-(double)(xDim)/2) * (1.0 / spaceScalar)+(y + 0.5-(double)(yDim)/2) * (1.0 / spaceScalar)*(y + 0.5-(double)(yDim)/2) * (1.0 / spaceScalar))/(10))); //Centered Gaussian
//                    if (Math.sqrt((x+0.5)*(x+0.5)+(y+0.5)*(y+0.5))>3*xDim/4) {
//                        curr.Set(x,y,0);
//                    }
                    //curr.Set(x, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), (y + 0.5) * (1.0 / spaceScalar), -1, curr.Get(x), spaceScalar));
                }
            }
            curr.Update();

            //run rxn diffusion
            for (int t = 0; t < numTimeSteps; t++) {
                if (vis != null) {
                    for (int x = 0; x < vis.xDim; x++) {
                        for (int y = 0; y < vis.yDim; y++) {
                            int drawColor = ColorFn.GenColor(curr.Get((int) ((x + 0.5) * (curr.xDim * 1.0 / vis.xDim)), (int) ((y + 0.5)* (curr.yDim * 1.0 / vis.yDim))));
                            vis.SetPix(x, y, drawColor);
                        }
                    }
                    if(t==0||t==numTimeSteps-1){
                        vis.ToPNG("FunFigure"+t+".png");
                    }
                    for (int x = 0; x < xDim; x++) {
                        for (int y = 0; y < yDim; y++) {
                            curr.Set(x, y, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), (y + 0.5) * (1.0 / spaceScalar), t, curr.Get(x,y), spaceScalar));
                        }
                    }
                    DifAdvFn.DiffusionAdvection2D(curr, constants);
                    curr.Update();
                }

                if (prev != null) {
                    double[] differences = new double[xDimOrig * yDimOrig];
                    for (int x = 0; x < xDimOrig; x++) {
                        for (int y = 0; y < yDimOrig; y++) {
                            differences[x * yDimOrig + y] = Math.abs(prev.Get((int) ((x + 0.5) * prevSpaceScalar), (int) ((y + 0.5) * prevSpaceScalar)) - curr.Get((int) ((x + 0.5) * spaceScalar), (int) ((y + 0.5) * spaceScalar)));
                        }
                        ret[ERROR][i - 1] = ErrorFn.Eval(differences);
                    }
                }
            }
        }
        return ret;
    }


    public static double[][] ConvergenceTest3D(ConvergenceReaction2D RxnFn, ConvergenceDiffusionAdvection2D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ, int numTimeSteps, double[] rateConstants, double spaceScalingFactor,double timeScalingFactor,  double[] rateConstantScalingFactors, int[] scalesToCompare) {
        return ConvergenceTest2D(RxnFn, DifAdvFn, ErrorFn, xDim, yDim,wrapX,wrapY, numTimeSteps, rateConstants, timeScalingFactor, spaceScalingFactor, rateConstantScalingFactors, scalesToCompare, null, 0, null);
    }

    public static double[][] ConvergenceTest3D(ConvergenceReaction3D RxnFn, ConvergenceDiffusionAdvection3D DifAdvFn, DoubleArrayToDouble ErrorFn, int xDim, int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ, int numTimeSteps, double[] rateConstants, double spaceScalingFactor,double timeScalingFactor,  double[] rateConstantScalingFactors, int[] scalesToCompare, UIGrid vis, int pauseMS, DoubleToColor ColorFn) {
        int xDimOrig = xDim;
        int yDimOrig = yDim;
        int zDimOrig = zDim;
        int numStepsOrig = numTimeSteps;
        double[] constants = rateConstants.clone();
        PDEGrid3D prev = null;
        PDEGrid3D curr = null;
        double spaceScalar = 1;
        double prevSpaceScalar;
        double[][] ret = new double[3][];
        ret[SPACE_FACTOR] = new double[scalesToCompare.length];//time scales
        ret[TIME_FACTOR] = new double[scalesToCompare.length];//space scales
        ret[ERROR] = new double[scalesToCompare.length - 1];//errors

        for (int i = 0; i < scalesToCompare.length; i++) {
            //scale constants
            prevSpaceScalar = spaceScalar;
            spaceScalar = Math.pow(spaceScalingFactor, scalesToCompare[i]);
            double timeScalar = Math.pow(timeScalingFactor, scalesToCompare[i]);
            for (int j = 0; j < constants.length; j++) {
                constants[j] = rateConstants[j] * Math.pow(rateConstantScalingFactors[j], scalesToCompare[i]);
            }

            //check proper scaling
            if ((xDimOrig * 1.0 * spaceScalar) % 1 != 0 || (yDimOrig * 1.0 * spaceScalar) % 1 != 0||(zDimOrig * 1.0 * spaceScalar) % 1 != 0) {
                throw new IllegalStateException("space dims " + xDimOrig + ", " + yDimOrig + ","+zDimOrig+" do not evenly multiply by spaceScalar " + spaceScalar);
            }
            if ((numStepsOrig * 1.0 * timeScalar) % 1 != 0) {
                throw new IllegalStateException("time step " + numStepsOrig + " does not evenly multiply by timeScalar " + timeScalar);
            }
            xDim = (int) (xDimOrig * spaceScalar);
            yDim = (int) (yDimOrig * spaceScalar);
            zDim = (int) (zDimOrig * spaceScalar);
            numTimeSteps = (int) (numStepsOrig * timeScalar);

            ret[SPACE_FACTOR][i] = spaceScalar;
            ret[TIME_FACTOR][i] = timeScalar;
            prev = curr;

            curr = new PDEGrid3D(xDim, yDim,zDim,wrapX,wrapY,wrapZ);
            //set initial condition
            for (int x = 0; x < xDim; x++) {
                for (int y = 0; y < yDim; y++) {
                    for (int z = 0; z < zDim; z++) {
                        //curr.Set(x,y,z,1);
                        double dist=(x + 0.5-xDim/2) * (1.0 / spaceScalar)*(x + 0.5-xDim/2) * (1.0 / spaceScalar)+(y + 0.5-yDim/2) * (1.0 / spaceScalar)*(y + 0.5-yDim/2) * (1.0 / spaceScalar)+(z + 0.5-zDim/2) * (1.0 / spaceScalar)*(z + 0.5-zDim/2) * (1.0 / spaceScalar);
                        double Radius=10;
                        curr.Set(x,y,z,Math.exp(-(dist)/Radius));
                        //curr.Set(x, y, z, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), (y + 0.5) * (1.0 / spaceScalar), (z + 0.5) * (1.0 / spaceScalar), -1, curr.Get(x, y, z), spaceScalar));
                    }
                }
            }
            curr.Update();


            //run rxn diffusion
            for (int t = 0; t < numTimeSteps; t++) {
                if (vis != null) {
                    for (int x  = 0; x < vis.xDim; x++) {
                        for (int y = 0; y < vis.yDim; y++) {
                            vis.SetPix(x,y,Util.HeatMapRGB(curr.Get((int)((x+0.5)* (curr.xDim*1.0 / vis.xDim)),(int)((y+0.5)* (curr.yDim*1.0 / vis.yDim)),curr.zDim/2)));
                        }

                    }
//                    for (int x = 0; x < vis.xDim; x++) {
//                        for (int y = 0; y < vis.yDim; y++) {
//                            double stackSum = 0;
//                            for (int z = 0; z < curr.zDim; z++) {
//                                stackSum += curr.Get((int) ((x + 0.5) * (curr.xDim * 1.0 / vis.xDim)), (int) ((y + 0.5) * (curr.yDim * 1.0 / vis.yDim)), z);
//                            }
//                            stackSum /= curr.zDim;
//                            int drawColor = ColorFn.GenColor(stackSum);
//                            vis.SetPix(x, y, drawColor);
//                        }
//                    }
                }
                    for (int x = 0; x < xDim; x++) {
                        for (int y = 0; y < yDim; y++) {
                            for (int z = 0; z < zDim; z++) {
                                curr.Set(x, y,z, RxnFn.React((x + 0.5) * (1.0 / spaceScalar), (y + 0.5) * (1.0 / spaceScalar),(z + 0.5) * (1.0 / spaceScalar), t, curr.Get(x, y,z), spaceScalar));
                            }
                        }
                    }
                    DifAdvFn.DiffusionAdvection3D(curr, constants);
                    curr.Update();
                    if (vis!=null){
                        vis.TickPause(1000);
                    }
                }

                if (prev != null) {
                    double[] differences = new double[xDimOrig * yDimOrig*zDimOrig];
                    for (int x = 0; x < xDimOrig; x++) {
                        for (int y = 0; y < yDimOrig; y++) {
                            for (int z = 0; z < zDimOrig; z++) {
                                differences[x * yDimOrig *zDimOrig + y*zDimOrig+z] = Math.abs(prev.Get((int) ((x + 0.5) * prevSpaceScalar), (int) ((y + 0.5) * prevSpaceScalar),(int)((z+0.5)*prevSpaceScalar)) - curr.Get((int) ((x + 0.5) * spaceScalar), (int) ((y + 0.5) * spaceScalar),(int)((z+0.5)*spaceScalar)));
                            }
                        }
                        ret[ERROR][i - 1] = ErrorFn.Eval(differences);
                    }
                }
            }
        return ret;
    }

    public static void main(String[] args) {
        ConvergenceEx3D();
    }
}
