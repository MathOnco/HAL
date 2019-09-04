package HAL.Tools.PY4J;


import java.util.ArrayList;

public interface Py4jModel<M extends Py4jModel> {
    M GenSeed();
    void Reset(double[]params);
    double[] Eval();
    ArrayList<String> GetParamHeaders();
    ArrayList<String> GetResultHeaders();
}
