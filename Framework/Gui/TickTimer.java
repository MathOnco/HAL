package Framework.Gui;

/**
 * class used to impose a frame rate on the running of a model
 * call TickPause every timestep to impose a framerate
 * Created by rafael on 2/16/17.
 */
public class TickTimer {
    private long lastSleepTime;

    /**
     * waits until the time since the last TickPause is equal to millis
     * @param millis wait duration
     */
    public void TickPause(long millis){
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
