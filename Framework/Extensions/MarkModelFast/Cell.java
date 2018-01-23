package Framework.Extensions.MarkModelFast;

import Framework.Extensions.PDEGrid2DCoarse;
import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Rand;
import Framework.Util;

import static Framework.Extensions.MarkModelFast.Tissue.*;
import static Framework.Util.*;

/**
 * Created by Rafael on 10/10/2017.
 */
//angiogenic phenotype axis may be interesting to include in new model version
//phenotypic axis one side is antiangio, other is proangio
//microenvironment informs angio vs antiangio decision, and mutation affects mapping, possibly with bias factor
//grow homeostatic tissue with normal cells using this system

public class Cell extends AgentSQ2Dunstackable<Tissue> {

    private final static double inSide=2.0/3.0,outSide=1.0/3.0,inCorner=4.0/9.0,outCorner=5.0/18.0;
    public int type;
    public int drawColor;
    public double glycRate;
    public double acidResistPH;
    public double availableATPprop;
    public double vesselHP;
    public double cycleRemaining;
    public Cell Divide(int index){
        return G().NewAgentSQ(index);
    }

    public Cell InitVessel() {
        this.type = Tissue.VESSEL;
        this.vesselHP = G().VESSEL_HP_MAX;
        this.drawColor = Tissue.VESSEL_COLOR;
        G().vesselList.add((Cell)this);
        return (Cell) this;
    }

    public Cell InitNormal(double startCycleTime) {
        this.type = Tissue.NORMAL;
        this.acidResistPH=G().NORMAL_PHENO_ACID_RESIST;
        this.glycRate=G().NORMAL_PHENO_GLYC;
        this.drawColor = Tissue.NORMAL_COLOR;
        cycleRemaining=startCycleTime;
        return (Cell) this;
    }

    public Cell InitTumor(double glycRate, double acidResistPH,double startCycleTime) {
        this.type = TUMOR;
        this.glycRate=glycRate;
        this.acidResistPH=acidResistPH;
        this.drawColor = CbCrPlaneColor(GetAcidResistPheno(acidResistPH),GetGlycPheno(glycRate));
        this.cycleRemaining=startCycleTime;
        return (Cell) this;
    }
    int InFallback(int val,int fallback,int dim){
        return(Util.InDim(dim,val))?val:fallback;
    }
    public double GetInterp(PDEGrid2DCoarse diff){
        PDEGrid2D g=diff.grid;
        final int xCell=Xsq();
        final int yCell=Ysq();
        final int xDiff=xCell/3;
        final int yDiff=yCell/3;
        final int xMod=xCell%3;
        final int yMod=yCell%3;
        switch (xMod) {
            case 0:
                switch (yMod) {
                    case 0://left bottom
                        return g.Get(xDiff, yDiff) * inCorner +
                                g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outCorner +
                                g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * outCorner;
                    case 1://left middle
                        return g.Get(xDiff, yDiff) * inSide + g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outSide;
                    case 2://left top
                        return g.Get(xDiff, yDiff) * inCorner +
                                g.Get(InFallback(xDiff - 1, xDiff, g.xDim), yDiff) * outCorner +
                                g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * outCorner;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }
            case 1:
                switch (yMod){
                    case 0://middle bottom
                        return g.Get(xDiff, yDiff) * inSide + g.Get(xDiff, InFallback(yDiff-1,yDiff,g.yDim)) * outSide;
                    case 1://middle
                        return g.Get(xDiff,yDiff);
                    case 2://middle top
                        return g.Get(xDiff, yDiff) * inSide + g.Get(xDiff, InFallback(yDiff+1,yDiff,g.yDim)) * outSide;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }
            case 2:
                switch (yMod) {
                    case 0://right bottom
                        return g.Get(xDiff, yDiff) * inCorner +
                                g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outCorner +
                                g.Get(xDiff, InFallback(yDiff - 1, yDiff, g.yDim)) * outCorner;
                    case 1://right middle
                        return g.Get(xDiff, yDiff) * inSide + g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outSide;
                    case 2://right top
                        return g.Get(xDiff, yDiff) * inCorner +
                                g.Get(InFallback(xDiff + 1, xDiff, g.xDim), yDiff) * outCorner +
                                g.Get(xDiff, InFallback(yDiff + 1, yDiff, g.yDim)) * outCorner;
                    default: throw new IllegalStateException("mod calculation did not work!");
                }

            default: throw new IllegalStateException("mod calculation did not work!");
        }
    }



    public double GetConc(Diff diff) {
        return diff.Get(Xsq(),Ysq());
    }

    public void AddConc(Diff diff, double val) {
        diff.Add(Xsq(),Ysq(), val);
    }

    public void SetConc(Diff diff, double val) {
        diff.Set(Xsq(),Ysq(), val);
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
    }

    public double GetAcidResistPheno(double acidResistPH) {
        return G().GetAcidResistPheno(acidResistPH);
    }

    public Cell Mutate(Rand rn) {
        //uniform for acid resist, exponential for glycolytic
        double newGlycPheno=Bound(GetGlycPheno(glycRate) + (rn.Double() * 2 - 1) * G().MUT_RATE_GLYC, 0, 1);
        double newAcidResistPheno=Bound(GetAcidResistPheno(acidResistPH) + (rn.Double() * 2 - 1) * G().MUT_RATE_ACID_RESIST, 0, 1);
        glycRate=GetGlycRate(newGlycPheno);
        acidResistPH= GetAcidResistPH(newAcidResistPheno);
        drawColor = CbCrPlaneColor(newAcidResistPheno,newGlycPheno);
        return (Cell) this;
    }
    public boolean AttemptDivideTumor(){
        //tumor cells can either divide into empty space or degrade vessels
        int nIs = HoodToIs(G().tumorHood, G().tumorHoodIs);
        int nOptions = 0;
        int nVessels = 0;
        for (int i = 0; i < nIs; i++) {
            int index=G().tumorHoodIs[i];
            Cell isVessel = G().GetAgent(index);
            if (isVessel == null){
                G().tumorHoodIs[nOptions]=index;
                nOptions++;
            }else if(isVessel.type == VESSEL) {
                G().vesselIs[nVessels] = index;
                nVessels++;
            }
        }
        if (nOptions > 0) {
            int chosenI = G().tumorHoodIs[G().rn.Int(nOptions)];
            //division of tumor cell
            Cell c=Divide(chosenI);
            if(c==null){
                return false;
            }
            c.InitTumor(glycRate, acidResistPH,G().MIN_CELL_CYCLE_TIME).Mutate(G().rn);
            Mutate(G().rn);
            cycleRemaining = 1;
            return true;
        } else if (nVessels > 0) {
            Cell degradeMe = G().GetAgent(G().vesselIs[G().rn.Int(nVessels)]);
            if (degradeMe != null) {
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
        int nEmpty = HoodToEmptyIs(G().normalHood, G().normalHoodIs);
        if (nEmpty > G().NORMAL_EMPTY_DIV_REQ) {
            //division of normal cell
            int chosenI = G().normalHoodIs[G().rn.Int(nEmpty)];
            Cell c=Divide(chosenI);
            if(c==null){
                return false;
            }
            c.InitNormal(G().MIN_CELL_CYCLE_TIME);
            return true;
        }
        //contact inhibition
        return false;
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
//        double surviveProb=1.0-(nDisruptors*1.0/G().vesselIs.length)*G().VESSEL_DEATH_CONST;
//        return 1.0-surviveProb;
//    }
    public double DieProb(){
        //Random Death Check
        double surviveProb=1.0-G().DEATH_PROB_NORM_COND;
        //Acid Death Check
        if (ProtonsToPh(GetConc(G().acid)) < acidResistPH) {
            surviveProb*=1.0-G().DEATH_PROB_POOR_COND;
        }
        //ATP Death Check
        if (availableATPprop < G().DEATH_THRESH_ATP) {
            surviveProb*=0;
        }
        return 1.0-surviveProb;
    }
    public void CellStep() {
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
//            if(G().rn.Double()<DieProbVessel()){
//                Die(false);
//            }
//        }

        else if (type == NORMAL||type==TUMOR) {
            if(G().rn.Double()<DieProb()){
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
        double consumedO2 = MichaelisMenten(GetInterp(G().oxygen), G().MAX_CONSUMPTION_O2, G().HALF_MAX_CONC_O2);
        double consumedGluc = MichaelisMenten(GetInterp(G().glucose), (glycRate * G().ATP_TARGET) / 2 - (27 * consumedO2) / 10, G().HALF_MAX_CONC_GLUCOSE);
        availableATPprop = (2 * consumedGluc + (27 * consumedO2) / 5) / G().ATP_TARGET;
    }
    public void Metabolism() {
        if (type == DEAD||type==VESSEL) {
            return;
        } else {
            //oxygen consumption
            double consumedO2 = MichaelisMenten(GetConc(G().oxygen), G().MAX_CONSUMPTION_O2, G().HALF_MAX_CONC_O2);
            AddConc(G().oxygen, -consumedO2*G().CELLS_PER_SQ * G().DIFF_TIMESTEP);
            //Glucose consumption
            double consumedGluc = MichaelisMenten(GetConc(G().glucose), (glycRate * G().ATP_TARGET) / 2 - (27 * consumedO2) / 10, G().HALF_MAX_CONC_GLUCOSE);
            AddConc(G().glucose, -consumedGluc*G().CELLS_PER_SQ * G().DIFF_TIMESTEP);
            //Conversion to ATP
            //availableATPprop = (2 * consumedGluc + (27 * consumedO2) / 5) / G().ATP_TARGET;
            //Production of acid
            AddConc(G().acid, (G().PROTON_BUFFERING_COEFF * (consumedGluc - consumedO2 / 5) *G().CELLS_PER_SQ* G().DIFF_TIMESTEP));
        }
    }
}

