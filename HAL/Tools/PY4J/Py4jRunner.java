package HAL.Tools.PY4J;

/*

GOAL: be able to add modules and manage parameters from the python side
Entities:
    Seed (can be equipped with modules), implements modular modeling
    Model (used to run with double[] param inputs and generates double[] outputs)
 */

import HAL.Util;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Py4jRunner<M extends Py4jModel> {
    public List<M> modelPool=new ArrayList<>();
    int numPoolModels;
    M seed;
    public Py4jRunner(M seed){
        this.seed=seed;
    }
    public void ResetModelPool(){
        this.modelPool= Collections.synchronizedList(new ArrayList<>());
        numPoolModels=0;
    }
    public double[]RunModel(double[]params,Py4jModel model){
        model.Reset(params);
        return model.Eval();
    }
    public double[][]EvalGen(double[][]params,int nThreads){
        double[][]ret=new double[params.length][];
        if(nThreads>1){
            Util.MultiThread(params.length,nThreads,(i)->{
                M runMe= GetModelFromPool();
                if(params[i].length!=runMe.GetParamHeaders().size()){
                    throw new IllegalStateException("parameter array of incorrect length, check the learning algorithm!");
                }
                ret[i]=RunModel(params[i],runMe);
                synchronized (modelPool) {
                    modelPool.add(runMe);
                }
            });
        }
        else{
            M runMe= GetModelFromPool();
            for (int i = 0; i < params.length; i++) {
                ret[i]=RunModel(params[i],runMe);
            }
            synchronized (modelPool) {
                modelPool.add(runMe);
            }
        }
        return ret;
    }
    public ArrayList<String> GetParamHeaders(){
        return modelPool.get(0).GetParamHeaders();
    }

    public M AddPoolModel(){
        M newModel=(M)seed.GenSeed();
        synchronized (modelPool) {
            modelPool.add(newModel);
        }
        numPoolModels++;
        return newModel;
    }

    public int NumPoolModels(){
        return numPoolModels;
    }

    public M GetModelFromPool(){
        synchronized (modelPool) {
            if (modelPool.size() > 0) {
                return modelPool.remove(modelPool.size() - 1);
            } else {
                throw new IllegalThreadStateException("model pool too small!");
            }
        }
    }
    public int NumParams(){
        return modelPool.get(0).GetParamHeaders().size();
    }

    public M GenModel(){
        return (M) seed.GenSeed();
    }
}
