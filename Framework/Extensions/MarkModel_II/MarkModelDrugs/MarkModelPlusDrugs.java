package Framework.Extensions.MarkModel_II.MarkModelDrugs;

import Framework.Extensions.MarkModel_II.MarkModel_II;
import Framework.Extensions.ModuleManager;
import Framework.Rand;

import java.util.ArrayList;

public class MarkModelPlusDrugs extends MarkModel_II<DrugCell> implements ModuleManager{
    public static int CHEMO=0,HAP=1,RAP=2,BICARB=3,ANTI_ANGIO=4,VESSEL_STABILIZER=5,NUM_DRUGS=6;
    int[] drugIDs =new int[NUM_DRUGS];
    protected double[] intensities;
    public double AngioProb=1;
    ArrayList<Drug>drugs=new ArrayList<>();
    ArrayList<Drug>diffusingDrugs=new ArrayList<>();
    private int _numPropsInternal =0;
    public MarkModelPlusDrugs(int x, int y, boolean reflectiveBoundary, boolean setupConstants, Rand rn) {
        super(x, y, reflectiveBoundary, setupConstants, DrugCell.class,rn);
        for (int i = 0; i < drugIDs.length; i++) {
            drugIDs[i]=-1;//deactivated by default
        }
    }
    @Override
    public ArrayList<Drug> GetAllModules() {
        return drugs;
    }

    @Override
    public int _GetNumPropsInternal() {
        return _numPropsInternal;
    }

    @Override
    public void _SetNumPropsInternal(int numProps) {
        _numPropsInternal=numProps;
    }
    public void AddDrug(Drug addMe){
        if(drugIDs[addMe.id]!=-1){
            throw new IllegalStateException("Can't set same drug twice!");
        }
        drugIDs[addMe.id]=AddModule(addMe);
        if(addMe.diffusing){
            diffusingDrugs.add(addMe);
        }
    }

    @Override
    public void SetupConstants(){
        super.SetupConstants();
        int ctControllable=0;
        for (Drug drug : diffusingDrugs) {
            ctControllable+=drug.controllable?1:0;
            drug.SetDiffRate();
        }
        intensities=new double[ctControllable];
    }
    @Override
    public double GetMaxDiffRate(){
        double ret = super.GetMaxDiffRate();
        for (Drug drug : diffusingDrugs) {
            ret=Math.max(ret,drug.GetDiffRateBase());
        }
        return ret;
    }
    @Override
    public boolean IsSteady(){
        boolean ret=super.IsSteady();
        if(!ret){
            return false;
        }
        double maxMaxDif=Double.MIN_VALUE;
        for (Drug drug : diffusingDrugs) {
            maxMaxDif+=Math.max(maxMaxDif,drug.GetMaxDif());
            if(maxMaxDif>ADI_STOP_DIF){
                return false;
            }
        }
        return true;
    }
    @Override
    public int SteadyStateDiff(boolean setBoundary, int minSteps, int maxSteps){
        int intensitiesCt=0;
        for (Drug drug : drugs) {
            drug.PreDiffusionStep(intensities[intensitiesCt]);
            if(drug.controllable){
                intensitiesCt++;
            }
        }
        int ret=super.SteadyStateDiff(setBoundary,minSteps,maxSteps);
        intensitiesCt=0;
        for (Drug drug : drugs) {
            drug.PostDiffusionStep(intensities[intensitiesCt]);
            if(drug.controllable){
                intensitiesCt++;
            }
        }
        return ret;
    }

    @Override
    public void ADIStep(){
        ADIFirstHalf();
        for (Drug drug : diffusingDrugs) {
            drug.ADIFirstHalf();
        }
        for (DrugCell vessel : vesselList) {
            vessel.Metabolism();
            for (Drug drug : diffusingDrugs) {
                drug.VesselReaction(vessel);
            }
        }
        ADISecondHalf();
        for (Drug drug : diffusingDrugs) {
            drug.ADISecondHalf();
        }
    }

    public Drug GetDrug(int drug){
        return drugIDs[drug]==-1?null:drugs.get(drugIDs[drug]);
    }

}
