package Framework.Extensions;

import Framework.GridsAndAgents.AgentSQ2Dunstackable;
import Framework.Utils;

import static Framework.Utils.*;

public class MarksModelCell <A extends MarksModelCell,G extends MarksModelGrid<A>> extends AgentSQ2Dunstackable<G> {
    public double glycolysisPheno;
    public double acidResistancePheno;
    public int VesselDegredationCount;
    public double deathRemainProb;
    public double atp;
    public double cellCycleTime;
    public boolean isCancer;

    public void Init(double glycolysisPheno, double acidResistance, boolean cancer, boolean isVessel) {
        deathRemainProb = 0;
        this.glycolysisPheno = glycolysisPheno;
        this.acidResistancePheno = acidResistance;
        this.isCancer = cancer;
        cellCycleTime = G().MIN_CELL_CYCLE_TIME + G().rn.nextDouble() * G().MIN_CELL_CYCLE_TIME;
        if (isVessel) {
            VesselDegredationCount = G().VESSEL_STABILITY;
        } else {
            VesselDegredationCount = -1;
        }

    }

    public boolean IsVessel() {
        return !(VesselDegredationCount == -1);
    }

    public boolean IsAlive() {
        if (deathRemainProb == 0) {
            return true;
        }
        return false;
    }

    //turns cell into a "dead cell" to be removed eventually
    public void Die(double prob) {
        deathRemainProb = prob;
    }

    //decrements based on atp
    public void DecrementCellCycle() {
        cellCycleTime -= (atp / G().MAX_ATP_PRODUCTION) * G().CELL_TIME_STEP;
    }

    //Mutates the cell, altering its glycolytic and acid resistance phenotypes
    public void Mutate() {
        //mutate acid resistance pheno
        double delta = Math.random() * 2.0 - 1.0;
        acidResistancePheno = Bound(acidResistancePheno + G().ACID_RESIST_MUTATION_RATE * delta, G().MAX_ACID_RESIST_PHENO, G().MIN_ACID_RESIST_PHENO);
        delta = Math.random() * 2.0 - 1.0;
        //mutate glycolysis pheno
        //System.out.print("BeforeGly: ")
        glycolysisPheno = Bound((double) (Math.pow(10, Math.log10(glycolysisPheno) + G().GLYCOLYSIS_MUTATION_RATE * delta)), G().MIN_GLYCOLYTIC_PHENO, G().MAX_GLYCOLYTIC_PHENO);
        //glycolysisPheno=(double)Math.pow(10,Math.log10((double)glycolysisPheno)+GLYCOLYSIS_MUTATION_RATE*delta);
        //System.out.println("After: "+glycolysisPheno);
    }

    //returns the index to divide into, or -1 if none is found
    public int CheckDivide() {
        int iMoveOpt = 0;
        int nCheck = G().HoodToIs(G().MOORE_NEIGHBORHOOD, G().moveIs, Xsq(), Ysq(), false, false);
        for (int i = 0; i < nCheck; i++) {
            MarksModelCell checkMe = (MarksModelCell) G().GetAgent(G().moveIs[i]);
            //find surrounding vessels and empty squares
            if (checkMe == null || (checkMe.IsVessel() && isCancer)) {
                G().moveIs[iMoveOpt] = G().moveIs[i];
                iMoveOpt += 1;
            }
        }
        if (iMoveOpt > 0 && (isCancer || iMoveOpt > G().EMPTY_SQUARES_FOR_DIV)) {// + Utils.Gaussian(0, DIV_NOISE_STD_DEV))) {
            int iRand = iMoveOpt > 1 ? G().rn.nextInt(iMoveOpt) : 0;
            int iChosen = G().moveIs[iRand];
            if (isCancer) {
                MarksModelCell amIVessel = (MarksModelCell) G().GetAgent(iChosen);
                if (amIVessel != null) {
                    amIVessel.VesselDegredationCount--;
                    //vessel degradation and death
                    if (amIVessel.VesselDegredationCount <= 0) {
                        amIVessel.Dispose();
                    }
                    return -1;
                }
            }
            return iChosen;
        }
        return -1;
    }

    public A Divide(int iDiv) {
        A child = G().NewAgentSQ(iDiv);
        child.Init(glycolysisPheno, acidResistancePheno, isCancer, false);
        if (isCancer) {
            Mutate();
            child.Mutate();
        }
        return child;
    }
    //returns true if the rest of the cell step function can be skipped, kills the cell if appropriate
    public boolean CellDeathSkipStep(){
        if (IsAlive()) {
            if (!IsVessel()) {
                //random death
                if (G().rn.nextFloat() < G().NORMAL_DEATH_PROB) {
                    Die(G().APOPTOTIC_REMOVE_PROB);
                    return true;
                }
                double pH = ProtonsToPh(G().protons.Get(Isq()));
                //check acid death
                if (pH < acidResistancePheno && G().rn.nextFloat() < G().POOR_CONDITION_DEATH_RATE) {
                    Die(G().APOPTOTIC_REMOVE_PROB);
                    return true;
                }
                //cell necrosis from metabolism
                if (atp / G().MAX_ATP_PRODUCTION < G().ATP_DEATH) {
                    Die(G().NECROTIC_REMOVE_PROB);
                    return true;
                }
            }
            else{ return true;}
        }
        else {
            if (G().rn.nextFloat() < deathRemainProb) {
                Dispose();
            }
            return true;
        }
        return false;
    }

    //example cell step
    public void DefaultStep() {
        if (!CellDeathSkipStep()) {
            //cell proliferation
            if (atp / G().MAX_ATP_PRODUCTION > G().ATP_QUIESCENT) {
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

    //computes cell consumption
    public void SetRates() {
        //applies cell and vessel consumption to diffusion calculation
        if (IsAlive()) {
            int i=Isq();
            if (!IsVessel()) {
                //cell consumption
                double o2Rate = -MichaelisMenten(G().oxygen.Get(i), G().OXYGEN_MAX_RATE, G().OXYGEN_HALF_RATE_CONC);
                //System.out.println("o2Rate:"+o2Rate/OXYGEN_MAX_RATE);
                //System.out.println("o2Rate:"+o2Rate);
                double maxGlycolysisRate = glycolysisPheno * G().MAX_ATP_PRODUCTION / 2;// + 27 * o2Rate / 10;
                double gluRate = -Utils.MichaelisMenten(G().glucose.Get(i), maxGlycolysisRate, G().GLUCOSE_HALF_RATE_CONC);
                //System.out.println("gluRate:"+gluRate/maxGlycolysisRate);
                //System.out.println("gluRate:"+gluRate);
                atp = -(2 * gluRate + ((27 * o2Rate) / 5));
                //System.out.println(2*gluRate+","+(27*o2Rate)/5+","+atp);
                //System.out.println(atp/TARGET_ATP_PRODUCTION);
                double protonRate = (29.0 * G().BUFFERING_COEFFICIENT / 5.0) * (glycolysisPheno * G().OXYGEN_MAX_RATE + o2Rate);
                G().protons.Set(i, G().protons.Get(i) + protonRate);
                G().glucose.Set(i, G().glucose.Get(i) + gluRate);
                G().oxygen.Set(i, G().oxygen.Get(i) + o2Rate);
            } else {
                //vessel production
                G().glucose.Set(i, G().GLUCOSE_VESSEL_CONC);
                G().oxygen.Set(i, G().OXYGEN_VESSEL_CONC);
                G().protons.Set(i, G().ACID_VESSEL_CONC);
            }
        }
    }

}

