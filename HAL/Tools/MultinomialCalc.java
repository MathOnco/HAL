package HAL.Tools;

import HAL.Rand;
import HAL.Util;

import java.io.Serializable;

public class MultinomialCalc extends Rand implements Serializable{
    public int popRemaining;
    public double probRemaining;
    public MultinomialCalc(Rand rng){
        super(rng.rn);
    }
    public void Setup(int pop){
        this.popRemaining=pop;
        this.probRemaining =1;
    }
    public int Sample(double prob){
        if(popRemaining==0||prob==0){
            return 0;
        }
        if(probRemaining-prob<=0){
            int ret=popRemaining;
            popRemaining=0;
            probRemaining-=prob;
            return ret;
        }
        int popSelected=Binomial(popRemaining,prob/ probRemaining);
        probRemaining -=prob;
        popRemaining-=popSelected;
        return popSelected;
    }
    public int GetPopRemaining(){
        return popRemaining;
    }
    public double GetProbRemaining(){
        return probRemaining;
    }
}
