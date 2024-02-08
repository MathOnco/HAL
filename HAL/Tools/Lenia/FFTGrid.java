package HAL.Tools.Lenia;

import HAL.GridsAndAgents.Grid2Ddouble;

public class FFTGrid {


    public boolean NORMALIZE = false;

    final public Grid2Ddouble REAL;// Matrix (Grid2Ddouble) of complex numbers: 2n rows and n columns
    final public Grid2Ddouble IMAG;// Matrix (Grid2Ddouble) of complex numbers: 2n rows and n columns

    // takes in an array of real integers, stores them in correct format, then does FFT.
    public FFTGrid(Grid2Ddouble gridReal){

//        int n = gridReal.xDim ;
//        double ld = Math.log(n) / Math.log(2.0);
//
//        if (((int) ld) - ld != 0) {
//            System.out.println("The number of elements is not a power of 2.");
//            return;
//        }

        REAL = new Grid2Ddouble(gridReal.xDim,gridReal.yDim);
        IMAG = new Grid2Ddouble(gridReal.xDim,gridReal.yDim);

        for (int row = 0; row < gridReal.xDim; row++) { //2n
            for (int col = 0; col < gridReal.yDim; col++) { //n
                REAL.Set(row,col, gridReal.Get(row,col) );
                IMAG.Set(row,col, 0); // zeros for Imag.
            }
        }
    }
    public FFTGrid(int xDim, int yDim){
        REAL=new Grid2Ddouble(xDim,yDim);
        IMAG=new Grid2Ddouble(xDim,yDim);
    }

    public void SetGrid(Grid2Ddouble gridReal){
        for (int row = 0; row < gridReal.xDim; row++) { //2n
            for (int col = 0; col < gridReal.yDim; col++) { //n
                REAL.Set(row,col, gridReal.Get(row,col) );
                IMAG.Set(row,col, 0); // zeros for Imag.
            }
        }
    }


    public static void main(String[] args) {

        int dimension = 2;
        Grid2Ddouble myGrid = new Grid2Ddouble(dimension,dimension);

        for (int i = 0; i < myGrid.length; i++) {
            myGrid.Set(i,((double)i+1)/10);
        }


        FFTGrid M = new FFTGrid(myGrid);


        M.PrintReal();
        M.fftshift();
        M.fft2();
        M.PrintReal();

        // this should be: 4,3;2,1
        M.ifft2();
        M.PrintReal();

//        M.fft2();
//        M.PrintReal();

//        M.ifft2();
//        M.PrintReal();

        return;
    }


    public void fftshift() {
        double temp;
        int x0,y0;
        for (int row = 0; row < REAL.xDim/2; row++) {
            for (int col = 0; col < REAL.yDim; col++) {
                // get value:
                temp = REAL.Get(row,col);

                // shift coordinates:
                x0 = (row + REAL.xDim/2) % REAL.xDim;
                y0 = (col + REAL.yDim/2) % REAL.yDim;

                // REAL
                REAL.Set(row,col, REAL.Get(x0,y0));
                REAL.Set(x0,y0,temp);

                // IMAG
                temp = IMAG.Get(row,col);
                IMAG.Set(row,col, IMAG.Get(x0,y0));
                IMAG.Set(x0,y0,temp);
            }
        }
    }

    public void fft2() {
        NORMALIZE = false;
        this.fft_transform_rows(true);
        this.fft_transform_columns(true);
    }
    public void ifft2() {
        NORMALIZE = true;
        this.fft_transform_rows(false);
        this.fft_transform_columns(false);
    }

    // transform M to F
    public void fft_transform_columns(boolean DIRECT) {
        for (int col = 0; col < REAL.yDim; col++) {
            int n = REAL.xDim ;
            double ld = Math.log(n) / Math.log(2.0);

//            if (((int) ld) - ld != 0) {
//                System.out.println("The number of elements is not a power of 2.");
//                return;
//            }

            int nu = (int) ld;
            int n2 = n / 2;
            int nu1 = nu - 1;
            double tReal, tImag, p, arg, c, s;

            double constant;
            if (DIRECT)
                constant = -2 * Math.PI;
            else
                constant = 2 * Math.PI;

            // First phase - calculation
            int k = 0;
            for (int l = 1; l <= nu; l++) {
                while (k < n) {
                    for (int i = 1; i <= n2; i++) {
                        p = bitreverseReference(k >> nu1, nu);
                        // direct FFT or inverse FFT
                        arg = constant * p / n;
                        c = Math.cos(arg);
                        s = Math.sin(arg);
                        tReal = REAL.Get((k + n2), col) * c + IMAG.Get((k + n2), col) * s;
                        tImag = IMAG.Get((k + n2), col) * c - REAL.Get((k + n2), col) * s;
                        REAL.Set((k + n2), col, REAL.Get(k, col) - tReal);
                        IMAG.Set((k + n2), col, IMAG.Get(k, col) - tImag);
                        REAL.Add(k, col, tReal); //+=
                        IMAG.Add(k, col, tImag); //+=
                        k++;
                    }
                    k += n2;
                }
                k = 0;
                nu1--;
                n2 /= 2;
            }

            // Second phase - recombination
            k = 0;
            int r;
            while (k < n) {
                r = bitreverseReference(k, nu);
                if (r > k) {
                    tReal = REAL.Get(k, col);
                    tImag = IMAG.Get(k, col);
                    REAL.Set(k, col, REAL.Get(r, col));
                    IMAG.Set(k, col, IMAG.Get(r, col));
                    REAL.Set(r, col, tReal);
                    IMAG.Set(r, col, tImag);
                }
                k++;
            }

            // by this point, M is transformed, but not normalized.
            double radice = (NORMALIZE) ? 1 / Math.sqrt(n*n) : 1;
            for (int row = 0; row < REAL.xDim; row++) {
                REAL.Set(row, col, radice * REAL.Get(row, col));
                IMAG.Set(row, col, radice * IMAG.Get(row, col));
            }
        }
    }


    // transform M to F
    public void fft_transform_rows(boolean DIRECT) {
        for (int row = 0; row < REAL.xDim; row++) {
            int n = REAL.yDim;
            double ld = Math.log(n) / Math.log(2.0);
            int nu = (int) ld;
            int n2 = n / 2;
            int nu1 = nu - 1;
            double tReal, tImag, p, arg, c, s;

            double constant;
            if (DIRECT)
                constant = -2 * Math.PI;
            else
                constant = 2 * Math.PI;

            // First phase - calculation
            int k = 0;
            for (int l = 1; l <= nu; l++) {
                while (k < n) {
                    for (int i = 1; i <= n2; i++) {
                        p = bitreverseReference(k >> nu1, nu);
                        // direct FFT or inverse FFT
                        arg = constant * p / n;
                        c = Math.cos(arg);
                        s = Math.sin(arg);
                        tReal = REAL.Get(row, (k + n2)) * c + IMAG.Get(row, (k + n2)) * s;
                        tImag = IMAG.Get(row, (k + n2)) * c - REAL.Get(row, (k + n2)) * s;
                        REAL.Set(row, (k + n2), REAL.Get(row, k) - tReal);
                        IMAG.Set(row, (k + n2), IMAG.Get(row, k) - tImag);
                        REAL.Add(row, k, tReal); //+=
                        IMAG.Add(row, k, tImag); //+=
                        k++;
                    }
                    k += n2;
                }
                k = 0;
                nu1--;
                n2 /= 2;
            }

            // Second phase - recombination
            k = 0;
            int r;
            while (k < n) {
                r = bitreverseReference(k, nu);
                if (r > k) {
                    tReal = REAL.Get(row, k);
                    tImag = IMAG.Get(row, k);
                    REAL.Set(row, k, REAL.Get(row, r));
                    IMAG.Set(row, k, IMAG.Get(row, r));
                    REAL.Set(row, r, tReal);
                    IMAG.Set(row, r, tImag);
                }
                k++;
            }

            // by this point, M is transformed, but not normalized.
            double radice = (NORMALIZE) ? 1 / Math.sqrt(n*n) : 1;
            for (int col = 0; col < REAL.yDim; col++) {
                REAL.Set(row, col, radice * REAL.Get(row, col));
                IMAG.Set(row, col, radice * IMAG.Get(row, col));
            }
        }
    }

    // element-wise multiplication!
    public void ComplexMultiplication(FFTGrid grid2) {
        double r1,r2,i1,i2;
        for (int row = 0; row < REAL.xDim; row++) {
            for (int col = 0; col < REAL.yDim; col++) {
                r1 = REAL.Get(row,col);
                i1 = IMAG.Get(row,col);

                r2 = grid2.REAL.Get(row,col);
                i2 = grid2.IMAG.Get(row,col);

                REAL.Set(row,col, r1*r2 - (i1*i2));
                IMAG.Set(row,col, r1*i2 + r2*i1);
            }
        }
    }


    // transform F^-1 to M
    public void ifft() {
        this.fft_transform_columns(false);
    }
    // transform M to F^-1
    public void fft() {
        this.fft_transform_columns(true);
    }
    public static int bitreverseReference(int j, int nu) {
        int j2;
        int j1 = j;
        int k = 0;
        for (int i = 1; i <= nu; i++) {
            j2 = j1 / 2;
            k = 2 * k + j1 - 2 * j2;
            j1 = j2;
        }
        return k;
    }
    public void PrintReal() {
        System.out.println("\nReal components:");
        for (int row = 0; row < REAL.xDim; row++) {
            String str = "| ";
            for (int col = 0; col < REAL.yDim; col++) {
                // print all rows
                str += Round2F(REAL.Get(row, col));
                str += " | ";
            }
            System.out.println(str);
        }
    }
    public void PrintImag() {
        System.out.println("\nImag components:");
        for (int row = 0; row < IMAG.xDim; row++) {
            String str = "| ";
            for (int col = 0; col < IMAG.yDim; col++) {
                // print all rows
                str += Round2F(IMAG.Get(row, col));
                str += " | ";
            }
            System.out.println(str);

        }
    }
    public static double Round2F ( double val){
        return ((double) Math.round(val * 100d) / 100d);
    }


}

