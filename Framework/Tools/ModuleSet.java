package Framework.Tools;

import Framework.Util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ModuleSet <T>{
    Class<T> baseClass;
    boolean doneAddingModules=false;
    HashMap<String,ArrayList<T>> moduleLists=new HashMap<>();
    ArrayList<String>methodNames;
    ArrayList<T> modules =new ArrayList<>();

    public ModuleSet(Class<T> baseClass){
        this.baseClass=baseClass;
        Method[] baseMethods=baseClass.getDeclaredMethods();
        methodNames=new ArrayList<>();
        for (Method method : baseMethods) {
            moduleLists.put(method.getName(),new ArrayList<>());
            methodNames.add(method.getName());
        }
    }
    public int AddModule(T newMod) {
        if (doneAddingModules) {
            throw new IllegalStateException("can't add more modules after running iteration");
        }
        for (String method : methodNames) {
            if(Util.IsMethodOverridden(newMod.getClass(),baseClass,method)){
                moduleLists.get(method).add(newMod);
            }
        }
        modules.add(newMod);
        return modules.size()-1;
    }
    public T GetModule(int index){
        return modules.get(index);
    }
    public Iterable<T> IterMethod(String methodName){
        doneAddingModules=true;
        return moduleLists.get(methodName);
    }
}
