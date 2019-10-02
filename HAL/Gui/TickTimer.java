package HAL.Gui;

/**
 * class used to impose a frame rate on the running of a model
 * call TickPause every timestep to impose a framerate
 * usually used within a window of some kind, but can also be used by itself
 * Created by rafael on 2/16/17.
 */
public class TickTimer {
    private long lastSleepTime;

    /**
     * pauses execution so that the amount of time between calls to TickPause is equal to millis
     * @param millis wait duration
     */
    public void TickPause(int millis){
        if (lastSleepTime == 0) {
            lastSleepTime = System.currentTimeMillis();
            return;
        }
        long currTime=System.currentTimeMillis();
        long waitTime=millis-(currTime-lastSleepTime);
        if(waitTime>0){
            try {
                Thread.sleep(waitTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        lastSleepTime=System.currentTimeMillis();
    }
}
