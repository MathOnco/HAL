package HAL.Tools.Lenia;
import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Interfaces.Grid2D;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import static HAL.Util.Bound;
public class LeniaNPlayer implements Grid2D, Serializable, Iterable<Grid2Ddouble> {
    public final int nPlayers;
    public final double deltaT;
    private Coords1DDoubleArrToDouble Growth;
    private final Grid2Ddouble[] fields;
    private final FFTGrid[][]fftFields;
    private final double[] growthScratch;
    private int tick;
    public final int xDim,yDim,length;
    private HashMap<String, KernelInfo>kernelStorage=new HashMap<>();
    private Coords2DDoubleToDouble Kernel;
    private Grid2Ddouble[][]kernelFields;
    private double[][]kSums;
    private int[][]fftFieldRefs;
    private FFTGrid[][]fftKernels;
    public LeniaNPlayer(int sideLenExp, int nPlayers, double deltaT, Coords2DDoubleToDouble Kernel, Coords1DDoubleArrToDouble Growth) {
        int sideLen = (int) Math.pow(2, sideLenExp);
        this.xDim = sideLen;
        this.yDim = sideLen;
        this.length = xDim * yDim;
        this.nPlayers = nPlayers;
        this.deltaT = deltaT;
        this.Kernel = Kernel;
        this.Growth = Growth;
        this.fields = new Grid2Ddouble[nPlayers];
        this.fftFields = new FFTGrid[nPlayers][nPlayers];
        this.fftFieldRefs=new int[nPlayers][nPlayers];
        this.fftKernels = new FFTGrid[nPlayers][nPlayers];
        this.kernelFields = new Grid2Ddouble[nPlayers][nPlayers];
        this.kSums = new double[nPlayers][nPlayers];
        this.growthScratch = new double[nPlayers];
        for (int i = 0; i < nPlayers; i++) {
            this.fields[i] = new Grid2Ddouble(sideLen, sideLen,true,true);
//            this.growthScratch[i] = new Grid2Ddouble(sideLen, sideLen,true,true);
        }
        for (int i = 0; i < nPlayers; i++) {
            for (int j = 0; j < nPlayers; j++) {
                fftFields[i][j] = new FFTGrid(fields[i]);
                fftKernels[i][j] = new FFTGrid(xDim, yDim);
                kernelFields[i][j] = new Grid2Ddouble(sideLen, sideLen);
            }
        }
        RecalcKernels();
    }
    public void StoreKernel(String label){
        this.kernelStorage.put(label, SaveKernelInfo());
    }
    public void LoadKernel(String label){
        LoadKernelInfo(this.kernelStorage.get(label));
    }
    public void SetKernelFunction(Coords2DDoubleToDouble Kernel){
        this.Kernel=Kernel;
        RecalcKernels();
    }
    public void SetGrowthFunction(Coords1DDoubleArrToDouble Growth){
        this.Growth=Growth;
    }
    private void FindKernelDuplicates(){
        for (int j = 0; j < nPlayers; j++) {
            for (int i = 0; i < nPlayers; i++) {
                Grid2Ddouble g1Real=fftKernels[i][j].REAL;
                Grid2Ddouble g1Imag=fftKernels[i][j].IMAG;
                for (int k = 0; k < i; k++) {
                    Grid2Ddouble g2Real=fftKernels[k][j].REAL;
                    Grid2Ddouble g2Imag=fftKernels[k][j].IMAG;
                    boolean identical=true;
                    for (int l = 0; l < g2Real.length; l++) {
                        if(g1Real.Get(l)!=g2Real.Get(l)){
                            identical=false;
                            break;
                        }
                        if(g1Imag.Get(l)!=g2Imag.Get(l)){
                            identical=false;
                            break;
                        }
                    }
                    if(identical){
                        fftFieldRefs[i][j]=k;
                    }
                    else{
                        fftFieldRefs[i][j]=i;
                    }
                }
            }
        }
    }
    public void RecalcKernels(){
        for (int i = 0; i < nPlayers; i++) {
            for (int j = 0; j < nPlayers; j++) {
                Grid2Ddouble newKernel = kernelFields[i][j];
                double kSum=0;
                for (int x = 0; x < xDim; x++) {
                    for (int y = 0; y < yDim; y++) {
                        double val=Kernel.Eval(i,j,Dist(x,y,xDim/2.0,yDim/2.0));
                        kSum+=val;
                        newKernel.Set(x,y,val);
                    }
                }
                kSums[i][j]=kSum;
                for (int k = 0; k < newKernel.length; k++) {
                    newKernel.Set(k,newKernel.Get(k)/kSum);
                }
                fftKernels[i][j].SetGrid(newKernel);
                fftKernels[i][j].fftshift();
                fftKernels[i][j].fft2();
            }
        }
        FindKernelDuplicates();
    }
    public void WriteGrowthField(int i,Grid2Ddouble scratch){
        Grid2Ddouble currField = this.fields[i];
        for (int k = 0; k < currField.length; k++) {
            for (int j = 0; j < nPlayers; j++) {
                growthScratch[j]= fftFields[i][fftFieldRefs[i][j]].REAL.Get(k);
            }
            double delta=Bound(currField.Get(k) + this.Growth.Eval(i, growthScratch) * deltaT, 0.0, 1.0)-currField.Get(k);
            scratch.Set(k,delta);
        }
    }
    public void Update() {
        for (int i = 0; i < nPlayers; i++) {
            Grid2Ddouble currField = this.fields[i];
//            Grid2Ddouble currG=growthScratch[i];
            for (int j = 0; j < nPlayers; j++) {
                if (fftFieldRefs[i][j] == i) {
                    Grid2Ddouble convField = this.fields[j];
                    FFTGrid currFftField = this.fftFields[i][j];
                    currFftField.SetGrid(convField);
                    currFftField.fft2();
                    currFftField.ComplexMultiplication(fftKernels[i][j]);
                    currFftField.ifft2();
                }
            }
        }
        for (int i = 0; i < nPlayers; i++) {
            Grid2Ddouble currField = this.fields[i];
            //calc Gis for all Ais
            for (int k = 0; k < currField.length; k++) {
                for (int j = 0; j < nPlayers; j++) {
                    growthScratch[j]= fftFields[fftFieldRefs[i][j]][j].REAL.Get(k);
                }
                currField.Set(k, Bound(currField.Get(k) + this.Growth.Eval(i, growthScratch) * deltaT, 0.0, 1.0));
            }
        }
        //update Ais with Gis
        this.tick++;
    }
    // this is U = K * A
    public double GetConvolvedFieldVal(int iPlayer, int jPlayer, int i){
        return fftFields[fftFieldRefs[iPlayer][jPlayer]][jPlayer].REAL.Get(i);
    }
    public double GetConvolvedFieldVal(int iPlayer, int jPlayer, int x, int y){
        return fftFields[fftFieldRefs[iPlayer][jPlayer]][jPlayer].REAL.Get(x,y);
    }
    public Grid2Ddouble GetField(int iPlayer){
        return fields[iPlayer];
    }
    public double GetKernelVal(int iPlayer, int jPlayer, int i){
        return kernelFields[iPlayer][jPlayer].Get(i)*kSums[iPlayer][jPlayer];
    }
    public double GetKernelVal(int iPlayer, int jPlayer, int x, int y){
        return kernelFields[iPlayer][jPlayer].Get(x,y)*kSums[iPlayer][jPlayer];
    }
    public void ResetTick(){
        this.tick=0;
    }
    public int GetTick(){
        return tick;
    }
    public double Get(int iPlayer,int i){
        return this.fields[iPlayer].Get(i);
    }
    public double Get(int iPlayer,int x,int y){
        return this.fields[iPlayer].Get(x,y);
    }
    public void Set(int iPlayer,int i,double v){
        this.fields[iPlayer].Set(i,v);
    }
    public void Set(int iPlayer,int x,int y,double v){
        this.fields[iPlayer].Set(x,y,v);
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
        return new KernelInfo(Kernel,kernelFields,kSums,fftFieldRefs,fftKernels);
    }
    private void LoadKernelInfo(KernelInfo from){
        this.Kernel=from.Kernel;
        this.kernelFields=from.kernelFields;
        this.kSums=from.kSums;
        this.fftFieldRefs=from.fftFieldRefs;
        this.fftKernels=from.fftKernels;
    }
    @Override
    public Iterator<Grid2Ddouble> iterator() {
        return new FieldIterator();
    }
    private class FieldIterator implements Iterator<Grid2Ddouble>,Iterable<Grid2Ddouble>,Serializable{
        int i;
        @Override
        public Iterator<Grid2Ddouble> iterator() {
            return this;
        }
        @Override
        public boolean hasNext() {
            if(i<nPlayers){
                return true;
            }
            return false;
        }
        @Override
        public Grid2Ddouble next() {
            return fields[i++];
        }
    }
    private class KernelInfo {
        public final Coords2DDoubleToDouble Kernel;
        public final Grid2Ddouble[][]kernelFields;
        public final double[][]kSums;
        public final int[][]fftFieldRefs;
        public final FFTGrid[][]fftKernels;
        KernelInfo(Coords2DDoubleToDouble Kernel,Grid2Ddouble[][] kernelFields, double[][] kSums, int[][] fftFieldRefs, FFTGrid[][] fftKernels){
            this.Kernel=Kernel;
            this.kernelFields = kernelFields;
            this.kSums = kSums;
            this.fftFieldRefs = fftFieldRefs;
            this.fftKernels = fftKernels;
        }
    }
}