package Examples.MarkModel3;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Rand;

import static Examples.MarkModel3.Tissue.*;
import static Framework.Util.*;

/**
 * Created by Rafael on 10/10/2017.
 */
//angiogenic phenotype axis may be interesting to include in new model version
//phenotypic axis one side is antiangio, other is proangio
//microenvironment informs angio vs antiangio decision, and mutation affects mapping, possibly with bias factor
//grow homeostatic tissue with normal cells using this system

//public class MarkCell_II<A extends MarkCell_II, G extends MarkModel_II<A>> extends AgentSQ2Dunstackable<G> //What is A? Agent?******


public class Cell<C extends Cell,T extends Tissue<C>> extends AgentSQ2Dunstackable<T> {

    public int type;
    public int drawColor;
    public double glycRate;
    public double acidResistPH;
    public double availableATPprop;
    public double vesselHP;
    public double cycleRemaining;
    public C Divide(int index){
        return (C)G().NewAgentSQ(index);
    }

    public C InitVessel() {
        this.type = Tissue.VESSEL;
        this.vesselHP = G().VESSEL_HP_MAX;
        this.drawColor = Tissue.VESSEL_COLOR;
        G().vesselList.add((C)this);
        return (C) this;
    }

    public C InitNormal(double startCycleTime) {
        this.type = Tissue.NORMAL;
        this.acidResistPH=G().NORMAL_PHENO_ACID_RESIST;
        this.glycRate=G().NORMAL_PHENO_GLYC;
        this.drawColor = Tissue.NORMAL_COLOR;
        cycleRemaining=startCycleTime;
        return (C) this;
    }

    public C InitTumor(double glycRate, double acidResistPH,double startCycleTime) {
        this.type = TUMOR;
        this.glycRate=glycRate;
        this.acidResistPH=acidResistPH;
        this.drawColor = CbCrPlaneColor(GetAcidResistPheno(acidResistPH),GetGlycPheno(glycRate));
        this.cycleRemaining=startCycleTime;
        return (C) this;
    }



    public double GetConc(Diff diff) {
        if(G().DIFF_SPACE_SCALE==1){
            return diff.grid.Get(Xsq(),Ysq());
        }
        else {
            return diff.Get(Xsq(), Ysq());
        }
    }

    public void AddConc(Diff diff, double val) {
        if(G().DIFF_SPACE_SCALE==1){
            diff.grid.Add(Xsq(),Ysq(),val);
        }
        else {
            diff.Add(Xsq(), Ysq(), val);
        }
    }

    public void SetConc(Diff diff, double val) {
        if(G().DIFF_SPACE_SCALE==1){
            diff.grid.Set(Xsq(),Ysq(),val);
        }
        else {
            diff.Set(Xsq(), Ysq(), val);
        }
    }

    public void Die(boolean necrotic) {
        if(type==VESSEL){
            G().vesselList.remove(this);
        }
        if(necrotic){
            this.type = Tissue.NECRO;
            this.drawColor=NECRO_COLOR;
        } else {
            this.type = Tissue.DEAD;
            this.drawColor=DEAD_COLOR;
        }
    }
    public double GetGlycRate(double pheno) {
        return G().GetGlycRate(pheno);
    }

    public double GetAcidResistPH(double pheno) {
        return G().GetAcidResistPH(pheno);
    }

    public double GetGlycPheno(double glycRate) {
        return G().GetGlycPheno(glycRate);
    } //***

    public double GetAcidResistPheno(double acidResistPH) {
        return G().GetAcidResistPheno(acidResistPH);
    }  //***

    public double GetGlycPheno() {
        return G().GetGlycPheno(glycRate);
    } //***

    public double GetAcidResistPheno() {
        return G().GetAcidResistPheno(acidResistPH);
    }  //***


    public C Mutate(Rand rn) {
        //uniform for acid resist, exponential for glycolytic
        double newGlycPheno=Bound(GetGlycPheno(glycRate) + (rn.Double() * 2 - 1) * G().MUT_RATE_GLYC, 0, 1);
        double newAcidResistPheno=Bound(GetAcidResistPheno(acidResistPH) + (rn.Double() * 2 - 1) * G().MUT_RATE_ACID_RESIST, 0, 1);
        glycRate=GetGlycRate(newGlycPheno);
        acidResistPH= GetAcidResistPH(newAcidResistPheno);
        drawColor = CbCrPlaneColor(newAcidResistPheno,newGlycPheno);
        return (C) this;
    }
    public boolean AttemptDivideTumor(){
        //tumor cells can either divide into empty space or degrade vessels
        int nOptions = 0;
        int nVessels = 0;
        int len=G().MapHood(G().tumorHood,Isq());
        for (int i = 0; i < len; i++) {
            int index=G().tumorHood[i];
            C isVessel = (C)G().GetAgent(index);
            if (isVessel == null){
                G().tumorHood[nOptions]=index;
                nOptions++;
            }else if(isVessel.type == VESSEL) {
                G().vesselIs[nVessels] = index;
                nVessels++;
            }
        }
        if (nOptions > 0) {
            int chosenI = G().tumorHood[G().rn.Int(nOptions)];
            //division of tumor cell
            C c=Divide(chosenI);
            if(c==null){
                return false;
            }
            c.InitTumor(glycRate, acidResistPH,G().MIN_CELL_CYCLE_TIME).Mutate(G().rn);
            Mutate(G().rn);
            cycleRemaining = 1;
            return true;
        } else if (nVessels > 0) {
            C degradeMe = (C)G().GetAgent(G().vesselIs[G().rn.Int(nVessels)]);
            if (degradeMe != null&&(G().VESSEL_DEGRADE_PROB ==1||G().rn.Double()<G().VESSEL_DEGRADE_PROB)) {
                degradeMe.vesselHP-=G().VESSEL_DEGRADATION_RATE;
                if (degradeMe.vesselHP <= 0) {
                    //vessel degraded
                    degradeMe.Die(false);
                }
            }
        }
        return false;
        }

    public boolean AttemptDivideNormal() {
        //look for open positions to divide into
        int options=MapEmptyHood(G().normalHood);
        if (options <= G().NORMAL_EMPTY_DIV_REQ) {
            return false;
        }
        C newC = Divide(G().normalHood[G().rn.Int(options)]);
        if (newC == null) {
            return false;
        }
        G().cellDivided = true;
        cycleRemaining = 1;
        newC.InitNormal(G().MIN_CELL_CYCLE_TIME);
        return true;
    }
    public boolean AttemptDivide(){
        if(type== TUMOR) {
            return AttemptDivideTumor();
        }
        return AttemptDivideNormal();
    }
//    public boolean AttemptAngiogenesis(){
//        double conc=G().diffs[OXYGEN].Get(Xsq(),Ysq());
//        if(conc<G().HYPOX_ANGIO_ZONE_MAX&&conc>G().HYPOX_ANGIO_ZONE_MIN){
//            return true;
//        }
//        return false;
//    }
//    public void Angiogenesis(){
//        double xcomp=G().diffs[OXYGEN].GradientX(Xsq(),Ysq());
//        double ycomp=G().diffs[OXYGEN].GradientY(Xsq(),Ysq());
//        double norm=Norm(xcomp,ycomp);
//        int xNew=(int)(Xsq()+(xcomp/norm)*(G().VESSEL_SPACING_MIN/G().SQUARE_DIAM));
//        int yNew=(int)(Ysq()+(ycomp/norm)*(G().VESSEL_SPACING_MIN/G().SQUARE_DIAM));
//        int n=G().HoodToIs(G().vesselAngioHood,G().vesselAngioIs,xNew,yNew);
//        for (int i = 0; i < n; i++) {
//            Cell c=G().GetAgent(G().vesselAngioIs[i]);
//            if(c!=null&&c.type==VESSEL){
//                //vessel already placed here!
//                return;
//            }
//        }
//        if(G().GetAgent(xNew,yNew)==null){
//            G().NewAgentSQ(xNew,yNew);
//        }
//        G().GetAgent(xNew,yNew).InitVessel();
//    }

    public double DivideProb(){
        double acidRMult=Math.min(1,G().ACID_RES_CYCLE_COST*-GetAcidResistPheno(acidResistPH)+1);
        double glycMult=Math.min(1,G().GLYC_CYCLE_COST*-GetGlycPheno(glycRate)+1);
        double ret= G().CELL_TIMESTEP*acidRMult*glycMult* 24/G().MIN_CELL_CYCLE_TIME*(Math.atan((availableATPprop-G().ATP_HALF_MAX)*2)/Math.PI+0.5);

        return ret;
    }
//    public double DieProbVessel(){
//        int nNeighbors=HoodToIs(G().vesselHood,G().vesselIs);
//        int nDisruptors=0;
//        for (int i = 0; i < nNeighbors; i++) {
//            Cell c= G().GetAgent(G().vesselIs[i]);
//            if(c!=null&&(c.type==TUMOR||c.type==NECRO)){
//                nDisruptors++;
//            }
//        }
//        double surviveProb=1.0-(nDisruptors*1.0/G().vesselIs.length)*G().VESSEL_DEGRADE_PROB;
//        return 1.0-surviveProb;
//    }
    public double DieProb(double[]intensities){
        //Random Death Check
        double surviveProb=1.0-G().DEATH_PROB_NORM_COND;
        //Acid Death Check
        if (ProtonsToPh(G().acid.GetInterp(Xsq(),Ysq())) < acidResistPH) {
            surviveProb*=1.0-G().DEATH_PROB_POOR_COND;
        }
        //ATP Death Check
        if (availableATPprop < G().DEATH_THRESH_ATP) {
            surviveProb*=0;
        }
        if(intensities!=null) {
            for (Drug drug : G().modDeathProb) {
                surviveProb*=1.0-drug.DeathProb(this, intensities[drug.I()]);
            }
        }
        return 1.0-surviveProb;
    }


    public void CellStep(double[]intensities) {
        //TODO may want to return an int that maps to an event code system for the sake of display
        if (type == DEAD) {
            if (G().rn.Double() < G().DISPOSE_PROB_DEAD) {
                Dispose();
            }
        }
        else if(type==NECRO){
            if(G().rn.Double()<G().DISPOSE_PROB_NECRO){
                Dispose();
            }
        }

//        else if(type==VESSEL){
//           if(G().rn.Double()<DieProbVessel()){
//                Die(false);
//            }
//        }

        else if (type == NORMAL||type==TUMOR) {
            if(intensities!=null){
                for (Drug drug : G().onCellStep) {
                    drug.OnCellStep(this,intensities[drug.I()]);
                }
            }
            if(G().rn.Double()<DieProb(intensities)){
                if(availableATPprop<G().DEATH_THRESH_ATP){
                    Die(true);
                }else {
                    Die(false);
                }
                return;//death event
            }
            if (availableATPprop < G().QUIESCENCE_THRESH_ATP) {
                return;
            }
            //decrement cell cycle
            //if(DivideProb())
            cycleRemaining -= DivideProb();
            //if (G().rn.Double()< DivideProb()) {
                if (cycleRemaining <= 0) {
                //Cycle Complete -> Attempt Prolif
                AttemptDivide();
            }
        }
    }

    public void ATPComp(){
        double consumedO2 = MichaelisMenten(G().oxygen.GetInterp(Xsq(),Ysq()), G().MAX_CONSUMPTION_O2, G().HALF_MAX_CONC_O2);
        double consumedGluc = MichaelisMenten(G().glucose.GetInterp(Xsq(),Ysq()), (glycRate * G().ATP_TARGET) / 2 - (27 * consumedO2) / 10, G().HALF_MAX_CONC_GLUCOSE);
        availableATPprop = (2 * consumedGluc + (27 * consumedO2) / 5) / G().ATP_TARGET;
    }
    public void Metabolism(double[]intensities) {
        if (type == DEAD || type == VESSEL) {
            return;
        }
        if (intensities != null) {
            for (Drug drug : G().onCellDiffStep) {
                drug.OnCellDiffStep(this, intensities[drug.I()]);
            }
        }
        //oxygen consumption
        double consumedO2 = MichaelisMenten(G().oxygen.GetInterp(Xsq(),Ysq()), G().MAX_CONSUMPTION_O2, G().HALF_MAX_CONC_O2);
        G().oxygen.AddSwap(Xsq(), Ysq(), -consumedO2 * G().CELLS_PER_SQ * G().DIFF_TIMESTEP);
        //Glucose consumption
        double consumedGluc = MichaelisMenten(G().glucose.GetInterp(Xsq(),Ysq()), (glycRate * G().ATP_TARGET) / 2 - (27 * consumedO2) / 10, G().HALF_MAX_CONC_GLUCOSE);
        G().glucose.AddSwap(Xsq(), Ysq(), -consumedGluc * G().CELLS_PER_SQ * G().DIFF_TIMESTEP);
        //Conversion to ATP
        //availableATPprop = (2 * consumedGluc + (27 * consumedO2) / 5) / G().ATP_TARGET;
        //Production of acid
        G().acid.AddSwap(Xsq(), Ysq(), G().PROTON_BUFFERING_COEFF * (consumedGluc - consumedO2 / 5) * G().CELLS_PER_SQ * G().DIFF_TIMESTEP);
    }
}

