package Examples.GameOfLife;
import HAL.Rand;
import HAL.Tools.FileIO;
import HAL.Util;

import java.util.ArrayList;

import static HAL.Util.*;

/**
 * Created by mark on 7/7/17.
 */
class ReturnVals{
    //this object will hold the results of each run
    int[] pops;
    double startLivingProb;
    ReturnVals(int timeSteps,double startLivingProb){
        pops=new int[timeSteps];
        this.startLivingProb=startLivingProb;
    }
}

public class GOLSweepEx {
    public static void main(String[] args) {
        int nRuns=100;
        ArrayList<double[]>runOutputs=new ArrayList<>(nRuns);
        for (int i = 0; i < nRuns; i++) {
            runOutputs.add(null);
        }
        MultiThread(nRuns,8,(iThread)->{
            Rand rng=new Rand();
            //the function that will run in parallel is specified here
            System.out.println("Started Thread:"+iThread);
            double startLivingProb=rng.Double();
            int runTicks=1000;
            GOLGrid model=new GOLGrid(100,100,startLivingProb,runTicks,0,null);
            double[]ret=new double[runTicks+1];
            ret[0]=startLivingProb;
            for (int i = 0; i < runTicks; i++) {
                model.StepAgents();
                ret[i+1]=model.liveCt;
            }
            System.out.println("Finished Thread:"+iThread);
            runOutputs.set(iThread,ret);
        });
        //after all runs finish, we loop through the array of ReturnVals objects and write out their data
        FileIO out=new FileIO("SweepResults.csv","w");
        out.Write("startProp,popAtEachStep\n");
        for (double[] ret : runOutputs) {
            out.Write(Util.ArrToString(ret,",")+"\n");//first entry is the starting prob of living cells
        }
        out.Close();
    }
}
