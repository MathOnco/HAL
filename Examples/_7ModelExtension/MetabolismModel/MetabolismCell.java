package Examples._7ModelExtension.MetabolismModel;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Util;

import static Framework.Util.*;

public class MetabolismCell<A extends MetabolismCell,G extends MetabolismGrid<A>> extends AgentSQ2Dunstackable<G> {
    public static final int NORMAL=0,CANCER=1,VESSEL=2;
    public int type;
    public double glycolysisPheno;
    public double acidResistancePheno;
    public double VesselDegredationCount;
    public double deathRemainProb;
    public double atp;
    public double cellCycleTime;

    public void Init(double glycolysisPheno, double acidResistance, int type,boolean randomCycleTime) {
        deathRemainProb = 0;
        this.glycolysisPheno = glycolysisPheno;
        this.acidResistancePheno = acidResistance;
        this.type=type;
        //cellCycleTime = G.MIN_CELL_CYCLE_TIME + G.rn.Double() * G.MIN_CELL_CYCLE_TIME;
        if(randomCycleTime){
            cellCycleTime=G.rn.Double();
        }
        else {
            cellCycleTime = 1;// + G.rn.Double() * G.MIN_CELL_CYCLE_TIME;
        }
        if (type==VESSEL) {
            VesselDegredationCount = G.VESSEL_STABILITY;
        } else {
            VesselDegredationCount = -1;
        }

    }

    public boolean IsAlive() {
        if (deathRemainProb == 0) {
            return true;
        }
        return false;
    }

    //turns cell into a "dead cell" to be removed eventually
    public void Die(double prob) {
        if(prob==0){
            Dispose();
            return;
        }
        deathRemainProb = prob;
    }

    //decrements based on atp
    public void DecrementCellCycle() {
        double dec=G.CELL_TIME_STEP / G.MIN_CELL_CYCLE_TIME * ( Math.atan( 2 * atp /G.MAX_ATP_PRODUCTION - 2.2 ) / Math.PI + 0.5);
        cellCycleTime -= dec;
    }

    //Mutates the cell, altering its glycolytic and acid resistance phenotypes
    public void Mutate() {
        //mutate acid resistance pheno
        double delta = G.rn.Double() * 2.0 - 1.0;
        //acidResistancePheno = Bound(acidResistancePheno + G.ACID_RESIST_MUTATION_RATE * delta, G.MAX_ACID_RESIST_PHENO, G.MIN_ACID_RESIST_PHENO);
        acidResistancePheno = Bound( acidResistancePheno * Math.pow(10, Math.log10(1+G.ACID_RESIST_MUTATION_RATE) * delta), G.MAX_ACID_RESIST_PHENO, G.MIN_ACID_RESIST_PHENO);
        delta = G.rn.Double() * 2.0 - 1.0;
        //glycolysisPheno = Bound((double) (Math.pow(10, Math.log10(glycolysisPheno) + G.GLYCOLYSIS_MUTATION_RATE * delta)), G.MIN_GLYCOLYTIC_PHENO, G.MAX_GLYCOLYTIC_PHENO);
        glycolysisPheno = Bound( glycolysisPheno * Math.pow(10, Math.log10(1+G.GLYCOLYSIS_MUTATION_RATE) * delta), G.MIN_GLYCOLYTIC_PHENO, G.MAX_GLYCOLYTIC_PHENO);
        //glycolysisPheno=(double)Math.pow(10,Math.log10((double)glycolysisPheno)+GLYCOLYSIS_MUTATION_RATE*delta);
        //System.out.println("After: "+glycolysisPheno);
    }

    //returns the index to divide into, or -1 if none is found
    public int CheckDivide() {
        int nMoveOpt = G.MapEmptyHood(G.mooreHood, Xsq(), Ysq());
        if ((nMoveOpt > 0 && type==CANCER) || (nMoveOpt > G.EMPTY_SQUARES_FOR_DIV) || (nMoveOpt==G.EMPTY_SQUARES_FOR_DIV%1 && G.rn.Double()<G.EMPTY_SQUARES_FOR_DIV%1)) {// + Util.Gaussian(0, DIV_NOISE_STD_DEV))) {
            int iChosen = G.mooreHood[G.rn.Int(nMoveOpt)];
            return iChosen;
        }
        if (type==CANCER) {
            int iVessel = G.RandomHoodI(G.mooreHood,Xsq(),Ysq(),G.rn);
            if(iVessel>0){
                MetabolismCell v=G.GetAgent(iVessel);
                if(v!=null&&v.type==VESSEL){
                    v.VesselDegredationCount-=G.CELL_TIME_STEP;
                    //vessel degradation and death
                    if (v.VesselDegredationCount <= 0) {
                        v.Dispose();
                    }
                }
            }
        }
        return -1;
    }

    public A Divide(int iDiv) {
        cellCycleTime = 1;
        if (type == CANCER) {
            A child = G.GenTumorCell(iDiv, glycolysisPheno, acidResistancePheno, false);
            Mutate();
            child.Mutate();
            return child;
        }
        return G.GenNormalCell(iDiv, false);
    }
    //returns true if the rest of the cell step function can be skipped, kills the cell if appropriate
    public boolean CellDeathSkipStep(){
        if (IsAlive()) {
            if (type != VESSEL) {
                //random death
                if (G.rn.Double() < G.NORMAL_DEATH_PROB) {
                    Die(G.APOPTOTIC_REMOVE_PROB);
                    return true;
                }
                double pH = ProtonsToPh(G.protons.Get(Isq()));
                //check acid death
                if (pH < acidResistancePheno && G.rn.Double() < G.POOR_CONDITION_DEATH_RATE) {
                    Die(G.APOPTOTIC_REMOVE_PROB);
                    return true;
                }
                //cell necrosis from metabolism
                if (atp / G.MAX_ATP_PRODUCTION < G.ATP_DEATH) {
                    Die(G.NECROTIC_REMOVE_PROB);
                    return true;
                }
            }
            else{ return true;}
        }
        else {
            if (G.rn.Double() < deathRemainProb) {
                Dispose();
            }
            return true;
        }
        return false;
    }

    //example cell step
    public void DefaultCellStep() {
        if (!CellDeathSkipStep()) {
            //cell proliferation
            if (atp / G.MAX_ATP_PRODUCTION > G.ATP_QUIESCENT) {
                DecrementCellCycle();
            }
            if (cellCycleTime <= 0) {
                int iDiv=CheckDivide();
                if(iDiv!=-1){
                    Divide(iDiv);
                }
            }
        }
    }
    //todo: vessels

    //computes cell consumption
    public void CellVesselReaction() {
        //applies cell and vessel consumption to diffusion calculation
        if (IsAlive()) {
            int i=Isq();
            if (!(type == VESSEL)) {
                //cell consumption
                double o2Rate = -MichaelisMenten(G.oxygen.Get(i), G.OXYGEN_MAX_RATE, G.OXYGEN_HALF_RATE_CONC);
                double maxGlycolysisRate = glycolysisPheno * G.MAX_ATP_PRODUCTION / 2 + 27 * o2Rate / 10;
                double gluRate = -Util.MichaelisMenten(G.glucose.Get(i), maxGlycolysisRate, G.GLUCOSE_HALF_RATE_CONC);
                atp = -(2 * gluRate + ((27 * o2Rate) / 5));
                //double protonRate = (29.0 * G.BUFFERING_COEFFICIENT / 10.0) * (glycolysisPheno * G.OXYGEN_MAX_RATE + o2Rate);
                double protonRate= - G.BUFFERING_COEFFICIENT * ( gluRate - o2Rate / 5 );
                G.protons.Add(i,protonRate*G.DIFF_TIME_STEP);
                G.glucose.Add(i,gluRate*G.DIFF_TIME_STEP);
                G.oxygen.Add(i,o2Rate*G.DIFF_TIME_STEP);
            } else {
                //vessel production
                G.glucose.Set(i, G.GLUCOSE_VESSEL_CONC);
                G.oxygen.Set(i, G.OXYGEN_VESSEL_CONC);
                G.protons.Set(i, G.ACID_VESSEL_CONC);
            }
        }
    }

}

