package HAL.Interfaces;

/**
 * Created by Rafael on 10/26/2017.
 */
@FunctionalInterface
public interface ParamSetChangeAction {
    void Action(String name,String val,String PrevVal);
}
