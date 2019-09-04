package HAL.Interfaces;
import HAL.GridsAndAgents.AgentBase;

/**
 * Created by rafael on 8/19/17.
 */
@FunctionalInterface
public interface AgentToString<T extends AgentBase>{
    String AtoS(T agent);
}
