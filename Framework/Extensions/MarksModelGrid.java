package Framework.Extensions;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GuiGridVis;
import Framework.Utils;

import java.util.Random;

import static Framework.Utils.*;

/**
 * Created by bravorr on 6/28/17.
 */

public class MarksModelGrid<T extends MarksModelCell> extends AgentGrid2D<T> {
    public PDEGrid2D oxygen;
    public PDEGrid2D glucose;
    public PDEGrid2D protons;
    public  int angioIs[];
    public  int[] vesselPosToCheck;
    public  int[] vesselPosIs;
    static public  int[] MOORE_NEIGHBORHOOD = MooreHood(false);
    public  int[] moveIs=new int[8];

     public  Random rn=new Random();
    //Stores all constant values and global functions
    //GENERAL CONSTANTS
     public  double GRID_SIZE=20f;

    //DIFFUSION CONSTANTS
     public  int CHEM_GRID_SCALE=1;
     public  double DIFF_TIME_STEP=0.05f;//in seconds
     public  double CELL_TIME_STEP=0.2f;//in days
    //OXYGEN
     public  double DELTA_SCALE=10;
     public  double OXYGEN_MAX_RATE =0.012f*DIFF_TIME_STEP;
     public  double OXYGEN_HALF_RATE_CONC =0.005f;
     public  double OXYGEN_VESSEL_CONC =0.056f;
     double OXYGEN_AVG_CONC =0.01f;
    //     double OXYGEN_AVG_CONC =OXYGEN_VESSEL_CONC;
     public  double OXYGEN_DIFF_RATE =1820f*DIFF_TIME_STEP*1.0f/(GRID_SIZE*GRID_SIZE);
     public  double OXYGEN_MAX_DELTA =0.001f*DIFF_TIME_STEP*DELTA_SCALE;
    //GLUCOSE
     public  double GLUCOSE_HALF_RATE_CONC =0.04f;
     public  double GLUCOSE_VESSEL_CONC =5.0f;
     double GLUCOSE_AVG_CONC =2.0f;
    //     double GLUCOSE_AVG_CONC =GLUCOSE_VESSEL_CONC;
     public  double GLUCOSE_DIFF_RATE=500f*DIFF_TIME_STEP*1.0f/(GRID_SIZE*GRID_SIZE);
     public  double GLUCOSE_MAX_DELTA =0.2f*DIFF_TIME_STEP*DELTA_SCALE;
    //ACID
     public  double BUFFERING_COEFFICIENT=0.00025f;
     public  double ACID_VESSEL_CONC = PhToProtons(7.4f);
     public  double ACID_DIFF_RATE=1080f*DIFF_TIME_STEP*1.0f/(GRID_SIZE*GRID_SIZE);
     double ACID_AVG_PROTONS = PhToProtons(7.0f);
    //    double ACID_AVG_PROTONS = ACID_VESSEL_CONC;
     public  double ACID_MAX_DELTA= PhToProtons(2.0f)*DIFF_TIME_STEP*DELTA_SCALE;

    //CELL CONSTANTS
     public  double MAX_ATP_PRODUCTION =29*OXYGEN_MAX_RATE/5;//scaled by diff timestep
     public  double EMPTY_SQUARES_FOR_DIV =2.2f;
     public  double DIV_NOISE_STD_DEV =0.5f;
     public  double POOR_CONDITION_DEATH_RATE=0.7f;
     public  double ATP_DEATH=0.3f;
     public  double ATP_QUIESCENT=0.8f;
     public  double MIN_CELL_CYCLE_TIME =0.8f;//in days

    //MUTATION CONSTANTS
     public  double MUTATION_SCALE=10;
     public  double NORMAL_ACID_RESIST_PHENO =6.65f;
     public  double MAX_ACID_RESIST_PHENO =6.1f;
     public  double MIN_ACID_RESIST_PHENO=6.8f;
     public  double ACID_RESIST_MUTATION_RATE=0.001f*MUTATION_SCALE;
     public  double GLYCOLYSIS_MUTATION_RATE=0.005f*MUTATION_SCALE;
     public  double MAX_GLYCOLYTIC_PHENO=50f;
     public  double NORMAL_GLYCOLYTIC_PHENO=1f;
     public  double MIN_GLYCOLYTIC_PHENO=0f;

    //CELL DEATH CONSTANTS
     public  double NORMAL_DEATH_PROB=ProbScale(0.005f,CELL_TIME_STEP);
     public  double APOPTOTIC_REMOVE_PROB=ProbScale(0.5f,CELL_TIME_STEP);
     public  double NECROTIC_REMOVE_PROB=(double)Math.pow(0.0005f,CELL_TIME_STEP);

    //VESSEL CONSTANTS
     public  double MIN_SPACING=80f/GRID_SIZE;
     public  double MEAN_SPACING=158f/GRID_SIZE;
     public  double ANGIOGEN_RATE=0.3f;
     public  int VESSEL_STABILITY=20;

     public  double ANGEOGENESIS_MIN_O_CONC=0.0008f;
     public  double ANGEOGENESIS_MAX_O_CONC=0.002f;

     //typeGrid constructor sets up diffusible grids
    public MarksModelGrid(int x,int y,Class<T> classObj){
        super(x,y,classObj);
        oxygen = new PDEGrid2D(xDim, yDim);
        glucose = new PDEGrid2D(xDim, yDim);
        protons = new PDEGrid2D(xDim, yDim);
        angioIs=new int[xDim*yDim];
        vesselPosToCheck= CircleHood(true, MIN_SPACING);
        vesselPosIs=new int[vesselPosToCheck.length/2];
    }
    public MarksModelGrid(int x,int y,Class<T> classObj,Random rn){
        super(x,y,classObj);
        this.rn=rn;
        oxygen = new PDEGrid2D(xDim, yDim);
        glucose = new PDEGrid2D(xDim, yDim);
        protons = new PDEGrid2D(xDim, yDim);
        angioIs=new int[xDim*yDim];
        vesselPosToCheck= CircleHood(true, MIN_SPACING);
        vesselPosIs=new int[vesselPosToCheck.length/2];
    }

    public void InitDiffusibles(){
        glucose.SetAll(GLUCOSE_AVG_CONC);
        oxygen.SetAll(OXYGEN_AVG_CONC);
        protons.SetAll(ACID_AVG_PROTONS);
    }

    //initializes the typeGrid with vessels and normal cells
    public void FillGrid(double propCells) {
        int expectedVessels = (int) ((xDim * yDim) / (MEAN_SPACING * MEAN_SPACING));
        int actualVessels = MakeVessels(expectedVessels);
        //System.out.println("expectedVessels: "+expectedVessels+"acutalVessels: "+actualVessels);
        int[] ret= GenIndicesArray(xDim * yDim);
        Shuffle(ret, xDim * yDim, (int)((xDim * yDim) * propCells), rn);
        int randomIs[] = ret;
        for (int i = 0; i <(int)(xDim * yDim) * propCells; i++) {
            int RandomGridI = randomIs[i];
            if (GetAgent(RandomGridI) == null) {
                MarksModelCell c= NewAgentSQ(RandomGridI);
                c.Init(NORMAL_GLYCOLYTIC_PHENO, NORMAL_ACID_RESIST_PHENO, false, false);
            }
        }
    }

    //creates a circular tumor by turning normal cells into tumor cells
    public void CreateTumor(double radius,int centerX,int centerY){
        int[] cancerSQs = CircleHood(true,radius);
        int[] cancerIs=new int[cancerSQs.length/2];
        HoodToIs(cancerSQs,cancerIs,centerX,centerY,false,false);
        for (int i:cancerIs) {
            MarksModelCell c = GetAgent(i);
            if (c != null&&!c.IsVessel()) {
                c.isCancer = true;
            }
        }
    }

    //MICROENVIRONMENT VARIABLES


    public boolean AddVessel(int i) {
        // make room and add vessel
        MarksModelCell c = GetAgent(i);
        if(c!=null) {
            c.Dispose();
        }
        c = NewAgentSQ(i);
        c.Init(-1, -1, false, true);
        return true;
    }

    public int CountBelowAngio() {
        int count=0;
        for(int i=0;i<oxygen.length;i++) {
            if (oxygen.Get(i) < ANGEOGENESIS_MIN_O_CONC) {
                count++;
            }
        }
        return count;
    }

    public int GetAngioIs(){
        int count=0;
        for(int i=0;i<oxygen.length;i++){
            if(oxygen.Get(i)> ANGEOGENESIS_MIN_O_CONC&&oxygen.Get(i)< ANGEOGENESIS_MAX_O_CONC){
                angioIs[count]=i;
                count++;
            }
        }
        return count;
    }
    public void Angiogenesis(){
        int hypoxicArea=CountBelowAngio();
        //System.out.println(CountBelowAngio());
        if(hypoxicArea>0) {
            NewVessels(1);
        }
        //if(rn.nextDouble()<(hypoxicArea*1.0)/(compX*compY)){
        //}
    }
    public void NewVessels(int count){
        int nIs=GetAngioIs();
        if(nIs>count) {
            Utils.Shuffle(angioIs, nIs, count, rn);
        }
        else{
            Utils.Shuffle(angioIs, nIs, nIs-1, rn);
        }
        for(int i=0;i<count;i++){
            AddVessel(angioIs[i]);
        }
    }

    public boolean CheckVesselsNearby(int i) {
        int x=ItoX(i);
        int y=ItoY(i);
        HoodToIs(vesselPosToCheck,vesselPosIs,x,y,true,true);
        for (int icheck : vesselPosIs) {
            MarksModelCell c=GetAgent(icheck);
            if(c!=null&&c.IsVessel()) {
                return true;
            }
        }
        return false;
    }

    public int MakeVessels(int expectedVessels) {
        int nVessels=0;
        int[] ret= GenIndicesArray(length);
        Shuffle(ret, length, length, rn);
        int[] Is= ret;
        for (int i = 0; i < length; i++) {
            if(!CheckVesselsNearby(Is[i])){
                AddVessel(Is[i]);
                nVessels++;
                if(nVessels==expectedVessels){
                    break;
                }
            }
        }
        return nVessels;
    }



    public boolean CheckGrid(PDEGrid2D checkMe) {
        for (int i = 0; i < checkMe.length; i++) {
            if (checkMe.Get(i) < 0) {
                return false;
            }
        }
        return true;
    }

    //runs a step of consumption and diffusion
    public int DiffLoop(boolean changeAvgs) {
        //diffusion loop continues until steady state is reached
        int DiffCount = 0;
        double o2MaxDelta, glucoseMaxDelta, protonsMaxDelta;
        do {
            //apply cell consumption
            for (MarksModelCell c : this) {
                c.SetRates();
            }
            o2MaxDelta = oxygen.MaxDifSwap();
            glucoseMaxDelta = glucose.MaxDifSwap();
            protonsMaxDelta = protons.MaxDifSwap();
            //apply diffusion
            glucose.Diffusion(GLUCOSE_DIFF_RATE, GLUCOSE_VESSEL_CONC, false, false);
            oxygen.Diffusion(OXYGEN_DIFF_RATE, OXYGEN_VESSEL_CONC, false, false);
            protons.Diffusion(ACID_DIFF_RATE, ACID_VESSEL_CONC, false, false);
            if (!CheckGrid(glucose) || !CheckGrid(oxygen) || !CheckGrid(protons)) {
                System.out.println("A GRID HAS NEGATIVES");
            }
            DiffCount++;
            if (changeAvgs) {
                GLUCOSE_AVG_CONC = glucose.GetAvg();
                OXYGEN_AVG_CONC = oxygen.GetAvg();
                ACID_AVG_PROTONS = protons.GetAvg();
            }
        }
        while (o2MaxDelta > OXYGEN_MAX_DELTA || glucoseMaxDelta > GLUCOSE_MAX_DELTA || protonsMaxDelta > ACID_MAX_DELTA);
        //    System.out.println("Loops:"+DiffCount+" O:"+o2MaxDelta/OXYGEN_MAX_DELTA+" G:"+glucoseMaxDelta/GLUCOSE_MAX_DELTA+" A:"+protonsMaxDelta/ACID_MAX_DELTA);
        //System.out.println("Ticks:" + GetTick() + " GetAvg O:" + oxygen.GetAvg() + "GetAvg G:" + glucose.GetAvg() + " GetAvg A:" + protons.GetAvg());
        return DiffCount;
    }

    //can draw all 3 diffusibles simultaneously, one for every color channel
    public void DrawMicroEnv(GuiGridVis vis, boolean drawGlucose, boolean drawProtons, boolean drawOxygen) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                float r = 0;
                float g = 0;
                float b = 0;
                if (drawGlucose) {
                    r = (float) (glucose.Get(x, y) / GLUCOSE_VESSEL_CONC);
                }
                if (drawOxygen) {
                    b = (float) (oxygen.Get(x, y) / OXYGEN_VESSEL_CONC);
                }
                if (drawProtons) {
                    g = (float) ((Utils.ProtonsToPh(protons.Get(x, y)) - 6) / 1.4f);
                }
                vis.SetPix(x, y, RGB(r, g, b));
            }
        }
    }
    //can only draw one diffusible at a time
    public void DrawMicroEnvHeat(GuiGridVis vis, boolean drawGlucose, boolean drawProtons, boolean drawOxygen) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                float r = 0;
                float g = 0;
                float b = 0;
                if (drawGlucose) {
                    vis.SetPix(x, y,Utils.HeatMapRGB(glucose.Get(x,y)/ GLUCOSE_VESSEL_CONC));
                }
                else if (drawOxygen) {
                    vis.SetPix(x, y,Utils.HeatMapRGB(oxygen.Get(x, y) / OXYGEN_VESSEL_CONC));
                }
                else if (drawProtons) {
                    vis.SetPix(x, y,Utils.HeatMapRGB((Utils.ProtonsToPh(protons.Get(x, y)) - 6) / 1.4f));
                }
            }
        }
    }
    public void DrawCells(GuiGridVis vis) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                MarksModelCell c = GetAgent(x, y);
                if (c != null) {
                    if (c.IsAlive()) {
                        if (c.IsVessel()) {
                            vis.SetPix(x, y, RGB((double) 0, (double) 1, (double) 1));
                        } else if (c.isCancer) {
                            double acidPhenoScaled = Utils.Rescale0to1(c.acidResistancePheno, MAX_ACID_RESIST_PHENO, MIN_ACID_RESIST_PHENO);
                            double glycoPhenoScaled = Utils.Rescale0to1(c.glycolysisPheno, MIN_GLYCOLYTIC_PHENO, MAX_GLYCOLYTIC_PHENO);
                            //double acidPhenoScaled = Utils.Rescale0to1(c.acidResistancePheno, MAX_ACID_RESIST_PHENO, MIN_ACID_RESIST_PHENO);
                            //double glycoPhenoScaled = Utils.Rescale0to1((double) Math.log(c.glycolysisPheno), (double) Math.log(MIN_GLYCOLYTIC_PHENO + 0.001), (double) Math.log(MAX_GLYCOLYTIC_PHENO));
                            vis.SetPix(x, y, RGB((float) (1 - acidPhenoScaled)*0.8f+0.2f, (float) (glycoPhenoScaled)*0.8f+0.2f, (float) 0));
                            //vis.SetBound(xDim, yDim, 1, 0, 1);
                        } else {
                            vis.SetPix(x, y, RGB((double) 0, (double) 0, (double) 1));
                        }
                    } else {
                        vis.SetPix(x, y, RGB((double) 0, (double) 0, (double) 0.5f));
                    }
                }
                else{
                    vis.SetPix(x, y, RGB((double) 0, (double) 0, (double) 0));
                }
            }
        }
    }
    public void DrawPheno(GuiGridVis vis){
        vis.Clear(RGB((double) 0, (double) 0, (double) 0));
        for (MarksModelCell c : this) {
            if(c.isCancer&&c.IsAlive()){
                double acidPhenoScaled = Utils.Rescale0to1(c.acidResistancePheno, MAX_ACID_RESIST_PHENO, MIN_ACID_RESIST_PHENO);
                double glycoPhenoScaled = Utils.Rescale0to1(c.glycolysisPheno, MIN_GLYCOLYTIC_PHENO, MAX_GLYCOLYTIC_PHENO);
                //double xDraw= Bound(acidPhenoScaled,0,1);
                //double yDraw= Bound(glycoPhenoScaled,0,1);
                vis.SetPix((int)((glycoPhenoScaled)*(vis.xDim-1)), (int)((1-acidPhenoScaled)*(vis.yDim-1)), RGB((float)(1 - acidPhenoScaled)*0.8f+0.2f, (float)(glycoPhenoScaled)*0.8f+0.2f, (float) 0));
            }
        }
    }
    public void DefaultStep(){
        DiffLoop(false);
        for (MarksModelCell c : this) {
            c.DefaultStep();
        }
        CleanShuffInc(rn);
        Angiogenesis();
    }
}
