package Testing;

import Framework.Rand;
import Framework.Tools.PerformanceTimer;

public class RandSpeedTest {
    static int GetZero(int bound){
        if(bound==1){
            return 0;
        }
        return -1;
    }
    public static void main(String[] args) {
        Rand rn=new Rand();
        PerformanceTimer pt=new PerformanceTimer();
        int x=0;
        pt.Start("oldRand");
        for (int i = 0; i < 10000000; i++) {
            x+=rn.Int(1);
        }
        pt.Stop("oldRand");
        pt.Start("newRand");
        for (int i = 0; i < 10000000; i++) {
            x+=GetZero(1);
        }
        pt.Stop("newRand");
    }
}
