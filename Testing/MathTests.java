package Testing;

import HAL.Interfaces.OdeSolution;
import HAL.Rand;
import HAL.Tools.ODESolver.Derivative;
import HAL.Tools.ODESolver.ODESolver;
import HAL.Util;

import static HAL.Util.*;

import static Testing.UnitTester.*;

public class MathTests {
    static final int STD_DEV_TOL_SCALAR=4;


    static void GaussianTest(double mean, double stdDev,int nSamples){
        Rand rng=new Rand(0);
        double[]res=new double[nSamples];
        for (int i = 0; i < nSamples; i++) {
            res[i]= rng.Gaussian(mean,stdDev);
        }
        double realMean=ArrayMean(res);
        double meanTol=stdDev*3;
        double stdDevTol=stdDev/STD_DEV_TOL_SCALAR;
        double realStdDev=ArrayStdDev(res);
        AssertEqual("Check Mean mean:"+mean+" stdDev:"+stdDev,mean,realMean,meanTol);
        AssertEqual("Check Std Dev mean:"+mean+" stdDev:"+stdDev,stdDev,realStdDev,stdDevTol);
    }

    static void ODETestR4(Derivative ode, OdeSolution solution, double[]t0state, double dt, double tf, double tol){
        double[]state=t0state.clone();
        double[]actual=new double[state.length];
        ODESolver solver=new ODESolver();
        double t=0;
        while(t<tf){
            solver.Runge4(ode,state,t,dt);
            t+=dt;
            solution.Get(t,actual);
            for (int i = 0; i < state.length; i++) {
                AssertRelativeError("Runge 4 ODE solution at time "+t+", value index "+i,state[i],actual[i],tol);
            }
        }
    }
    static void ODETestR45(Derivative ode, OdeSolution solution, double[]t0state, double dt, double tf, double tol){
        double[]state=t0state.clone();
        double[]actual=new double[state.length];
        ODESolver solver=new ODESolver();
        double t=0;
        double[]ts=new double[(int)(tf/dt)];
        int k=0;
        double err=tol;
        for (int i = 0; i < ts.length; i++) {
            t+=dt;
            ts[i]=t;
        }
        t=0;
        for (int i = 0; i < ts.length; i++) {
            err=solver.Runge45(ode,state,t,ts[i],err,tol);
            t=ts[i];
            solution.Get(t,actual);
            for (int j = 0; j < state.length; j++) {
                AssertRelativeError("Runge 45 ODE solution at time "+t+", value index "+j,state[j],actual[j],tol);
            }
        }
    }

    static void BinomialTest(long n,double p,int trials){
        Rand rng=new Rand(0);
        long[]res=new long[trials];
        for (int i = 0; i < trials; i++) {
            res[i]=rng.Binomial(n,p);
        }
        double expMean=n*p;
        double expStdDev=Math.sqrt(n*p*(1-p));
        double meanTol=expStdDev/STD_DEV_TOL_SCALAR;
        double expDevTol=expStdDev/STD_DEV_TOL_SCALAR;
        double mean=ArrayMean(res);
        double stdDev=ArrayStdDev(res);
        AssertEqual("Check Mean n:"+n+" p:"+p,mean,expMean,meanTol);
        AssertEqual("Check Std Dev n:"+n+" p:"+p,stdDev,Math.sqrt(n*p*(1-p)),expDevTol);
    }

    static void MultinomialTest(int n,double[]ps,int trials){
        Rand rng=new Rand(0);
        int[][]resPops=new int[trials][ps.length];
        for (int i = 0; i < trials; i++) {
            rng.Multinomial(ps,n,resPops[i]);
        }
        int[][] pRows=new int[ps.length][trials];
        for (int i = 0; i < ps.length; i++) {
            for (int j = 0; j < trials; j++) {
                pRows[i][j]=resPops[j][i];
            }
        }
        for (int i = 0; i < ps.length; i++) {
            double expDev=Math.sqrt(n*ps[i]*(1-ps[i]));
            AssertEqual("Check Mean n:"+n+" p:"+ps[i],ArrayMean(pRows[i]),ps[i]*n,expDev*3);
            AssertEqual("Check Std Dev n:"+n+" p:"+ps[i],ArrayStdDev(pRows[i]),expDev,expDev*STD_DEV_TOL_SCALAR);
        }
    }

    static void ProbScaleTest(double p,double[] tScales,int trials){
        Rand rng=new Rand(0);
        for (int i = 0; i < tScales.length; i++) {
            int successCt=0;
            double pScaled=Util.ProbScale(p,tScales[i]);
            for (int j = 0; j < trials; j++) {
                for (double k = 0; k < 1; k += tScales[i]) {
                    if(rng.Double() < pScaled){
                        successCt += 1;
                        break;
                    }
                }
            }
            double expMean=p*trials;
            double expStdDev=Math.sqrt((trials)*p*(1-p));
            double meanTol=expStdDev*STD_DEV_TOL_SCALAR;
            AssertEqual("Check Mean p:"+p+" tScale:"+tScales[i],successCt,expMean,meanTol);
        }
    }


    public static void AddTests(UnitTester tester){

        tester.AddTest("ProbScale Test",()-> {

                    double[] tScales = new double[]{1, 0.5, 0.25,0.1,0.01};
                    double prob = 0;
                    for (int probs = 0; probs < 5; probs++) {
                        switch (probs) {
                            case 0:
                                prob = 0.0001;
                                break;
                            case 1:
                                prob = 0.01;
                                break;
                            case 2:
                                prob = 0.5;
                                break;
                        }
                    }
                    ProbScaleTest(prob,tScales,1000);
                }
        );

        tester.AddTest("Binomial Test",()->{
            for (int popSizes = 0; popSizes < 5; popSizes++) {
                long pop=0;
                switch (popSizes){
                    case 0:pop=1;break;
                    case 1:pop=10;break;
                    case 2:pop=1000;break;
                    case 3:pop=1000000;break;
                    case 4:pop=Long.MAX_VALUE/1000;break;
                }
                for (int probs = 0; probs < 5; probs++) {
                    double prob=0;
                    switch(probs){
                        case 0:prob=0.0001;break;
                        case 1:prob=0.01;break;
                        case 2:prob=0.5;break;
                        case 3:prob=0.9;break;
                        case 4:prob=0.9999;break;
                    }
                    BinomialTest(pop,prob,100000);
                }
            }
        });

        tester.AddTest("Gaussian Test",()->{
            double mean=0;
            for (int means = 0; means < 3; means++) {
                switch (means){
                    case 0:mean=0;
                    case 1:mean=100000;
                    case 2:mean=-100000;
                }
                double stdDev=0;
                for (int stdDevs = 0; stdDevs < 5; stdDevs++) {
                    switch (stdDevs){
                        case 0:stdDev=0;
                        case 1:stdDev=0.0001;
                        case 2:stdDev=1;
                        case 4:stdDev=100;
                        case 5:stdDev=10000;
                    }
                    GaussianTest(mean,stdDev,100000);
                }
            }
        });

        tester.AddTest("integral dy/dt=y R4 ",()->{
            double[]s0=new double[]{1};

            ODETestR4((t,state,out)->{
                out[0]=state[0];
                    },
                    (t,out)->{
                out[0]=Math.exp(t);
                    },
                    new double[]{1},0.1,100,0.01
            );
        });
        tester.AddTest("integral dy/dt=y R45 ",()->{
            double[]s0=new double[]{1};

            ODETestR45((t,state,out)->{
                        out[0]=state[0];
                    },
                    (t,out)->{
                        out[0]=Math.exp(t);
                    },
                    new double[]{1},0.1,10,0.01
            );
        });
        tester.AddTest("integral dy/dt=y*(1-y) (sigmoid) R4 ",()->{

            ODETestR4((t,state,out)->{
                        out[0]=state[0]*(1-state[0]);
                    },
                    (t,out)->{
                        out[0]=Math.exp(t)/(999+Math.exp(t));
                    },
                    new double[]{0.001},0.1,100,0.01
            );
        });
        tester.AddTest("integral dy/dt=y*(1-y) (sigmoid) R45 ",()->{

            ODETestR45((t,state,out)->{
                        out[0]=state[0]*(1-state[0]);
                    },
                    (t,out)->{
                        out[0]=Math.exp(t)/(999+Math.exp(t));
                    },
                    new double[]{0.001},0.1,10,0.01
            );
        });
//        tester.AddTest("integral dy/dt=y R45",()->{
//            double[]s0=new double[]{1};
//
//            ODETestR45((t,state,out)->{
//                        out[0]=state[0];
//                    },
//                    (t,out)->{
//                        out[0]=Math.exp(t);
//                    },
//                    new double[]{1},0.1,100,0.01
//            );
//        });
//        tester.AddTest("simple differential equation R4",()->{
//            double a=1.5,b=1,c=3,d=1;
//            double[]s0=new double[]{5,10};
//
//            ODETestR4((t,state,out)->{
//                out[0]=a*state[0]-b*state[0]*state[1];
//                out[1]=c*state[0]*state[1]-d*state[1];
//                    },
//                    (t,out)->{
//
//                    },
//                    new double[]{0,0},0.1,100,0.01
//            );
//        });
//        tester.AddTest("simple differential equation RK45",()->{
//            double a=1.5,b=1,c=3,d=1;
//            double[]s0=new double[]{5,10};
//
//            ODETestR45((t,state,out)->{
//                        out[0]=a*state[0]-b*state[0]*state[1];
//                        out[1]=c*state[0]*state[1]-d*state[1];
//                    },
//                    (t,out)->{
//
//                    },
//                    new double[]{0,0},0.1,100,0.01
//            );
//        });

        tester.AddTest("Factorial Test",()->{
            tester.AssertEqual("3 Factorial",6L,Factorial(3));
            tester.AssertEqual("12 Factorial",479001600L,Factorial(12));
        });
        tester.AddTest("Binomial PMF Test",()->{
            tester.AssertEqual("BinomialPMFTest, 4 choose 0", 1.0/Math.pow(2,4),Util.BinomialDistPDF(4,0.5,0),0.00001);
            tester.AssertEqual("BinomialPMFTest, 4 choose 2", 6.0/Math.pow(2,4),Util.BinomialDistPDF(4,0.5,2),0.00001);
        });

        tester.AddTest("Multinomial Test 1",()->{
            MultinomialTest(1000,new double[]{0.1,0.1,0.1,0.2},10000);
        });
        tester.AddTest("Multinomial Test 2",()->{
            MultinomialTest(10,new double[]{0.1,0.1,0.1},10000);
        });
        tester.AddTest("Multinomial Test 3",()->{
            MultinomialTest(10,new double[]{0.01,0.2,0.5},10000);
        });
    }

    public static void main(String[] args) {
        UnitTester tester=new UnitTester();
        AddTests(tester);
        tester.RunTests(true);
    }

}
