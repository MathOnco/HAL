package Testing.OldTests;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;

/**
 * Created by Rafael on 10/28/2017.
 */

class Mover2D extends AgentSQ2Dunstackable<MovementTest> {

}
public class MovementTest extends AgentGrid2D<Mover2D> {
    public MovementTest(int x, int y) {
        super(x, y, Mover2D.class);
    }
}
