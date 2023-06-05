package HAL.Tools.Lenia;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Interfaces.DoubleToDouble;
import HAL.Interfaces.Grid2D;

import java.io.Serializable;

import static HAL.Util.*;

/*
TODO:
- simplify Lenia1Player by removing commented code and renaming variables for clarity
- make LeniaNPlayer that stores keeps lenia objects in arrays along with Kijs and Gs
    - at init:
        -calculate fKs for each Kij
        -check if any fKs are identical, if so calculate only once for each group

    - at each step:
        - calculate fAis for all Ais
        - for each fAi:
            - calculate yij=f-1(fAi fKij) for each fKij in all j
            - Gi(yij for all j) to get the new Ai
            - Ai+1=Gi*dt+Ai
            - clip Ai+1 to between 0 and 1 everywhere

- make overarching class that handles either case internally
 */

public class Lenia1Player implements Grid2D, Serializable {

    public final double deltaT; //  deltaT = 1/T
    public final Grid2Ddouble field; // world state, A [n,n]
    private DoubleToDouble Growth;
//    private final Grid2Ddouble kernelField; // kernal, K [n,n]
    private Grid2Ddouble kernelField;
    private DoubleToDouble Kernel;
    private FFTGrid fftKernel; // FFT of K (never changes)
    private final FFTGrid fftField; // FFT of A (changes every time step)
    private int tick;
    private double kSum;

    // for convenience:
    public final int xDim, yDim, length;

    // constructor from parameters (sidelength = 2^P)
    public Lenia1Player(int sideLenExp, double deltaT, DoubleToDouble Kernel, DoubleToDouble Growth) {
        int sideLen = (int) Math.pow(2, sideLenExp);
        this.Kernel = Kernel;
        this.Growth = Growth;
        xDim = sideLen;
        yDim = sideLen;
        length = xDim * yDim;
        this.deltaT = deltaT;
        field = new Grid2Ddouble(xDim, yDim,true,true);
        fftField = new FFTGrid(xDim,yDim);
        kernelField = new Grid2Ddouble(xDim, yDim);
        fftKernel = new FFTGrid(xDim,yDim);
        RecalcKernel();
    }
    public void SetKernelFunction(DoubleToDouble Kernel){
        this.Kernel=Kernel;
        RecalcKernel();
    }
    public void SetGrowthFunction(DoubleToDouble Growth){
        this.Growth=Growth;
    }
    public void RecalcKernel(){
        kSum=0;
        for (int row = 0; row < xDim; row++) {
            for (int col = 0; col < yDim; col++) {
                // focal cell is xDim/2, yDim/2
                double rxy = Dist(row, col, xDim / 2.0, yDim / 2.0);
                double val = this.Kernel.Eval(rxy);
                kSum += val;
                kernelField.Set(row, col, val);
            }
        }

        // normalize K, then shift, then FFT2d
        for (int i = 0; i < length; i++) {
            kernelField.Set(i, kernelField.Get(i) / kSum);
        }
        fftKernel.SetGrid(kernelField);
        fftKernel.fftshift();
        fftKernel.fft2();
    }
    public void WriteGrowthField(Grid2Ddouble scratch){
        for (int i = 0; i < field.length; i++) {
            double delta=Bound(field.Get(i) + this.Growth.Eval(fftField.REAL.Get(i)) * deltaT, 0.0, 1.0)-field.Get(i);
            scratch.Set(i,delta);
        }
    }


    public void Update() {
        //one fA per population per timestep (N)
        fftField.SetGrid(field);

        this.fftField.fft2();
        this.fftField.ComplexMultiplication(this.fftKernel);
        this.fftField.ifft2();

        for (int i = 0; i < field.length; i++) {
            field.Set(i, Bound(field.Get(i) + this.Growth.Eval(fftField.REAL.Get(i)) * deltaT, 0.0, 1.0));
            // after A is set, reset fA:
        }
        this.tick++;

    }
    public double GetConvolvedFieldVal(int i){
        return fftField.REAL.Get(i);
    }
    public double GetConvolvedFieldVal(int x, int y){
        return fftField.REAL.Get(x,y);
    }
    public Grid2Ddouble GetField(){
        return field;
    }

    //used for visualization
    public double GetKernelVal(int i) {
        return kernelField.Get(i)*kSum;
    }
    public double GetKernelVal(int x, int y) {
        return kernelField.Get(x,y)*kSum;
    }

    public void Set(int i, double val) {
        this.field.Set(i, val);
    }

    public void Set(int x, int y, double val) {
        this.field.Set(x, y, val);
    }

    public double Get(int i) {
        return this.field.Get(i);
    }

    public double Get(int x, int y) {
        return this.field.Get(x, y);
    }

    public int GetTick() {
        return tick;
    }

    public void ResetTick() {
        this.tick = 0;
    }

    @Override
    public int Xdim() {
        return xDim;
    }

    @Override
    public int Ydim() {
        return yDim;
    }

    @Override
    public int Length() {
        return length;
    }

    @Override
    public boolean IsWrapX() {
        return true;
    }

    @Override
    public boolean IsWrapY() {
        return true;
    }
    private KernelInfo SaveKernelInfo(){
        return new KernelInfo(Kernel,kernelField,kSum,fftKernel);
    }
    private void LoadKernelInfo(KernelInfo from){
        this.Kernel=from.Kernel;
        this.kernelField=from.kernelField;
        this.kSum=from.kSum;
        this.fftKernel=from.fftKernels;
    }
    private class KernelInfo {
        public final DoubleToDouble Kernel;
        public final Grid2Ddouble kernelField;
        public final double kSum;
        public final FFTGrid fftKernels;
        KernelInfo(DoubleToDouble Kernel,Grid2Ddouble kernelField, double kSum, FFTGrid fftKernel){
            this.Kernel=Kernel;
            this.kernelField = kernelField;
            this.kSum = kSum;
            this.fftKernels = fftKernel;
        }
    }

}

