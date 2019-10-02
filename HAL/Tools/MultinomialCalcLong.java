package HAL.Tools;

import HAL.Rand;

import java.io.Serializable;

public class MultinomialCalcLong extends Rand implements Serializable{
    public long popRemaining;
    public double probRemaining;
    public MultinomialCalcLong(Rand rng){
        super(rng.rn);
    }
    public void Setup(long pop){
        this.popRemaining=pop;
        this.probRemaining =1;
    }
    public long Sample(double prob){
        if(popRemaining==0||prob==0){
            return 0;
        }
        if(probRemaining-prob==0){
            long ret=popRemaining;
            popRemaining=0;
            probRemaining-=prob;
            return ret;
        }
        long popSelected=Binomial(popRemaining,prob/ probRemaining);
        probRemaining -=prob;
        if(probRemaining <0){
            throw new IllegalStateException("total probability sum for MultinomialCalc < 0! prob:"+prob+" probRemaining:"+ probRemaining);
        }
        popRemaining-=popSelected;
        return popSelected;
    }
    public long GetPopRemaining(){
        return popRemaining;
    }
    public double GetProbRemaining(){
        return probRemaining;
    }
}
