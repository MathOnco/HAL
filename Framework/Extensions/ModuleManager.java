package Framework.Extensions;

import java.util.ArrayList;

public interface ModuleManager  {
    <M extends Module>ArrayList<M> GetAllModules();//should start with empty arraylist
    int _GetNumPropsInternal();//should start with value 0
    default int GetNumProps(){
        return _GetNumPropsInternal()-1;
    }
    void _SetNumPropsInternal(int numProps);
    default boolean IsInitialized(){
        return _GetNumPropsInternal()!=0;
    }
    default void Initialize(){
        //sets up the properties of all modules
        if(IsInitialized()){
            throw new IllegalStateException("Can't initialize twice!");
        }
        ArrayList<Module> allMods=GetAllModules();
        int nProps=0;
        for (int i = 0; i < allMods.size(); i++) {
            Module mod=allMods.get(i);
            int nNeededProps=mod.GetNumNeededProps();
            for (int j = 0; j < nNeededProps; j++) {
                mod.SetModPropID(nProps,j);
                nProps++;
            }
        }
        _SetNumPropsInternal(nProps+1);
    }
    default int AddModule(Module addMe){
        int ret=-1;//used to get id of module when a new one is added
        ArrayList<Module>allMods=GetAllModules();
        //add new modules as needed
        if(!allMods.contains(addMe)){
            if(IsInitialized()){
                throw new IllegalStateException("Can't add new Module after Init!");
            }
            ret=allMods.size();
            allMods.add(addMe);
        }
        return ret;
    }
    default void InitModProps(ModularAgent initMe,double[]PropInit){
        int numProps=GetNumProps();
        if(numProps==0){
            return;
        }
        if(IsInitialized()){
            throw new IllegalStateException("Can't initialize modProps for agent before ModuleManger is initialized!");
        }
        if(initMe.GetAllModProps().length!=numProps){
            throw new IllegalStateException("Agent ModProps array has wrong length!: "+initMe.GetAllModProps().length+" expected: "+numProps);
        }
        if(initMe.GetAllModProps()==null){
            initMe._AssignModPropsInternal(new double[GetNumProps()]);
        } else if(numProps!=PropInit.length){
            throw new IllegalStateException("PropInit array has wrong length! length: "+PropInit.length+" expected: "+numProps);
        }
        System.arraycopy(PropInit,0,initMe.GetAllModProps(),0,PropInit.length);
    }
}
