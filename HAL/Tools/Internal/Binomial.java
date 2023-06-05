package HAL.Tools.Internal;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import HAL.Rand;

import java.io.Serializable;

public class Binomial implements Serializable {
    protected int n;
    protected double p;
    private int n_last = -1;
    private int n_prev = -1;
    private long n_lastL = -1;
    private long n_prevL = -1;
    private double par;
    private double np;
    private double p0;
    private double q;
    private double p_last = -1.0D;
    private double p_prev = -1.0D;
    private int b;
    private int m;
    private int nm;
    private long bL;
    private long mL;
    private long nmL;
    private double pq;
    private double rc;
    private double ss;
    private double xm;
    private double xl;
    private double xr;
    private double ll;
    private double lr;
    private double c;
    private double p1;
    private double p2;
    private double p3;
    private double p4;
    private double ch;
    private double log_p;
    private double log_q;
    private double log_n;

    private static final double[] stirlingCorrection =  {
            0.0,
            8.106146679532726e-02, 4.134069595540929e-02,
            2.767792568499834e-02, 2.079067210376509e-02,
            1.664469118982119e-02, 1.387612882307075e-02,
            1.189670994589177e-02, 1.041126526197209e-02,
            9.255462182712733e-03, 8.330563433362871e-03,
            7.573675487951841e-03, 6.942840107209530e-03,
            6.408994188004207e-03, 5.951370112758848e-03,
            5.554733551962801e-03, 5.207655919609640e-03,
            4.901395948434738e-03, 4.629153749334029e-03,
            4.385560249232324e-03, 4.166319691996922e-03,
            3.967954218640860e-03, 3.787618068444430e-03,
            3.622960224683090e-03, 3.472021382978770e-03,
            3.333155636728090e-03, 3.204970228055040e-03,
            3.086278682608780e-03, 2.976063983550410e-03,
            2.873449362352470e-03, 2.777674929752690e-03,
    };
    protected static final double[] logFactorials = {
            0.00000000000000000,   0.00000000000000000,   0.69314718055994531,
            1.79175946922805500,   3.17805383034794562,   4.78749174278204599,
            6.57925121201010100,   8.52516136106541430,  10.60460290274525023,
            12.80182748008146961,  15.10441257307551530,  17.50230784587388584,
            19.98721449566188615,  22.55216385312342289,  25.19122118273868150,
            27.89927138384089157,  30.67186010608067280,  33.50507345013688888,
            36.39544520803305358,  39.33988418719949404,  42.33561646075348503,
            45.38013889847690803,  48.47118135183522388,  51.60667556776437357,
            54.78472939811231919,  58.00360522298051994,  61.26170176100200198,
            64.55753862700633106,  67.88974313718153498,  71.25703896716800901
    };

    public Binomial() {
    }

    public int BinomialProbSample(int n,double p,Rand rng){
        int total=0;
        for (int i = 0; i < n; i++) {
            if (rng.Double() < p) {
                total++;
            }
        }
        return total;
    }
    public long BinomialProbSample(long n,double p,Rand rng){
        int total=0;
        for (int i = 0; i < n; i++) {
            if (rng.Double() < p) {
                total++;
            }
        }
        return total;
    }
    private static double stirlingCorrection(int k) {
        final double C1 =  8.33333333333333333e-02;     //  +1/12
        final double C3 = -2.77777777777777778e-03;     //  -1/360
        final double C5 =  7.93650793650793651e-04;     //  +1/1260
        final double C7 = -5.95238095238095238e-04;     //  -1/1680

        double r, rr;

        if (k > 30) {
            r = 1.0 / (double) k;
            rr = r * r;
            return r*(C1 + rr*(C3 + rr*(C5 + rr*C7)));
        }
        else return stirlingCorrection[k];
    }
    private static double stirlingCorrection(long k) {
        final double C1 =  8.33333333333333333e-02;     //  +1/12
        final double C3 = -2.77777777777777778e-03;     //  -1/360
        final double C5 =  7.93650793650793651e-04;     //  +1/1260
        final double C7 = -5.95238095238095238e-04;     //  -1/1680

        double r, rr;

        if (k > 30) {
            r = 1.0 / (double) k;
            rr = r * r;
            return r*(C1 + rr*(C3 + rr*(C5 + rr*C7)));
        }
        else return stirlingCorrection[(int) k];
    }
    public long rk_binomial_inversion(long n, double p, Rand rn){
        /*
         see rk_binomial_inversion at
         https://github.com/numpy/numpy/blob/master/numpy/random/mtrand/distributions.c
         for use when p*n < 30
          */

        double q, qn, np, px, U;
        long X, bound;

        q = 1.0 - p;
        qn = Math.exp(n * Math.log(q));
        np = n*p;
        bound = (long)Math.min(n, np + 10.0*Math.sqrt(np*q + 1));

        X = 0;
        px = qn;

        U = rn.Double();
        while (U > px)
        {
            X++;
            if (X > bound)
            {
                X = 0;
                px = qn;
                U = rn.Double();
            } else
            {
                U -= px;
                px  = ((n-X+1) * p * px)/(X*q);
            }
        }
        return X;

    }
    public int rk_binomial_inversion(int n, double p, Rand rn){
        /*
         see rk_binomial_inversion at
         https://github.com/numpy/numpy/blob/master/numpy/random/mtrand/distributions.c
         for use when p*n < 30
          */

        double q, qn, np, px, U;
        int X, bound;

        q = 1.0 - p;
        qn = Math.exp(n * Math.log(q));
        np = n*p;
        bound = (int)Math.min(n, np + 10.0*Math.sqrt(np*q + 1));

        X = 0;
        px = qn;

        U = rn.Double();
        while (U > px)
        {
            X++;
            if (X > bound)
            {
                X = 0;
                px = qn;
                U = rn.Double();
            } else
            {
                U -= px;
                px  = ((n-X+1) * p * px)/(X*q);
            }
        }
        return X;

    }
    private static double logFactorial(int k) {
        if (k >= 30) {
        double  r, rr;
        final double C0 =  9.18938533204672742e-01;
        final double C1 =  8.33333333333333333e-02;
        final double C3 = -2.77777777777777778e-03;
        final double C5 =  7.93650793650793651e-04;
        final double C7 = -5.95238095238095238e-04;

        r  = 1.0 / (double) k;
        rr = r * r;
        return (k + 0.5)*Math.log(k) - k + C0 + r*(C1 + rr*(C3 + rr*(C5 + rr*C7)));
    }
	else {
            return logFactorials[k];
        }
}
    public long ColtLong(long n, double p, Rand rn) {
        if(p==1){
            return n;
        }
        double C1_3 = 0.3333333333333333D;
        double C5_8 = 0.625D;
        double C1_6 = 0.16666666666666666D;
        boolean DMAX_KM = true;
        long i;
        double f;
        if(n != this.n_lastL || p != this.p_last) {
            this.n_lastL = n;
            this.p_last = p;
            this.par = Math.min(p, 1.0D - p);
            this.q = 1.0D - this.par;
            this.np = (double)n * this.par;
            if(this.np <= 0.0D) {
                return -1;
            }

            double rm = this.np + this.par;
            this.mL = (long)rm;
            if(this.np < 10.0D) {
                this.p0 = Math.exp((double)n * Math.log(this.q));
                long bh = (long)(this.np + 10.0D * Math.sqrt(this.np * this.q));
                this.bL = Math.min(n, bh);
            } else {
                this.rc = ((double)n + 1.0D) * (this.pq = this.par / this.q);
                this.ss = this.np * this.q;
                i = (long)(2.195D * Math.sqrt(this.ss) - 4.6D * this.q);
                this.xm = (double)this.mL + 0.5D;
                this.xl = (double)(this.mL - i);
                this.xr = (double)((long)(this.mL + i) + 1L);
                f = (rm - this.xl) / (rm - this.xl * this.par);
                this.ll = f * (1.0D + 0.5D * f);
                f = (this.xr - rm) / (this.xr * this.q);
                this.lr = f * (1.0D + 0.5D * f);
                this.c = 0.134D + 20.5D / (15.3D + (double)this.mL);
                this.p1 = (double)i + 0.5D;
                this.p2 = this.p1 * (1.0D + this.c + this.c);
                this.p3 = this.p2 + this.c / this.ll;
                this.p4 = this.p3 + this.c / this.lr;
            }
        }

        long K;
        double U;
        if(this.np < 10.0D) {
            K = 0;
            double pk = this.p0;
            U = rn.Double();

            while(U > pk) {
                ++K;
                if(K > this.bL) {
                    U = rn.Double();
                    K = 0;
                    pk = this.p0;
                } else {
                    U -= pk;
                    pk = (double)(n - K + 1) * this.par * pk / ((double)K * this.q);
                }
            }

            return p > 0.5D?n - K:K;
        } else {
            while(true) {
                double V;
                while(true) {
                    V = rn.Double();
                    if((U = rn.Double() * this.p4) <= this.p1) {
                        K = (long)(this.xm - U + this.p1 * V);
                        return p > 0.5D?n - K:K;
                    }

                    double X;
                    if(U <= this.p2) {
                        X = this.xl + (U - this.p1) / this.c;
                        if((V = V * this.c + 1.0D - Math.abs(this.xm - X) / this.p1) < 1.0D) {
                            K = (long)X;
                            break;
                        }
                    } else if(U <= this.p3) {
                        if((X = this.xl + Math.log(V) / this.ll) >= 0.0D) {
                            K = (long)X;
                            V *= (U - this.p2) * this.ll;
                            break;
                        }
                    } else if((K = (long)(this.xr - Math.log(V) / this.lr)) <= n) {
                        V *= (U - this.p3) * this.lr;
                        break;
                    }
                }

                long Km;
                if((Km = Math.abs(K - this.mL)) > 20 && (double)((long)(Km + Km) + 2L) < this.ss) {
                    V = Math.log(V);
                    double T = (double)(-Km * Km) / (this.ss + this.ss);
                    double E = (double)Km / this.ss * (((double)Km * ((double)Km * 0.3333333333333333D + 0.625D) + 0.16666666666666666D) / this.ss + 0.5D);
                    if(V <= T - E) {
                        break;
                    }

                    if(V <= T + E) {
                        if(n != this.n_prevL || this.par != this.p_prev) {
                            this.n_prevL = n;
                            this.p_prev = this.par;
                            this.nmL = n - this.mL + 1;
                            this.ch = this.xm * Math.log(((double)this.mL + 1.0D) / (this.pq * (double)this.nmL)) + stirlingCorrection(this.mL + 1) + stirlingCorrection(this.nmL);
                        }

                        long nK = n - K + 1;
                        if(V <= this.ch + ((double)n + 1.0D) * Math.log((double)this.nmL / (double)nK) + ((double)K + 0.5D) * Math.log((double)nK * this.pq / ((double)K + 1.0D)) - stirlingCorrection(K + 1) - stirlingCorrection(nK)) {
                            break;
                        }
                    }
                } else {
                    f = 1.0D;
                    if(this.m < K) {
                        i = this.m;

                        while(i < K) {
                            ++i;
                            if((f *= this.rc / (double)i - this.pq) < V) {
                                break;
                            }
                        }
                    } else {
                        i = K;

                        while(i < this.m) {
                            ++i;
                            if((V *= this.rc / (double)i - this.pq) > f) {
                                break;
                            }
                        }
                    }

                    if(V <= f) {
                        break;
                    }
                }
            }

            return p > 0.5D?n - K:K;
        }
    }
    public int ColtInt(Rand rn){
        return ColtInt(this.n_last,this.p_last,rn);
    }
    public long ColtLong(Rand rn){
        return ColtLong(this.n_lastL,this.p_last,rn);
    }
    public int ColtInt(int n, double p, Rand rn) {
        double C1_3 = 0.3333333333333333D;
        double C5_8 = 0.625D;
        double C1_6 = 0.16666666666666666D;
        boolean DMAX_KM = true;
        int i;
        double f;
        if(n != this.n_last || p != this.p_last) {
            this.n_last = n;
            this.p_last = p;
            this.par = Math.min(p, 1.0D - p);
            this.q = 1.0D - this.par;
            this.np = (double)n * this.par;
            if(this.np <= 0.0D) {
                return -1;
            }

            double rm = this.np + this.par;
            this.m = (int)rm;
            if(this.np < 10.0D) {
                this.p0 = Math.exp((double)n * Math.log(this.q));
                int bh = (int)(this.np + 10.0D * Math.sqrt(this.np * this.q));
                this.b = Math.min(n, bh);
            } else {
                this.rc = ((double)n + 1.0D) * (this.pq = this.par / this.q);
                this.ss = this.np * this.q;
                i = (int)(2.195D * Math.sqrt(this.ss) - 4.6D * this.q);
                this.xm = (double)this.m + 0.5D;
                this.xl = (double)(this.m - i);
                this.xr = (double)((long)(this.m + i) + 1L);
                f = (rm - this.xl) / (rm - this.xl * this.par);
                this.ll = f * (1.0D + 0.5D * f);
                f = (this.xr - rm) / (this.xr * this.q);
                this.lr = f * (1.0D + 0.5D * f);
                this.c = 0.134D + 20.5D / (15.3D + (double)this.m);
                this.p1 = (double)i + 0.5D;
                this.p2 = this.p1 * (1.0D + this.c + this.c);
                this.p3 = this.p2 + this.c / this.ll;
                this.p4 = this.p3 + this.c / this.lr;
            }
        }

        int K;
        double U;
        if(this.np < 10.0D) {
            K = 0;
            double pk = this.p0;
            U = rn.Double();

            while(U > pk) {
                ++K;
                if(K > this.b) {
                    U = rn.Double();
                    K = 0;
                    pk = this.p0;
                } else {
                    U -= pk;
                    pk = (double)(n - K + 1) * this.par * pk / ((double)K * this.q);
                }
            }

            return p > 0.5D?n - K:K;
        } else {
            while(true) {
                double V;
                while(true) {
                    V = rn.Double();
                    if((U = rn.Double() * this.p4) <= this.p1) {
                        K = (int)(this.xm - U + this.p1 * V);
                        return p > 0.5D?n - K:K;
                    }

                    double X;
                    if(U <= this.p2) {
                        X = this.xl + (U - this.p1) / this.c;
                        if((V = V * this.c + 1.0D - Math.abs(this.xm - X) / this.p1) < 1.0D) {
                            K = (int)X;
                            break;
                        }
                    } else if(U <= this.p3) {
                        if((X = this.xl + Math.log(V) / this.ll) >= 0.0D) {
                            K = (int)X;
                            V *= (U - this.p2) * this.ll;
                            break;
                        }
                    } else if((K = (int)(this.xr - Math.log(V) / this.lr)) <= n) {
                        V *= (U - this.p3) * this.lr;
                        break;
                    }
                }

                int Km;
                if((Km = Math.abs(K - this.m)) > 20 && (double)((long)(Km + Km) + 2L) < this.ss) {
                    V = Math.log(V);
                    double T = (double)(-Km * Km) / (this.ss + this.ss);
                    double E = (double)Km / this.ss * (((double)Km * ((double)Km * 0.3333333333333333D + 0.625D) + 0.16666666666666666D) / this.ss + 0.5D);
                    if(V <= T - E) {
                        break;
                    }

                    if(V <= T + E) {
                        if(n != this.n_prev || this.par != this.p_prev) {
                            this.n_prev = n;
                            this.p_prev = this.par;
                            this.nm = n - this.m + 1;
                            this.ch = this.xm * Math.log(((double)this.m + 1.0D) / (this.pq * (double)this.nm)) + stirlingCorrection(this.m + 1) + stirlingCorrection(this.nm);
                        }

                        int nK = n - K + 1;
                        if(V <= this.ch + ((double)n + 1.0D) * Math.log((double)this.nm / (double)nK) + ((double)K + 0.5D) * Math.log((double)nK * this.pq / ((double)K + 1.0D)) - stirlingCorrection(K + 1) - stirlingCorrection(nK)) {
                            break;
                        }
                    }
                } else {
                    f = 1.0D;
                    if(this.m < K) {
                        i = this.m;

                        while(i < K) {
                            ++i;
                            if((f *= this.rc / (double)i - this.pq) < V) {
                                break;
                            }
                        }
                    } else {
                        i = K;

                        while(i < this.m) {
                            ++i;
                            if((V *= this.rc / (double)i - this.pq) > f) {
                                break;
                            }
                        }
                    }

                    if(V <= f) {
                        break;
                    }
                }
            }

            return p > 0.5D?n - K:K;
        }
    }
    public int SampleIntFast(int n,double p,Rand rn){
//        if(p>1||p<0||Double.isNaN(p)){
//            throw new IllegalArgumentException("Probability Argument Cannot be > 1 or < 0: "+p);
//        }
        if(p==1){
            return n;
        }
        if(p==0||n==0){
            return 0;
        }
        if(n<10){
            return BinomialProbSample(n,p,rn);
        }
//        if(n<=8||(p<0.5&&n<=64*p+8)||(p>=0.5&&n<=64*(1-p)+8)){
//            return BinomialProbSample(n,p,rn);
//        }
        if(p<0.5&&n*p<30){
            return rk_binomial_inversion(n,p,rn);
        }
        if(p>=0.5&&n*(1-p)<30){
            return n-rk_binomial_inversion(n,(1-p),rn);
        }
        else{
            return ColtInt(n,p,rn);
        }
    }
    public int SampleInt(int n, double p, Rand rn){
        if(p==1){
            return n;
        }
        if(p==0||n==0){
            return 0;
        }
        if(p<0.5&&n*p<30){
            return rk_binomial_inversion(n,p,rn);
        }
        if(p>=0.5&&n*(1-p)<30){
            return n-rk_binomial_inversion(n,(1-p),rn);
        }
        else{
            return ColtInt(n,p,rn);
        }
    }
    public long SampleLongFast(long n,double p,Rand rn){
        if(p>1||p<0||Double.isNaN(p)){
            throw new IllegalArgumentException("Probability Argument Cannot be > 1 or < 0: "+p);
        }
        if(p==1){
            return n;
        }
        if(p==0||n==0){
            return 0;
        }
        if(n<10){
            return BinomialProbSample(n,p,rn);
        }
//        if(n<=8||(p<0.5&&n<=64*p+8)||(p>=0.5&&n<=64*(1-p)+8)){
//            return BinomialProbSample(n,p,rn);
//        }
        if(p<0.5&&n*p<30){
            return rk_binomial_inversion(n,p,rn);
        }
        if(p>=0.5&&n*(1-p)<30){
            return n-rk_binomial_inversion(n,(1-p),rn);
        }
        else{
            return ColtLong(n,p,rn);
        }
    }
    public long SampleLong(long n, double p, Rand rn){
        if(p==1){
            return n;
        }
        if(p==0||n==0){
            return 0;
        }
        if(p<0.5&&n*p<30){
            return rk_binomial_inversion(n,p,rn);
        }
        if(p>=0.5&&n*(1-p)<30){
            return n-rk_binomial_inversion(n,(1-p),rn);
        }
        else{
            return ColtLong(n,p,rn);
        }

    }

    public void SetNandPInt(int n, double p) {
        if(n != this.n_last || p != this.p_last) {
            this.n_last = n;
            this.p_last = p;
            this.par = Math.min(p, 1.0D - p);
            this.q = 1.0D - this.par;
            this.np = (double) n * this.par;
            if (this.np <= 0.0D) {
                throw new IllegalArgumentException();
            }
        }
    }
    public void SetNandPLong(long n, double p) {
        if (n != this.n_lastL || p != this.p_last) {
            this.n_lastL = n;
            this.p_last = p;
            this.par = Math.min(p, 1.0D - p);
            this.q = 1.0D - this.par;
            this.np = (double) n * this.par;
            if (this.np <= 0.0D) {
                throw new IllegalArgumentException();
            }
        }
    }
}
