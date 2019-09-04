package HAL.Tools.Modularity;

import java.io.Serializable;

/**
 * the VarSetManager class maintains a count of parameters that are requested for an agent class that implements the
 * VarSet interface. this class is useful along with the ModuleSetManger to add agent variables that are only
 * manipulated by one module
 */
public class VarSetManager implements Serializable{
    private boolean setup = false;
    int nParams;

    /**
     * generates a new variable as part of the var array, returns the index of the variable
     */
    public int NewVar() {
        if (setup) {
            throw new IllegalStateException("can't generate varSets before done adding varManager!");
        }
        nParams++;
        return nParams - 1;
    }

    /**
     * adds a new variable set to the given agent. does nothing if the variable set has already been initialized
     */
    public <agentType extends VarSet> void AddVarSet(agentType agent) {
        setup = true;
        if (agent.GetVars() == null && nParams > 0) {
            agent.SetVars(new double[nParams]);
        }
    }

    public double[] GenVarSet(){
        setup = true;
        return new double[nParams];
    }

    public int NumParams(){
        return nParams;
    }
}
