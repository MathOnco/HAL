package Framework.Tools.Modularity;

public class VarSetManager <T extends VarSet>{
    private boolean setup=false;
    int nParams;
    public int NewVar(){
        if(setup){
            throw new IllegalStateException("can't generate varSets before done adding vars!");
        }
        nParams++;
        return nParams-1;
    }

    public void AddVarSet(T agent){
        setup=true;
        if(agent.getVars()==null&&nParams>0){
            agent.setVars(new double[nParams]);
        }
    }
}
