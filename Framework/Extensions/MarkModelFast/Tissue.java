package Framework.Extensions.MarkModelFast;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGrid;
import Framework.Interfaces.DoubleToColor;
import Framework.Rand;
import Framework.Util;

import java.util.LinkedList;

import static Framework.Util.*;

//more invasive phenotypes?: migration/chomping

//different world spaces

//something to consider is moving the domain as the edge is reached, with wraparound? (would have to do ADI with wraparound)

public class Tissue extends AgentGrid2D<Cell> {
    //OPTIMIZATION VARS
    public final int DIFF_SPACE_SCALE;
    //WTF VARS
    public double ADI_STOP_DIF =1E-5;
    public int ADI_MIN_STEPS=5;
    public double TISSUE_OPEN_NEIGH_THRESH=3.5;
    public double TISSUE_ATP_PROLIF_THRESH=0.8;
    public double ATP_HALF_MAX=1.1;
    public double COURANT_NUM=0.1;
    public double EPS=2E-52;

    //CELL TYPE ENUM
    public static int NORMAL = 1, TUMOR = 2, VESSEL = 3, DEAD = 4,NECRO=5;

    //DRAW COLORS
    public static int NORMAL_COLOR=RGB(0,0,1),VESSEL_COLOR=RGB(1,1,1), DEAD_COLOR =RGB(0.5,0,0.5),NECRO_COLOR=RGB(0.2,0.2,0.2),EMPTY_COLOR=RGB(0,0,0), BLACK=RGB(0,0,0), WHITE=RGB(1,1,1), GREY=RGB(0.5,0.5,0.5);
    public static final int DEFAULT_SIDE_LEN=76;

    //TIME AND SPACE SCALES
    public double SQUARE_DIAM = 20;//micrometers
    public final int CELLS_PER_SQ=1;
    public double CELL_TIMESTEP = 1.0/12;//in days
    public double ADI_MAX_SIM_TIME =50;
    public int ADI_MAX_SIM_TIME_START = (int) ADI_MAX_SIM_TIME *6;

    //MISC
    public double MIN_CELL_CYCLE_TIME = 18;

    //EVENT PROBABILITIES
    //public double DEATH_PROB_NORM_COND_BASE = 0.005;
    public double DEATH_PROB_NORM_COND_BASE = 0.002;
    public double DEATH_PROB_POOR_COND_BASE = 0.7;
    public double DISPOSE_PROB_NECRO_BASE = 5E-4;
    public double DISPOSE_PROB_DEAD_BASE = 0.5;

    //VESSEL
    public double VESSEL_SPACING_MIN_BASE = 80;//in microns
    public double VESSEL_SPACING_MEAN_BASE = 158;// TODO this seems not dense enough
    //public double VESSEL_SPACING_MEAN = 100;
    public double HYPOX_ANGIO_ZONE_MIN = 8E-4;
    public double HYPOX_ANGIO_ZONE_MAX = 4E-3;
    public double HYPOX_ZONE_SIZE=100;
    public double ANGIO_PROB=0.5;
    public int VESSEL_HP_MAX = 1;
    public double ANGIO_RATE = 0.0;
    public double VESSEL_EDGE_MAX_DIST=1;//no vessels this close to the edge
    public double VESSEL_DEGRADATION_RATE_BASE=1;
    public double VESSEL_DEATH_CONST=1;

    //NORMIES
    public double NORMAL_PHENO_ACID_RESIST = 6.65;
    //public double NORMAL_PHENO_ACID_RESIST = 7.00;
    public double NORMAL_PHENO_GLYC = 1;
    public double NORMAL_EMPTY_DIV_REQ = 2;
    //public double NORMAL_EMPTY_DIV_WIGGLE = 1.0;
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
    public double BOUNDARY_GLUCOSE;

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

    //OTHER OBJECTS
    public Rand rn;
    public Diff oxygen;
    public Diff glucose;
    public Diff acid;

    public final LinkedList<Cell> vesselList=new LinkedList<>();
    public int[] hypoxicIs;
    public int[] normalHood = MooreHood(false);
    public int[] tumorHood = MooreHood(false);
    //public int[] tumorHood = VonNeumannHood(true);
    public int[] vesselHood= new int[]{-2,0,-1,0,1,0,2,0,0,-1,0,-2,0,1,0,2};
    public int[] vesselAngioHood;
    public int[] vesselAngioIs;
    public int[] normalHoodIs = new int[normalHood.length / 2];
    public int[] tumorHoodIs = new int[normalHood.length / 2];
    public int[] vesselIs = new int[vesselHood.length / 2];
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
    public double DISPOSE_PROB_DEAD;
    public double VESSEL_SPACING_MIN;
    public double VESSEL_SPACING_MEAN;
    public double VESSEL_DEGRADATION_RATE;

    //DRUGS

    private boolean drugsSetup;
    String[]drugNames;
    int[]drugColors;


    public Tissue(int x, int y,int diffScale, boolean reflectiveBoundary, boolean setupConstants, Rand rn) {
        super(x, y, Cell.class);
        if(x%diffScale!=0||y%diffScale!=0){
            throw new IllegalArgumentException("DIFF_SPACE_SCALE must divide evenly!");
        }
        this.DIFF_SPACE_SCALE = diffScale;
        oxygen=new Diff(xDim/ DIFF_SPACE_SCALE, yDim/ DIFF_SPACE_SCALE, DIFF_SPACE_SCALE, this,true);
        glucose=new Diff(xDim/ DIFF_SPACE_SCALE, yDim/ DIFF_SPACE_SCALE, DIFF_SPACE_SCALE, this,true);
        acid=new Diff(xDim/ DIFF_SPACE_SCALE, yDim/ DIFF_SPACE_SCALE, DIFF_SPACE_SCALE, this,true);
        hypoxicIs = new int[oxygen.grid.length];
        this.rn=rn;
        this.REFLECTIVE_BOUNDARY = reflectiveBoundary;
        if(setupConstants) {
            SetupConstants();
        }
    }
    public static String GetDiffVals(PDEGrid2D o2, PDEGrid2D gluc, PDEGrid2D acid){
        return "cornerO2:"+o2.Get(1,1)+", cornerGluc:"+gluc.Get(1,1)+", cornerAcid:"+ProtonsToPh(acid.Get(1,1))+
        "midO2:"+o2.Get(o2.xDim/2,o2.yDim/2)+", midGluc:"+gluc.Get(gluc.xDim/2,gluc.yDim/2)+", midAcid:"+ProtonsToPh(acid.Get(acid.xDim/2,acid.yDim/2));
    }

    public double GetGlycRate(double pheno) {
        return Math.exp(pheno * Math.log(MAX_PHENO_GLYC));
    }

    public double GetAcidResistPH(double pheno) {
        return ScaleMinToMax(pheno, NORMAL_PHENO_ACID_RESIST, MAX_PHENO_ACID_RESIST);
    }

    public double GetGlycPheno(double glycRate) {
        return Math.log10(glycRate) / Math.log10(MAX_PHENO_GLYC);
    }

    public double GetAcidResistPheno(double acidResistPH) {
        return Scale0to1(acidResistPH, NORMAL_PHENO_ACID_RESIST, MAX_PHENO_ACID_RESIST);
    }
    public void CalcVesselDegradationRate(){
        VESSEL_DEGRADATION_RATE=VESSEL_DEGRADATION_RATE_BASE*CELL_TIMESTEP;
    }
    public double GetMaxDiffRate() {
        double max = Double.MIN_VALUE;
        max = oxygen.diffRate > max ? oxygen.diffRate : max;
        max = glucose.diffRate > max ? glucose.diffRate : max;
        max = acid.diffRate > max ? acid.diffRate : max;
        return max;
    }
    public void SetupConstants(){
        DEATH_PROB_NORM_COND= Util.ProbScale(DEATH_PROB_NORM_COND_BASE, CELL_TIMESTEP);
        DEATH_PROB_POOR_COND = Util.ProbScale(DEATH_PROB_POOR_COND_BASE, CELL_TIMESTEP);
        DISPOSE_PROB_NECRO= Util.ProbScale(DISPOSE_PROB_NECRO_BASE,CELL_TIMESTEP);
        DISPOSE_PROB_DEAD = Util.ProbScale(DISPOSE_PROB_DEAD_BASE,CELL_TIMESTEP);
        VESSEL_SPACING_MIN=VESSEL_SPACING_MIN_BASE/SQUARE_DIAM;
        VESSEL_SPACING_MEAN=VESSEL_SPACING_MEAN_BASE/SQUARE_DIAM;
        ATP_TARGET = 29 * MAX_CONSUMPTION_O2 / 5;
        oxygen.vesselConc=VESSEL_O2;
        glucose.vesselConc=VESSEL_GLUC;
        acid.vesselConc=PhToProtons(VESSEL_PH);
        oxygen.diffRate=DIFF_RATE_O2;
        glucose.diffRate=DIFF_RATE_GLUCOSE;
        acid.diffRate=DIFF_RATE_PROTON;
        oxygen.boundaryValue=BOUNDARY_O2;
        glucose.boundaryValue=BOUNDARY_GLUCOSE;
        acid.boundaryValue=BOUNDARY_ACID;
        MAX_DIFF_RATE=GetMaxDiffRate();
        ADI_MAX_STEPS =(int)(Math.max(10,Math.round(ADI_MAX_SIM_TIME *(Math.pow(MAX_DIFF_RATE,0.5))/(COURANT_NUM*SQUARE_DIAM* DIFF_SPACE_SCALE * DIFF_SPACE_SCALE))));
        ADI_MAX_STEPS_START =(int)(Math.max(10,Math.round(ADI_MAX_SIM_TIME_START *(Math.pow(MAX_DIFF_RATE,0.5))/(COURANT_NUM*SQUARE_DIAM* DIFF_SPACE_SCALE * DIFF_SPACE_SCALE))));
        DIFF_TIMESTEP= ADI_MAX_SIM_TIME / ADI_MAX_STEPS;
        DIFF_SCALE_FACTOR = DIFF_TIMESTEP / (SQUARE_DIAM * SQUARE_DIAM);
        oxygen.diffRate=oxygen.diffRate*DIFF_SCALE_FACTOR;
        glucose.diffRate=glucose.diffRate*DIFF_SCALE_FACTOR;
        acid.diffRate=acid.diffRate*DIFF_SCALE_FACTOR;
        CalcVesselDegradationRate();
        vesselAngioHood=CircleHood(true,(VESSEL_SPACING_MIN-SQUARE_DIAM)*1.0/SQUARE_DIAM);
        vesselAngioIs=new int[vesselAngioHood.length/2];
    }

    public int SetupVessels() {
        //place vessels using circle packing, so no vessels are within VESSEL_SPACING_MIN
        int[] vesselCheck = CircleHood(true, VESSEL_SPACING_MIN);
        int[] circleIs = new int[vesselCheck.length / 2];
        int[] ret= GenIndicesArray(length);
        rn.Shuffle(ret, length, length);
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
                        Cell newVessel = NewAgentSQ(i);
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
                if (rn.Double() < startProp) {
                    Cell c = NewAgentSQ(i);
                    c.InitNormal(rn.Double()*MIN_CELL_CYCLE_TIME);
                }
            }
        }
    }

    public void Angiogenesis() {
        //find hypoxic areas
        int nHypox = 0;
        int nPossible=oxygen.grid.length;
        for (int i = 0; i < oxygen.grid.length; i++) {
            double concO2 = oxygen.grid.Get(i);
            if (concO2 >= HYPOX_ANGIO_ZONE_MIN && concO2 <= HYPOX_ANGIO_ZONE_MAX) {
                hypoxicIs[nHypox] = i;
                nHypox++;
            }
        }
        int nNewVessels=(int)(nHypox*(DIFF_SPACE_SCALE * DIFF_SPACE_SCALE)/100.0+0.5);
        rn.Shuffle(hypoxicIs, nHypox, nNewVessels);
        for (int i = 0; i < nNewVessels; i++) {
            double angioProb=ANGIO_RATE*CELL_TIMESTEP;
            if (rn.Double() < angioProb) {
                //replace cell or empty space at random position in hypoxic zone with new vessel
                int x=oxygen.grid.ItoX(hypoxicIs[i])* DIFF_SPACE_SCALE +rn.Int(DIFF_SPACE_SCALE);
                int y=oxygen.grid.ItoY(hypoxicIs[i])* DIFF_SPACE_SCALE +rn.Int(DIFF_SPACE_SCALE);
                Cell occupant = GetAgent(x,y);
                if(occupant!=null&&occupant.type==NECRO) {
                    break;
                }
                if (occupant == null) {
                    occupant = NewAgentSQ(x,y);
                }
                occupant.InitVessel();
            }
        }
    }

    public void SetupTumor(double startRad, double startGlycPheno, double startAcidResistPheno) {
        //convert all normal cells within radius to tumor cells
        int[] startHood = CircleHood(true, startRad);
        int[] coordIs = new int[startHood.length / 2];
        int nCells = HoodToOccupiedIs(startHood, coordIs, xDim / 2, yDim / 2);
        for (int i = 0; i < nCells; i++) {
            Cell c = GetAgent(coordIs[i]);
            if (c.type == NORMAL) {
                c.InitTumor(startGlycPheno, startAcidResistPheno,rn.Double()*MIN_CELL_CYCLE_TIME);
            }
        }
    }

    public int SetupBoundaryConds() {
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
    public void RunDiffusion(){
        oxygen.Diffuse(REFLECTIVE_BOUNDARY);
        glucose.Diffuse(REFLECTIVE_BOUNDARY);
        acid.Diffuse(REFLECTIVE_BOUNDARY);
    }


    public boolean DiffStep(boolean checkSteady) {
        //returns true if steady state conditions are met
        for (Cell c : this) {
            c.Metabolism();
        }
        for (Cell vessel : vesselList) {
            int x=vessel.Xsq();
            int y=vessel.Ysq();
            oxygen.SetVesselConc(x,y);
            glucose.SetVesselConc(x,y);
            acid.SetVesselConc(x,y);
        }
        RunDiffusion();
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
                oxygen.boundaryValue=oxygen.grid.GetAvg();
                glucose.boundaryValue=glucose.grid.GetAvg();
                acid.boundaryValue=acid.grid.GetAvg();
            }
        } while (!steady&&step<maxSteps);
        for (Cell cell:this) {
            cell.ATPComp();
        }
        return step;
    }

    public void DrawCells(GuiGrid drawHere){
        for (int i = 0; i < length; i++) {
            Cell c=GetAgent(i);
            if(c==null) {
                drawHere.SetPix(i,EMPTY_COLOR);
            } else{
                drawHere.SetPix(i,c.drawColor);
            }
        }
    }
    public void DrawAllDiffs(GuiGrid acidVis, GuiGrid oxygenVis, GuiGrid glucoseVis){
        DrawAcidOld(acidVis);
        DrawOxygenOld(oxygenVis);
        DrawGlucoseOld(glucoseVis);

    }
    public void DrawOxygenMinMaxAngio(GuiGrid oxygenVis){

        DrawDiff(oxygenVis,oxygen,(val)->{
            if(val>HYPOX_ANGIO_ZONE_MAX){
                return WHITE;
            }
            else if(val<HYPOX_ANGIO_ZONE_MIN){
                return BLACK;
            }
            else{
                return GREY;
            }
            //return HeatMapBGR(val,min,max);
        });
    }
    public void DrawDiff(GuiGrid drawHere, Diff drawMe, DoubleToColor ColorFun){
        for (int x = 0; x < drawMe.grid.xDim; x++) {
            for (int y = 0; y < drawMe.grid.yDim; y++) {
                drawHere.SetPix(x,y,ColorFun.GenColor(drawMe.grid.Get(x,y)));
            }
        }
    }
    public void DrawPhenos(GuiGrid drawHere){
        for (int x = 0; x < drawHere.xDim; x++) {
            for (int y = 0; y < drawHere.yDim; y++) {
                drawHere.SetPix(x,y,CbCrPlaneColor(y*1.0/yDim,x*1.0/xDim));
            }
        }
        for (Cell c : this) {
            if(c.type ==TUMOR){
                drawHere.SetPix((int)(c.GetGlycPheno(c.glycRate)*(xDim-1)),(int)(c.GetAcidResistPheno(c.acidResistPH)*(yDim-1)),EMPTY_COLOR);
            }
        }
    }
    public boolean IsSteady(){
        double maxMaxDif=Double.MIN_VALUE;
            maxMaxDif=Math.max(maxMaxDif,oxygen.grid.MaxDifInternalScaled(EPS));
            maxMaxDif=Math.max(maxMaxDif,glucose.grid.MaxDifInternalScaled(EPS));
            maxMaxDif=Math.max(maxMaxDif,acid.grid.MaxDifInternalScaled(EPS));
            if(maxMaxDif> ADI_STOP_DIF){
                return false;
            }
        return true;
    }

    public void StepAll() {
        SteadyStateDiff(false,ADI_MIN_STEPS,ADI_MAX_STEPS);
        Angiogenesis();
        StepCells();
        CleanShuffInc(rn);
        if(GetTick()%20==0){
            System.out.println("tick:"+GetTick()+", "+GetDiffVals(oxygen.grid,glucose.grid,acid.grid));
        }
    }
    public void StepCells(){
        for (Cell c : this) {
            c.CellStep();
        }
    }

    public void SetupAll(double startDensity, double tumorRad) {
        SetupVessels();
        SetupTissue(startDensity);
        SetupBoundaryConds();
        SetupTumor(tumorRad, NORMAL_PHENO_GLYC, NORMAL_PHENO_ACID_RESIST);
        IncTick();
    }
    public void PrintDiffs() {
        System.out.println("Oxygen:");
        System.out.println(oxygen.grid.ToMatrixString("\t", 4));
        System.out.println("Glucose:");
        System.out.println(glucose.grid.ToMatrixString("\t", 4));
        System.out.println("Acid:");
        System.out.println(acid.grid.ToMatrixString("\t", Util::ProtonsToPh, 4));
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Oxygen BC:" + oxygen.boundaryValue);
        }
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Glucose BC:" + glucose.boundaryValue);
        }
        if (!REFLECTIVE_BOUNDARY) {
            System.out.println("Acid BC:" + ProtonsToPh(acid.boundaryValue));
        }
    }

    public int AlphaColorDiff(double val,double min,double max,double alphaMin,double alphaMax,int color){
        return SetAlpha(color, Rescale(val,min,max,alphaMin,alphaMax));
    }

    final static int CYAN=RGB(0,1,1);
    final static int YELLOW=RGB(1,1,0);
    final static int GREEN=RGB(0,1,0);
    public void DrawOxygen(GuiGrid alphaVis){
        DrawDiff(alphaVis,oxygen,(double v)->AlphaColorDiff(v,0,VESSEL_O2,0,0.7,CYAN));
    }
    public void DrawGlucose(GuiGrid alphaVis){
        DrawDiff(alphaVis,glucose,(double v)->AlphaColorDiff(v,0,VESSEL_GLUC,0,0.7,YELLOW));
    }
    public void DrawAcid(GuiGrid alphaVis){
        DrawDiff(alphaVis,acid,(double v)->AlphaColorDiff(ProtonsToPh(v),VESSEL_PH,5.5,0,0.7,GREEN));
    }
    public void DrawOxygenOld(GuiGrid oxygenVis){
        DrawDiff(oxygenVis,oxygen,(val)->{
            //return HeatMapBGR(val,min,max);
            return HeatMapBGR(val,0,VESSEL_O2);
        });
    }
    public void DrawGlucoseOld(GuiGrid glucoseVis){
        DrawDiff(glucoseVis,glucose,(val)->{
            return HeatMapRGB(val,0,VESSEL_GLUC);
            //return HeatMapRGB(val,min,max);
        });
    }
    public void DrawAcidOld(GuiGrid acidVis){
        DrawDiff(acidVis,acid,(val)->{
            return HeatMapGRB(ProtonsToPh(val),VESSEL_PH,NORMAL_PHENO_ACID_RESIST);
            //return HeatMapGRB(1-Scale0to1(ProtonsToPh(val),min,max));
        });
    }
}

