package Examples.GameOfLife;
import Framework.Tools.FileIO;
import Framework.Util;

import java.util.ArrayList;
import java.util.Random;

import static Framework.Util.*;

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
        int nRuns=10;
        ArrayList<ReturnVals>runOutputs=new ArrayList<>(nRuns);
        for (int i = 0; i < nRuns; i++) {
            runOutputs.add(null);
        }
        MultiThread(nRuns,8,(iThread)->{
            Random rn=new Random();
            //the function that will run in parallel is specified here
            System.out.println("Started Thread:"+iThread);
            double startLivingProb=Math.random();
            int runTicks=1000;
            GOLGrid model=new GOLGrid(100,100,startLivingProb,runTicks,0,null);
            ReturnVals ret=new ReturnVals(runTicks,startLivingProb);
            for (int i = 0; i < runTicks; i++) {
                model.StepAgents();
                ret.pops[i]=model.liveCt;
            }
            System.out.println("Finished Thread:"+iThread);
            runOutputs.set(iThread,ret);
        });
        //after all runs finish, we loop through the array of ReturnVals objects and write out their data
        FileIO out=new FileIO("SweepResults.csv","w");
        for (ReturnVals ret : runOutputs) {
            out.Write(ret.startLivingProb+","+ Util.ArrToString(ret.pops,",")+"\n");//first entry is the starting prob of living cells
        }
        out.Close();
    }
}
