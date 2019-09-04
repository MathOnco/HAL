package HAL.Tools.Internal;

import java.awt.event.KeyEvent;

public class KeyRecorder {
    public final boolean[] keys = new boolean[525];

    public boolean KeyPress(int keyCode) {
        //returns whether key was recorded as already being pressed
        if (keyCode < keys.length) {
            boolean ret = !keys[keyCode];
            keys[keyCode] = true;
            return ret;
        }
        return false;
    }

    public boolean KeyRelease(int keyCode) {
        //returns wether key was recorded as already being released
        if (keyCode < keys.length) {
            boolean ret = keys[keyCode];
            keys[keyCode] = false;
            return ret;
        }
        return false;
    }

    public boolean IsPressed(int keyCode) {
        //returns whether key is recorded as currently pressed
        return keys[keyCode];
    }
    public boolean IsPressed(char c) {
        int code=KeyEvent.getExtendedKeyCodeForChar(c);
        //returns whether key is recorded as currently pressed
        return keys[code];
    }
}
