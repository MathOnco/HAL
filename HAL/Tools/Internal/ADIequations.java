package HAL.Tools.Internal;

import HAL.Interfaces.*;
import HAL.Tools.TdmaSolver;

import static HAL.Util.InDim;
import static HAL.Util.Wrap;

public class ADIequations {

    /*3D changes:
    diffScaleFactor=3
    scale lapacian by D/3 rather than D/2
    keep multiplying extra boundary term by 2
    */
    public static void Diffusion3DADI(double[]field, double[]scratch1, double[]scratch2, double[]deltas, double diffRate, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DDouble BC, TdmaSolver tdma){
        for (int y = 0; y < yDim; y++) {//do the x rows
            for (int z = 0; z < zDim; z++) {
                int finalY = y;
                int finalZ = z;
                ADISolveRow(xDim, diffRate, 3, wrapX, BC != null, tdma,
                        (i) -> ExplicitDiffusionYZ3ADI(i, finalY, finalZ, field, diffRate, xDim, yDim, zDim, wrapX, wrapY, wrapZ, BC),
                        (i, v) -> scratch1[i * yDim * zDim + finalY * zDim + finalZ] = v);
            }
        }
        for (int x = 0; x < xDim; x++) {//do the y rows
            for (int z = 0; z < zDim; z++) {
                int finalX = x;
                int finalZ = z;
                ADISolveRow(yDim, diffRate, 3, wrapY, BC != null, tdma,
                        (i) -> ExplicitDiffusionXZ3ADI(finalX, i, finalZ, scratch1, diffRate, xDim, yDim, zDim, wrapX, wrapY, wrapZ, BC),
                        (i, v) -> scratch2[finalX * yDim * zDim + i * zDim + finalZ] = v);
            }
        }
        for (int x = 0; x < xDim; x++) {//do the z rows
            for (int y = 0; y < yDim; y++) {
                int finalX = x;
                int finalY = y;
                ADISolveRow(zDim, diffRate, 3, wrapZ, BC != null, tdma,
                        (i) -> ExplicitDiffusionXY3ADI(finalX, finalY, i, scratch2, diffRate, xDim, yDim, zDim, wrapX, wrapY, wrapZ, BC),
                        (i, v) -> {
                            int index = finalX * yDim * zDim + finalY * zDim + i;
                            deltas[index]+=v-field[index];
                        });
            }
        }
    }

    public static void ADISolveRow(int lenToSolve, double diffRate, double diffScaleFactor, boolean wrapDim, boolean BC, TdmaSolver tdma, IntToDouble GetIn, IntDoubleToVoid SetOut){
        if(wrapDim){//wrap around
            tdma.TDMAperiodic(lenToSolve,
                    GetIn,
                    SetOut,
                    (i) ->  1 + diffRate*2/diffScaleFactor,
                    (i) -> -diffRate / diffScaleFactor,
                    (i) -> -diffRate / diffScaleFactor);
        }
        else {
            IntToDouble GetB;
            if (!BC) {//zero flux
                GetB = (i) -> {
                    if (i == 0 || i == lenToSolve - 1) {
                        return 1 + diffRate / diffScaleFactor;
                    }
                    return 1 + diffRate*2/diffScaleFactor;
                };
            } else {//dirichlet
                GetB = (i) -> {
                    if (i == 0 || i == lenToSolve - 1) {
                        return 1 + diffRate * 3 / diffScaleFactor;
                    }
                    return 1 + diffRate*2/diffScaleFactor;
                };
            }
            tdma.TDMA(lenToSolve,
                    GetIn,
                    SetOut,
                    GetB,
                    (i) -> -diffRate / diffScaleFactor,
                    (i) -> -diffRate / diffScaleFactor);
        }
    }
    public static double DeltaX3DADI(double[]vals, double centerVal, int x, int y, int z, int xDim, int yDim, int zDim, boolean wrapX, Coords3DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x,y,z);
            return (bc-centerVal)*2+2*bc;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaY3DADI(double[]vals, double centerVal, int x, int y, int z, int xDim, int yDim, int zDim, boolean wrapY, Coords3DDouble BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim*zDim+Wrap(y,yDim)*zDim+z]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x,y,z);
            return (bc-centerVal)*2+2*bc;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaZ3DADI(double[]vals, double centerVal, int x, int y, int z, int xDim, int yDim, int zDim, boolean wrapZ, Coords3DDouble BoundaryCond){
        if(InDim(z, zDim)){
            return vals[x*yDim*zDim+y*zDim+z]-centerVal;
        }
        else if(wrapZ){
            return vals[x*yDim*zDim+y*zDim+Wrap(z,zDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x,y,z);
            return (bc-centerVal)*2+2*bc;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }

   //2D ADI
    public static void Diffusion2DADI(double[]field, double[]scratch, double[]deltas, double diffRate, int xDim, int yDim, boolean wrapX, boolean wrapY, Coords2DDouble BC, TdmaSolver tdma){
        for (int y = 0; y < yDim; y++) {//do the x rows
            int finalY = y;
            ADISolveRow(xDim,diffRate,2,wrapX,BC!=null,tdma,
                    (i)->ExplicitDiffusionY2ADI(i,finalY,field,diffRate,xDim,yDim,wrapX,wrapY,BC),
                    (i,v)->scratch[i*yDim+finalY]=v);
        }
        for (int x = 0; x < xDim; x++) {//do the y columns
            int finalX = x;
            ADISolveRow(yDim,diffRate,2,wrapY,BC!=null,tdma,
                    (i)->ExplicitDiffusionX2ADI(finalX,i,scratch,diffRate,xDim,yDim,wrapX,wrapY,BC),
                    (i,v)->{
                        int index=finalX*yDim+i;
                        deltas[index]+=v-field[index];
                    });
        }
    }
    public static double ExplicitDiffusionYZ3ADI(int x, int y, int z, double[]field, double diffRate, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DDouble BC) {
        int i = x * yDim * zDim + y * zDim + z;
        double centerVal = field[i];
        double deltaSum = DeltaY3DADI(field, centerVal, x, y + 1, z, xDim, yDim, zDim, wrapY, BC);
        deltaSum += DeltaY3DADI(field, centerVal, x, y - 1, z, xDim, yDim, zDim, wrapY, BC);
        deltaSum += DeltaZ3DADI(field, centerVal, x, y, z + 1, xDim, yDim, zDim, wrapZ, BC);
        deltaSum += DeltaZ3DADI(field, centerVal, x, y, z - 1, xDim, yDim, zDim, wrapZ, BC);
        return centerVal+(deltaSum * diffRate)/3;
    }
    public static double ExplicitDiffusionXZ3ADI(int x, int y, int z, double[]field, double diffRate, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DDouble BC) {
        int i = x * yDim * zDim + y * zDim + z;
        double centerVal = field[i];
        double deltaSum = DeltaX3DADI(field, centerVal, x + 1, y, z, xDim, yDim, zDim, wrapX, BC);
        deltaSum += DeltaX3DADI(field, centerVal, x - 1, y, z, xDim, yDim, zDim, wrapX, BC);
        deltaSum += DeltaZ3DADI(field, centerVal, x, y, z + 1, xDim, yDim, zDim, wrapZ, BC);
        deltaSum += DeltaZ3DADI(field, centerVal, x, y, z - 1, xDim, yDim, zDim, wrapZ, BC);
        return centerVal+(deltaSum * diffRate)/3;
    }
    public static double ExplicitDiffusionXY3ADI(int x, int y, int z, double[]field, double diffRate, int xDim, int yDim, int zDim, boolean wrapX, boolean wrapY, boolean wrapZ, Coords3DDouble BC) {
        int i = x * yDim * zDim + y * zDim + z;
        double centerVal = field[i];
        double deltaSum = DeltaX3DADI(field, centerVal, x + 1, y, z, xDim, yDim, zDim, wrapX, BC);
        deltaSum += DeltaX3DADI(field, centerVal, x - 1, y, z, xDim, yDim, zDim, wrapX, BC);
        deltaSum += DeltaY3DADI(field, centerVal, x, y + 1, z, xDim, yDim, zDim, wrapY, BC);
        deltaSum += DeltaY3DADI(field, centerVal, x, y - 1, z, xDim, yDim, zDim, wrapY, BC);
        return centerVal+(deltaSum * diffRate)/3;
    }
    public static double ExplicitDiffusionX2ADI(int x, int y, double[]field, double diffRate, int xDim, int yDim, boolean wrapX,boolean wrapY, Coords2DDouble BC) {
        int i = x * yDim + y;
        double centerVal = field[i];
        double deltaSum = DeltaX2DADI(field, centerVal, x + 1, y, xDim, yDim, wrapX, BC);
        deltaSum += DeltaX2DADI(field, centerVal, x - 1, y, xDim, yDim, wrapX, BC);
        return centerVal +(deltaSum * diffRate)/2;
    }
    public static double ExplicitDiffusionY2ADI(int x, int y, double[]field, double diffRate, int xDim, int yDim,boolean wrapX, boolean wrapY, Coords2DDouble BC) {
        int i = x * yDim + y;
        double centerVal = field[i];
        double deltaSum = DeltaY2DADI(field, centerVal, x, y + 1, xDim, yDim, wrapY, BC);
        deltaSum += DeltaY2DADI(field, centerVal, x, y - 1, xDim, yDim, wrapY, BC);
        return centerVal +(deltaSum * diffRate)/2;
    }

    public static double DeltaX2DADI(double[]vals, double centerVal, int x, int y, int xDim, int yDim, boolean wrapX, Coords2DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)*yDim+y]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x,y);
            return (bc-centerVal)*2+2*bc;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }
    public static double DeltaY2DADI(double[]vals, double centerVal, int x, int y, int xDim, int yDim, boolean wrapY, Coords2DDouble BoundaryCond){
        if(InDim(y, yDim)){
            return vals[x*yDim+y]-centerVal;
        }
        else if(wrapY){
            return vals[x*yDim+ Wrap(y,yDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x,y);
            return (bc-centerVal)*2+2*bc;//Derichlet boundary conds.
        }
        return 0;//Zero flux
    }

    //1D Crank-Nicholson
    public static void Diffusion1ADI(double[]field, double[]deltas, double diffRate, int xDim, boolean wrapX, Coords1DDouble BC, TdmaSolver tdma){
        ADISolveRow(xDim,diffRate,2,wrapX,BC!=null,tdma,
                (i)->ExplicitDiffusion1ADI(i, field, diffRate, xDim, wrapX, BC),
                (i,v)->deltas[i]+=v-field[i]);
//        if(wrapX){//wrap around
//            tdma.TDMAperiodic(xDim,
//                    (i) -> ExplicitDiffusion1ADI(i, field, diffRate, xDim, wrapX, BC),
//                    (i, d) -> deltas[i] += d - field[i],
//                    (i) ->  1 + diffRate,
//                    (i) -> -diffRate / 2,
//                    (i) -> -diffRate / 2);
//        }
//        else {
//            IntToDouble GetB;
//            if (BC == null) {//zero flux
//                GetB = (i) -> {
//                    if (i == 0 || i == xDim - 1) {
//                        return 1 + diffRate / 2;
//                    }
//                    return 1 + diffRate;
//                };
//            } else {//dirichlet/neumann
//                GetB = (i) -> {
//                    if (i == 0 || i == xDim - 1) {
//                        return 1 + diffRate * 3 / 2;
//                    }
//                    return 1 + diffRate;
//                };
//            }
//            tdma.TDMA(xDim,
//                    (i) -> ExplicitDiffusion1ADI(i, field, diffRate, xDim, wrapX, BC),
//                    (i, d) -> deltas[i] += d - field[i],
//                    GetB,
//                    (i) -> -diffRate / 2,
//                    (i) -> -diffRate / 2);
//        }

    }
    public static double ExplicitDiffusion1ADI(int x, double[]field, double diffRate, int xDim, boolean wrapX, Coords1DDouble BC){
        double centerVal = field[x];
        double valSum = Delta1DADI(field, centerVal, x + 1, xDim, wrapX, BC);
        valSum += Delta1DADI(field, centerVal, x - 1, xDim, wrapX, BC);
        return centerVal + (valSum * diffRate)/2;
    }
    public static double Delta1DADI(double[]vals, double centerVal, int x, int xDim, boolean wrapX, Coords1DDouble BoundaryCond){
        if(InDim(x, xDim)){
            return vals[x]-centerVal;
        }
        else if(wrapX){
            return vals[Wrap(x,xDim)]-centerVal;
        }
        else if(BoundaryCond!=null){
            double bc=BoundaryCond.GenDouble(x);
            return (bc-centerVal)*2+2*bc;//Dirichlet boundary conds.
        }
        return 0;//Zero flux
    }
}
