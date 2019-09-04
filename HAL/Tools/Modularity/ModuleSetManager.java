package HAL.Tools.Modularity;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;

import static HAL.Util.IsMethodOverridden;

/**
 * the ModuleSetManager class is used to store and use module objects. the type argument is the baseclass module type that the ModuleSetManager will manage
 */
public class ModuleSetManager<baseModule>{
    public Class<baseModule> baseClass;
    public HashMap<String,ArrayList<baseModule>> moduleLists=new HashMap<>();
    public ArrayList<String>methodNames;
    public ArrayList<baseModule> modules =new ArrayList<>();

    /**
     * the module base class object should define all of the method hooks that modules can use, the behavior of the base class object will be ignored
     */
    public ModuleSetManager(Class<baseModule> baseClass){
        this.baseClass=baseClass;
        Method[] baseMethods=baseClass.getDeclaredMethods();
        methodNames=new ArrayList<>();
        for (Method method : baseMethods) {
            moduleLists.put(method.getName(),new ArrayList<>());
            methodNames.add(method.getName());
        }
    }

    /**
     * adds a module to the ModuleSetManager. the module should override any functions in the baseClass that the will be used with the model
     */
    public void AddModule(baseModule newMod) {
        for (String method : methodNames) {
            if(IsMethodOverridden(newMod.getClass(),baseClass,method)){
                moduleLists.get(method).add(newMod);
            }
        }
        modules.add(newMod);
    }

    /**
     * use with foreach loop to iterate over modules that override a given method. used to run the module functions when appropriate
     */
    public Iterable<baseModule> Iter(String methodName){
        return moduleLists.get(methodName);
    }

    public int CountModsWithMethod(String methodName){
        return moduleLists.get(methodName).size();
    }
}
