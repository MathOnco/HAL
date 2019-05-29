package Examples._7ModelExtension.MetabolismModel;

import Framework.GridsAndAgents.AgentGrid2D;
import Framework.GridsAndAgents.AgentList;
import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;
import Framework.Rand;
import Framework.Util;

import static Framework.Util.*;
import static Examples._7ModelExtension.MetabolismModel.MetabolismCell.*;

/**
 * Created by bravorr on 6/28/17.
 */

public class MetabolismGrid<A extends MetabolismCell> extends AgentGrid2D<A> {
    public PDEGrid2D oxygen;
    public PDEGrid2D glucose;
    public PDEGrid2D protons;
    public  int angioIs[];
    public  int[] vesselPosToCheck;
    public int[] mooreHood = MooreHood(false);
    public int[] moveHood = new int[mooreHood.length/3];
    public int nMoveOpts;
    public int[] vesselHood = new int[mooreHood.length/3];
    public int nVesselOpts;
    static final int GREY =RGB(0.25,0.25,0.25);
    static final int NECRO_COLOR=RGB(0.42,0.42,0.42);

     public Rand rn=new Rand();
    //Stores all constant values and global functions
    //GENERAL CONSTANTS
     public  double GRID_SIZE=20;

    //DIFFUSION CONSTANTS
     public  int CHEM_GRID_SCALE=1;
     public  double DIFF_TIME_STEP=0.047619047619048;//in seconds
     public  double CELL_TIME_STEP=1.0/12;//in days
     public  double EPS=2e-16;
     public  double DELTA_TOL=1e-4;
    //OXYGEN
     public  double DELTA_SCALE=10;
     public  double OXYGEN_MAX_RATE =0.012;
     public  double OXYGEN_HALF_RATE_CONC =0.005;
     public  double OXYGEN_VESSEL_CONC =0.0556;
     double OXYGEN_AVG_CONC =0.01;
    //     double OXYGEN_AVG_CONC =OXYGEN_VESSEL_CONC;
     public  double OXYGEN_DIFF_RATE =1820*DIFF_TIME_STEP*1.0/(GRID_SIZE*GRID_SIZE);
     public  double OXYGEN_MAX_DELTA =0.001*DIFF_TIME_STEP*DELTA_SCALE;
    //GLUCOSE
     public  double GLUCOSE_HALF_RATE_CONC =0.04;
     public  double GLUCOSE_VESSEL_CONC =5.0;
     double GLUCOSE_AVG_CONC =2.0;
    //     double GLUCOSE_AVG_CONC =GLUCOSE_VESSEL_CONC;
     public  double GLUCOSE_DIFF_RATE=500*DIFF_TIME_STEP*1.0/(GRID_SIZE*GRID_SIZE);
     public  double GLUCOSE_MAX_DELTA =0.2*DIFF_TIME_STEP*DELTA_SCALE;
    //ACID
     public  double BUFFERING_COEFFICIENT=0.00025;
     public  double ACID_VESSEL_CONC = PhToProtons(7.4);
     public  double ACID_DIFF_RATE=1080*DIFF_TIME_STEP*1.0/(GRID_SIZE*GRID_SIZE);
     double ACID_AVG_PROTONS = PhToProtons(7.0);
    //    double ACID_AVG_PROTONS = ACID_VESSEL_CONC;
     public  double ACID_MAX_DELTA= PhToProtons(2.0)*DIFF_TIME_STEP*DELTA_SCALE;


    public  double OXYGEN_BOUNDARY_CONC =OXYGEN_VESSEL_CONC;
    public  double GLUCOSE_BOUNDARY_CONC =GLUCOSE_VESSEL_CONC;
    public  double ACID_BOUNDARY_CONC =ACID_VESSEL_CONC;
    public  double LAYERS_TO_CHECK_PROP =0.1;

    //CELL CONSTANTS
     public  double MAX_ATP_PRODUCTION =29*OXYGEN_MAX_RATE/5;//scaled by diff timestep
     public  double EMPTY_SQUARES_FOR_DIV =2.8;
     public  double DIV_NOISE_STD_DEV =0.5;
     public  double POOR_CONDITION_DEATH_RATE=ProbScale(0.7,CELL_TIME_STEP);
     public  double ATP_DEATH=0.3;
     public  double ATP_QUIESCENT=0.8;
     public  double MIN_CELL_CYCLE_TIME =23.0/24;//in days

    //MUTATION CONSTANTS
     public  double MUTATION_SCALE=1;
     public  double NORMAL_ACID_RESIST_PHENO =6.65;
     public  double MAX_ACID_RESIST_PHENO =6.1;
     public  double MIN_ACID_RESIST_PHENO=6.85;
     public  double ACID_RESIST_MUTATION_RATE=0.001*MUTATION_SCALE;
     public  double GLYCOLYSIS_MUTATION_RATE=0.05*MUTATION_SCALE;
     public  double MAX_GLYCOLYTIC_PHENO=50;
     public  double NORMAL_GLYCOLYTIC_PHENO=1;
     public  double MIN_GLYCOLYTIC_PHENO=0.4;

    //CELL DEATH CONSTANTS
     public  double NORMAL_DEATH_PROB=ProbScale(0.005,CELL_TIME_STEP);
    // public  double APOPTOTIC_REMOVE_PROB=ProbScale(0.5,CELL_TIME_STEP);
    public  double APOPTOTIC_REMOVE_PROB=0;
     public  double NECROTIC_REMOVE_PROB=(double)Math.pow(0.0005,CELL_TIME_STEP);

    //VESSEL CONSTANTS
     public  double MIN_VESSEL_SPACING =80/GRID_SIZE;
     public  double MEAN_VESSEL_SPACING =150/GRID_SIZE;
     public  double ANGIOGEN_RATE=0.3;
     public  int VESSEL_STABILITY=20;
     public double HYPOX_ZONE_TO_VESSEL_SCALAR=100;
    public double HYPOX_ZONE_TO_VESSEL_POWER=0.93;
    public double VESSEL_ADD_PROB=0.5*CELL_TIME_STEP;

     public  double ANGEOGENESIS_MIN_O_CONC=0.0008;
     public  double ANGEOGENESIS_MAX_O_CONC=0.002;

     private double maxDelta;

     public AgentList<A>vessels=new AgentList<>();
     private double[]oxygenPrevState;
     private double[]glucosePrevState;
     private double[]protonsPrevState;

    //typeGrid constructor sets up diffusible grids
    public MetabolismGrid(int x, int y, Class<A> classObj){
        this(x,y,classObj,new Rand());
    }
    public MetabolismGrid(int x, int y, Class<A> classObj, Rand rn){
        super(x,y,classObj);
        this.rn=rn;
        oxygen = new PDEGrid2D(xDim, yDim);
        glucose = new PDEGrid2D(xDim, yDim);
        protons = new PDEGrid2D(xDim, yDim);
        oxygenPrevState=new double[oxygen.length];
        glucosePrevState=new double[glucose.length];
        protonsPrevState =new double[protons.length];
        angioIs=new int[xDim*yDim];
        vesselPosToCheck= CircleHood(true, MIN_VESSEL_SPACING);
    }
    public double GetAvgConc(PDEGrid2D grid,int numLayersX,int numLayersY){
        double sum=0;
        int ct=0;
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < numLayersY; y++) {
                ct+=2;
                sum+=grid.Get(x,y);
                sum+=grid.Get(x,(yDim-1)-y);
            }
        }
        for (int x = 0; x < numLayersX; x++) {
            for (int y = numLayersY; y < (yDim-1)-numLayersY; y++) {
                ct+=2;
                sum+=grid.Get(x,y);
                sum+=grid.Get((xDim-1)-x,y);
            }
        }
        return sum/ct;
    }
    public void SetBCs(){
        int numLayersInX=(int)(xDim*LAYERS_TO_CHECK_PROP);
        int numLayersInY=(int)(yDim*LAYERS_TO_CHECK_PROP);
        OXYGEN_BOUNDARY_CONC=GetAvgConc(oxygen,numLayersInX,numLayersInY);
        GLUCOSE_BOUNDARY_CONC=GetAvgConc(glucose,numLayersInX,numLayersInY);
        ACID_BOUNDARY_CONC=GetAvgConc(protons,numLayersInX,numLayersInY);

    }

    public void InitDiffusibles(){
        glucose.SetAll(GLUCOSE_AVG_CONC);
        oxygen.SetAll(OXYGEN_AVG_CONC);
        protons.SetAll(ACID_AVG_PROTONS);
        UpdatePDEs();
    }

    public A GenNormalCell(int i, boolean randomCycleTime){
        A c=NewAgentSQ(i);
        c.Init(NORMAL_GLYCOLYTIC_PHENO,NORMAL_ACID_RESIST_PHENO,NORMAL,randomCycleTime);
        return c;
    }

    public A GenTumorCell(int i, double glycPheno, double acidResPheno, boolean randomCycleTime){
        A c=NewAgentSQ(i);
        c.Init(glycPheno,acidResPheno,CANCER,randomCycleTime);
        return c;
    }

    public A GenVessel(int i) {
        // make room and add vessel
        A c = GetAgent(i);
        if(c!=null) {
            c.Dispose();
        }
        A v=NewAgentSQ(i);
        v.Init(-1, -1, VESSEL,false);
        vessels.AddAgent(v);
        return v;
    }

    //initializes the typeGrid with vessels and normal cells
    public void InitNormalCells(double propCells) {
        //System.out.println("expectedVessels: "+expectedVessels+"acutalVessels: "+actualVessels);
        int[] ret= GenIndicesArray(xDim * yDim);
        rn.Shuffle(ret, xDim * yDim, (int)((xDim * yDim) * propCells));
        int randomIs[] = ret;
        for (int i = 0; i <(int)(xDim * yDim) * propCells; i++) {
            int RandomGridI = randomIs[i];
            if (GetAgent(RandomGridI) == null) {
                GenNormalCell(RandomGridI,true);
            }
        }
    }

    //creates a circular tumor by turning normal cells into tumor cells
    public void InitTumor(double radius, int centerX, int centerY, double glycPheno, double arPheno){
        int[] cancerSQs = CircleHood(true,radius);
        int len=MapHood(cancerSQs,centerX,centerY);
        for (int i = 0; i < len; i++) {
            A c = GetAgent(cancerSQs[i]);
            if (c != null&&!(c.type == A.VESSEL)) {
                c.type = CANCER;
                c.acidResistancePheno=arPheno;
                c.glycolysisPheno=glycPheno;
            }
        }
    }

    //MICROENVIRONMENT VARIABLES



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
        int hypoxicArea=GetAngioIs();
        int numGen=(int)(rn.Double()*Math.pow(hypoxicArea/HYPOX_ZONE_TO_VESSEL_SCALAR,HYPOX_ZONE_TO_VESSEL_POWER));
        int numGenFinal=numGen;

        //System.out.println(CountBelowAngio());
        for (int i = 0; i < numGen; i++) {
            if(rn.Double()<VESSEL_ADD_PROB){
                numGenFinal--;
            }
        }
        if(numGenFinal>0){
            NewVessels(numGenFinal,hypoxicArea);
        }
        //if(rn.nextDouble()<(hypoxicArea*1.0)/(compX*compY)){
        //}
    }
    public void NewVessels(int count,int hypoxicArea){
        if(hypoxicArea>count) {
            rn.Shuffle(angioIs, hypoxicArea, count);
        }
        for(int i=0;i<count;i++){
            GenVessel(angioIs[i]);
        }
    }

    public boolean CheckVesselsNearby(int x,int y) {
        int lenCheck=MapHood(vesselPosToCheck,x,y);
        for (int j = 0; j < lenCheck; j++) {
            A c=GetAgent(vesselPosToCheck[j]);
            if(c!=null&& c.type == VESSEL) {
                return true;
            }
        }
        return false;
    }

    public int InitVessels(int expectedVessels) {
        int nVessels=0;
        int[] inds= GenIndicesArray((xDim-2)*(yDim-2));
        rn.Shuffle(inds);
        int[] Is= inds;
        for (int i = 0; i < inds.length; i++) {
            int x=Is[i]/(yDim-2)+1;
            int y=Is[i]%(yDim-2)+1;
            if(!CheckVesselsNearby(x,y)){
                GenVessel(I(x,y));
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

    public double MaxDelta(PDEGrid2D grid,double[]prev){
        double max=0;
        for (int i = 0; i < grid.length; i++) {
            max=Math.max(max,Math.abs((grid.Get(i)-prev[i])/(prev[i]+EPS)));
        }
        return max;
    }

    public void UpdatePDEs(){
        glucose.Update();
        oxygen.Update();
        protons.Update();
    }

    public void ReactionDiffusion(boolean setBCs,boolean checkDeltas){
        if(checkDeltas) {
            System.arraycopy(oxygen.GetField(), 0, oxygenPrevState, 0, oxygen.length);
            System.arraycopy(glucose.GetField(), 0, glucosePrevState, 0, glucose.length);
            System.arraycopy(protons.GetField(), 0, protonsPrevState, 0, protons.length);
        }
        else{
            maxDelta=Double.MAX_VALUE;
        }
        //apply cell consumption
        for (A c : this) {
            c.CellVesselReaction();
        }
        UpdatePDEs();
        //apply diffusion
        glucose.DiffusionADI(GLUCOSE_DIFF_RATE, GLUCOSE_BOUNDARY_CONC,(double[]field)->{
            for (A v : this.vessels) {
                field[v.Isq()]=GLUCOSE_VESSEL_CONC;
            }
        });
        oxygen.DiffusionADI(OXYGEN_DIFF_RATE, OXYGEN_BOUNDARY_CONC,(double[]field)->{
            for (A v : this.vessels) {
                field[v.Isq()] = OXYGEN_VESSEL_CONC;
            }
        });
        protons.DiffusionADI(ACID_DIFF_RATE, ACID_BOUNDARY_CONC,(double[]field)->{
            for (MetabolismCell v : this.vessels) {
                field[v.Isq()] = ACID_VESSEL_CONC;
            }
        });
        UpdatePDEs();
        if(checkDeltas) {
            maxDelta = 0;
            double oxygenMaxDelta = MaxDelta(oxygen, oxygenPrevState);
            double glucoseMaxDelta = MaxDelta(glucose, glucosePrevState);
            double protonsMaxDelta = MaxDelta(protons, protonsPrevState);
            maxDelta = Math.max(maxDelta, oxygenMaxDelta);
            maxDelta = Math.max(maxDelta, glucoseMaxDelta);
            maxDelta = Math.max(maxDelta, protonsMaxDelta);
        }
        if (!CheckGrid(glucose) || !CheckGrid(oxygen) || !CheckGrid(protons)) {
            System.out.println("A GRID HAS NEGATIVES");
        }
        if (setBCs) {
            SetBCs();
        }
//        if(oxygenMaxDelta>glucoseMaxDelta&&oxygenMaxDelta>protonsMaxDelta){
//            return 0;
//        }
//        else if(glucoseMaxDelta>protonsMaxDelta){
//            return 1;
//        }
//        else{
//            return 2;
//        }
    }


    //runs a step of consumption and diffusion
    public int DiffLoop(boolean setBCs) {
        //ArrayList<Integer>maxDeltas=new ArrayList<>();
        //diffusion loop continues until steady state is reached
       int diffCount = 0;
        do {
            if(diffCount<5) {
                ReactionDiffusion(setBCs,false);
            }
            else{
                ReactionDiffusion(setBCs,true);
            }
            diffCount++;
        }
        //while (o2MaxDelta > OXYGEN_MAX_DELTA || glucoseMaxDelta > GLUCOSE_MAX_DELTA || protonsMaxDelta > ACID_MAX_DELTA);
        while (maxDelta>DELTA_TOL);
        //FileIO maxDeltasOut=new FileIO("MaxDeltasType.csv","w");
        //maxDeltasOut.WriteDelimit(maxDeltas.toArray(),",");
        //maxDeltasOut.Close();
        //    System.out.println("Loops:"+DiffCount+" O:"+o2MaxDelta/OXYGEN_MAX_DELTA+" G:"+glucoseMaxDelta/GLUCOSE_MAX_DELTA+" A:"+protonsMaxDelta/ACID_MAX_DELTA);
        //System.out.println("Ticks:" + GetTick() + " GetAvg O:" + oxygen.GetAvg() + "GetAvg G:" + glucose.GetAvg() + " GetAvg A:" + protons.GetAvg());
        return diffCount;
    }

    //can draw all 3 diffusibles simultaneously, one for every color channel
    public void DrawMicroEnv(UIGrid vis, boolean drawGlucose, boolean drawProtons, boolean drawOxygen) {
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
                    g = (float) ((Util.ProtonsToPh(protons.Get(x, y)) - 6) / 1.4f);
                }
                vis.SetPix(x, y, RGB(r, g, b));
            }
        }
    }
    //can only draw one diffusible at a time
    public void DrawMicroEnvHeat(UIGrid vis, boolean drawGlucose, boolean drawProtons, boolean drawOxygen) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                float r = 0;
                float g = 0;
                float b = 0;
                if (drawGlucose) {
                    vis.SetPix(x, y, Util.HeatMapRGB(glucose.Get(x,y)/ GLUCOSE_VESSEL_CONC));
                }
                else if (drawOxygen) {
                    vis.SetPix(x, y, Util.HeatMapRGB(oxygen.Get(x, y) / OXYGEN_VESSEL_CONC));
                }
                else if (drawProtons) {
                    vis.SetPix(x, y, Util.HeatMapRGB((Util.ProtonsToPh(protons.Get(x, y)) - 6) / 1.4f));
                }
            }
        }
    }
    public void DrawPhenos(UIGrid vis){
        for (int x = 0; x < vis.xDim; x++) {
            for (int y = 0; y < vis.yDim; y++) {
                vis.SetPix(x, y, CbCrPlaneColor(y * 1.0 / vis.yDim, x * 1.0 / vis.xDim));
            }
        }
        for (A a : this) {
            if(a.type==CANCER){
                vis.SetPix((int)(GetGlycDrawScalar(a)*vis.xDim),(int)(GetARDrawScalar(a)*vis.yDim),BLACK);
            }
        }
    }
    public double GetARDrawScalar(A c){
        return 1-Util.Scale0to1(c.acidResistancePheno, MAX_ACID_RESIST_PHENO, MIN_ACID_RESIST_PHENO);
    }
    public double GetGlycDrawScalar(A c){
        return Util.Scale0to1(Math.log10(c.glycolysisPheno), Math.log10(MIN_GLYCOLYTIC_PHENO), Math.log10(MAX_GLYCOLYTIC_PHENO));
    }
    public void DrawCells(UIGrid vis) {
        for (int x = 0; x < xDim; x++) {
            for (int y = 0; y < yDim; y++) {
                A c = GetAgent(x, y);
                if (c != null) {
                    if (c.IsAlive()) {
                        if (c.type == VESSEL) {
                            vis.SetPix(x, y, WHITE);
                        }
                        else if (c.type==CANCER) {
                            //double acidPhenoScaled = Util.Scale0to1(c.acidResistancePheno, MAX_ACID_RESIST_PHENO, MIN_ACID_RESIST_PHENO);
                            //double glycoPhenoScaled = Util.Scale0to1((double) Math.log(c.glycolysisPheno), (double) Math.log(MIN_GLYCOLYTIC_PHENO + 0.001), (double) Math.log(MAX_GLYCOLYTIC_PHENO));
                            vis.SetPix(x, y, CbCrPlaneColor(GetARDrawScalar(c),GetGlycDrawScalar(c)));
                            //vis.SetBound(xDim, yDim, 1, 0, 1);
                        }
                        else {
                            vis.SetPix(x, y, GREY);
                        }
                    } else {
                        vis.SetPix(x, y, NECRO_COLOR);
                    }
                }
                else{
                    vis.SetPix(x, y, RGB((double) 0, (double) 0, (double) 0));
                }
            }
        }
    }
    public void DefaultSetup(double radius,double glycPheno,double arPheno){
        protons.SetAll(ACID_VESSEL_CONC);
        glucose.SetAll(GLUCOSE_VESSEL_CONC);
        oxygen.SetAll(OXYGEN_VESSEL_CONC);
        UpdatePDEs();

        InitNormalCells(0.8);
        InitVessels((int)(length/(1.0*MEAN_VESSEL_SPACING*MEAN_VESSEL_SPACING)));
        InitTumor(radius,xDim/2,yDim/2,glycPheno,arPheno);
    }
    public void DefaultGridStep(){
        if(GetTick()<50){
            DiffLoop(true);
        }
        else {
            DiffLoop(false);
        }
        for (A c : this) {
            c.DefaultCellStep();
        }
        CleanAgents();
        ShuffleAgents(rn);
        Angiogenesis();
        IncTick();
    }
}
