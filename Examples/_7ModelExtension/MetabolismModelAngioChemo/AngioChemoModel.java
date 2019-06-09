package Examples._7ModelExtension.MetabolismModelAngioChemo;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.UIGrid;
import Framework.Gui.UILabel;
import Framework.Interfaces.SerializableModel;
import Framework.Rand;
import Framework.Tools.FileIO;
import Framework.Util;
import Examples._7ModelExtension.MetabolismModel.MetabolismCell;
import Examples._7ModelExtension.MetabolismModel.MetabolismGrid;
import Examples._7ModelExtension.MetabolismModel.MetabolismModelVis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static Examples._7ModelExtension.MetabolismModel.MetabolismCell.CANCER;
import static Framework.Util.BLACK;
import static Framework.Util.RED;
import static Examples._7ModelExtension.MetabolismModelAngioChemo.AngioChemoModel.*;

class AngioChemoVis extends MetabolismModelVis{
    UIGrid visChemo;
    UIGrid visAA;

    public AngioChemoVis(AngioChemoModel model, boolean killOnClose, int scaleFactor) {
        super(model, true,false, scaleFactor);
        if(IsActive()) {
            visChemo = new UIGrid(G.xDim, G.yDim,scaleFactor);
            AddCol(2,new UILabel("Chemo"));
            AddCol(2,visChemo);
            visAA = new UIGrid(G.xDim, G.yDim,scaleFactor);
            AddCol(2,new UILabel("AAngio"));
            AddCol(2,visAA);
        }
    }
    public void Draw(){
        super.Draw();
        if(IsActive()) {
            DrawDif(visChemo, ((AngioChemoModel) G).Chemo, Util::HeatMapRGB);

            int AAcolor=((AngioChemoModel) G).antiAngioOn?RED:BLACK;
            for (int i = 0; i < visAA.length; i++) {
                visAA.SetPix(i,AAcolor);
            }
        }
    }
    public void SetModel(AngioChemoModel newModel){
        this.G=newModel;
    }
}

class AngioChemoCell extends MetabolismCell<AngioChemoCell,AngioChemoModel> {
    @Override
    public AngioChemoCell Divide(int iDiv) {
        double chemoConc=G.Chemo.Get(iDiv);
        if(G.rn.Double()<G.CHEMO_MAX_KILL_PROB*2*(1/(1+Math.exp(-(chemoConc)/G.CHEMO_HALF_MAX))-0.5)){
            Die(G.APOPTOTIC_REMOVE_PROB);
            return null;
        };
        cellCycleTime = 1;
        if (type == CANCER) {
            AngioChemoCell child = G.GenTumorCell(iDiv, glycolysisPheno, acidResistancePheno, false);
            Mutate();
            child.Mutate();
            return child;
        }
        return G.GenNormalCell(iDiv, false);
    }

}

public class AngioChemoModel extends MetabolismGrid<AngioChemoCell> implements Serializable,SerializableModel {

    double CHEMO_DIFF_RATE=1.0e2*DIFF_TIME_STEP*1.0/(GRID_SIZE*GRID_SIZE);
    double CHEMO_MAX_KILL_PROB=0.9;
    double CHEMO_HALF_MAX=0.25;
    double TREATMENT_START_AREA;
    int CHEMO_DURATION;
    int ANTIANGIO_DURATION;
    int CHEMO_START;
    int ANTIANGIO_START;
    int STOP;

    boolean recording;
    boolean[]tumorTerritory=new boolean[xDim*yDim];
    int[]territoryHood=Util.VonNeumannHood(false);
    public PDEGrid2D Chemo;
    public boolean antiAngioOn;
    public boolean chemoOn;
    int startTick;
    public AngioChemoModel(int x, int y, Rand rn) {
        super(x, y, AngioChemoCell.class, rn);
        this.Chemo=new PDEGrid2D(x,y,false,false);
    }
    public AngioChemoCell GenNormalCell(int i, boolean randomCycleTime){
        tumorTerritory[i]=false;
        ApplyHood(territoryHood,i,(x,y)->{
            AngioChemoCell c=GetAgent(x,y);
            if(c!=null&&c.type!=CANCER){
                tumorTerritory[i]=false;
            }
        });
        return super.GenNormalCell(i,randomCycleTime);
    }

    public AngioChemoCell GenTumorCell(int i, double glycPheno, double acidResPheno, boolean randomCycleTime){
        tumorTerritory[i]=true;
        return super.GenTumorCell(i,glycPheno,acidResPheno,randomCycleTime);
    }
    public double GetTumorRad(){//in cells
        int sum=0;
        for (int i = 0; i < tumorTerritory.length; i++) {
            if(tumorTerritory[i]){
                sum++;
            }
        }
        return Math.sqrt(sum/Math.PI);
    }

    public double GetAvgChemo(){
        int numLayersInX=(int)(xDim*LAYERS_TO_CHECK_PROP);
        int numLayersInY=(int)(yDim*LAYERS_TO_CHECK_PROP);
        return GetAvgConc(Chemo,numLayersInX,numLayersInY);
    }
    public int ChemoDiffLoop(){
        double dif= Double.MAX_VALUE;
        int loops=0;
        double vesselConc=chemoOn?1:0;
        do {
            double avg=GetAvgChemo();
            for (AngioChemoCell v : vessels) {
                Chemo.Set(v.Isq(), vesselConc);
            }
            Chemo.DiffusionADI(CHEMO_DIFF_RATE,avg);
            if(loops>=5) {
                dif = Chemo.MaxDeltaScaled(EPS);//do at least 5 rounds of diffusion
            }
            Chemo.Update();
            loops++;
        }while(dif>DELTA_TOL&&loops<MAX_DIFF_STEPS);
        return loops;
    }
    public void DrugStep(boolean chemoOn,boolean antiAngioOn){
        this.chemoOn=chemoOn;
        this.antiAngioOn=antiAngioOn;
        DefaultGridStep();
    }

    public void DefaultGridStep(){
        if(GetTick()<50){
            DiffLoop(true);
        }
        else {
            DiffLoop(false);
        }
        ChemoDiffLoop();
        for (AngioChemoCell c : this) {
            c.DefaultCellStep();
        }
        CleanAgents();
        ShuffleAgents(rn);
        Angiogenesis();
        IncTick();
    }

        @Override
        public void Angiogenesis(){
        if(!antiAngioOn){
            super.Angiogenesis();
        }
    }

    @Override
    public void SetupConstructors() {
        _PassAgentConstructor(AngioChemoCell.class);
    }

    public int GetTumorArea(){
        int territoryCt=0;
        for (int i = 0; i < tumorTerritory.length; i++) {
            if(tumorTerritory[i]){territoryCt++;}
        }
        return territoryCt;
    }

    public int GetTumorPop() {
        int tumorCt = 0;
        for (AngioChemoCell c : this) {
            if (c.type == CANCER) {
                tumorCt++;
            }
        }
        return tumorCt;
    }

    //defaults:2.8,0.5,1.0,150,
    static public int[][] RunModel(int sideLen,double emptySquaresForDiv,double vesselRegrowthRate,double phenoMutRate,double initDiam, double startTreatmentDiam,double chemoDuration,double antiAngioDuration,double chemoStartDay,double antiAngioStartDay,int stopTreatmentCycles,int stopDay,int recordRate,boolean vis){
        AngioChemoModel model=new AngioChemoModel(sideLen,sideLen,new Rand());
        model.EMPTY_SQUARES_FOR_DIV=emptySquaresForDiv;
        model.VESSEL_ADD_PROB=Util.ProbScale(vesselRegrowthRate,model.CELL_TIME_STEP);
        model.MUTATION_SCALE=phenoMutRate;
        model.ACID_RESIST_MUTATION_RATE=0.001*model.MUTATION_SCALE;
        model.GLYCOLYSIS_MUTATION_RATE=0.05*model.MUTATION_SCALE;
        model.TREATMENT_START_AREA =Math.PI*Math.pow(startTreatmentDiam/2,2);
        model.CHEMO_DURATION=(int)(chemoDuration/model.CELL_TIME_STEP);
        model.ANTIANGIO_DURATION=(int)(antiAngioDuration/model.CELL_TIME_STEP);
        model.CHEMO_START=(int)(chemoStartDay/model.CELL_TIME_STEP);
        model.ANTIANGIO_START=(int)(antiAngioStartDay/model.CELL_TIME_STEP);
        model.STOP =(int)(stopDay/model.CELL_TIME_STEP);
        model.DefaultSetup(initDiam/2,model.NORMAL_GLYCOLYTIC_PHENO,model.NORMAL_ACID_RESIST_PHENO);
        recordRate=(int)(recordRate/model.CELL_TIME_STEP);
        int CYCLE_DURATION= (int) (21/model.CELL_TIME_STEP);
        ArrayList<Integer>tumorArea=new ArrayList<>();
        ArrayList<Integer>tumorCells=new ArrayList<>();
        AngioChemoVis win=null;
        int startTick=-1;
        if(vis!=false){
            win=new AngioChemoVis(model,false,1);
        }
        while(true){
            boolean chemo=false;
            boolean antiangio=false;
            if(startTick>-1){//setup drugs
                int chemoTick=model.GetTick()-(startTick+model.CHEMO_START);
                if(chemoTick>=0&&chemoTick/CYCLE_DURATION<stopTreatmentCycles&&chemoTick%CYCLE_DURATION<model.CHEMO_DURATION){
                    chemo=true;
                }
                int aaTick=model.GetTick()-(startTick+model.ANTIANGIO_START);
                if(aaTick>=0&&aaTick/CYCLE_DURATION<stopTreatmentCycles&&aaTick%CYCLE_DURATION<model.ANTIANGIO_DURATION){
                    antiangio=true;
                }
            }
            model.DrugStep(chemo,antiangio);
            if(startTick==-1){//check if tumor is large enough to start treatment
                if(model.GetTumorArea()>model.TREATMENT_START_AREA){
                    startTick=model.GetTick();
                }
            }
            else{
                if(((model.GetTick()-startTick)*1.0/recordRate)%1==0){
                    tumorArea.add(model.GetTumorArea());
                    tumorCells.add(model.GetTumorPop());
                }
                if(model.GetTick()-startTick>model.STOP){
                    break;
                }
            }
            if(win!=null){
                win.Draw();
            }
        }
        if(win!=null){
            win.Close();
        }
        return new int[][]{Util.ArrayListToArrayInt(tumorArea),Util.ArrayListToArrayInt(tumorCells)};
    }
    final static int TUMOR_AREA=0,TUMOR_CELL_CTS=1;
    static public int[][]RunModel(double[]params,boolean vis){
        return RunModel((int)params[ModelSideLen],params[EmptySquqresForNormalDivision],params[VesselRegrowthProb],params[PhenotypeMutationRate],params[InitTumorDiameter],params[StartTreatmentDiameter],params[ChemoApplicationDuration],params[AntiAngioApplicationDuration],params[ChemoStart],params[AntiAngioStart],(int)params[NumTreatmentCycles],(int)params[ExptDuration],(int)params[RecordRate],vis);
    }
    final static int ModelSideLen=0,InitTumorDiameter=1,StartTreatmentDiameter=2,ChemoApplicationDuration=3,ChemoStart=4,AntiAngioApplicationDuration=5,AntiAngioStart=6,RecordRate=7,ExptDuration=8,NumTreatmentCycles=9,EmptySquqresForNormalDivision=10,VesselRegrowthProb=11,PhenotypeMutationRate=12,numParams=13;
    final static String params="ModelSideLen(cells),InitTumorDiameter(cells),StartTreatmentDiameter(cells),ChemoApplicationDuration(days),ChemoStart(DaysFromStart),AntiAngioApplicationDuration(days),AntiAngioStart(DaysFromStart),RecordRate(days),ExperimentDuration(DaysFromStart),NumberOfTreatmentCycles,EmptySquqresForNormalDivision,VesselRegrowthProb,PhenotypeMutationRate";
    static int NUM_PARAMS=0;

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("required args: 'inputFilePath' 'outputFilePath' will run multithreaded by default. do not include extension for outputFilePath");
            System.out.println("optional arg: '-v' for visualization, will run single-threaded");
            System.out.println("input file should be a csv with the following info per line:\n" + params);
            System.exit(0);
        }
        boolean vis = false;
        ArrayList<double[]>params = null;
        String outputPath=null;
        int inputState = 0;//0: reading input file 1:reading output file 2:done reading
        for (String arg : args) {
            if (arg.equals("-v")) {
                vis = true;
            } else if (inputState == 0) {
                FileIO inputReader = new FileIO(arg,"r");
                params=inputReader.ReadDoubles(",");
                inputState=1;
            } else if(inputState==1){
                outputPath=arg;
                inputState=2;
            }
        }
        if(inputState!=2){
            throw new IllegalArgumentException("Please input an input file path and an output file path from the command line!");
        }
        int[][]outputTumorAreas=new int[params.size()][];
        int[][]outputTumorCellCounts=new int[params.size()][];
        if(vis){
            for (int i = 0; i < params.size(); i++) {
                double[]set= params.get(i);
                int[][]out=RunModel(set,true);
                outputTumorAreas[i]=out[TUMOR_AREA];
                outputTumorCellCounts[i]=out[TUMOR_CELL_CTS];
            }
        }
        else{
            ArrayList<double[]> finalParams = params;
            Util.MultiThread(params.size(),(i)->{
                int[][]out=RunModel(finalParams.get(i),false);
                outputTumorAreas[i]=out[TUMOR_AREA];
                outputTumorCellCounts[i]=out[TUMOR_CELL_CTS];
            });
        }
        FileIO outAreas=new FileIO(outputPath+"_TumorAreas.csv","w");
        FileIO outCounts=new FileIO(outputPath+"_TumorCellCounts.csv","w");
        for (int[] counts : outputTumorCellCounts) {
            outCounts.WriteDelimit(counts,",");
            outCounts.Write("\n");
            outCounts.Close();
        }
        for (int[] areas : outputTumorAreas) {
            outAreas.WriteDelimit(areas,",");
            outAreas.Write("\n");
            outAreas.Close();
        }
    }
}
