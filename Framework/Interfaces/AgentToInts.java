package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBaseSpatial;

/**
 * Created by rafael on 9/7/17.
 */
@FunctionalInterface
public interface AgentToInts <T>{
    int[] AgentToInts(T agent);
}
