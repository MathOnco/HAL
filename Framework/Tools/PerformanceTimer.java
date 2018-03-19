package Framework.Tools;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Rafael on 8/1/2017.
 */
public class PerformanceTimer implements Serializable {
    HashMap<String,Long> times=new HashMap<>();
    public void Start(String label){
        times.put(label,System.currentTimeMillis());
    }
    public long Stop(String label){
        long time=System.currentTimeMillis()-times.get(label);
        System.out.println(label+": "+time+" ms");
        return time;
    }
    public long StopQuiet(String label) {
        return System.currentTimeMillis() - times.get(label);
    }
}
