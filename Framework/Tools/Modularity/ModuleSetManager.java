package Framework.Tools.Modularity;

import Framework.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static Beta.BetaUtils.IsMethodOverridden;

/**
 * the ModuleSetManager class is used to store and use module objects. the type argument is the baseclass module type that the ModuleSetManager will manage
 */
public class ModuleSetManager<T>{
    Class<T> baseClass;
    boolean doneAddingModules=false;
    HashMap<String,ArrayList<T>> moduleLists=new HashMap<>();
    ArrayList<String>methodNames;
    ArrayList<T> modules =new ArrayList<>();

    /**
     * the module base class object should define all of the method hooks that modules can use, the behavior of the base class object will be ignored
     */
    public ModuleSetManager(Class<T> baseClass){
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
    public void AddModule(T newMod) {
        if (doneAddingModules) {
            throw new IllegalStateException("can't add more modules after running iteration");
        }
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
    public Iterable<T> IterMethod(String methodName){
        doneAddingModules=true;
        return moduleLists.get(methodName);
    }

    public int CountModsWithMethod(String methodName){
        return moduleLists.get(methodName).size();
    }
}
