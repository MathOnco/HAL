package Examples.Lenia;

import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Gui.TickTimer;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Tools.Lenia.LeniaNPlayer;
import HAL.Util;

import static java.lang.Math.abs;
import static java.lang.Math.exp;

public class OrbiumNPlayer {

    //Lenia1Player model;
    public int R = 13;
    public double alpha = 4.0;
    public double mu = 0.15;
    public double sigma = 0.017;
    double[] gamma = {1.0,1.0};
    public double[][] payoff = {{1,0.5},{0,1}};


    public static final int DRAW_MODIFIER = 1; // draw every N steps
    public TickTimer tickTimer = new TickTimer();
    public int tickPause = 0; // in milliseconds (default is no pause)
    UIGrid[][]viss;
    public static int KERNEL=0,FIELD=1,FFT_FIELD=2;
    Grid2Ddouble[]gFields=new Grid2Ddouble[2];

    UIWindow win;
    public int sf = 2; // scale-factor (for drawing)

    // constructor from parameters (sidelength = 2^P)
    OrbiumNPlayer(int sideLenExp){
        this.win = new UIWindow("Lenia",true);
        int sideLen=(int)Math.pow(2,sideLenExp);
        viss=new UIGrid[3][4];
        for (int i = 0; i < 2; i++) {
            gFields[i]=new Grid2Ddouble(sideLen,sideLen);
        }
        for (int i = 0; i < viss.length; i++) {
            for (int j = 0; j < 4; j++) {
                viss[i][j]=new UIGrid(sideLen,sideLen,sf);
                win.AddCol(i,viss[i][j]);
            }
        }
        win.RunGui();
    }

    //generates a weight based on radius for interaction kernel
    public double Kc2(int i,int j,double r) {
        return (r>7.5) ? 0 : (r<2.5) ? 0.0 : 1.0;
    }
    //density dependent growth/death
    public double G2(int type,double[]x) {
        // Gaussian growth map:
            double sum = 0.0;
            for (int j = 0; j < x.length; j++) {
                sum += payoff[type][j]*x[j];
            }
            return gamma[type] * x[type] * (1.0 - sum);
        }
    //generates a weight based on radius for interaction kernel
    public double Kc(int i,int j,double r) {
        return (r>R) ? 0 : exp(alpha - (alpha)/(4*r/R*(1-r/R))); // exponential Kc (from Lenia paper)
    }
    //density dependent growth/death
    public double G(int i,double[] densities) {
        // Gaussian growth map:
        double l = abs(densities[0]-mu);
        double k = 2*sigma*sigma;
        return 2*exp(-(l*l)/k)-1;
    }

    // custom initial condition:
    public void AddOrbium(LeniaNPlayer model) {
        // add an orbium
        double IC[][] = { {0,0,0,0,0,0,0.1,0.14,0.1,0,0,0.03,0.03,0,0,0.3,0,0,0,0}, {0,0,0,0,0,0.08,0.24,0.3,0.3,0.18,0.14,0.15,0.16,0.15,0.09,0.2,0,0,0,0}, {0,0,0,0,0,0.15,0.34,0.44,0.46,0.38,0.18,0.14,0.11,0.13,0.19,0.18,0.45,0,0,0}, {0,0,0,0,0.06,0.13,0.39,0.5,0.5,0.37,0.06,0,0,0,0.02,0.16,0.68,0,0,0}, {0,0,0,0.11,0.17,0.17,0.33,0.4,0.38,0.28,0.14,0,0,0,0,0,0.18,0.42,0,0}, {0,0,0.09,0.18,0.13,0.06,0.08,0.26,0.32,0.32,0.27,0,0,0,0,0,0,0.82,0,0}, {0.27,0,0.16,0.12,0,0,0,0.25,0.38,0.44,0.45,0.34,0,0,0,0,0,0.22,0.17,0}, {0,0.07,0.2,0.02,0,0,0,0.31,0.48,0.57,0.6,0.57,0,0,0,0,0,0,0.49,0}, {0,0.59,0.19,0,0,0,0,0.2,0.57,0.69,0.76,0.76,0.49,0,0,0,0,0,0.36,0}, {0,0.58,0.19,0,0,0,0,0,0.67,0.83,0.9,0.92,0.87,0.12,0,0,0,0,0.22,0.07}, {0,0,0.46,0,0,0,0,0,0.7,0.93,1,1,1,0.61,0,0,0,0,0.18,0.11}, {0,0,0.82,0,0,0,0,0,0.47,1,1,0.98,1,0.96,0.27,0,0,0,0.19,0.1}, {0,0,0.46,0,0,0,0,0,0.25,1,1,0.84,0.92,0.97,0.54,0.14,0.04,0.1,0.21,0.05}, {0,0,0,0.4,0,0,0,0,0.09,0.8,1,0.82,0.8,0.85,0.63,0.31,0.18,0.19,0.2,0.01}, {0,0,0,0.36,0.1,0,0,0,0.05,0.54,0.86,0.79,0.74,0.72,0.6,0.39,0.28,0.24,0.13,0}, {0,0,0,0.01,0.3,0.07,0,0,0.08,0.36,0.64,0.7,0.64,0.6,0.51,0.39,0.29,0.19,0.04,0}, {0,0,0,0,0.1,0.24,0.14,0.1,0.15,0.29,0.45,0.53,0.52,0.46,0.4,0.31,0.21,0.08,0,0}, {0,0,0,0,0,0.08,0.21,0.21,0.22,0.29,0.36,0.39,0.37,0.33,0.26,0.18,0.09,0,0,0}, {0,0,0,0,0,0,0.03,0.13,0.19,0.22,0.24,0.24,0.23,0.18,0.13,0.05,0,0,0,0}, {0,0,0,0,0,0,0,0,0.02,0.06,0.08,0.09,0.07,0.05,0.01,0,0,0,0,0} };

        for (int x = 0; x < IC.length; x++) {
            for (int y = 0; y < IC[0].length; y++) {
                model.Set(0,x+model.xDim/2-IC.length/2,y+model.yDim/2-IC[0].length/2,IC[x][y]);
//                model.Set(x+model.xDim/2-IC.length/2,y+model.yDim/2-IC[0].length/2,IC[x][y]);
            }
        }
    }

    public void Draw(LeniaNPlayer model){
        for (int i = 0; i < model.nPlayers; i++) {
            model.WriteGrowthField(i,gFields[i]);
            Grid2Ddouble field=model.GetField(i);
            UIGrid vis=viss[FIELD][i*2];
            for (int k = 0; k < vis.length; k++) {
                vis.SetPix(k,Util.GreyScale(field.Get(k)));
            }
            UIGrid visG=viss[FIELD][i*2+1];
            for (int k = 0; k < visG.length; k++) {
                visG.SetPix(k,Util.HeatMapJet(gFields[i].Get(k),-1,1));
            }
            for (int j = 0; j < model.nPlayers; j++) {
                UIGrid vis2=viss[FFT_FIELD][i*2+j];
                for (int k = 0; k < vis.length; k++) {
                    vis2.SetPix(k,Util.HeatMapJet(model.GetConvolvedFieldVal(i,j,k),-1,1));
                }
            }
        }
    }

    public void RandomIC(LeniaNPlayer model) {

        Rand rn = new Rand();
        for (int i = 0; i < model.length; i++) {
            model.Set(0, i, rn.Double() / 10);
            model.Set(1, i, rn.Double() / 10);
        }
    }


        public static void main(String[] args) {
        int sideLenExp=6;
        OrbiumNPlayer orb = new OrbiumNPlayer(sideLenExp);
        LeniaNPlayer model=new LeniaNPlayer(sideLenExp,2,0.2, orb::Kc2, orb::G2);
//        Grid2Ddouble[]kernelFields=new Grid2Ddouble[]{model.GenKernelField(0,0),model.GenKernelField(0,1),model.GenKernelField(1,0),model.GenKernelField(1,1)};
//        Grid2Ddouble kernelField=model.GenKernelField();
        for (int i = 0; i < model.nPlayers; i++) {
            for (int j = 0; j < model.nPlayers; j++) {
                UIGrid vis=orb.viss[KERNEL][i*2+j];
                for (int k = 0; k < vis.length; k++) {
                    vis.SetPix(k, Util.GreyScale(model.GetKernelVal(i,j,k)));
                }
            }
        }
//        orb.AddOrbium(model);
        orb.RandomIC(model);
//        model.DrawKernel(orb.kVis,Util::GreyScale);

        int totalTime = 10000;
        while (model.GetTick() < totalTime) {
            orb.Draw(model);
            model.Update(); // increment model.time
//            for (int i = 0; i < model.length; i++) {
//                orb.aVis.SetPix(i,Util.GreyScale(model.Get(i)));
//            }
//            orb.win.TickPause(100);
        }

        orb.win.Close();
    }
}


