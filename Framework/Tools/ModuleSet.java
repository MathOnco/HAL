package Framework.Tools;

import Framework.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import static Beta.BetaUtils.IsMethodOverridden;

/**
 * the ModuleSet class is used to store and use module objects. the type argument is the baseclass module type that the ModuleSet will manage
 */
public class ModuleSet <T>{
    Class<T> baseClass;
    boolean doneAddingModules=false;
    HashMap<String,ArrayList<T>> moduleLists=new HashMap<>();
    ArrayList<String>methodNames;
    ArrayList<T> modules =new ArrayList<>();

    /**
     * the module base class object should define all of the method hooks that modules can use, the behavior of the base class object will be ignored
     */
    public ModuleSet(Class<T> baseClass){
        this.baseClass=baseClass;
        Method[] baseMethods=baseClass.getDeclaredMethods();
        methodNames=new ArrayList<>();
        for (Method method : baseMethods) {
            moduleLists.put(method.getName(),new ArrayList<>());
        }
    }

    /**
     * adds a module to the ModuleSet. the module should override any functions in the baseClass that the will be used with the model
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
}
