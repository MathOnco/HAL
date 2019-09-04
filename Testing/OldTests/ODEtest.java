package Testing.OldTests;

import HAL.Gui.PlotLine;
import HAL.Gui.PlotWindow;
import HAL.Tools.ODESolver.ODESolver;

import java.util.ArrayList;

import static HAL.Util.RED;

public class ODEtest {

    public static void Equation(double t, double[] state, double[] out){
        out[0]=Math.sin(t)*t;
        //out[0]=state[0]*state[0]+1;
        //out[0]=state[0]-t*t+1;
        //integral: x^2+1 = x^3/3 + x
    }
    public static void main(String[] args) {
        ODESolver s=new ODESolver();
        ArrayList<double[]>states=new ArrayList<>();
        ArrayList<Double>ts=new ArrayList<>();
        ts.add(0.0);
        states.add(new double[]{0.5});

        //s.Runge45(state,0,1.4,2E-5,0.1);
        //s.Runge45(state,0,1.4,2E-5,0.2);
        s.Runge45(ODEtest::Equation,states,ts,100,2E-5,0.2,0);
        PlotWindow win=new PlotWindow(100,100,5);
        PlotLine pl=new PlotLine(win,RED);
        for (int i = 0; i < states.size(); i++) {
            pl.AddSegment(ts.get(i),states.get(i)[0]);
            //win.Point(ts.get(i),states.get(i)[0],RED);
        }
    }
}
//testing comment