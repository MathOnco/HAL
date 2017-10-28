package Framework.Extensions.MarkModel_II;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;

import java.util.Random;

import static Framework.Utils.*;

/**
 * Created by Rafael on 10/10/2017.
 */
//angiogenic phenotype axis may be interesting to include in new model version
    //phenotypic axis one side is antiangio, other is proangio
    //microenvironment informs angio vs antiangio decision, and mutation affects mapping, possibly with bias factor
    //grow homeostatic tissue with normal cells using this system

public class MarkCell_II<A extends MarkCell_II,G extends MarkModel_II<A>> extends AgentSQ2Dunstackable<G> {
    public int type;
    public int drawColor;
    public double cycleRemaining;
    public double glycRate;
    public double acidResistPH;
    public double availableATPprop;
    public double disposeProb;//also used to store vessel HP if type is vessel
    public A Divide(int index){
        return G().NewAgentSQ(index);
    }

    public A InitVessel() {
        this.type = MarkModel_II.VESSEL;
        this.disposeProb = G().VESSEL_HP_MAX;
        this.drawColor = MarkModel_II.VESSEL_COLOR;
        G().vesselList.add((A)this);
        return (A) this;
    }

    public A InitNormal(double startCycleTime) {
        this.type = MarkModel_II.NORMAL;
        this.acidResistPH=G().NORMAL_PHENO_ACID_RESIST;
        this.glycRate=G().NORMAL_PHENO_GLYC;
        this.drawColor = MarkModel_II.NORMAL_COLOR;
        cycleRemaining=startCycleTime;
        return (A) this;
    }
    public A InitNormal() {
        return InitNormal(1);
    }

    public A InitTumor(double glycRate, double acidResistPH,double startCycleTime) {
        this.type = MarkModel_II.TUMOR;
        this.glycRate=glycRate;
        this.acidResistPH=acidResistPH;
        this.drawColor = CbCrPlaneColor(GetGlycPheno(glycRate),GetAcidResistPheno(acidResistPH));
        cycleRemaining=startCycleTime;
        return (A) this;
    }

    public A InitTumor(double glycRate, double acidResistPH){
        return InitTumor(glycRate,acidResistPH,1);
    }


    public double GetConc(int diffusible) {
        return G().GetDiff(diffusible).Get(Isq());
    }

    public void AddConc(int diffusible, double val) {
        G().GetDiff(diffusible).Add(Isq(), val);
    }

    public void SetConc(int diffusible, double val) {
        G().GetDiff(diffusible).Set(Isq(), val);
    }

    public void Die(double disposeProb) {
        if(type== MarkModel_II.VESSEL){
            G().vesselList.remove(this);
        }
        this.type = MarkModel_II.DEAD;
        this.drawColor = disposeProb==G().DISPOSE_PROB_NECRO? MarkModel_II.NECRO_COLOR: MarkModel_II.APOP_COLOR;
        this.disposeProb = disposeProb;
    }
    public double GetGlycRate(double pheno) {
        return Math.exp(pheno * Math.log(G().MAX_PHENO_GLYC));
    }

    public double GetAcidResistPH(double pheno) {
        return RescaleMinToMax(pheno, G().NORMAL_PHENO_ACID_RESIST, G().MAX_PHENO_ACID_RESIST);
    }

    public double GetGlycPheno(double glycRate) {
        return Math.log10(glycRate) / Math.log10(G().MAX_PHENO_GLYC);
    }

    public double GetAcidResistPheno(double acidResistPH) {
        return Rescale0to1(acidResistPH, G().NORMAL_PHENO_ACID_RESIST, G().MAX_PHENO_ACID_RESIST);
    }

    public A Mutate(Random rn) {
        //uniform for acid resist, exponential for glycolytic
        double newGlycPheno=Bound(GetGlycPheno(glycRate) + (rn.nextDouble() * 2 - 1) * G().MUT_RATE_GLYC, 0, 1);
        double newAcidResistPheno=Bound(GetAcidResistPheno(acidResistPH) + (rn.nextDouble() * 2 - 1) * G().MUT_RATE_ACID_RESIST, 0, 1);
        glycRate=GetGlycRate(newGlycPheno);
        acidResistPH= GetAcidResistPH(newAcidResistPheno);
        drawColor = CbCrPlaneColor(newGlycPheno,newAcidResistPheno);
        return (A) this;
    }
    public boolean AttemptDivideTumor(){
        //tumor cells can either divide into empty space or degrade vessels
        int nIs = HoodToIs(G().tumorHood, G().hoodIs);
        int nOptions = 0;
        int nVessels = 0;
        for (int i = 0; i < nIs; i++) {
            int index=G().hoodIs[i];
            MarkCell_II isVessel = G().GetAgent(index);
            if (isVessel == null){
                G().hoodIs[nOptions]=index;
                nOptions++;
            }else if(isVessel.type == MarkModel_II.VESSEL) {
                G().vesselIs[nVessels] = index;
                nVessels++;
            }
        }
        if (nOptions > 0) {
            int chosenI = G().hoodIs[G().rn.nextInt(nOptions)];
            //division of tumor cell
            MarkCell_II c=Divide(chosenI);
            if(c==null){
                return false;
            }
            c.InitTumor(glycRate, acidResistPH).Mutate(G().rn);
            Mutate(G().rn);
            cycleRemaining = 1;
            return true;
        } else if (nVessels > 0) {
            MarkCell_II degradeMe = G().GetAgent(G().vesselIs[G().rn.nextInt(nOptions)]);
            if (degradeMe != null) {
                degradeMe.disposeProb-=G().VESSEL_DEGRADATION_RATE;
                if (degradeMe.disposeProb <= 0) {
                    //vessel degraded
                    degradeMe.Die(-1);
                }
            }
        }
        return false;
    }

    public boolean AttemptDivideNormal() {
        //look for open positions to divide into
        int nEmpty = HoodToEmptyIs(G().normalHood, G().hoodIs);
        if (nEmpty + G().rn.nextDouble() * G().NORMAL_EMPTY_DIV_WIGGLE > G().NORMAL_EMPTY_DIV_REQ) {
            //division of normal cell
            int chosenI = G().hoodIs[G().rn.nextInt(nEmpty)];
            MarkCell_II c=Divide(chosenI);
            if(c==null){
                return false;
            }
            c.InitNormal();
            cycleRemaining = 1;
            return true;
        }
        //contact inhibition
        return false;
    }

    public double GetCellCycleDecrement(){
        double acidRMult=Math.min(1,G().ACID_RES_CYCLE_COST*-GetAcidResistPheno(acidResistPH)+1);
        double glycMult=Math.min(1,G().GLYC_CYCLE_COST*-GetGlycPheno(glycRate)+1);
        return G().CELL_TIMESTEP*acidRMult*glycMult* 24/G().MIN_CELL_CYCLE_TIME*(Math.atan((availableATPprop-G().ATP_HALF_MAX)*2)/Math.PI+0.5);
    }
    public void CellStep(Random rn) {
        //TODO may want to return an int that maps to an event code system for the sake of display
        if (type == MarkModel_II.DEAD) {
            //possibly dispose of dead cells
            if (rn.nextDouble() < disposeProb) {
                Dispose();
            }
        } else if (type != MarkModel_II.VESSEL) {
            //Random Death Check
            if (rn.nextDouble() < G().DEATH_PROB_NORM_COND) {
                Die(G().DISPOSE_PROB_APOP);
                return;
            }
            //Acid Death Check
            if (ProtonsToPh(GetConc(MarkModel_II.ACID)) < acidResistPH && rn.nextDouble() < G().DEATH_PROB_POOR_COND) {
                Die(G().DISPOSE_PROB_APOP);
                return;
            }
            //ATP Death Check
            if (availableATPprop < G().DEATH_THRESH_ATP) {
                Die(G().DISPOSE_PROB_NECRO);
                return;
            }
            //ATP Quiescence Check
            if (availableATPprop < G().QUIESCENCE_THRESH_ATP) {
                return;
            }
            //decrement cell cycle
            cycleRemaining -= GetCellCycleDecrement();
            if (cycleRemaining <= 0) {
                //Cycle Complete -> Attempt Prolif
                if(type== MarkModel_II.TUMOR){
                    AttemptDivideTumor();
                }else {
                    AttemptDivideNormal();
                }
            }
        }
    }

    public void Metabolism() {
        if (type == MarkModel_II.DEAD) {
            return;
        }
        if (type == MarkModel_II.VESSEL) {
            //vessels are sources of diffusible
            for (int i = 0; i < MarkModel_II.N_DIFFS; i++) {
                SetConc(i, G().vesselConcs[i]);
            }
        } else {
            //oxygen consumption
            double consumedO2 = MichaelisMenten(GetConc(MarkModel_II.OXYGEN), G().MAX_CONSUMPTION_O2, G().HALF_MAX_CONC_O2);
            AddConc(MarkModel_II.OXYGEN, -consumedO2 * G().DIFF_TIMESTEP);
            //Glucose consumption
            double consumedGluc = MichaelisMenten(GetConc(MarkModel_II.GLUCOSE), (glycRate * G().ATP_TARGET) / 2 - (27 * consumedO2) / 10, G().HALF_MAX_CONC_GLUCOSE);
            AddConc(MarkModel_II.GLUCOSE, -consumedGluc * G().DIFF_TIMESTEP);
            //Conversion to ATP
            availableATPprop = (2 * consumedGluc + (27 * consumedO2) / 5) / G().ATP_TARGET;
            //Production of acid
            AddConc(MarkModel_II.ACID, (G().PROTON_BUFFERING_COEFF * (consumedGluc - consumedO2 / 5) * G().DIFF_TIMESTEP));
        }
    }
}
