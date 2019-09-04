package HAL.Interfaces;

import java.awt.event.WindowEvent;

/**
 * Created by bravorr on 1/30/17.
 */
@FunctionalInterface
public interface GuiCloseAction {
    void Action(WindowEvent e);
}
