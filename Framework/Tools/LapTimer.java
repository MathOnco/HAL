package Framework.Tools;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Rafael on 8/1/2017.
 */
public class LapTimer implements Serializable {
    long time=System.currentTimeMillis();
    public long Lap(String label){
        long time=System.currentTimeMillis();
        long delta=time-this.time;
        if(label!=null) {
            System.out.println(label + ": " + delta + " ms");
        }
        this.time=time;
        return delta;
    }
    public long Lap(){
        long time=System.currentTimeMillis();
        long delta=time-this.time;
        this.time=time;
        return delta;
    }
}
