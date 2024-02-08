package HAL.GridsAndAgents;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Interfaces.Coords2DDouble;
import HAL.Interfaces.DoubleToDouble;
import HAL.Interfaces.Grid2D;
import HAL.Tools.Lenia.FFTGrid;

public class KernelGrid implements Grid2D {
    private final FFTGrid fftKernel;
    private final FFTGrid fftField;
    public final Grid2Ddouble out;
    public final int xDim,yDim,length;
    //Kernel must hav the same dimensions as the domain that will be convolved with it!
    public KernelGrid(Grid2Ddouble kernel) {
        if (kernel.xDim != kernel.yDim || (kernel.xDim & kernel.xDim - 1) != 0) {
            throw new IllegalArgumentException("Kernel must have equal xDim and yDim, and xDim, yDim must be powers of 2");
        }
        this.fftKernel=new FFTGrid(kernel.xDim,kernel.yDim);
        this.fftField=new FFTGrid(kernel.xDim,kernel.yDim);
        this.xDim=kernel.xDim;
        this.yDim=kernel.yDim;
        this.length=kernel.length;
        fftKernel.SetGrid(kernel);
        fftKernel.fftshift();
        fftKernel.fft2();
        this.out = fftField.REAL;
    }
    private static Grid2Ddouble GenKernel(DoubleToDouble KernelGen, int sideLen,boolean sumToOne) {
        Grid2Ddouble out = new Grid2Ddouble(sideLen, sideLen);
        for (int x = 0; x < out.xDim; x++) {
            for (int y = 0; y < out.yDim; y++) {
                out.Set(x, y, KernelGen.Eval(out.Dist(x, y, out.xDim / 2.0, out.yDim / 2.0)));
            }
        }
        if (sumToOne) {
            double sum = 0;
            for (int i = 0; i <out.length; i++) {
                sum+=out.Get(i);
            }
            for (int i = 0; i < out.length; i++) {
                out.Set(i,out.Get(i)/sum);
            }
        }
        return out;
    }

    public KernelGrid(DoubleToDouble KernelGen,int sideLen,boolean sumToOne){
        this(GenKernel(KernelGen,sideLen,sumToOne));
    }
    public void Convolve(Grid2Ddouble field){
        fftField.SetGrid(field);
        fftField.fft2();
        fftField.ComplexMultiplication(fftKernel);
        fftField.ifft2();
    }

    public void Convolve(Coords2DDouble GetFieldValue){
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                fftField.IMAG.Set(x,y,0);
                fftField.REAL.Set(x,y,GetFieldValue.GenDouble(x,y));
                fftField.fft2();
                fftField.ComplexMultiplication(fftKernel);
                fftField.ifft2();

            }
        }
    }
    public double Get(int i){
        return out.Get(i);
    }
    public double Get(int x,int y){
        return out.Get(x,y);
    }
    public double GetKernel(int i){return fftKernel.REAL.Get(i);}
    public double GetKernel(int x,int y){return fftKernel.REAL.Get(x,y);}

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
}
