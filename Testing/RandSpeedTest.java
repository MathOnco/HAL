package Testing;

import Framework.Rand;
import Framework.Tools.LapTimer;

public class RandSpeedTest {
    static int GetZero(int bound){
        if(bound==1){
            return 0;
        }
        return -1;
    }
    public static void main(String[] args) {
        Rand rn=new Rand();
        int x=0;
        LapTimer t=new LapTimer();
        for (int i = 0; i < 10000000; i++) {
            x+=rn.Int(1);
        }
        t.Lap("oldRand");
        for (int i = 0; i < 10000000; i++) {
            x+=GetZero(1);
        }
        t.Lap("newRand");
    }
}
