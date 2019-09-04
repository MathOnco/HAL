package HAL.Tools.ODESolver;

import java.io.Serializable;
import java.util.ArrayList;

import static HAL.Util.Norm;

/**
 * use this class to solve any ODE system. the integrator and state array size can be changed at any time
 * currently Euler and Runge-Kutta 4 can be used as fixed-step-size integrators
 * currently Runge-Kutta Fehlberg 4,5 exists as an adaptive-step-size integrator
 */

public class ODESolver implements Serializable{
    protected ArrayList<double[][]> scratches=new ArrayList<>();
    int lenSet;
    int numSet;
    protected double[] s0;
    protected double[] s1;
    protected double[] s2;
    protected double[] s3;
    protected double[] s4;
    protected double[] s5;
    protected double[] s6;
    protected double[] s7;
    protected double[] s8;

    /**
     * runs 1 round of Euler integration, putting the result in the out array
     */
    public void Euler(Derivative Derivative,double[]state,double[]out,double t,double dt) {
        SetupScratch(state.length,1);
        Derivative.Set(t,state, s0);
        EulerAdd(state, s0, dt, out);
    }
    /**
     * runs 1 round of Euler integration, putting the result in the state array
     */
    public void Euler(Derivative Derivative,double[]state,double t,double dt) {
        Euler(Derivative,state,state,t,dt);
    }

    /**
     * runs Euler integration from t0 to tf in increments of dt
     */
    public void Euler(Derivative Derivative,double[]state,double[]out,double t0,double tf,double dt){
        Integrate(Derivative,this::Euler,state,out,t0,tf,dt);
    }

    /**
     * runs Euler integration from t0 to tf in increments of dt, puts the resulting states in the states array, and the resulting ts in the ts array
     */
    public void Euler(Derivative Derivative,ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
        IntegrateArrayList(Derivative,this::Euler,states,ts,tf,dt,startStateIndex);
    }

    /**
     * runs 1 round of Runge-Kutta 4 integration, putting the result in the out array
     */
    public void Runge4(Derivative Derivative,double[]state,double[]out,double t,double dt) {
        double halfStep = dt / 2.0;
        SetupScratch(state.length,5);
        Derivative.Set(t,state, s1);
        EulerAdd(state, s1, halfStep, s0);
        Derivative.Set(t, s0, s2);
        EulerAdd(state, s2, halfStep, s0);
        Derivative.Set(t, s0, s3);
        EulerAdd(state, s3, dt, s0);
        Derivative.Set(t, s0, s4);
        for (int i = 0; i < state.length; i++) {
            out[i] = state[i]+(s1[i] + 2 * s2[i] + 2 * s3[i] + s4[i]) * dt / 6.0;
        }
    }


    /**
     * runs 1 round of Runge-Kutta 4 integration, putting the result in the state array
     */
    public void Runge4(Derivative Derivative,double[]state,double t,double dt){
        Runge4(Derivative,state,state,t,dt);
    }

    /**
     * runs Runge-Kutta 4 integration from t0 to tf in increments of dt
     */
    public void Runge4(Derivative Derivative,double[]state,double t0,double tf,double dt){
        Integrate(Derivative,this::Runge4,state,state,t0,tf,dt);
    }

    /**
     * runs Runge-Kutta 4,5 integration from t0 to tf in increments of dt, puts the resulting states in the states array, and the resulting ts in the ts array
     */
    public void Runge4(Derivative Derivative,ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
        IntegrateArrayList(Derivative,this::Runge4,states,ts,tf,dt,startStateIndex);
    }

    /**
     * calls runge kutta 4,5 iteratively, updating the state array until tf. returns the final dt that works for the provided tolerance. dtStart is the starting stepsize
     */
    public double Runge45(Derivative Derivative,double[] state,double[]out,double t0,double tf,double dtStart,double errorTolerance){
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
            error = Runge45internal(Derivative,state, t, dt, errorTolerance);
            scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            if(error>errorTolerance){
                dt*=scale;
                error = Runge45internal(Derivative,state, t, dt, errorTolerance);
                scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            }
            //update state
            if(first) {
                for (int i = 0; i < state.length; i++) {
                    out[i] = state[i]+ s7[i];
                }
                first=false;
            } else{
                for (int i = 0; i < state.length; i++) {
                    out[i] = out[i]+ s7[i];
                }
            }
            //advance t
            t+=dt;
            dt*=scale;
        }
        return dt;
    }
    /**
     * calls runge kutta 4,5 iteratively, updating the state array until tf. returns the final dt that works for the provided tolerance. dtStart is the starting stepsize
     */
    public double Runge45(Derivative Derivative,double[] state,double[]out,double t0,double tf,double dtStart,double errorTolerance,double dtMin,double dtMax) {
        if(dtMax<dtMin){
            throw new IllegalArgumentException("dtMax can't be less than dtMin!");
        }
        boolean first = true;
        double dt = dtStart;
        double t = t0;
        double error, scale;
        while (t < tf) {
            //calc dt
            if (dt > tf - t) {
                dt = tf - t;
            }
            //calc next state
            error = Runge45internal(Derivative, state, t, dt, errorTolerance);
            scale = Math.pow(0.84 * (errorTolerance / error), (1.0 / 4));
            if (dt > dtMin && error > errorTolerance) {
                dt *= scale;
                if (dt < dtMin) {
                    dt = dtMin;
                } else if (dt < dtMin) {
                    dt = dtMin;
                }
                error = Runge45internal(Derivative, state, t, dt, errorTolerance);
                scale = Math.pow(0.84 * (errorTolerance / error), (1.0 / 4));
            }
            //update state
            if (first) {
                for (int i = 0; i < state.length; i++) {
                    out[i] = state[i] + s7[i];
                }
                first = false;
            } else {
                for (int i = 0; i < state.length; i++) {
                    out[i] = out[i] + s7[i];
                }
            }
            //advance t
            t += dt;
            dt *= scale;
            if (dt > dtMax) {
                dt = dtMax;
            } else if (dt < dtMin) {
                dt = dtMin;
            }
        }
        return dt;
    }
    public double Runge45(Derivative Derivative,double[] state,double t0,double tf,double dtStart,double errorTolerance){
        return Runge45(Derivative,state,state,t0,tf,dtStart,errorTolerance);
    }
    public double Runge45(Derivative Derivative,double[] state,double t0,double tf,double dtStart,double errorTolerance,double dtMin,double dtMax){
        return Runge45(Derivative,state,state,t0,tf,dtStart,errorTolerance,dtMin,dtMax);
    }

    /**
     * calls runge kutta 4,5 iteratively, putting new states and the times for which those states were computed into the states and ts arraylists. dtStart is the starting stepsize
     */
    public int Runge45(Derivative Derivative,ArrayList<double[]> states, ArrayList<Double> ts,double tf,double dtStart,double errorTolerance,int startStateIndex){
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
            error = Runge45internal(Derivative,state, t, dt, errorTolerance);
            scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            if(error>errorTolerance){
                dt*=scale;
                error = Runge45internal(Derivative,state, t, dt, errorTolerance);
                scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            }
            //update state
            iState++;
            if(states.size()<=iState){
                states.add(new double[state.length]);
            }
            double[]nextState=states.get(iState);
            for (int i = 0; i < state.length; i++) {
                nextState[i]=state[i]+ s7[i];
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
    /**
     * calls runge kutta 4,5 iteratively, putting new states and the times for which those states were computed into the states and ts arraylists. dtStart is the starting stepsize
     */
    public int Runge45(Derivative Derivative,ArrayList<double[]> states, ArrayList<Double> ts,double tf,double dtStart,double errorTolerance,int startStateIndex,double dtMin,double dtMax){
        if(states.size()<startStateIndex||ts.size()<startStateIndex){
            throw new IllegalArgumentException("states and ts ArrayLists must contain starting state entries");
        }
        if(dtMax<dtMin){
            throw new IllegalArgumentException("dtMax can't be less than dtMin!");
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
            } else if(dt<dtMin){
                dt=dtMin;
            }
            //calc next state
            error = Runge45internal(Derivative,state, t, dt, errorTolerance);
            scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            if(dt>dtMin&&error>errorTolerance){
                dt*=scale;
                if(dt<dtMin){
                    dt=dtMin;
                }
                error = Runge45internal(Derivative,state, t, dt, errorTolerance);
                scale=Math.pow(0.84*(errorTolerance/error),(1.0/4));
            }
            //update state
            iState++;
            if(states.size()<=iState){
                states.add(new double[state.length]);
            }
            double[]nextState=states.get(iState);
            for (int i = 0; i < state.length; i++) {
                nextState[i]=state[i]+ s7[i];
            }
            //advance t
            t+=dt;
            if(ts.size()<=iState){
                ts.add(t);
            } else {
                ts.set(iState, t);
            }
            dt*=scale;
            if(dt>dtMax){
                dt=dtMax;
            } else if(dt<dtMin){
                dt=dtMin;
            }
        }
        return iState;
    }


    double Runge45internal(Derivative Derivative,double[]state,double t,double dt,double tol){
        SetupScratch(state.length,9);
        SetDy1(Derivative,state,t,dt);
        System.arraycopy(state,0, s0,0,state.length);
        TempAdd(s1,1.0/4);
        SetDy(Derivative,s2,t+dt*1.0/4,dt);
        System.arraycopy(state,0, s0,0,state.length);
        TempAdd(s1,3.0/32);
        TempAdd(s2,9.0/32);
        SetDy(Derivative,s3,t+dt*3.0/8,dt);
        System.arraycopy(state,0, s0,0,state.length);
        TempAdd(s1,1932.0/2197);
        TempAdd(s2,-7200.0/2197);
        TempAdd(s3,7296.0/2197);
        SetDy(Derivative,s4,t+dt*12.0/13,dt);
        System.arraycopy(state,0, s0,0,state.length);
        TempAdd(s1,439.0/216);
        TempAdd(s2,-8.0);
        TempAdd(s3,3680.0/513);
        TempAdd(s4,-845.0/4104);
        SetDy(Derivative,s5,t+dt,dt);
        System.arraycopy(state,0, s0,0,state.length);
        TempAdd(s1,-8.0/27);
        TempAdd(s2,2.0);
        TempAdd(s3,-3544.0/2565);
        TempAdd(s4,1859.0/4104);
        TempAdd(s5,-11.0/40);
        SetDy(Derivative,s6,t+dt/2.0,dt);
        for (int i = 0; i < state.length; i++) {
            //RK4
            s0[i] = state[i] + 25.0 / 216 * s1[i] + 1408.0 / 2565 * s3[i] + 2197.0 / 4104 * s4[i] - 1.0 / 5 * s5[i];
            //RK5
            s7[i] = 16.0 / 135 * s1[i] + 6656.0 / 12825 * s3[i] + 28561.0 / 56430 * s4[i] - 9.0 / 50 * s5[i] + 2.0 / 55 * s6[i];
        }
        for (int i = 0; i < state.length; i++) {
            s8[i]= (s7[i]+state[i])- s0[i];
        }
        return (1.0/dt)*(Norm(s8));
    }


    void SetupScratch(int len,int num){
        //already set
        if(lenSet==len&&numSet>=num){
            return;
        }
        lenSet=len;
        numSet=num;
        //add new scratches entries if needed
        if(scratches.size()<len){
            for (int i = scratches.size(); i < len; i++) {
                scratches.add(null);
            }
        }
        //add more scratch to scratches entry if needed
        double[][]ss=scratches.get(len-1);
        if(ss==null||ss.length<num){
            ss=new double[num][len];
           scratches.set(len-1,ss);
        }
        for (int i = 0; i < num; i++) {
            switch (i){
                case 0: s0=ss[i];break;
                case 1: s1=ss[i];break;
                case 2: s2=ss[i];break;
                case 3: s3=ss[i];break;
                case 4: s4=ss[i];break;
                case 5: s5=ss[i];break;
                case 6: s6=ss[i];break;
                case 7: s7=ss[i];break;
                case 8: s8=ss[i];break;
            }
        }
    }

//    void SetupScratchRunge(int len){
//        if(lenSet==len&&numSet>=5){
//            return;
//        }
//        if(scratches.size()<=len){
//            scratches.ensureCapacity(len);
//            for (int i = scratches.size(); i <= len; i++) {
//                scratches.add(null);
//            }
//            double[][]mydys= scratches.get(len);
//            if(mydys==null||mydys.length<5){
//                scratches.set(len,new double[5][len]);
//            }
//        }
//        if(s2 ==null){
//            s0 =new double[len];
//            s1 =new double[len];
//            s2 =new double[len];
//            s3 =new double[len];
//            s4 =new double[len];
//        }
//    }
//    void SetupScratchEuler(int len){
//        if(lenSet==len&&numSet>=1){
//            return;
//        }
//        if(s1 ==null){
//            s1 =new double[len];
//        }
//    }
//    void SetupScratchRunge45(int len){
//        if(lenSet==len&&numSet>=1){
//            return;
//        }
//        if(s7 ==null){
//            s0 =new double[len];
//            s7 =new double[len];
//            s7 =new double[len];
//            s8 =new double[len];
//            s1 =new double[len];
//            s2 =new double[len];
//            s3 =new double[len];
//            s4 =new double[len];
//            s5 =new double[len];
//            s6 =new double[len];
//        }
//    }
    void EulerAdd(double[]state,double[]dy,double dt,double[]out){
        for (int i = 0; i < state.length; i++) {
            out[i]=state[i]+dy[i]*dt;
        }
    }
    void SetDy1(Derivative Derivative,double[]state, double t,double dt){
        Derivative.Set(t,state, s1);
        for (int i = 0; i < state.length; i++) {
            s1[i]*=dt;
        }
    }

    void SetDy(Derivative Derivative,double[]k, double t,double dt){
        Derivative.Set(t, s0,k);
        for (int i = 0; i < k.length; i++) {
            k[i]*=dt;
        }
    }
    void TempAdd(double[]a, double a2scale){
        for (int i = 0; i < a.length; i++) {
            s0[i]+=a[i]*a2scale;
        }
    }


    void IntegrateArrayList(Derivative Derivative,Integrator I,ArrayList<double[]> states,ArrayList<Double> ts,double tf,double dt,int startStateIndex){
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
            I.Integrate(Derivative,state,nextState,t,dt);
            t+=dt;
            if(ts.size()<=iState){
                ts.add(t);
            } else {
                ts.set(iState, t);
            }
        }
    }

    void Integrate(Derivative Derivative,Integrator I,double[]state,double[]out,double t0,double tf,double dt){
        boolean first=true;
        double t=t0;
        while(t<tf){
            if(t+dt>tf){
                dt=tf-t;
            }
            if(first){
                I.Integrate(Derivative,state,out,t,dt);
                first=false;
            }else {
                I.Integrate(Derivative,out, out, t, dt);
            }
            t+=dt;
        }
    }

    @FunctionalInterface
    interface Integrator{
        void Integrate(Derivative Derivative,double[]state,double[]out,double t,double dt);
    }

}
