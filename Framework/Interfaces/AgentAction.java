package Framework.Interfaces;

import Framework.GridsAndAgents.AgentBase;

public interface AgentAction {
    public <T extends AgentBase> void AgentAction(T agent,int countValidAgents);
}
