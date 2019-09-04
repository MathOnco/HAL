package HAL.Tools.Internal;

import HAL.Interfaces.ParallelFunction;

/**
 * Created by rafael on 4/15/17.
 */
public class SweepRun<T> implements Runnable{
    //Runs parameter sweep, saves results to a file
    final int iParamSet;
    final ParallelFunction RunFun;
    public SweepRun(ParallelFunction RunFun, int iParamSet){
        this.iParamSet=iParamSet;
        this.RunFun =RunFun;
    }

    @Override
    public void run() {
        RunFun.Run(iParamSet);
    }
}
