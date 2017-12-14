package Framework.Tools;

public class KeyRecorder {
    public final boolean[] keys = new boolean[525];

    public boolean KeyPress(char keyCode) {
        //returns whether key was recorded as already being pressed
        if (keyCode < keys.length) {
            boolean ret = !keys[keyCode];
            keys[keyCode] = true;
            return ret;
        }
        return false;
    }

    public boolean KeyRelease(char keyCode) {
        //returns wether key was recorded as already being released
        if (keyCode < keys.length) {
            boolean ret = keys[keyCode];
            keys[keyCode] = false;
            return ret;
        }
        return false;
    }

    public boolean IsPressed(char keyCode) {
        //returns whether key is recorded as currently pressed
        return keys[keyCode];
    }
}
