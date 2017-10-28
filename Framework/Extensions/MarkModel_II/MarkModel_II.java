package Framework.Extensions.MarkModel_II;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGridVis;
import Framework.Interfaces.DoubleToColor;
import Framework.Utils;

import java.util.LinkedList;
import java.util.Random;

import static Framework.Utils.*;

public class MarkModel_II<A extends MarkCell_II> extends AgentGrid2D<A> {
    //WTF VARS
    public double ADI_STOP_DIF =1E-5;
    public int ADI_MIN_STEPS=5;
    public double TISSUE_OPEN_NEIGH_THRESH=3.5;
    public double TISSUE_ATP_PROLIF_THRESH=0.8;
    public double ATP_HALF_MAX=1.1;
    public double COURANT_NUM=0.1;
    public double EPS=2E-52;

    //CELL TYPE ENUM
    public static int NORMAL = 1, TUMOR = 2, VESSEL = 3, DEAD = 4;

    //DRAW COLORS
    public static int NORMAL_COLOR=RGB(0,0,1),VESSEL_COLOR=RGB(1,0,0), APOP_COLOR =RGB(0.5,0,0.5),NECRO_COLOR=RGB(0.2,0.2,0.2),EMPTY_COLOR=RGB(0,0,0);
    public static final int DEFAULT_SIDE_LEN=76;

    //TIME AND SPACE SCALES
    public double SQUARE_DIAM = 20;//micrometers
    public double CELL_TIMESTEP = 1.0/12;//in days
    public double ADI_MAX_SIM_TIME =50;
    public int ADI_MAX_SIM_TIME_START = (int) ADI_MAX_SIM_TIME *6;

    //MISC
    public double MIN_CELL_CYCLE_TIME = 18;

    //EVENT PROBABILITIES
    public double DEATH_PROB_NORM_COND_BASE = 0.005;
    public double DEATH_PROB_POOR_COND_BASE = 0.7;
    public double DISPOSE_PROB_NECRO_BASE = 5E-4;
    public double DISPOSE_PROB_APOP_BASE = 0.5;

    //VESSEL
    public double VESSEL_SPACING_MIN_BASE = 80;//in microns
    public double VESSEL_SPACING_MEAN_BASE = 158;// TODO this seems not dense enough
    //public double VESSEL_SPACING_MEAN = 100;
    public double HYPOX_ANGIO_ZONE_MIN = 8E-4;
    public double HYPOX_ANGIO_ZONE_MAX = 2E-3;
    public double HYPOX_ZONE_SIZE=100;
    public double ANGIO_PROB=0.5;
    public int VESSEL_HP_MAX = 20;
    public double ANGIO_RATE = 0.3;
    public double VESSEL_EDGE_MAX_DIST=1;//no vessels this close to the edge
    public double VESSEL_DEGRADATION_RATE_BASE=1;

    //NORMIES
    public double NORMAL_PHENO_ACID_RESIST = 6.65;
    public double NORMAL_PHENO_GLYC = 1;
    public double NORMAL_EMPTY_DIV_REQ = 3.5;
    public double NORMAL_EMPTY_DIV_WIGGLE = 1.0;
    public double NORMAL_START_DENSITY = 0.8;

    //TUMOR
    public double MAX_PHENO_ACID_RESIST = 6.1;
    public double MAX_PHENO_GLYC = 50;
    public double MUT_RATE_GLYC = 0.05;
    public double MUT_RATE_ACID_RESIST = 0.05;//TODO: get actual parameter value
    //public double MUT_RATE_GLYC = 0.2;
    //public double MUT_RATE_ACID_RESIST = 0.2;//TODO: get actual parameter value
    public double TUMOR_START_RAD = 5;
    public double ACID_RES_CYCLE_COST=0.5;
    public double GLYC_CYCLE_COST=0.4;

    //GLUCOSE
    public double DIFF_RATE_GLUCOSE = 500;
    public double HALF_MAX_CONC_GLUCOSE = 0.03;//for michaelis menten
    public double VESSEL_GLUC = 5;

    //ACID
    public double DIFF_RATE_PROTON = 1080;
    public double VESSEL_PH = 7.4;
    public double PROTON_BUFFERING_COEFF = 2.5E-4;
    public double BOUNDARY_ACID;

    //OXYGEN
    public double DIFF_RATE_O2 = 1820;
    public double VESSEL_O2 = 0.0556;
    public double HALF_MAX_CONC_O2 = 0.005;//for michaelis menten
    public double MAX_CONSUMPTION_O2 = 0.012;
    public double BOUNDARY_O2;

    //ATP
    public double DEATH_THRESH_ATP = 0.3;
    public double QUIESCENCE_THRESH_ATP = 0.8;
    public double ATP_TARGET;

    //DIFFUSIBLES ENUM
    public static int OXYGEN = 0, GLUCOSE = 1, ACID = 2, N_DIFFS = 3;

    //OTHER OBJECTS
    public Random rn;
    public PDEGrid2D[] diffs;
    public double[] vesselConcs;
    public double[] diffRates;
    public double[] boundaryConds;

    protected LinkedList<A> vesselList=new LinkedList<>();
    public int[] hypoxicIs;
    public int[] normalHood = MooreHood(false);
    public int[] tumorHood = MooreHood(false);
    //public int[] tumorHood = VonNeumannHood(true);
    public int[] hoodIs = new int[normalHood.length / 2];
    public int[] vesselIs = new int[normalHood.length / 2];
    public final boolean REFLECTIVE_BOUNDARY;

    //MORE CONSTANTS
    public double MAX_DIFF_RATE;
    public int ADI_MAX_STEPS;
    public int ADI_MAX_STEPS_START;
    public double DIFF_TIMESTEP;
    public double DIFF_SCALE_FACTOR;

    //SET DURING SETUP_CONSTANTS()
    public double DEATH_PROB_NORM_COND;
    public double DEATH_PROB_POOR_COND;
    public double DISPOSE_PROB_NECRO;
    public double DISPOSE_PROB_APOP;
    public double VESSEL_SPACING_MIN;
    public double VESSEL_SPACING_MEAN;
    public double VESSEL_DEGRADATION_RATE;

    public MarkModel_II(int x, int y, boolean reflectiveBoundary,boolean setupConstants,Class<A> classObj,Random rn) {
        super(x, y, classObj);
        this.rn=rn;
        this.REFLECTIVE_BOUNDARY = reflectiveBoundary;
        if(setupConstants) {
            SetupConstants();
        }
    }
    public void CalcVesselDegradationRate(){
        VESSEL_DEGRADATION_RATE=VESSEL_DEGRADATION_RATE_BASE*CELL_TIMESTEP;
    }
    public double GetMaxDiffRate(){
        return ArrayMax(diffRates);
    }
    public void SetupConstants(){
        DEATH_PROB_NORM_COND=Utils.ProbScale(DEATH_PROB_NORM_COND_BASE, CELL_TIMESTEP);
        DEATH_PROB_POOR_COND = Utils.ProbScale(DEATH_PROB_POOR_COND_BASE, CELL_TIMESTEP);
        DISPOSE_PROB_NECRO=Utils.ProbScale(DISPOSE_PROB_NECRO_BASE,CELL_TIMESTEP);
        DISPOSE_PROB_APOP=Utils.ProbScale(DISPOSE_PROB_APOP_BASE,CELL_TIMESTEP);
        VESSEL_SPACING_MIN=VESSEL_SPACING_MIN_BASE/SQUARE_DIAM;
        VESSEL_SPACING_MEAN=VESSEL_SPACING_MEAN_BASE/SQUARE_DIAM;
        ATP_TARGET = 29 * MAX_CONSUMPTION_O2 / 5;
        SetupDiffusibleArrs();
        MAX_DIFF_RATE=GetMaxDiffRate();
        ADI_MAX_STEPS =(int)Math.max(10,Math.round(ADI_MAX_SIM_TIME *(Math.pow(MAX_DIFF_RATE,0.5))/(COURANT_NUM*SQUARE_DIAM)));
        ADI_MAX_STEPS_START =(int)Math.max(10,Math.round(ADI_MAX_SIM_TIME_START *(Math.pow(MAX_DIFF_RATE,0.5))/(COURANT_NUM*SQUARE_DIAM)));
        DIFF_TIMESTEP= ADI_MAX_SIM_TIME / ADI_MAX_STEPS;
        DIFF_SCALE_FACTOR = DIFF_TIMESTEP / (SQUARE_DIAM * SQUARE_DIAM);
        for (int i = 0; i < N_DIFFS; i++) {
            diffRates[i]=diffRates[i]*DIFF_SCALE_FACTOR;
        }
        CalcVesselDegradationRate();
    }
    private void SetupDiffusibleArrs(){
        if(diffs==null) {
            diffs = new PDEGrid2D[N_DIFFS];
            for (int i = 0; i < N_DIFFS; i++) {
                diffs[i] = new PDEGrid2D(xDim, yDim);
            }
        }
        if(vesselConcs==null) {
            vesselConcs = new double[N_DIFFS];
        }
        vesselConcs[OXYGEN]=VESSEL_O2;
        vesselConcs[GLUCOSE]=VESSEL_GLUC;
        vesselConcs[ACID]=PhToProtons(VESSEL_PH);
        if(diffRates==null) {
            diffRates = new double[N_DIFFS];
        }
        diffRates[OXYGEN]=DIFF_RATE_O2;
        diffRates[GLUCOSE]=DIFF_RATE_GLUCOSE;
        diffRates[ACID]=DIFF_RATE_PROTON;
        if(boundaryConds==null) {
            boundaryConds = new double[N_DIFFS];
        }
        if(hypoxicIs==null) {
            hypoxicIs = new int[GetDiff(OXYGEN).length];
        }
    }

    public PDEGrid2D GetDiff(int diffusible) {
        return diffs[diffusible];
    }


    public int GetTypePop(int type) {
        int ct=0;
        for (MarkCell_II c : this) {
            if(c.type==type){
                ct++;
            }
        }
        return ct;
    }

    public int InitVessels() {
        //place vessels using circle packing, so no vessels are within VESSEL_SPACING_MIN
        int[] vesselCheck = CircleHood(true, VESSEL_SPACING_MIN);
        int[] circleIs = new int[vesselCheck.length / 2];
        int[] ret= GenIndicesArray(length);
        Shuffle(ret, length, length, rn);
        int[] indices = ret;
        int nVesselsRequested = (int) (length / VESSEL_SPACING_MEAN*VESSEL_SPACING_MEAN);
        int nVesselsPlaced = 0;
        for (int i : indices) {
            int x=ItoX(i);
            int y=ItoY(i);
            //make sure that we aren't putting a vessel too close to the edge
            if(x>=VESSEL_EDGE_MAX_DIST&&y>=VESSEL_EDGE_MAX_DIST&&xDim-x>VESSEL_EDGE_MAX_DIST&&yDim-y>VESSEL_EDGE_MAX_DIST) {
                int nNeigbhors = HoodToOccupiedIs(vesselCheck, circleIs, ItoX(i), ItoY(i));
                for (int j = 0; j < nNeigbhors + 1; j++) {
                    if (j == nNeigbhors) {
                        MarkCell_II newVessel = NewAgentSQ(i);
                        newVessel.InitVessel();
                        nVesselsPlaced++;
                        if (nVesselsPlaced == nVesselsRequested) {
                            return nVesselsPlaced;
                        }
                    } else if (GetAgent(circleIs[j]).type == VESSEL) {
                        break;
                    }
                }
            }
        }
        return nVesselsPlaced;
    }

    public void SetupTissue(double startProp) {
        //randomly distribute cells with proportion startProp
        for (int i = 0; i < length; i++) {
            if (GetAgent(i) == null) {
                if (rn.nextDouble() < startProp) {
                    MarkCell_II c = NewAgentSQ(i);
                    c.InitNormal();
                    //initialize cells with random cell cycle progress
                    c.cycleRemaining=c.cycleRemaining*rn.nextDouble();
                }
            }
        }
    }

    public boolean Angiogenesis() {
        //find hypoxic areas
        int nHypox = 0;
        PDEGrid2D oxygen = GetDiff(OXYGEN);
        int nPossible=oxygen.length;
            for (int i = 0; i < oxygen.length; i++) {
                double concO2 = oxygen.Get(i);
                if (concO2 >= HYPOX_ANGIO_ZONE_MIN && concO2 <= HYPOX_ANGIO_ZONE_MAX) {
                    hypoxicIs[nHypox] = i;
                    nHypox++;
                }
            }
        if (rn.nextDouble() < (nHypox * ANGIO_RATE*CELL_TIMESTEP) / nPossible) {
            //replace cell or empty space at random position in hypoxic zone with new vessel
            Shuffle(hypoxicIs, nHypox, 1, rn);
            MarkCell_II occupant = GetAgent(hypoxicIs[0]);
            if (occupant == null) {
                occupant = NewAgentSQ(hypoxicIs[0]);
            }
            occupant.InitVessel();
            return true;
        }
        return false;
    }

    public void SetupTumor(double startRad, double startGlycPheno, double startAcidResistPheno) {
        //convert all normal cells within radius to tumor cells
        int[] startHood = CircleHood(true, startRad);
        int[] coordIs = new int[startHood.length / 2];
        int nCells = HoodToOccupiedIs(startHood, coordIs, xDim / 2, yDim / 2);
        for (int i = 0; i < nCells; i++) {
            MarkCell_II c = GetAgent(coordIs[i]);
            if (c.type == NORMAL) {
                c.InitTumor(startGlycPheno, startAcidResistPheno, rn.nextDouble());
            }
        }
    }

    public int InitDiffs() {
        IncTick();//make sure cells/vessels are present
        if (REFLECTIVE_BOUNDARY) {
            return SteadyStateDiff(false,ADI_MIN_STEPS, ADI_MAX_STEPS_START);
        } else {
            return SteadyStateDiff(true,ADI_MIN_STEPS, ADI_MAX_STEPS_START);
        }
    }
    public int StepDiffs(){
        return SteadyStateDiff(false,ADI_MIN_STEPS, ADI_MAX_STEPS);
    }
    public void ADIFirstHalf(){
        for (int iDiff = 0; iDiff < N_DIFFS; iDiff++) {
            PDEGrid2D diff = diffs[iDiff];
            if (!REFLECTIVE_BOUNDARY) {
                diff.DiffusionADIHalfX(diffRates[iDiff], true, boundaryConds[iDiff]);
            } else {
                diff.DiffusionADIHalfX(diffRates[iDiff], false, 0);
            }
        }
    }
    public void ADISecondHalf() {
        for (int iDiff = 0; iDiff < N_DIFFS; iDiff++) {
            PDEGrid2D diff = diffs[iDiff];
            if (!REFLECTIVE_BOUNDARY) {
                diff.DiffusionADIHalfY(diffRates[iDiff], true, boundaryConds[iDiff]);
            } else {
                diff.DiffusionADIHalfY(diffRates[iDiff], false, 0);
            }
        }
    }

    public void ADIStep() {
        ADIFirstHalf();
        for (MarkCell_II vessel : this.vesselList) {
            vessel.Metabolism();
        }
        ADISecondHalf();
    }

    public boolean DiffStep(boolean checkSteady) {
        //returns true if steady state conditions are met
        for (MarkCell_II c : this) {
            c.Metabolism();
        }
        ADIStep();
        return checkSteady && IsSteady();
    }

    public int SteadyStateDiff(boolean setBoundary,int minSteps,int maxSteps) {
        //Steady state reaction diffusion
        boolean steady;
        int step = 0;
        do {
            if(step==maxSteps){
                steady=true;
                System.err.println("Steady state calculation went all the way! steps: "+maxSteps);
            } else if(step<minSteps-1) {
                steady = DiffStep(false);
            } else if(step==minSteps-1){
                //set the internal compare array for the steady state calculations in subsequent steps
                DiffStep(true);
                steady=false;
            }else{
                steady=DiffStep(true);
            }
            step++;
            if (setBoundary) {
                for (int i = 0; i < N_DIFFS; i++) {
                    boundaryConds[i] = diffs[i].GetAvg();
                }
            }
        } while (!steady&&step<maxSteps);
        return step;
    }

    public void DrawCells(GuiGridVis drawHere){
        for (int i = 0; i < length; i++) {
            MarkCell_II c=GetAgent(i);
            if(c==null) {
                drawHere.SetPix(i,EMPTY_COLOR);
            } else{
                drawHere.SetPix(i,c.drawColor);
            }
        }
    }
    public void DrawAllDiffs(GuiGridVis acidVis,GuiGridVis oxygenVis,GuiGridVis glucoseVis){
        DrawAcid(acidVis);
        DrawOxygen(oxygenVis);
        DrawGlucose(glucoseVis);

    }
    public void DrawOxygen(GuiGridVis oxygenVis){
        double min=diffs[OXYGEN].GetMin();
        double max=diffs[OXYGEN].GetMax();
        DrawDiff(oxygenVis,OXYGEN,(val)->{
            //return HeatMapBGR(val,min,max);
            return HeatMapBGR(val,0,VESSEL_O2);
        });
    }
    public void DrawGlucose(GuiGridVis glucoseVis){
        double min=diffs[GLUCOSE].GetMin();
        double max=diffs[GLUCOSE].GetMax();
        DrawDiff(glucoseVis,GLUCOSE,(val)->{
            return HeatMapRGB(val,0,VESSEL_GLUC);
            //return HeatMapRGB(val,min,max);
        });
    }
    public void DrawAcid(GuiGridVis acidVis){
        double min=ProtonsToPh(diffs[ACID].GetMax());
        double max=ProtonsToPh(diffs[ACID].GetMin());
        DrawDiff(acidVis,ACID,(val)->{
            return HeatMapGRB(ProtonsToPh(val),VESSEL_PH,NORMAL_PHENO_ACID_RESIST);
            //return HeatMapGRB(1-Rescale0to1(ProtonsToPh(val),min,max));
        });
    }
    public void DrawDiff(GuiGridVis drawHere,int iDiff, DoubleToColor ColorFun){
        PDEGrid2D drawMe=diffs[iDiff];
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    drawHere.SetPix(x,y,ColorFun.GenColor(drawMe.Get(x,y)));
                }
            }
    }
    public void DrawPhenos(GuiGridVis drawHere){
        for (int x = 0; x < drawHere.xDim; x++) {
            for (int y = 0; y < drawHere.yDim; y++) {
                drawHere.SetPix(x,y,CbCrPlaneColor(y*1.0/yDim,x*1.0/xDim));
            }
        }
        for (MarkCell_II c : this) {
            if(c.type ==TUMOR){
                drawHere.SetPix((int)(c.GetAcidResistPheno(c.acidResistPH)*yDim),(int)(c.GetGlycPheno(c.glycRate)*xDim),EMPTY_COLOR);
            }
        }
    }
    public boolean IsSteady(){
        double maxMaxDif=Double.MIN_VALUE;
        for (PDEGrid2D diff : this.diffs) {
            maxMaxDif=Math.max(maxMaxDif,diff.MaxDifInternalScaled(EPS));
            if(maxMaxDif> ADI_STOP_DIF){
                return false;
            }
        }
        return true;
    }

    public void StepAll() {
        SteadyStateDiff(false,ADI_MIN_STEPS,ADI_MAX_STEPS);
        Angiogenesis();
        StepCells();
        CleanShuffInc(rn);
    }
    public void StepCells(){
        for (MarkCell_II c : this) {
            c.CellStep(rn);
        }
    }

    public void InitAll(double startDensity,double tumorRad) {
        InitVessels();
        SetupTissue(startDensity);
        InitDiffs();
        SetupTumor(tumorRad, NORMAL_PHENO_GLYC, NORMAL_PHENO_ACID_RESIST);
        IncTick();
    }
    public void PrintDiffs() {
        System.out.println("Oxygen:");
        System.out.println(GetDiff(OXYGEN).ToMatrixString("\t", 4));
        System.out.println("Glucose:");
        System.out.println(GetDiff(GLUCOSE).ToMatrixString("\t", 4));
        System.out.println("Acid:");
        System.out.println(GetDiff(ACID).ToMatrixString("\t", Utils::ProtonsToPh, 4));
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Oxygen BC:" + boundaryConds[OXYGEN]);
        }
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Glucose BC:" + boundaryConds[GLUCOSE]);
        }
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Acid BC:" + ProtonsToPh(boundaryConds[ACID]));
        }
    }


}
