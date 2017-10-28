package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Extensions.MarkModel_II.MarkCell_II;
import Framework.Extensions.ModularAgent;

/**
 * Created by Rafael on 10/25/2017.
 */
public class DrugCell extends MarkCell_II<DrugCell,MarkModelPlusDrugs> implements ModularAgent {
    @Override
    public double[] GetAllModProps() {
        return null;
    }


    @Override
    public void _AssignModPropsInternal(double[] modProps) {
    }
    @Override
    public DrugCell Divide(int i){
        Chemo Chemo=(Chemo)G().GetDrug(MarkModelPlusDrugs.CHEMO);
        if(Chemo!=null&&Chemo.CheckDivideKill(this)){
            Die(G().DISPOSE_PROB_APOP);
            return null;
        } else {
            return super.Divide(i);
        }
    }
}
