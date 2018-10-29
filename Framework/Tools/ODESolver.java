package Framework.Tools;

import Framework.Interfaces.Derivative;

import java.lang.reflect.Array;
import java.util.ArrayList;

import static Framework.Util.Norm;

/**
 * use this class to solve any ODE system. the integrator and state array size can be changed at any time
 * currently Euler and Runge-Kutta 4 can be used as fixed-step-size integrators
 * currently Runge-Kutta Fehlberg 4,5 exists as an adaptive-step-size integrator
 */

public class ODESolver {

    protected Derivative Derivative;
    protected double[] dy1;
    protected double[] dy2;
    protected double[] dy3;
    protected double[] dy4;
    protected double[] dy5;
    protected double[] dy6;

    protected double[] temp;
    protected double[] temp2;
    protected double[] temp3;



    /**
     * the Derivative parameter must be a function that takes a time value, a state array, and an output array as argument, and sets values in the output array.
     */
    public ODESolver(Derivative Derivative){
        this.Derivative=Derivative;
    }

    /**
     * changes the function that the ODESolver uses for integration
     */
    public void SetDerivative(Derivative newDerivative){
        this.Derivative=newDerivative;
    }

    /**
     * runs 1 round of Euler integration, putting the result in the out array
     */
    public void Euler(double[]state,double[]out,double t,double dt) {
        EnsureScratchEuler(state.length);
        Derivative.Set(t,state, dy1);
        EulerAdd(state, dy1, dt, out);
    }
    /**
     * runs 1 round of Euler integration, putting the result in the state array
     */
    public void Euler(double[]state,double t,double dt) {
        Euler(state,state,t,dt);
    }

    /**
     * runs Euler integration from t0 to tf in increments of dt
     */
    public void Euler(double[]state,double[]out,double t0,double tf,double dt){
        Integrate(this::Euler,state,out,t0,tf,dt);
    }

    /**
     * runs Euler integration from t0 to tf in increments of dt, puts the resulting states in the states array, and the resulting ts in the ts array
     */
    public void Euler(ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
        IntegrateArrayList(this::Euler,states,ts,tf,dt,startStateIndex);
    }

    /**
     * runs 1 round of Runge-Kutta 4 integration, putting the result in the out array
     */
    public void Runge4(double[]state,double[]out,double t,double dt) {
        double halfStep = dt / 2.0;
        EnsureScratchRunge(state.length);
        Derivative.Set(t,state, dy1);
        EulerAdd(state, dy1, halfStep, temp);
        Derivative.Set(t,temp, dy2);
        EulerAdd(state, dy2, halfStep, temp);
        Derivative.Set(t,temp, dy3);
        EulerAdd(state, dy3, dt, temp);
        Derivative.Set(t,temp, dy4);
        for (int i = 0; i < state.length; i++) {
            out[i] = state[i]+(dy1[i] + 2 * dy2[i] + 2 * dy3[i] + dy4[i]) * dt / 6.0;
        }
    }


    /**
     * runs 1 round of Runge-Kutta 4 integration, putting the result in the state array
     */
    public void Runge4(double[]state,double t,double dt){
        Runge4(state,state,t,dt);
    }

    /**
     * runs Runge-Kutta 4 integration from t0 to tf in increments of dt
     */
    public void Runge4(double[]state,double t0,double tf,double dt){
        Integrate(this::Runge4,state,state,t0,tf,dt);
    }

    /**
     * runs Runge-Kutta 4,5 integration from t0 to tf in increments of dt, puts the resulting states in the states array, and the resulting ts in the ts array
     */
    public void Runge4(ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
        IntegrateArrayList(this::Runge4,states,ts,tf,dt,startStateIndex);
    }

    /**
     * calls runge kutta 4,5 iteratively, updating the state array until tf. returns the final dt that works for the provided tolerance. dtStart is the starting stepsize
     */
    public double Runge45(double[] state,double[]out,double t0,double tf,double dtStart,double errorTolerance){
        boolean first=true;
        double dt=dtStart;
        double t=t0;
        double error,scale;
        while(t<tf){
            //calc dt
            if(dt>tf-t){
                dt=tf-t;
            }
            //calc next state
            error = Runge45internal(state, t, dt, errorTolerance);
            scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            if(error>errorTolerance){
                dt*=scale;
                error = Runge45internal(state, t, dt, errorTolerance);
                scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            }
            //update state
            if(first) {
                for (int i = 0; i < state.length; i++) {
                    out[i] = state[i]+temp2[i];
                }
                first=false;
            } else{
                for (int i = 0; i < state.length; i++) {
                    out[i] = out[i]+temp2[i];
                }
            }
            //advance t
            t+=dt;
            dt*=scale;
        }
        return dt;
    }
    public double Runge45(double[] state,double t0,double tf,double dtStart,double errorTolerance){
        return Runge45(state,state,t0,tf,dtStart,errorTolerance);
    }

    /**
     * calls runge kutta 4,5 iteratively, putting new states and the times for which those states were computed into the states and ts arraylists. dtStart is the starting stepsize
     */
    public int Runge45(ArrayList<double[]> states, ArrayList<Double> ts,double tf,double dtStart,double errorTolerance,int startStateIndex){
        if(states.size()<startStateIndex||ts.size()<startStateIndex){
            throw new IllegalArgumentException("states and ts ArrayLists must contain starting state entries");
        }
        int iState=startStateIndex;
        double dt=dtStart;
        double t= ts.get(iState);
        double error,scale;
        while(t<tf){
            double[]state=states.get(iState);
            //calc dt
            if(dt>tf-t){
                dt=tf-t;
            }
            //calc next state
            error = Runge45internal(state, t, dt, errorTolerance);
            scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            if(error>errorTolerance){
                dt*=scale;
                error = Runge45internal(state, t, dt, errorTolerance);
                scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            }
            //update state
            iState++;
            if(states.size()<=iState){
                states.add(new double[state.length]);
            }
            double[]nextState=states.get(iState);
            for (int i = 0; i < state.length; i++) {
                nextState[i]=state[i]+temp2[i];
            }
            //advance t
            t+=dt;
            if(ts.size()<=iState){
                ts.add(t);
            } else {
                ts.set(iState, t);
            }
            dt*=scale;
        }
        return iState;
    }


    double Runge45internal(double[]state,double t,double dt,double tol){
        EnsureScratchRunge45(state.length);
        SetDy1(state,t,dt);
        System.arraycopy(state,0,temp,0,state.length);
        TempAdd(dy1,1.0/4);
        SetDy(dy2,t+dt*1.0/4,dt);
        System.arraycopy(state,0,temp,0,state.length);
        TempAdd(dy1,3.0/32);
        TempAdd(dy2,9.0/32);
        SetDy(dy3,t+dt*3.0/8,dt);
        System.arraycopy(state,0,temp,0,state.length);
        TempAdd(dy1,1932.0/2197);
        TempAdd(dy2,-7200.0/2197);
        TempAdd(dy3,7296.0/2197);
        SetDy(dy4,t+dt*12.0/13,dt);
        System.arraycopy(state,0,temp,0,state.length);
        TempAdd(dy1,439.0/216);
        TempAdd(dy2,-8.0);
        TempAdd(dy3,3680.0/513);
        TempAdd(dy4,-845.0/4104);
        SetDy(dy5,t+dt,dt);
        System.arraycopy(state,0,temp,0,state.length);
        TempAdd(dy1,-8.0/27);
        TempAdd(dy2,2.0);
        TempAdd(dy3,-3544.0/2565);
        TempAdd(dy4,1859.0/4104);
        TempAdd(dy5,-11.0/40);
        SetDy(dy6,t+dt/2.0,dt);
        for (int i = 0; i < state.length; i++) {
            //RK4
            temp[i] = state[i] + 25.0 / 216 * dy1[i] + 1408.0 / 2565 * dy3[i] + 2197.0 / 4104 * dy4[i] - 1.0 / 5 * dy5[i];
            //RK5
            temp2[i] = 16.0 / 135 * dy1[i] + 6656.0 / 12825 * dy3[i] + 28561.0 / 56430 * dy4[i] - 9.0 / 50 * dy5[i] + 2.0 / 55 * dy6[i];
        }
        for (int i = 0; i < state.length; i++) {
            temp3[i]= (temp2[i]+state[i])-temp[i];
        }
        return (1.0/dt)*(Norm(temp3));
    }


    void ScratchLengthCheck(int len){
        if(dy1.length!=len){
            throw new IllegalArgumentException("cannot change state array length with the same ODESolver, create a different ODESolver object to handle the alternative state array size!");
        }
    }

    void EnsureScratchRunge(int len){
        if(dy2==null){
            dy1 =new double[len];
            dy2 =new double[len];
            dy3 =new double[len];
            dy4 =new double[len];
            temp=new double[len];
        }
        ScratchLengthCheck(len);
    }
    void EnsureScratchEuler(int len){
        if(dy1==null){
            dy1 =new double[len];
        }
        ScratchLengthCheck(len);
    }
    void EnsureScratchRunge45(int len){
        if(temp2 ==null){
            temp=new double[len];
            temp2=new double[len];
            temp2 =new double[len];
            temp3 =new double[len];
            dy1 =new double[len];
            dy2 =new double[len];
            dy3 =new double[len];
            dy4 =new double[len];
            dy5 =new double[len];
            dy6 =new double[len];
        }
        ScratchLengthCheck(len);
    }
    void EulerAdd(double[]state,double[]dy,double dt,double[]out){
        for (int i = 0; i < state.length; i++) {
            out[i]=state[i]+dy[i]*dt;
        }
    }
    void SetDy1(double[]state, double t,double dt){
        Derivative.Set(t,state,dy1);
        for (int i = 0; i < state.length; i++) {
            dy1[i]*=dt;
        }
    }

    void SetDy(double[]k, double t,double dt){
        Derivative.Set(t,temp,k);
        for (int i = 0; i < k.length; i++) {
            k[i]*=dt;
        }
    }
    void TempAdd(double[]a, double a2scale){
        for (int i = 0; i < a.length; i++) {
            temp[i]+=a[i]*a2scale;
        }
    }


    void IntegrateArrayList(Integrator I,ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
        if(states.size()<startStateIndex||ts.size()<startStateIndex){
            throw new IllegalArgumentException("states and ts ArrayLists must contain starting state entries");
        }
        int iState=startStateIndex;
        double t=ts.get(iState);
        while(t<tf){
            double[]state=states.get(iState);
            if(t+dt>tf){
                dt=tf-t;
            }
            iState++;
            if(states.size()<=iState){
                states.add(new double[state.length]);
            }
            double[]nextState=states.get(iState);
            I.Integrate(state,nextState,t,dt);
            t+=dt;
            if(ts.size()<=iState){
                ts.add(t);
            } else {
                ts.set(iState, t);
            }
        }
    }

    void Integrate(Integrator I,double[]state,double[]out,double t0,double tf,double dt){
        boolean first=true;
        double t=t0;
        while(t<tf){
            if(t+dt>tf){
                dt=tf-t;
            }
            if(first){
                I.Integrate(state,out,t,dt);
                first=false;
            }else {
                I.Integrate(out, out, t, dt);
            }
            t+=dt;
        }
    }

    @FunctionalInterface
    interface Integrator{
        void Integrate(double[]state,double[]out,double t,double dt);
    }

}
