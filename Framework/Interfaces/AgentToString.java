package Framework.Interfaces;
import Framework.GridsAndAgents.AgentBase;

/**
 * Created by rafael on 8/19/17.
 */
@FunctionalInterface
public interface AgentToString<T extends AgentBase>{
    String AtoS(T agent);
}
