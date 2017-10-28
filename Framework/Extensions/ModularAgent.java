package Framework.Extensions;

public interface ModularAgent {
    double[]GetAllModProps();
    void _AssignModPropsInternal(double[] ModProps);
    default double GetProp(int id){
        return GetAllModProps()[id];
    }
    default void SetProp(int id, double val){
        GetAllModProps()[id]=val;
    }
    default void AddProp(int id, double val){
        GetAllModProps()[id]+=val;
    }
    default void MulProp(int id, double val){
        GetAllModProps()[id]*=val;
    }
}
