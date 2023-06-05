package Examples.Lenia;
import HAL.GridsAndAgents.Grid2Ddouble;
import HAL.Gui.TickTimer;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Tools.Lenia.Lenia1Player;
import HAL.Util;

import static java.lang.Math.*;

public class Orbium {

    //Lenia1Player model;
    public int R = 13;
    public double alpha = 4.0;
    public double mu = 0.15;
    public double sigma = 0.017;


    public static final int DRAW_MODIFIER = 1; // draw every N steps
    public TickTimer tickTimer = new TickTimer();
    public int tickPause = 0; // in milliseconds (default is no pause)

    Grid2Ddouble gField;
    UIGrid aVis; // A (state of the world)
    UIGrid fVis; // G(U(x)))
    UIGrid uVis; // U(x)
    UIGrid kVis; // K
    UIWindow win;
    public int sf = 5; // scale-factor (for drawing)

    // constructor from parameters (sidelength = 2^P)
    Orbium(int sideLenExp){
        int sideLen=(int) pow(2,sideLenExp);
        this.aVis = new UIGrid(sideLen, sideLen, sf);
        this.fVis = new UIGrid(sideLen, sideLen, sf);
        this.uVis = new UIGrid(sideLen, sideLen, sf);
        this.kVis = new UIGrid(sideLen, sideLen, sf);
        this.gField=new Grid2Ddouble(sideLen,sideLen);

        this.win = new UIWindow("Lenia",true);
        win.AddCol(0,"field");
        win.AddCol(0, aVis);

        win.AddCol(1,"fftField");
        win.AddCol(1, fVis);

        win.AddCol(2,"U(x)");
        win.AddCol(2, uVis);

        win.AddCol(3,"Kernal");
        win.AddCol(3, kVis);

        win.RunGui();
    }

    public double Kc(double r) {
        return (r>R) ? 0 : exp(alpha - (alpha)/(4*r/R*(1-r/R))); // exponential Kc (from Lenia paper)
    }
    public double G(double density) {
        // Gaussian growth map:
        double l = abs(density-mu);
        double k = 2*sigma*sigma;
        return 2*exp(-(l*l)/k)-1;
    }

    // custom initial condition:
    public void AddOrbium(Lenia1Player model) {
        // add an orbium
        double IC[][] = { {0,0,0,0,0,0,0.1,0.14,0.1,0,0,0.03,0.03,0,0,0.3,0,0,0,0}, {0,0,0,0,0,0.08,0.24,0.3,0.3,0.18,0.14,0.15,0.16,0.15,0.09,0.2,0,0,0,0}, {0,0,0,0,0,0.15,0.34,0.44,0.46,0.38,0.18,0.14,0.11,0.13,0.19,0.18,0.45,0,0,0}, {0,0,0,0,0.06,0.13,0.39,0.5,0.5,0.37,0.06,0,0,0,0.02,0.16,0.68,0,0,0}, {0,0,0,0.11,0.17,0.17,0.33,0.4,0.38,0.28,0.14,0,0,0,0,0,0.18,0.42,0,0}, {0,0,0.09,0.18,0.13,0.06,0.08,0.26,0.32,0.32,0.27,0,0,0,0,0,0,0.82,0,0}, {0.27,0,0.16,0.12,0,0,0,0.25,0.38,0.44,0.45,0.34,0,0,0,0,0,0.22,0.17,0}, {0,0.07,0.2,0.02,0,0,0,0.31,0.48,0.57,0.6,0.57,0,0,0,0,0,0,0.49,0}, {0,0.59,0.19,0,0,0,0,0.2,0.57,0.69,0.76,0.76,0.49,0,0,0,0,0,0.36,0}, {0,0.58,0.19,0,0,0,0,0,0.67,0.83,0.9,0.92,0.87,0.12,0,0,0,0,0.22,0.07}, {0,0,0.46,0,0,0,0,0,0.7,0.93,1,1,1,0.61,0,0,0,0,0.18,0.11}, {0,0,0.82,0,0,0,0,0,0.47,1,1,0.98,1,0.96,0.27,0,0,0,0.19,0.1}, {0,0,0.46,0,0,0,0,0,0.25,1,1,0.84,0.92,0.97,0.54,0.14,0.04,0.1,0.21,0.05}, {0,0,0,0.4,0,0,0,0,0.09,0.8,1,0.82,0.8,0.85,0.63,0.31,0.18,0.19,0.2,0.01}, {0,0,0,0.36,0.1,0,0,0,0.05,0.54,0.86,0.79,0.74,0.72,0.6,0.39,0.28,0.24,0.13,0}, {0,0,0,0.01,0.3,0.07,0,0,0.08,0.36,0.64,0.7,0.64,0.6,0.51,0.39,0.29,0.19,0.04,0}, {0,0,0,0,0.1,0.24,0.14,0.1,0.15,0.29,0.45,0.53,0.52,0.46,0.4,0.31,0.21,0.08,0,0}, {0,0,0,0,0,0.08,0.21,0.21,0.22,0.29,0.36,0.39,0.37,0.33,0.26,0.18,0.09,0,0,0}, {0,0,0,0,0,0,0.03,0.13,0.19,0.22,0.24,0.24,0.23,0.18,0.13,0.05,0,0,0,0}, {0,0,0,0,0,0,0,0,0.02,0.06,0.08,0.09,0.07,0.05,0.01,0,0,0,0,0} };

        for (int x = 0; x < IC.length; x++) {
            for (int y = 0; y < IC[0].length; y++) {
//                model.Set(0,x+model.xDim/2-IC.length/2,y+model.yDim/2-IC[0].length/2,IC[x][y]);
                model.Set(x+model.xDim/2-IC.length/2,y+model.yDim/2-IC[0].length/2,IC[x][y]);
            }
        }
    }
    public void Draw(Lenia1Player model){
//        Grid2Ddouble field=model.GetField(0);
//        Grid2Ddouble fftField=model.GetFFTKernelField(0,0);
        model.WriteGrowthField(gField);
        for (int i = 0; i < model.length; i++) {
            aVis.SetPix(i,Util.GreyScale(model.Get(i)));
            fVis.SetPix(i,Util.HeatMapJet(model.GetConvolvedFieldVal(i),-1,1));
            uVis.SetPix(i,Util.HeatMapJet(gField.Get(i),-1,1));
        }

    }

    public static void main(String[] args) {
        int sideLenExp=6;
        Orbium orb = new Orbium(sideLenExp);
        Lenia1Player model=new Lenia1Player(sideLenExp,0.2, orb::Kc, orb::G);
//        LeniaNPlayer model=new LeniaNPlayer(sideLenExp,1,0.2,orb::Kc,orb::G);
//        Grid2Ddouble kernelField=model.GenKernelField(0,0);
        for (int i = 0; i < model.length; i++) {
            orb.kVis.SetPix(i,Util.GreyScale(model.GetKernelVal(i)));
        }
        orb.AddOrbium(model);
//        model.DrawKernel(orb.kVis,Util::GreyScale);

        int totalTime = 10000;
        while (model.GetTick() < totalTime) {
            model.Update(); // increment model.time
            orb.Draw(model);
//            for (int i = 0; i < model.length; i++) {
//                orb.aVis.SetPix(i,Util.GreyScale(model.Get(i)));
//            }
//            orb.win.TickPause(100);
        }

        orb.win.Close();
    }
}


