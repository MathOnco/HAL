package HAL.Tools.Internal;
import HAL.Interfaces.Coords1DDouble;
import HAL.Interfaces.Coords2DDouble;
import HAL.Interfaces.Coords3DDouble;

import static HAL.Util.*;

/**
 * Created by Rafael on 10/14/2017.
 *
 * future reference notes
 * constant: FD upwind
 * incompr: density cannot increase or decrease at any position FD upwind
 * compr continuous
 * compr discont
 */
public class PDEequations {


    public static void Diffusion1(double[]field,double[]deltas,double diffRate,int xDim,boolean wrapX,Coords1DDouble BC) {
        for (int x = 0; x < xDim; x++) {
            double centerVal = field[x];
            double valSum = Delta1D(field, centerVal, x + 1, xDim, wrapX, BC);
            valSum += Delta1D(field, centerVal, x - 1, xDim, wrapX, BC);
            deltas[x] += valSum * diffRate;
        }
    }
//    public static void Diffusion1(double[]field,double[]deltas,double[] diffRates,int xDim,boolean wrapX,Coords1DDouble BC,Coords1DDouble DiffRateBC) {
//        for (int x = 0; x < xDim; x++) {
//            double centerVal = field[x];
//            double valSum = Delta1D(field, centerVal, x + 1, xDim, wrapX, BC)*Displaced1D(diffRates,x+1,xDim,wrapX,DiffRateBC);
//            valSum += Delta1D(field, centerVal, x - 1, xDim, wrapX, BC)*Displaced1D(diffRates,x-1,xDim,wrapX,DiffRateBC);
//            deltas[x] += valSum;
//        }
//    }
    public static void Diffusion1(double[]field,double[]deltas,double[] diffRates,int xDim,boolean wrapX,Coords1DDouble BC,Coords1DDouble DiffRateBC) {
        for (int x = 0; x < xDim; x++) {
            double centerVal = field[x];
            double dp1x=(x==xDim-1&&!wrapX)?Displaced1D(diffRates,x+1,xDim,wrapX,DiffRateBC):diffRates[x];
            double dm1x=Displaced1D(diffRates,x-1,xDim,wrapX,DiffRateBC);
            double valSum = Delta1D(field, centerVal, x + 1, xDim, wrapX, BC)*dp1x;
            valSum += Delta1D(field, centerVal, x - 1, xDim, wrapX, BC)*dm1x;
            deltas[x] += valSum;
        }
    }

    public static void Diffusion2(double[]field,double[]deltas,double diffRate,int xDim,int yDim,boolean wrapX,boolean wrapY,Coords2DDouble BC) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = x * yDim + y;
                double centerVal = field[i];
                double deltaSum = DeltaX2D(field, centerVal, x + 1, y, xDim, yDim, wrapX, BC);
                deltaSum += DeltaX2D(field, centerVal, x - 1, y, xDim, yDim, wrapX, BC);
                deltaSum += DeltaY2D(field, centerVal, x, y + 1, xDim, yDim, wrapY, BC);
                deltaSum += DeltaY2D(field, centerVal, x, y - 1, xDim, yDim, wrapY, BC);
                deltas[i] += deltaSum * diffRate;
            }
        }
    }
//    public static void Diffusion2(double[]field,double[]deltas,double[] diffRates,int xDim,int yDim,boolean wrapX,boolean wrapY,Coords2DDouble BC,Coords2DDouble DiffRateBC) {
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < yDim; y++) {
//                int i = x * yDim + y;
//                double centerVal = field[i];
//                double deltaSum = DeltaX2D(field, centerVal, x + 1, y, xDim, yDim, wrapX, BC)*DisplacedX2D(diffRates,x+1,y,xDim,yDim,wrapX,DiffRateBC);
//                deltaSum += DeltaX2D(field, centerVal, x - 1, y, xDim, yDim, wrapX, BC)*DisplacedX2D(diffRates,x-1,y,xDim,yDim,wrapX,DiffRateBC);
//                deltaSum += DeltaY2D(field, centerVal, x, y + 1, xDim, yDim, wrapY, BC)*DisplacedY2D(diffRates,x,y+1,xDim,yDim,wrapY,DiffRateBC);
//                deltaSum += DeltaY2D(field, centerVal, x, y - 1, xDim, yDim, wrapY, BC)*DisplacedY2D(diffRates,x,y-1,xDim,yDim,wrapY,DiffRateBC);
//                deltas[i] += deltaSum;
//            }
//        }
//    }
    public static void Diffusion2(double[]field,double[]deltas,double[] diffRatesX,double[]diffRatesY,int xDim,int yDim,boolean wrapX,boolean wrapY,Coords2DDouble BC,Coords2DDouble DiffRateXBC,Coords2DDouble DiffRateYBC) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = x * yDim + y;
                double centerVal = field[i];
                double dp1x=(x==xDim-1&&!wrapX)?DisplacedX2D(diffRatesX,x+1,y,xDim,yDim,wrapX,DiffRateXBC):diffRatesX[i];
                double dm1x=DisplacedX2D(diffRatesX,x-1,y,xDim,yDim,wrapX,DiffRateXBC);
                double dp1y=(y==yDim-1&&!wrapY)?DisplacedY2D(diffRatesY,x,y+1,xDim,yDim,wrapY,DiffRateYBC):diffRatesY[i];
                double dm1y=DisplacedY2D(diffRatesY,x,y-1,xDim,yDim,wrapY,DiffRateYBC);
                double deltaSum = DeltaX2D(field, centerVal, x + 1, y, xDim, yDim, wrapX, BC)*dp1x;
                deltaSum += DeltaX2D(field, centerVal, x - 1, y, xDim, yDim, wrapX, BC)*dm1x;
                deltaSum += DeltaY2D(field, centerVal, x, y + 1, xDim, yDim, wrapY, BC)*dp1y;
                deltaSum += DeltaY2D(field, centerVal, x, y - 1, xDim, yDim, wrapY, BC)*dm1y;
                deltas[i] += deltaSum;
            }
        }
    }
    public static void Diffusion3(double[]field,double[]deltas,double diffRate,int xDim,int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ,Coords3DDouble BC){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    int i = x * yDim * zDim + y * zDim + z;
                    double centerVal=field[i];
                    double deltaSum=DeltaX3D(field,centerVal,x+1,y,z,xDim,yDim,zDim,wrapX,BC);
                    deltaSum+=DeltaX3D(field,centerVal,x-1,y,z,xDim,yDim,zDim,wrapX,BC);
                    deltaSum+=DeltaY3D(field,centerVal,x,y+1,z,xDim,yDim,zDim,wrapY,BC);
                    deltaSum+=DeltaY3D(field,centerVal,x,y-1,z,xDim,yDim,zDim,wrapY,BC);
                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z+1,xDim,yDim,zDim,wrapZ,BC);
                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z-1,xDim,yDim,zDim,wrapZ,BC);
                    deltas[i]+=deltaSum*diffRate;
                }
            }
        }
    }
//    public static void Diffusion3(double[]field,double[]deltas,double[] diffRates,int xDim,int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ,Coords3DDouble BC,Coords3DDouble DiffRateBC){
//        for (int x = 0; x < xDim; x++) {
//            for (int y = 0; y < yDim; y++) {
//                for (int z = 0; z < zDim; z++) {
//                    int i = x * yDim * zDim + y * zDim + z;
//                    double centerVal=field[i];
//                    double deltaSum=DeltaX3D(field,centerVal,x+1,y,z,xDim,yDim,zDim,wrapX,BC)*DisplacedX3D(diffRates,x+1,y,z,xDim,yDim,zDim,wrapX,DiffRateBC);
//                    deltaSum+=DeltaX3D(field,centerVal,x-1,y,z,xDim,yDim,zDim,wrapX,BC)*DisplacedX3D(diffRates,x-1,y,z,xDim,yDim,zDim,wrapX,DiffRateBC);
//                    deltaSum+=DeltaY3D(field,centerVal,x,y+1,z,xDim,yDim,zDim,wrapY,BC)*DisplacedY3D(diffRates,x,y+1,z,xDim,yDim,zDim,wrapY,DiffRateBC);
//                    deltaSum+=DeltaY3D(field,centerVal,x,y-1,z,xDim,yDim,zDim,wrapY,BC)*DisplacedY3D(diffRates,x,y-1,z,xDim,yDim,zDim,wrapY,DiffRateBC);
//                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z+1,xDim,yDim,zDim,wrapZ,BC)*DisplacedZ3D(diffRates,x,y,z+1,xDim,yDim,zDim,wrapZ,DiffRateBC);
//                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z-1,xDim,yDim,zDim,wrapZ,BC)*DisplacedZ3D(diffRates,x,y,z-1,xDim,yDim,zDim,wrapZ,DiffRateBC);
//                    deltas[i]+=deltaSum;
//                }
//            }
//        }
//    }
    public static void Diffusion3(double[]field,double[]deltas,double[] diffRatesX,double[]diffRatesY,double[]diffRatesZ,int xDim,int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ,Coords3DDouble BC,Coords3DDouble DiffRateXBC,Coords3DDouble DiffRateYBC,Coords3DDouble DiffRateZBC){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    int i = x * yDim * zDim + y * zDim + z;
                    double centerVal=field[i];
                    double dp1x=(x==xDim-1&&!wrapX)?DisplacedX3D(diffRatesX,x+1,y,z,xDim,yDim,zDim,wrapX,DiffRateXBC):diffRatesX[i];
                    double dm1x=DisplacedX3D(diffRatesX,x-1,y,z,xDim,yDim,zDim,wrapX,DiffRateXBC);
                    double dp1y=(y==yDim-1&&!wrapY)?DisplacedY3D(diffRatesY,x,y+1,z,xDim,yDim,zDim,wrapY,DiffRateYBC):diffRatesY[i];
                    double dm1y=DisplacedY3D(diffRatesY,x,y-1,z,xDim,yDim,zDim,wrapY,DiffRateYBC);
                    double dp1z=(z==zDim-1&&!wrapZ)?DisplacedZ3D(diffRatesZ,x,y,z+1,xDim,yDim,zDim,wrapZ,DiffRateZBC):diffRatesZ[i];
                    double dm1z=DisplacedZ3D(diffRatesZ,x,y,z-1,xDim,yDim,zDim,wrapZ,DiffRateZBC);
                    double deltaSum=DeltaX3D(field,centerVal,x+1,y,z,xDim,yDim,zDim,wrapX,BC)*dp1x;
                    deltaSum+=DeltaX3D(field,centerVal,x-1,y,z,xDim,yDim,zDim,wrapX,BC)*dm1x;
                    deltaSum+=DeltaY3D(field,centerVal,x,y+1,z,xDim,yDim,zDim,wrapY,BC)*dp1y;
                    deltaSum+=DeltaY3D(field,centerVal,x,y-1,z,xDim,yDim,zDim,wrapY,BC)*dm1y;
                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z+1,xDim,yDim,zDim,wrapZ,BC)*dp1z;
                    deltaSum+=DeltaZ3D(field,centerVal,x,y,z-1,xDim,yDim,zDim,wrapZ,BC)*dm1z;
                    deltas[i]+=deltaSum;
                }
            }
        }
    }
    public static void Advection1(double[]field,double[]deltas,double vel,int xDim,boolean wrapX,Coords1DDouble BC) {
        int disp = 0;
        for (int x = 0; x < xDim; x++) {
            if (vel > 0) {
                deltas[x] += Delta1D(field, field[x], x - 1, xDim, wrapX, BC)*vel;
            } else if (vel < 0) {
                deltas[x] -= Delta1D(field, field[x], x + 1, xDim, wrapX, BC)*vel;
            }
        }
    }

    public static void Advection2(double[]field,double[]deltas,double xVel,double yVel,int xDim,int yDim,boolean wrapX,boolean wrapY,Coords2DDouble BC) {
        int disp = 0;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i = x * yDim + y;
                double centerVal=field[i];
                if (xVel > 0) {
                    deltas[i] += DeltaX2D(field,centerVal, x - 1,y, xDim,yDim, wrapX, BC)*xVel;
                } else if(xVel < 0){
                    deltas[i] -= DeltaX2D(field,centerVal, x + 1,y, xDim,yDim, wrapX, BC)*xVel;
                }
                if (yVel > 0) {
                    deltas[i] += DeltaY2D(field,centerVal, x ,y-1, xDim,yDim, wrapY, BC)*yVel;
                } else if(yVel < 0){
                    deltas[i] -= DeltaY2D(field,centerVal, x ,y-1, xDim,yDim, wrapY, BC)*yVel;
                }
            }
        }
    }

    public static void Advection3(double[]field,double[]deltas,double xVel,double yVel,double zVel,int xDim,int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ,Coords3DDouble BC) {
        int disp = 0;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    int i = x * yDim * zDim + y * zDim + z;
                    double centerVal = field[i];
                    if (xVel > 0) {
                        deltas[i] += DeltaX3D(field, centerVal, x - 1, y,z, xDim, yDim,zDim, wrapX, BC)*xVel;
                    } else if (xVel < 0) {
                        deltas[i] -= DeltaX3D(field, centerVal, x + 1, y,z, xDim, yDim,zDim, wrapX, BC)*xVel;
                    }
                    if (yVel > 0) {
                        deltas[i] += DeltaY3D(field, centerVal, x, y-1,z, xDim, yDim,zDim, wrapY, BC)*yVel;
                    } else if (yVel < 0) {
                        deltas[i] -= DeltaY3D(field, centerVal, x, y+1,z, xDim, yDim,zDim, wrapY, BC)*yVel;
                    }
                    if (zVel > 0) {
                        deltas[i] += DeltaZ3D(field, centerVal, x, y,z-1, xDim, yDim,zDim, wrapZ, BC)*zVel;
                    } else if (zVel < 0) {
                        deltas[i] -= DeltaZ3D(field, centerVal, x, y,z+1, xDim, yDim,zDim, wrapZ, BC)*zVel;
                    }
                }
            }
        }
    }

    public static void Advection1(double[]field,double[]deltas,double[]xVels,int xDim,boolean wrapX,Coords1DDouble BC,Coords1DDouble XvelBC) {
        for (int x = 0; x < xDim; x++) {
            double xFluxp = 0, xFluxm = 0;
            double xVelp = xVels[x];
            //either use the wraparound Vels or the BCs if at the edge of the domain
            if (x == xDim - 1 && !wrapX) {
                xVelp = Displaced1D(xVels, x + 1, xDim, wrapX, XvelBC);
            }
            double xVelm = Displaced1D(xVels, x - 1, xDim, wrapX, XvelBC);
            double centerVal = field[x];
            if (xVelp > 0) {
                xFluxp = xVelp * centerVal;
            } else if (xVelp < 0) {
                xFluxp = xVelp * Displaced1D(field, x + 1, xDim, wrapX, BC);
            }
            if (xVelm > 0) {
                xFluxm = xVelm * Displaced1D(field, x - 1, xDim, wrapX, BC);
            } else if (xVelm < 0) {
                xFluxm = xVelm * centerVal;
            }
            deltas[x] += -xFluxp + xFluxm;
        }
    }

    public static void Advection2(double[]field,double[]deltas,double[]xVels,double[]yVels,int xDim,int yDim,boolean wrapX,boolean wrapY,Coords2DDouble BC,Coords2DDouble XvelBC,Coords2DDouble YvelBC){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                int i=x*yDim+y;
                double xFluxp=0,xFluxm=0,yFluxp=0,yFluxm=0;
                double xVelp=xVels[i];
                double yVelp=yVels[i];
                //either use the wraparound Vels or the BCs if at the edge of the domain
                if(x==xDim-1&&!wrapX){
                    xVelp=DisplacedX2D(xVels,x+1,y,xDim,yDim,wrapX,XvelBC);
                }
                if(y==yDim-1&&!wrapY){
                    yVelp=DisplacedY2D(yVels,x,y+1,xDim,yDim,wrapY,YvelBC);
                }
                double xVelm=DisplacedX2D(xVels,x-1,y,xDim,yDim,wrapX,XvelBC);
                double yVelm=DisplacedY2D(yVels,x,y-1,xDim,yDim,wrapY,YvelBC);
                double centerVal=field[i];
                if(xVelp>0){
                    xFluxp=xVelp*centerVal;
                }
                else if(xVelp<0){
                    xFluxp=xVelp*DisplacedX2D(field,x+1,y,xDim,yDim,wrapX,BC);
                }
                if(xVelm>0){
                    xFluxm=xVelm*DisplacedX2D(field,x-1,y,xDim,yDim,wrapX,BC);
                }
                else if(xVelm<0){
                    xFluxm=xVelm*centerVal;
                }
                if(yVelp>0){
                    yFluxp=yVelp*centerVal;
                }
                else if(yVelp<0){
                    yFluxp=yVelp*DisplacedY2D(field,x,y+1,xDim,yDim,wrapY,BC);
                }
                if(yVelm>0){
                    yFluxm=yVelm*DisplacedY2D(field,x,y-1,xDim,yDim,wrapY,BC);
                }
                else if(yVelm<0){
                    yFluxm=yVelm*centerVal;
                }
                deltas[i]+=-xFluxp+xFluxm-yFluxp+yFluxm;
            }

        }
    }

    public static void Advection3(double[]field,double[]deltas,double[]xVels,double[]yVels,double[]zVels,int xDim,int yDim,int zDim,boolean wrapX,boolean wrapY,boolean wrapZ,Coords3DDouble BC,Coords3DDouble XvelBC,Coords3DDouble YvelBC,Coords3DDouble ZvelBC) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                for (int z = 0; z < zDim; z++) {
                    int i = x * yDim * zDim + y * zDim + z;
                    double xFluxp = 0, xFluxm = 0, yFluxp = 0, yFluxm = 0,zFluxp=0,zFluxm=0;
                    double xVelp = xVels[i];
                    double yVelp = yVels[i];
                    double zVelp = zVels[i];
                    //either use the wraparound Vels or the BCs if at the edge of the domain
                    if (x == xDim - 1 && !wrapX) {
                        xVelp = DisplacedX3D(xVels, x + 1, y, z, xDim, yDim, zDim, wrapX, XvelBC);
                    }
                    if (y == yDim - 1 && !wrapY) {
                        yVelp = DisplacedY3D(yVels, x, y + 1,z, xDim, yDim,zDim, wrapY, YvelBC);
                    }
                    if (z == zDim - 1 && !wrapZ) {
                        zVelp = DisplacedZ3D(zVels, x, y,z+1, xDim, yDim,zDim, wrapX, ZvelBC);
                    }
                    double xVelm = DisplacedX3D(xVels, x - 1, y,z, xDim, yDim,zDim, wrapX, XvelBC);
                    double yVelm = DisplacedY3D(yVels, x, y - 1,z, xDim, yDim,zDim, wrapY, YvelBC);
                    double zVelm = DisplacedY3D(zVels, x, y,z, xDim, yDim,zDim, wrapZ, ZvelBC);
                    double centerVal = field[i];
                    if (xVelp > 0) {
                        xFluxp = xVelp * centerVal;
                    } else if (xVelp < 0) {
                        xFluxp = xVelp * DisplacedX3D(field, x + 1, y,z, xDim, yDim,zDim, wrapX, BC);
                    }
                    if (xVelm > 0) {
                        xFluxm = xVelm * DisplacedX3D(field, x - 1, y,z, xDim, yDim,zDim, wrapX, BC);
                    } else if (xVelm < 0) {
                        xFluxm = xVelm * centerVal;
                    }
                    if (yVelp > 0) {
                        yFluxp = yVelp * centerVal;
                    } else if (yVelp < 0) {
                        yFluxp = yVelp * DisplacedY3D(field, x, y + 1,z, xDim, yDim,zDim, wrapY, BC);
                    }
                    if (yVelm > 0) {
                        yFluxm = yVelm * DisplacedY3D(field, x, y - 1,z, xDim, yDim,zDim, wrapY, BC);
                    } else if (yVelm < 0) {
                        yFluxm = yVelm * centerVal;
                    }
                    if (zVelp > 0) {
                        zFluxp = zVelp * centerVal;
                    } else if (zVelp < 0) {
                        zFluxp = zVelp * DisplacedY3D(field, x, y,z+1, xDim, yDim,zDim, wrapZ, BC);
                    }
                    if (zVelm > 0) {
                        zFluxm = zVelm * DisplacedY3D(field, x, y,z-1, xDim, yDim,zDim, wrapZ, BC);
                    } else if (zVelm < 0) {
                        zFluxm = zVelm * centerVal;
                    }
                    deltas[i] += -xFluxp + xFluxm - yFluxp + yFluxm -zFluxp + zFluxm;
                }
            }
        }
    }

    public static void TDMA(final double[] in,final double[] out, final double[] scratch, final double diffRate){

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
    public static void TDMAx(final double[] in, final double[] out,final double[]orig,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate) {
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
    public static void TDMAy(final double[] in, final double[] out,final double[]orig,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate) {
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


    public static void TDMAx(final double[] in, final double[] out,final double[]orig,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate,double boundaryCond) {
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
    public static void TDMAy(final double[] in, final double[] out,final double[]orig,final double[] scratch,final int xDim, final int yDim, final int iRow, final double diffRate,double boundaryCond) {
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

    public static double Displaced1D(double[] vals,int x, int xDim, boolean wrapX, Coords1DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenDouble(x);
        }
        return 0;//default
    }
    public static double DisplacedX2D(double[] vals,int x,int y, int xDim,int yDim, boolean wrapX, Coords2DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim+y];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim+y];
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y));//Derichlet boundary conds.
        }
        return 0;
    }
    public static double DisplacedY2D(double[] vals,int x,int y, int xDim,int yDim, boolean wrapY, Coords2DDouble BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim+y];
        }
        else if(wrapY){
            return vals[x*yDim+ Wrap(y,yDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenDouble(x,y);
        }
        return 0;
    }
    public static double DisplacedX3D(double[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapX, Coords3DDouble BoundaryCond) {
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim*zDim+y*zDim+z];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenDouble(x,y,z);
        }
        return 0;
    }
    public static double DisplacedY3D(double[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapY, Coords3DDouble BoundaryCond) {
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapY){
            return vals[x*yDim*zDim+Wrap(y,yDim)*zDim+z];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenDouble(x,y,z);
        }
        return 0;//Zero flux
    }
    public static double DisplacedZ3D(double[] vals,int x,int y,int z, int xDim,int yDim,int zDim, boolean wrapZ, Coords3DDouble BoundaryCond) {
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapZ){
            return vals[x*yDim*zDim+y*zDim+Wrap(z,zDim)];
        }
        else if(BoundaryCond!=null){
            return BoundaryCond.GenDouble(x,y,z);//Dirichlet boundary conds.
        }
        return 0;//Zero flux
    }

    public static double Delta1D(double[]vals,double centerVal,int x,int xDim,boolean wrapX,Coords1DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x)-centerVal)*2;//Dirichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaX2D(double[]vals,double centerVal,int x,int y,int xDim,int yDim,boolean wrapX,Coords2DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim+y]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaY2D(double[]vals,double centerVal,int x,int y,int xDim,int yDim,boolean wrapY,Coords2DDouble BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim+ Wrap(y,yDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaX3D(double[]vals,double centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapX,Coords3DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaY3D(double[]vals,double centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapY,Coords3DDouble BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim*zDim+Wrap(y,yDim)*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaZ3D(double[]vals,double centerVal,int x,int y,int z,int xDim,int yDim,int zDim,boolean wrapZ,Coords3DDouble BoundaryCond){
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapZ){
            return vals[x*yDim*zDim+y*zDim+Wrap(z,zDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            return (BoundaryCond.GenDouble(x,y,z)-centerVal)*2;//Derichlet boundary conds.
        }
        return 0;//Zero flux
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
    /*
    public static void Diffusion3Old(final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final int zDim, final double
            diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX, final boolean wrapY, final boolean wrapZ) {
        int x, y, z;
        double valSum;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                for (z = 0; z < zDim; z++) {
                    //6 squares to check
                    valSum = 0;
                    if (InDim(x + 1, xDim)) {
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(0) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum+=inGrid[(x-1)*yDim*zDim+(y)*zDim+(z)];
                    }

                    if (InDim(x - 1, xDim)) {
                        valSum += inGrid[(x - 1) * yDim * zDim + (y) * zDim + (z)];
                    }  else if (boundaryCond) {
                        valSum += boundaryValue;
                    }else if (wrapX) {
                        valSum += inGrid[(xDim - 1) * yDim * zDim + (y) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x + 1) * yDim * zDim + (y) * zDim + (z)];
                    }

                    if (InDim(y + 1, yDim)) {
                        valSum += inGrid[(x) * yDim * zDim + (y + 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (0) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y-1) * zDim + (z)];
                    }

                    if (InDim(y - 1, yDim)) {
                        valSum += inGrid[(x) * yDim * zDim + (y - 1) * zDim + (z)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapY){
                        valSum += inGrid[(x) * yDim * zDim + (yDim-1) * zDim + (z)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y+1) * zDim + (z)];
                    }

                    if (InDim(z + 1, zDim)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (0)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    }

                    if (InDim(z - 1, zDim)) {
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z-1)];
                    } else if (boundaryCond) {
                        valSum += boundaryValue;
                    } else if(wrapZ){
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (zDim-1)];
                    } else{
                        valSum += inGrid[(x) * yDim * zDim + (y) * zDim + (z+1)];
                    }
                    int i = x * yDim * zDim + y * zDim + z;
                    outGrid[i] += diffRate * (-inGrid[i] * 6 + valSum);
                }
            }
        }
    }
    */
    /*
    public static void Diffusion2(int x,int y,final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final double diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX,final boolean wrapY) {
        //4 squares to check

        double valSum= DisplacedX2D(x+1,y,inGrid,xDim,yDim,x,boundaryCond,boundaryValue,wrapX);
        valSum+= DisplacedX2D(x-1,y,inGrid,xDim,yDim,x,boundaryCond,boundaryValue,wrapX);
        valSum+= DisplacedY2D(x,y+1,inGrid,xDim,yDim,y,boundaryCond,boundaryValue,wrapY);
        valSum+= DisplacedY2D(x,y-1,inGrid,xDim,yDim,y,boundaryCond,boundaryValue,wrapY);
        int i=x*yDim+y;
        outGrid[i]+=diffRate*(-inGrid[i]*4+valSum);
    }
    */
    /*
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
        currRate = DisplacedY2D(x, y+1, diffRates, xDim, yDim, y - 1, false, boundaryValue, wrapY);
        valSum += DisplacedY2D(x, y+1, inGrid, xDim, yDim, y - 1, boundaryCond, boundaryValue, wrapY) * currRate;
        rateSum += currRate;
        currRate = DisplacedY2D(x, y-1, diffRates, xDim, yDim, y + 1, false, boundaryValue, wrapY);
        valSum += DisplacedY2D(x, y-1, inGrid, xDim, yDim, y + 1, boundaryCond, boundaryValue, wrapY) * currRate;
        rateSum += currRate;
        outGrid[i] += (-inGrid[i] * rateSum + valSum) / 2;
    }
    public static void Diffusion1inhomogeneous(int x,final double[] inGrid, final double[] outGrid, final double[] diffRates, final int xDim, final boolean boundaryCond, final double boundaryValue, final boolean wrapX) {
        double valSum;
        double rateSum;
        double currRate;

        //4 squares to check
        valSum = 0;
        rateSum = 0;
        double diffRate = diffRates[x];
        if(diffRate>0.5){
            throw new IllegalArgumentException("Diffusion rate above stable maximum value of 0.5 value: "+diffRate+" x: "+x);
        }

        currRate = Displaced1D(x + 1, diffRates, xDim,  x - 1, false, boundaryValue, wrapX);
        valSum += Displaced1D(x + 1, inGrid, xDim, x - 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        currRate = Displaced1D(x - 1, diffRates, xDim, x + 1, false, boundaryValue, wrapX);
        valSum += Displaced1D(x - 1, inGrid, xDim, x + 1, boundaryCond, boundaryValue, wrapX) * currRate;
        rateSum += currRate;
        outGrid[x] += (-inGrid[x] * rateSum + valSum) / 2;
    }
    public static void Diffusion2Boundary(int x,int y,final double[] inGrid, final double[] outGrid, final int xDim, final int yDim, final double diffRate, Coords2DDouble BoundaryGen, final boolean wrapX,final boolean wrapY) {
        double valSum= DisplacedX2DFn(x+1,y,inGrid,xDim,yDim,BoundaryGen,wrapX);
        valSum+= DisplacedX2DFn(x-1,y,inGrid,xDim,yDim,BoundaryGen,wrapX);
        valSum+= DisplacedY2DFn(x,y+1,inGrid,xDim,yDim,BoundaryGen,wrapY);
        valSum+= DisplacedY2DFn(x,y-1,inGrid,xDim,yDim,BoundaryGen,wrapY);
        int i=x*yDim+y;
        outGrid[i]+=diffRate*(-inGrid[i]*4+valSum);
    }
    public static void Diffusion1(int x,final double[] inGrid, final double[] outGrid, final int xDim, final double diffRate, final boolean boundaryCond, final double boundaryValue, final boolean wrapX) {
        //4 squares to check

        double valSum= Displaced1D(x+1,inGrid,xDim,x,boundaryCond,boundaryValue,wrapX);
        valSum+= Displaced1D(x-1,inGrid,xDim,x,boundaryCond,boundaryValue,wrapX);
        outGrid[x]+=diffRate*(-inGrid[x]*2+valSum);
    }
    */
    /*
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
        outGrid[i]+=-xFlux-yFlux;
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
        outGrid[i]+=-xFlux-yFlux-zFlux;
    }
    */

    /*
    public static void ConservativeTransportStep2(final double[] inGrid,final double[] midGrid, final double[] outGrid, final int xDim, final int yDim,final double[] xVels,final double[] yVels, final boolean boundaryCond, final double boundaryValue){
        int x,y;
        double vXp1,vXm1,vYp1,vYm1,dVx,dVy;
        for (x = 0; x < xDim; x++) {
            for (y = 0; y < yDim; y++) {
                int i=x*yDim+y;
                if(InDim(x+1, xDim)){
                    vXp1=xVels[(x+1)*yDim+y];
                }
                else if(boundaryCond){
                    vXp1=boundaryValue;
                }
                else{
                    vXp1=xVels[(0)*yDim+y];
                }
                if(InDim(x-1, xDim)){
                    vXm1=xVels[(x-1)*yDim+y];
                }
                else if(boundaryCond){
                    vXm1=boundaryValue;
                }
                else{
                    vXm1=xVels[(xDim-1)*yDim+y];
                }

                if(InDim(y+1, yDim)){
                    vYp1=yVels[x*yDim+(y+1)];
                }
                else if(boundaryCond){
                    vYp1=boundaryValue;
                }
                else{
                    vYp1=yVels[x*yDim+(0)];
                }
                if(InDim(y-1, yDim)){
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
    public static double Displaced1D(int x, double[] vals, int xDim, int fallbackX, boolean boundaryCond, double boundaryValue, boolean wrapX){
        boolean inX=InDim(x, xDim);
        if(inX){
            return vals[x];
        }
        else if(!inX&&wrapX){
            x= Wrap(x,xDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            x=fallbackX;
        }
        return vals[x];
    }

    public static double DisplacedX2DFn(int x, int y, double[] vals, int xDim, int yDim, Coords2DDouble Fallback, boolean wrapX){
        if(InDim(x, xDim)){
            return vals[x*yDim+y];
        }
        else if(wrapX){
            x= Wrap(x,xDim);
        }
        else{
            return Fallback.GenDouble(x,y);
        }
        return vals[x*yDim+y];
    }

    public static double DisplacedY2DFn(int x, int y, double[] vals, int xDim, int yDim, Coords2DDouble Fallback, boolean wrapY){
        if(InDim(y, yDim)){
            return vals[x*yDim+y];
        }
        if(wrapY){
            y= Wrap(y,yDim);
        }
        else{
            return Fallback.GenDouble(x,y);
        }
        return vals[x*yDim+y];
    }

    public static double DisplacedX2D(int x, int y, double[] vals, int xDim, int yDim, int fallbackX, boolean boundaryCond, double boundaryValue, boolean wrapX){
        if(InDim(x, xDim)){
            return vals[x*yDim+y];
        }
        else if(wrapX){
            x= Wrap(x,xDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            x=fallbackX;
        }
        return vals[x*yDim+y];
    }

    public static double DisplacedY2D(int x, int y, double[] vals, int xDim, int yDim, int fallbackY, boolean boundaryCond, double boundaryValue, boolean wrapY){
        if(InDim(y, yDim)){
            return vals[x*yDim+y];
        }
        else if(wrapY){
            y= Wrap(y,yDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            y=fallbackY;
        }
        return vals[x*yDim+y];
    }
    public static double DisplacedX3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackX, boolean boundaryCond, double boundaryValue, boolean wrapX){
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapX){
            x= Wrap(x,xDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            x=fallbackX;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }
    public static double DisplacedY3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackY, boolean boundaryCond, double boundaryValue, boolean wrapY){
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapY){
            y= Wrap(y,yDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            y=fallbackY;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }
    public static double DisplacedZ3D(int x, int y, int z, double[] vals, int xDim, int yDim, int zDim, int fallbackZ, boolean boundaryCond, double boundaryValue, boolean wrapZ){
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z];
        }
        else if(wrapZ){
            z= Wrap(z,zDim);
        }
        else if(boundaryCond){
            return boundaryValue;
        }
        else{
            z=fallbackZ;
        }
        return vals[x*yDim*zDim+y*zDim+z];
    }
    public static void Advection1stOrder1D(int x, final double[] inGrid, final double[] outGrid, int xDim, double xVel, boolean boundaryCond, double boundaryValue){
        double xFlux=0;
        double yFlux=0;
        double prev=inGrid[x];
        if(xVel>0){
            xFlux=xVel*(prev- Displaced1D(x-1,inGrid,xDim,-1,boundaryCond,boundaryValue,true));
        }
        if(xVel<0){
            xFlux=xVel*(Displaced1D(x+1,inGrid,xDim,-1,boundaryCond,boundaryValue,true)-prev);
        }
        outGrid[x]+=-xFlux-yFlux;
    }
    */
