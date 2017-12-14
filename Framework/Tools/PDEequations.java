package Framework.Tools;
import static Framework.Util.*;

/**
 * Created by Rafael on 10/14/2017.
 */
public class PDEequations {

    /**
     * runs the finite differences equation in 2 dimensions explicitly
     *
     * @param inGrid        an array of values holding the starting state of the diffusible
     * @param outGrid       an array into which the result of diffusion will be written
     * @param xDim          xDim dimenison of the inGrid and outGrid
     * @param yDim          yDim dimension of the inGrid and outGrid
     * @param diffRate      diffusion rate for the diffusion equaition
     * @param boundaryCond  defines whether a constant boundary condition value should diffuse in from the boundaries
     * @param boundaryValue only impacts diffusion if boundaryCond is true, sets the boundary condition value
     * @param wrapX         whether to wrap around diffusion over the left and right boundaries
     */
    public static void Diffusion(double[] inGrid, double[] outGrid, int xDim, int yDim, double diffRate, boolean boundaryCond, double boundaryValue, final boolean wrapX, final boolean wrapY) {
        //This code is ugly and repetitive to improve performance by getting around bounds checking
        int x, y;
        //first we do the corners
        if (boundaryCond) {
            outGrid[0] = inGrid[0] + diffRate * (-inGrid[0] * 4 + inGrid[1] + inGrid[yDim] + 2 * boundaryValue);
            outGrid[(xDim - 1) * yDim] = inGrid[(xDim - 1) * yDim] + diffRate * (-inGrid[(xDim - 1) * yDim] * 4 + inGrid[(xDim - 2) * yDim] + inGrid[(xDim - 1) * yDim + 1] + 2 * boundaryValue);
            outGrid[(xDim - 1) * yDim + yDim - 1] = inGrid[(xDim - 1) * yDim + yDim - 1] + diffRate * (-inGrid[(xDim - 1) * yDim + yDim - 1] * 4 + inGrid[(xDim - 2) * yDim + yDim - 1] + inGrid[(xDim - 1) * yDim + yDim - 2] + 2 * boundaryValue);
            outGrid[yDim - 1] = inGrid[yDim - 1] + diffRate * (-inGrid[yDim - 1] * 4 + inGrid[yDim + yDim - 1] + inGrid[yDim - 2] + 2 * boundaryValue);
        } else {
            outGrid[0] = inGrid[0] + diffRate * (-inGrid[0] * 4 + (wrapX?inGrid[1*yDim+0]+inGrid[(xDim-1)*yDim]:inGrid[(1*yDim)+0]*2) + (wrapY?inGrid[0*yDim+yDim]+inGrid[0*yDim+1]:inGrid[0*yDim+1]*2));
            outGrid[(xDim - 1) * yDim] = inGrid[(xDim - 1) * yDim] + diffRate * (-inGrid[(xDim - 1) * yDim] * 4 + (wrapX?inGrid[(xDim - 2) * yDim]+inGrid[0]:2*inGrid[(xDim - 2)*yDim]) + (wrapY?inGrid[(xDim - 1) * yDim + 1]+inGrid[xDim*yDim-1]:2*inGrid[(xDim - 1) * yDim + 1]));
            outGrid[(xDim - 1) * yDim + yDim - 1] = inGrid[(xDim - 1) * yDim + yDim - 1] + diffRate * (-inGrid[(xDim - 1) * yDim + yDim - 1] * 4 + (wrapX?inGrid[(xDim - 2) * yDim + yDim - 1]+inGrid[yDim-1]:2*inGrid[(xDim - 2) * yDim + yDim - 1]) + (wrapY?inGrid[(xDim-1)*yDim]+inGrid[(xDim - 1) * yDim + yDim - 2]:2*inGrid[(xDim - 1) * yDim + yDim - 2]));
            outGrid[yDim - 1] = inGrid[yDim - 1] + diffRate * (-inGrid[yDim - 1] * 4 + (wrapX?inGrid[yDim + yDim - 1]+inGrid[(xDim-1)*yDim+yDim-1]:2*inGrid[yDim + yDim - 1]) + (wrapY?inGrid[0]+inGrid[yDim - 2]:2*inGrid[yDim - 2]));
        }
        //then we do the sides
        if (boundaryCond) {
            x = 0;
            for (y = 1; y < yDim - 1; y++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[(x + 1) * yDim + y] + inGrid[x * yDim + y + 1] + inGrid[x * yDim + y - 1] + boundaryValue);
            }
            x = xDim - 1;
            for (y = 1; y < yDim - 1; y++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[(x - 1) * yDim + y] + inGrid[x * yDim + y + 1] + inGrid[x * yDim + y - 1] + boundaryValue);
            }
            y = 0;
            for (x = 1; x < xDim - 1; x++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[x * yDim + y + 1] + inGrid[(x + 1) * yDim + y] + inGrid[(x - 1) * yDim + y] + boundaryValue);
            }
            y = yDim - 1;
            for (x = 1; x < xDim - 1; x++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[x * yDim + y - 1] + inGrid[(x + 1) * yDim + y] + inGrid[(x - 1) * yDim + y] + boundaryValue);
            }
        } else{
            x = 0;
            for (y = 1; y < yDim - 1; y++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[(x + 1) * yDim + y] + inGrid[x * yDim + y + 1] + inGrid[x * yDim + y - 1] + (wrapX?inGrid[(xDim - 1) * yDim + y]:inGrid[(x + 1) * yDim + y]));
            }
            x = xDim - 1;
            for (y = 1; y < yDim - 1; y++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[(x - 1) * yDim + y] + inGrid[x * yDim + y + 1] + inGrid[x * yDim + y - 1] + (wrapX?inGrid[0 * yDim + y]:inGrid[(x-1)*yDim+y]));
            }
            y = 0;
            for (x = 1; x < xDim - 1; x++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[x * yDim + y + 1] + inGrid[(x + 1) * yDim + y] + inGrid[(x - 1) * yDim + y]+(wrapY?inGrid[x*yDim+yDim-1]:inGrid[x*yDim+1]));
            }
            y = yDim - 1;
            for (x = 1; x < xDim - 1; x++) {
                outGrid[x * yDim + y] = inGrid[x * yDim + y] + diffRate * (-inGrid[x * yDim + y] * 4 + inGrid[x * yDim + y - 1] + inGrid[(x + 1) * yDim + y] + inGrid[(x - 1) * yDim + y]+(wrapY?inGrid[x*yDim]:inGrid[x*yDim+y-2]));
            }
        }
        //then we do the middle
        for (x = 1; x < xDim - 1; x++) {
            for (y = 1; y < yDim - 1; y++) {
                int i = x * yDim + y;
                outGrid[i] = inGrid[i] + diffRate * (-inGrid[i] * 4 + inGrid[(x + 1) * yDim + y] + inGrid[(x - 1) * yDim + y] + inGrid[x * yDim + y + 1] + inGrid[x * yDim + y - 1]);
            }
        }
    }

    //boolean in3(int compX,int compY,int zDim,int xDim,int yDim,int z){
    //    return xDim>=0&&xDim<compX&&yDim>=0&&yDim<compY&&z>=0&&z<zDim;
    //}


    /**
     * runs the diffusion equation in 3 dimensions
     *
     * @param inGrid        an array of values holding the starting state of the diffusible
     * @param outGrid       an array into which the result of diffusion will be written
     * @param xDim          xDim dimenison of the inGrid and outGrid
     * @param yDim          yDim dimension of the inGrid and outGrid
     * @param zDim          z dimension of the inGrid and outGrid
     * @param diffRate      diffusion rate for the diffusion equaition
     * @param boundaryCond  defines whether a constant boundary condition value should diffuse in from the boundaries
     * @param boundaryValue only impacts diffusion if boundaryCond is true, sets the boundary condition value
     * @param wrapX        whether to wrap around diffusion over the left and right and front and back boundaries
     * @param wrapY        whether to wrap around diffusion over the left and right and front and back boundaries
     * @param wrapZ        whether to wrap around diffusion over the left and right and front and back boundaries
     */
    public static void Diffusion3(final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final int zDim, final double
            diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX,final boolean wrapY,final boolean wrapZ) {
        int x, y, z;
        double valSum;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                for (z = 0; z < zDim; z++) {
                    //6 squares to check
                    valSum = 0;
                    if (InDim(xDim, x + 1)) {
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(0) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum+=inGrid[(x-1)*yDim*zDim+(y)*zDim+(z)];
                    }

                    if (InDim(xDim, x - 1)) {
                        valSum += inGrid[(x - 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(xDim - 1) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }

                    if (InDim(yDim, y + 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y + 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (0) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y-1) * zDim + (z)];
                    }

                    if (InDim(yDim, y - 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y - 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (yDim-1) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y+1) * zDim + (z)];
                    }

                    if (InDim(zDim, z + 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (0)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    }

                    if (InDim(zDim, z - 1)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (zDim-1)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    }
                    int i = x * yDim * zDim + y * zDim + z;
                    outGrid[i] = inGrid[i] + diffRate * (-inGrid[i] * 6 + valSum);
                }
            }
        }
    }
    public static void Diffusion2inhomogeneous(int x,int y,final double[] inGrid, final double[] outGrid,final double[] diffRates, final int xDim, final int yDim, final boolean boundaryCond, final double boundaryValue, final boolean wrapX,final boolean wrapY) {
        double valSum;
        double rateSum;
        double currRate;

        int i = x * yDim + y;
        //4 squares to check
        valSum = 0;
        rateSum = 0;
        double diffRate = diffRates[x * yDim + y];
        if(diffRate>0.25){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.25 value: "+diffRate+" x: "+x+" y: "+y);
        }

        currRate = DisplacedX2D(x + 1, y, diffRates, xDim, yDim, x - 1, false, boundaryValue, wrapX);
        valSum += DisplacedX2D(x + 1, y, inGrid, xDim, yDim, x - 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        currRate = DisplacedX2D(x - 1, y, diffRates, xDim, yDim, x + 1, false, boundaryValue, wrapX);
        valSum += DisplacedX2D(x - 1, y, inGrid, xDim, yDim, x + 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        currRate = DisplacedY2D(x, y+1, diffRates, xDim, yDim, y - 1, false, boundaryValue, wrapX);
        valSum += DisplacedY2D(x, y+1, inGrid, xDim, yDim, y - 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        currRate = DisplacedY2D(x, y-1, diffRates, xDim, yDim, y + 1, false, boundaryValue, wrapX);
        valSum += DisplacedY2D(x, y-1, inGrid, xDim, yDim, y + 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        outGrid[i] = inGrid[i] + (-inGrid[i] * rateSum + valSum) / 2;
    }
    public static void Diffusion2(int x,int y,final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final double diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX,final boolean wrapY) {
        //4 squares to check

        double valSum= DisplacedX2D(x+1,y,inGrid,xDim,yDim,x,boundaryCond,boundaryValue,wrapX);
        valSum+= DisplacedX2D(x-1,y,inGrid,xDim,yDim,x,boundaryCond,boundaryValue,wrapX);
        valSum+= DisplacedY2D(x,y+1,inGrid,xDim,yDim,y,boundaryCond,boundaryValue,wrapY);
        valSum+= DisplacedY2D(x,y-1,inGrid,xDim,yDim,y,boundaryCond,boundaryValue,wrapY);
        int i=x*yDim+y;
        outGrid[i]=inGrid[i]+diffRate*(-inGrid[i]*4+valSum);
    }


    public static void Advection2ndOrder(int x,int y,final double[]inGrid,final double[]outGrid,int xDim,int yDim,double xVel,double yVel,boolean boundaryCond,double boundaryValue) {
        int i=x*yDim+y;
        //if(inGrid[i]>0.5){
        //    System.out.println("");
        //}
        double xFlux=0;
        double yFlux=0;
        double prev=inGrid[i];
        if(xVel>0){
            xFlux=(xVel/2)*(3*prev
                    -4* DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedX2D(x-2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=(xVel/2)*(-3*prev
                    +4* DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedX2D(x+2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel>0){
            yFlux=(yVel/2)*(3*prev
                    -4* DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedY2D(x,y-2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        if(yVel<0){
            yFlux=(yVel/2)*(-3*prev
                    +4* DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedY2D(x,y+2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        outGrid[i]=inGrid[i]-xFlux-yFlux;
    }


    public static void Advection2ndOrderCorrection(int x,int y,final double[]intGrid,final double[]outGrid,int xDim,int yDim,double xVel,double yVel,boolean boundaryCond,double boundaryValue) {
        int i=x*yDim+y;
        //if(inGrid[i]>0.5){
        //    System.out.println("");
        //}
        double xFlux=0;
        double yFlux=0;
        double prev=intGrid[i];

        if(xVel>0){
            xFlux=(xVel/2)*(3*prev
                    -4* DisplacedX2D(x-1,y,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedX2D(x-2,y,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=(xVel/2)*(-3*prev
                    +4* DisplacedX2D(x+1,y,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedX2D(x+2,y,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel>0){
            yFlux=(yVel/2)*(3*prev
                    -4* DisplacedY2D(x,y-1,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedY2D(x,y-2,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        if(yVel<0){
            yFlux=(yVel/2)*(-3*prev
                    +4* DisplacedY2D(x,y+1,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedY2D(x,y+2,intGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        outGrid[i]=outGrid[i]-xFlux-yFlux;
    }

    public static void Advection2ndOrderPrediction(int x,int y,final double[]inGrid,final double[]outGrid,int xDim,int yDim,double xVel,double yVel,boolean boundaryCond,double boundaryValue) {
        int i=x*yDim+y;
        //if(inGrid[i]>0.5){
        //    System.out.println("");
        //}
        double xFlux=0;
        double yFlux=0;
        double prev=inGrid[i];
        if(xVel>0){
            xFlux=(xVel/2)*(3*prev
                    -4* DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedX2D(x-2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=(xVel/2)*(-3*prev
                    +4* DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedX2D(x+2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel>0){
            yFlux=(yVel/2)*(3*prev
                    -4* DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedY2D(x,y-2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        if(yVel<0){
            yFlux=(yVel/2)*(-3*prev
                    +4* DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedY2D(x,y+2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        outGrid[i]=inGrid[i]-xFlux/2-yFlux/2;
    }


    public static void Advection3rdOrder(int x,int y,final double[]inGrid,final double[]outGrid,int xDim,int yDim,double xVel,double yVel,boolean boundaryCond,double boundaryValue) {
        int i=x*yDim+y;
        //if(inGrid[i]>0.5){
        //    System.out.println("");
        //}
        double xFlux=0;
        double yFlux=0;
        double prev=inGrid[i];
        if(xVel>0){
            xFlux=(xVel/6)*(2* DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)+3*prev
                    -6* DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedX2D(x-2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=(xVel/6)*(-2* DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-3*prev
                    +6* DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedX2D(x+2,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel>0){
            yFlux=(yVel/2)*(2* DisplacedX2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)+3*prev
                    -6* DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    + DisplacedY2D(x,y-2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        if(yVel<0){
            yFlux=(yVel/2)*(-2* DisplacedX2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-3*prev
                    +6* DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)
                    - DisplacedY2D(x,y+2,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));

        }
        outGrid[i]=inGrid[i]-xFlux-yFlux;
    }



    public static void Advection2ndOrderLW(int x, int y, final double[] inGrid, final double[] outGrid, int xDim, int yDim, double xVel, double yVel, boolean boundaryCond, double boundaryValue){
        int i=x*yDim+y;
        double xFlux=0;
        double yFlux=0;
        double xyFlux=0;
        double xxFlux=0;
        double yyFlux=0;
        double prev=inGrid[i];
        xFlux=(xVel/2)*(DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)- DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        yFlux=(yVel/2)*(DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)- DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        //xyFlux=-(xVel*yVel/4)*(Displaced2D(x+1,y+1,inGrid,xDim,yDim,-1,-1,boundaryCond,boundaryValue,true,true)-Displaced2D(x-1,y+1,inGrid,xDim,yDim,-1,-1,boundaryCond,boundaryValue,true,true)-Displaced2D(x+1,y-1,inGrid,xDim,yDim,-1,-1,boundaryCond,boundaryValue,true,true)+Displaced2D(x-1,y-1,inGrid,xDim,yDim,-1,-1,boundaryCond,boundaryValue,true,true));
        xxFlux=-(xVel*xVel/2)*(DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-2*prev+ DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        yyFlux=-(yVel*yVel/2)*(DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-2*prev+ DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        outGrid[i]=inGrid[i]-xFlux-yFlux-xyFlux-xxFlux-yyFlux;
    }

    public static void Advection1stOrder(int x, int y, final double[] inGrid, final double[] outGrid, int xDim, int yDim, double xVel, double yVel, boolean boundaryCond, double boundaryValue){
        int i=x*yDim+y;
        double xFlux=0;
        double yFlux=0;
        double prev=inGrid[i];
        if(xVel>0){
            xFlux=xVel*(prev- DisplacedX2D(x-1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=xVel*(DisplacedX2D(x+1,y,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        if(yVel>0){
            yFlux=yVel*(prev- DisplacedY2D(x,y-1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel<0){
            yFlux=yVel*(DisplacedY2D(x,y+1,inGrid,xDim,yDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        outGrid[i]=inGrid[i]-xFlux-yFlux;
    }

    public static void Advection3D1stOrder(int x,int y,int z,final double[]inGrid,final double[]outGrid,int xDim,int yDim,int zDim,double xVel,double yVel,double zVel,boolean boundaryCond,double boundaryValue){
        int i=x*yDim*zDim+y*zDim+z;
        double xFlux=0;
        double yFlux=0;
        double zFlux=0;
        double prev=inGrid[i];
        if(xVel>0){
            xFlux=xVel*(prev- DisplacedX3D(x-1,y,z,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=xVel*(DisplacedX3D(x+1,y,z,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        if(yVel>0){
            yFlux=yVel*(prev- DisplacedY3D(x,y-1,z,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true));
        }
        if(yVel<0){
            yFlux=yVel*(DisplacedY3D(x,y+1,z,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        if(zVel>0){
            zFlux=zVel*(prev- DisplacedZ3D(x,y,z-1,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true));
        }
        if(zVel<0){
            zFlux=zVel*(DisplacedZ3D(x,y,z+1,inGrid,xDim,yDim,zDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        outGrid[i]=inGrid[i]-xFlux-yFlux-zFlux;
    }

    //final answer
    //b=1+4D
    //db=1-4D
    //ac=-2D
    //dac=2D
    public static void TDMAx(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate) {
        final double ac=-diffRate;
        final double b=2*(diffRate)+1;
        final double db=-2*(diffRate)+1;
        final double dac=diffRate;

        final int len =  xDim;
        final int max = yDim;

        //Doing the 0 entries
        scratch[0] = ac/(b+ac);
        double above = iRow == max - 1 ? in[(0)*yDim+ (iRow)]: in[(0)*yDim+(iRow + 1)];
        double below = iRow == 0 ? in[(0 *yDim)+(iRow)] : in[(0 *yDim)+(iRow - 1)];
        double middle=in[(0*yDim)+iRow];
        double di=db*middle + above*dac + below*dac;
        scratch[len] = (di) / (b+ac);

        //Doing the forward passes
        for (int i = 1; i < len-1; i++) {
            scratch[i] = ac / (b - ac*scratch[i - 1]);
        }
        for (int i = 1; i < len; i++) {
            above = iRow == max - 1 ? in[(i)*yDim +(iRow)] : in[(i)*yDim+ (iRow + 1)];
            below = iRow == 0 ? in[(i)*yDim+(iRow)] : in[(i)*yDim+(iRow - 1)];
            middle=in[(i)*yDim+(iRow)];
            di=(db*middle + above*dac + below*dac);
            if(i<len-1){scratch[len + i] = (di - ac*scratch[len + i - 1]) / (b - ac*scratch[i - 1]);}
            else{scratch[len + i] = (di - ac*scratch[len + i - 1]) / ((b+ac) - ac*scratch[i - 1]);}
        }

        //backward pass
        out[(len - 1)*yDim+(iRow)] = scratch[len * 2 - 1];
        for (int i = len - 2; i >= 0; i--) {
            out[(i)*yDim+(iRow)] = scratch[len + i] - scratch[i] * out[(i + 1)*yDim+(iRow)];
        }
    }
    public static void TDMAy(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate) {
        final double ac=-diffRate;
        final double b=2*(diffRate)+1;
        final double db=-2*(diffRate)+1;
        final double dac=diffRate;


        final int len = yDim;
        final int max = xDim;

        //Doing the 0 entries
        scratch[0] = ac/(b+ac);
        double above = iRow == max - 1 ? in[(iRow)*yDim+(0)] : in[(iRow + 1)*yDim+(0)];
        double below = iRow == 0 ? in[(iRow)*yDim+(0)] : in[(iRow - 1)* yDim+(0)];
        double middle=in[(iRow)*yDim+(0)];
        double di=db*middle + above*dac + below*dac;
        scratch[len] = (di) / (b+ac);

        //Doing the forward passes
        for (int i = 1; i < len-1; i++) {
            scratch[i] = ac / (b - ac*scratch[i - 1]);
        }
        for (int i = 1; i < len; i++) {
            above = iRow == max - 1 ? in[(iRow)*yDim+(i)] : in[(iRow + 1)*yDim+(i)];
            below = iRow == 0 ? in[(iRow)*yDim+(i)] : in[(iRow - 1)*yDim+(i)];
            middle=in[(iRow)*yDim+(i)];
            di=(db*middle + above*dac + below*dac);
            if(i<len-1){scratch[len + i] = (di - ac*scratch[len + i - 1]) / (b - ac*scratch[i - 1]);}
            else{scratch[len + i] = (di - ac*scratch[len + i - 1]) / ((b+ac) - ac*scratch[i - 1]);}
        }

        //backward pass
        out[(iRow)*yDim+(len - 1)] = scratch[len * 2 - 1];
        for (int i = len - 2; i >= 0; i--) {
            out[(iRow)*yDim+(i)] = scratch[len + i] - scratch[i] * out[(iRow)*yDim+(i + 1)];
        }
    }


    public static void TDMAx(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate,double boundaryCond) {
        final double ac=-diffRate;
        final double b=2*(diffRate)+1;
        final double db=-2*(diffRate)+1;
        final double dac=diffRate;

        final int len = xDim+2;
        final int max = yDim;

        //Doing the 0 entries (which is this time simply the boundary
        scratch[0] = ac/(b+ac);
        double di=boundaryCond;
        scratch[len] = (di) / (b+ac);

        //Doing the forward passes (special case at the last, which is once again the boundary)
        for (int i = 1; i < len-1; i++) {
            scratch[i] = ac / (b - ac*scratch[i - 1]);
        }
        for (int i = 1; i < len; i++) {
            if(i==len-1){
                di=boundaryCond;
            } else {
                double above = iRow == max - 1 ? boundaryCond : in[(i-1) * yDim + (iRow + 1)];
                double below = iRow == 0 ? boundaryCond : in[(i-1) * yDim + (iRow - 1)];
                double middle = in[(i-1) * yDim + (iRow)];
                di = (db * middle + above * dac + below * dac);
            }
            if (i < len - 1) {
                scratch[len + i] = (di - ac * scratch[len + i - 1]) / (b - ac * scratch[i - 1]);
            } else {
                scratch[len + i] = (di - ac * scratch[len + i - 1]) / ((b+ac) - ac * scratch[i - 1]);
            }
        }

        //backward pass
        double temp = scratch[len * 2 - 1];
        for (int i = len - 2; i >= 1; i--) {
            if(i==len-2){
                out[(i-1)*yDim+(iRow)] = scratch[len + i] - scratch[i] * temp;
            }else {
                out[(i-1) * yDim + (iRow)] = scratch[len + i] - scratch[i] * out[(i) * yDim + (iRow)];
            }
        }
    }
    public static void TDMAy(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate,double boundaryCond) {
        final double ac=-diffRate;
        final double b=2*(diffRate)+1;
        final double db=-2*(diffRate)+1;
        final double dac=diffRate;


        final int len = yDim + 2;
        final int max = xDim;

        //Doing the 0 entries
        scratch[0] = ac / (b+ac);
        double di = boundaryCond;
        scratch[len] = (di) / (b+ac);

        //Doing the forward passes
        for (int i = 1; i < len - 1; i++) {
            scratch[i] = ac / (b - ac * scratch[i - 1]);
        }
        for (int i = 1; i < len; i++) {
            if (i == len - 1) {
                di = boundaryCond;
            } else {
                double above = iRow == max - 1 ? boundaryCond : in[(iRow + 1) * yDim + (i-1)];
                double below = iRow == 0 ? boundaryCond : in[(iRow - 1) * yDim + (i-1)];
                double middle = in[(iRow) * yDim + (i-1)];
                di = (db * middle + above * dac + below * dac);
            }
            if (i < len - 1) {
                scratch[len + i] = (di - ac * scratch[len + i - 1]) / (b - ac * scratch[i - 1]);
            } else {
                scratch[len + i] = (di - ac * scratch[len + i - 1]) / ((b+ac) - ac * scratch[i - 1]);
            }
        }

        //backward pass
        double temp=scratch[len*2-1];
        for (int i = len - 2; i >= 1; i--) {
            if(i==len-2) {
                out[(iRow) * yDim + (i-1)] = scratch[len + i] - scratch[i] * temp;
            }else {
                out[(iRow) * yDim + (i-1)] = scratch[len + i] - scratch[i] * out[(iRow) * yDim + (i)];
            }
        }
    }

    public static void DiffusionADI2(boolean xAxis,final double[]inGrid, final double[]outGrid,final double[]scratch,final int xDim,final int yDim,final double diffRate,boolean boundaryCond,double boundaryValue) {
        int len=xAxis?yDim:xDim;
        if(xAxis){
            for (int i = 0; i < len; i++) {
                if(boundaryCond){
                    TDMAx(inGrid, outGrid, scratch, xDim, yDim, i, diffRate,boundaryValue);
                } else {
                    TDMAx(inGrid, outGrid, scratch, xDim, yDim, i, diffRate);
                }
            }
        }
        else {
            for (int i = 0; i < len; i++) {
                if(boundaryCond) {
                    TDMAy(inGrid, outGrid, scratch, xDim, yDim, i, diffRate,boundaryValue);
                }else{
                    TDMAy(inGrid, outGrid, scratch, xDim, yDim, i, diffRate);
                }
            }
        }
    }


    public static void ConservativeTransportStep2(final double[] inGrid,final double[] midGrid, final double[] outGrid, final int xDim, final int yDim,final double[] xVels,final double[] yVels, final boolean boundaryCond, final double boundaryValue){
        int x,y;
        double vXp1,vXm1,vYp1,vYm1,dVx,dVy;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                int i=x*yDim+y;
                if(InDim(xDim,x+1)){
                    vXp1=xVels[(x+1)*yDim+y];
                }
                else if(boundaryCond){
                    vXp1=boundaryValue;
                }
                else{
                    vXp1=xVels[(0)*yDim+y];
                }
                if(InDim(xDim,x-1)){
                    vXm1=xVels[(x-1)*yDim+y];
                }
                else if(boundaryCond){
                    vXm1=boundaryValue;
                }
                else{
                    vXm1=xVels[(xDim-1)*yDim+y];
                }

                if(InDim(yDim,y+1)){
                    vYp1=yVels[x*yDim+(y+1)];
                }
                else if(boundaryCond){
                    vYp1=boundaryValue;
                }
                else{
                    vYp1=yVels[x*yDim+(0)];
                }
                if(InDim(yDim,y-1)){
                    vYm1=yVels[x*yDim+(y-1)];
                }
                else if(boundaryCond){
                    vYm1=boundaryValue;
                }
                else{
                    vYm1=yVels[x*yDim+(yDim-1)];
                }
                dVx=vXp1-vXm1;
                dVy=vYp1-vYm1;
                outGrid[i]=midGrid[i]-(dVx/2+dVy/2)*inGrid[i];
            }
        }
    }

    public static double Displaced2D(int x, int y, double[] vals, int xDim, int yDim, int fallbackX, int fallbackY, boolean boundaryCond, double boundaryValue, boolean wrapX, boolean wrapY){
        boolean inX=InDim(xDim,x);
        boolean inY=InDim(yDim,y);
        if(inX&&inY){
            return vals[x*yDim+y];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(!inX&&wrapX){
            x=ModWrap(x,xDim);
        }
        else{
            x=fallbackX;
        }
        if(!inY&&wrapY){
            y=ModWrap(y,yDim);
        }
        else{
            y=fallbackY;
        }
        return vals[x*yDim+y];
    }

    public static double DisplacedX2D(int x, int y, double[] vals, int xDim, int yDim, int fallbackX, boolean boundaryCond, double boundaryValue, boolean wrapX){
        if(InDim(xDim,x)){
            return vals[x*yDim+y];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(wrapX){
            x=ModWrap(x,xDim);
        }
        else{
            x=fallbackX;
        }
        return vals[x*yDim+y];
    }

    public static double DisplacedY2D(int x, int y, double[] vals, int xDim, int yDim, int fallbackY, boolean boundaryCond, double boundaryValue, boolean wrapY){
        if(InDim(yDim,y)){
            return vals[x*yDim+y];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(wrapY){
            y=ModWrap(y,yDim);
        }
        else{
            y=fallbackY;
        }
        return vals[x*yDim+y];
    }
    public static double DisplacedX3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackX, boolean boundaryCond, double boundaryValue, boolean wrapX){
        if(InDim(xDim,x)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(wrapX){
            x=ModWrap(x,xDim);
        }
        else{
            x=fallbackX;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }
    public static double DisplacedY3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackY, boolean boundaryCond, double boundaryValue, boolean wrapY){
        if(InDim(yDim,y)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(wrapY){
            y=ModWrap(y,yDim);
        }
        else{
            y=fallbackY;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }
    public static double DisplacedZ3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackZ, boolean boundaryCond, double boundaryValue, boolean wrapZ){
        if(InDim(zDim,z)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        if(boundaryCond){
            return boundaryValue;
        }
        if(wrapZ){
            z=ModWrap(z,zDim);
        }
        else{
            z=fallbackZ;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }

}

//THE BELOW CODE IS A 3D ADI IMPLEMENTATION THAT SO FAR IS NOT WORKING!
//    //only changes from 2D to 3D are 4x diffRate for db, front and back added to d side of equation, and array accesses are different
//    public static void TDMA3x(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim,final int zDim, final int iRow1,final int iRow2, final double diffRate) {
//        //doing:x, iRow1:y, iRow2:z
//        final double ac=diffRate;
//        final double b=-2*(2*diffRate+1);
//        final double db=2*(2*diffRate-1);
//        final double dac=-diffRate;
//
//        final int len =  xDim;
//        final int max1 = yDim;
//        final int max2 = zDim;
//
//        //Doing the 0 entries
//        scratch[0] = (2.0*ac)/b;
//        double above = iRow1 == max1 - 1 ? in[(0)*yDim*zDim+ (iRow1 - 1)*yDim+iRow2]: in[(0)*yDim*zDim+(iRow1 + 1)*yDim+iRow2];
//        double below = iRow1 == 0 ? above : in[(0)*yDim*zDim+(iRow1 - 1)*yDim+iRow2];
//        double front = iRow2 == max2 - 1 ? in[(0)*yDim*zDim+ (iRow1)*yDim+iRow2-1]: in[(0)*yDim*zDim+(iRow1)*yDim+iRow2+1];
//        double back = iRow1 == 0 ? above : in[(0)*yDim*zDim+(iRow1)*yDim+iRow2-1];
//        double middle=in[(0)*yDim*zDim+iRow1*yDim+iRow2];
//        double di=db*middle + above*dac + below*dac + front*dac + back*dac;
//        scratch[len] = (di) / b;
//
//        //Doing the forward passes
//        for (int i = 1; i < len-1; i++) {
//            scratch[i] = ac / (b - ac*scratch[i - 1]);
//        }
//        for (int i = 1; i < len; i++) {
//
//            above = iRow1 == max1 - 1 ? in[(i)*yDim*zDim+ (iRow1 - 1)*yDim+iRow2]: in[(i)*yDim*zDim+(iRow1 + 1)*yDim+iRow2];
//            below = iRow1 == 0 ? above : in[(i)*yDim*zDim+(iRow1 - 1)*yDim+iRow2];
//            front = iRow2 == max2 - 1 ? in[(i)*yDim*zDim+ (iRow1)*yDim+iRow2-1]: in[(i)*yDim*zDim+(iRow1)*yDim+iRow2+1];
//            back = iRow1 == 0 ? above : in[(i)*yDim*zDim+(iRow1)*yDim+iRow2-1];
//            middle=in[(i)*yDim*zDim+iRow1*yDim+iRow2];
//            di=db*middle + above*dac + below*dac + front*dac + back*dac;
//            if(i<len-1){scratch[len + i] = (di - ac*scratch[len + i - 1]) / (b - ac*scratch[i - 1]);}
//            else{scratch[len + i] = (di - 2*ac*scratch[len + i - 1]) / (b - 2*ac*scratch[i - 1]);}
//        }
//
//        //backward pass, do not touch!
//        out[(len - 1)*yDim*zDim+(iRow1)*yDim+iRow2] = scratch[len * 2 - 1];
//        for (int i = len - 2; i >= 0; i--) {
//            out[(i)*yDim*zDim+(iRow1)*yDim+iRow2] = scratch[len + i] - scratch[i] * out[(i + 1)*yDim*zDim+(iRow1)*yDim+iRow2];
//        }
//    }
//    //only changes from 2D to 3D are 4x diffRate for db, front and back added to d side of equation, and array accesses are different
//    public static void TDMA3y(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim,final int zDim, final int iRow1,final int iRow2, final double diffRate) {
//        //doing:y, iRow1:x, iRow2:z
//        final double ac=diffRate;
//        final double b=-2*(2*diffRate+1);
//        final double db=2*(2*diffRate-1);
//        final double dac=-diffRate;
//
//        final int max1 = xDim;
//        final int len =  yDim;
//        final int max2 = zDim;
//
//        //Doing the 0 entries
//        scratch[0] = (2.0*ac)/b;
//        double above = iRow1 == max1 - 1 ? in[(iRow1 - 1)*yDim*zDim+ (0)*yDim+iRow2]: in[(iRow1+1)*yDim*zDim+(0)*yDim+iRow2];
//        double below = iRow1 == 0 ? above : in[(iRow1 - 1)*yDim*zDim+(0)*yDim+iRow2];
//        double front = iRow2 == max2 - 1 ? in[(iRow1)*yDim*zDim+ (0)*yDim+iRow2-1]: in[(iRow1)*yDim*zDim+(0)*yDim+iRow2+1];
//        double back = iRow1 == 0 ? above : in[(iRow1)*yDim*zDim+(0)*yDim+iRow2-1];
//        double middle=in[(iRow1)*yDim*zDim+0*yDim+iRow2];
//        double di=db*middle + above*dac + below*dac + front*dac + back*dac;
//        scratch[len] = (di) / b;
//
//        //Doing the forward passes
//        for (int i = 1; i < len-1; i++) {
//            scratch[i] = ac / (b - ac*scratch[i - 1]);
//        }
//        for (int i = 1; i < len; i++) {
//
//            above = iRow1 == max1 - 1 ? in[(iRow1 - 1)*yDim*zDim+ (i)*yDim+iRow2]: in[(iRow1+1)*yDim*zDim+(i)*yDim+iRow2];
//            below = iRow1 == 0 ? above : in[(iRow1 - 1)*yDim*zDim+(i)*yDim+iRow2];
//            front = iRow2 == max2 - 1 ? in[(iRow1)*yDim*zDim+ (i)*yDim+iRow2-1]: in[(iRow1)*yDim*zDim+(i)*yDim+iRow2+1];
//            back = iRow1 == 0 ? above : in[(iRow1)*yDim*zDim+(i)*yDim+iRow2-1];
//            middle=in[(iRow1)*yDim*zDim+i*yDim+iRow2];
//            di=db*middle + above*dac + below*dac + front*dac + back*dac;
//            if(i<len-1){scratch[len + i] = (di - ac*scratch[len + i - 1]) / (b - ac*scratch[i - 1]);}
//            else{scratch[len + i] = (di - 2*ac*scratch[len + i - 1]) / (b - 2*ac*scratch[i - 1]);}
//        }
//
//        //backward pass, do not touch!
//        out[(iRow1)*yDim*zDim+(len - 1)*yDim+iRow2] = scratch[len * 2 - 1];
//        for (int i = len - 2; i >= 0; i--) {
//            out[(iRow1)*yDim*zDim+(i)*yDim+iRow2] = scratch[len + i] - scratch[i] * out[(iRow1)*yDim*zDim+(i + 1)*yDim+iRow2];
//        }
//    }
//
//    //only changes from 2D to 3D are 4x diffRate for db, front and back added to d side of equation, and array accesses are different
//    public static void TDMA3z(final double[] in, final double[] out,final double[] scratch,final int xDim, final int yDim,final int zDim, final int iRow1,final int iRow2, final double diffRate) {
//        //doing:z, iRow1:x, iRow2:y
//        final double ac=diffRate;
//        final double b=-2*(2*diffRate+1);
//        final double db=2*(2*diffRate-1);
//        final double dac=-diffRate;
//
//        final int max1 = xDim;
//        final int max2 = yDim;
//        final int len =  zDim;
//
//        //Doing the 0 entries
//        scratch[0] = (2.0*ac)/b;
//        double above = iRow1 == max1 - 1 ? in[(iRow1 - 1)*yDim*zDim+ (iRow2)*yDim+0]: in[(iRow1+1)*yDim*zDim+(iRow2)*yDim+0];
//        double below = iRow1 == 0 ? above : in[(iRow1 - 1)*yDim*zDim+(iRow2)*yDim+0];
//        double front = iRow2 == max2 - 1 ? in[(iRow1)*yDim*zDim+ (iRow2-1)*yDim+0]: in[(iRow1)*yDim*zDim+(iRow2+1)*yDim+0];
//        double back = iRow1 == 0 ? above : in[(iRow1)*yDim*zDim+(iRow2-1)*yDim+0];
//        double middle=in[(iRow1)*yDim*zDim+iRow2*yDim+0];
//        double di=db*middle + above*dac + below*dac + front*dac + back*dac;
//        scratch[len] = (di) / b;
//
//        //Doing the forward passes
//        for (int i = 1; i < len-1; i++) {
//            scratch[i] = ac / (b - ac*scratch[i - 1]);
//        }
//        for (int i = 1; i < len; i++) {
//
//            above = iRow1 == max1 - 1 ? in[(iRow1 - 1)*yDim*zDim+ (iRow2)*yDim+i]: in[(iRow1+1)*yDim*zDim+(iRow2)*yDim+i];
//            below = iRow1 == 0 ? above : in[(iRow1 - 1)*yDim*zDim+(iRow2)*yDim+i];
//            front = iRow2 == max2 - 1 ? in[(iRow1)*yDim*zDim+ (iRow2-1)*yDim+i]: in[(iRow1)*yDim*zDim+(iRow2+1)*yDim+i];
//            back = iRow1 == 0 ? above : in[(iRow1)*yDim*zDim+(iRow2-1)*yDim+i];
//            middle=in[(iRow1)*yDim*zDim+iRow2*yDim+i];
//            di=db*middle + above*dac + below*dac + front*dac + back*dac;
//            if(i<len-1){scratch[len + i] = (di - ac*scratch[len + i - 1]) / (b - ac*scratch[i - 1]);}
//            else{scratch[len + i] = (di - 2*ac*scratch[len + i - 1]) / (b - 2*ac*scratch[i - 1]);}
//        }
//
//        //backward pass, do not touch!
//        out[(iRow1)*yDim*zDim+(iRow2)*yDim+len - 1] = scratch[len * 2 - 1];
//        for (int i = len - 2; i >= 0; i--) {
//            out[(iRow1)*yDim*zDim+(iRow2)*yDim+i] = scratch[len + i] - scratch[i] * out[(iRow1)*yDim*zDim+(iRow2)*yDim+i + 1];
//        }
//    }
//    public static void DiffusionADI3(int iAxis,double[]inGrid,double[]outGrid,final double[] scratch,final int xDim,final int yDim,final int zDim,final double diffRate){
//        switch (iAxis){
//            case 0://x axis case
//                for (int y = 0; y < yDim; y++) {
//                    for (int z = 0; z < zDim; z++) {
//                        TDMA3x(inGrid,outGrid,scratch,xDim,yDim,zDim,y,z,diffRate);
//                    }
//                }
//                break;
//            case 1://y axis case
//                for (int x = 0; x < yDim; x++) {
//                    for (int z = 0; z < zDim; z++) {
//                        TDMA3y(inGrid,outGrid,scratch,xDim,yDim,zDim,x,z,diffRate);
//                    }
//                }
//                break;
//            case 2://z axis case
//                for (int x = 0; x < xDim; x++) {
//                    for (int y = 0; y < zDim; y++) {
//                        TDMA3z(inGrid,outGrid,scratch,xDim,yDim,zDim,x,y,diffRate);
//                    }
//                }
//                break;
//            default:throw new IllegalArgumentException("iAxis variable must be one of 0(x),1(y),2(z)");
//        }
//    }
