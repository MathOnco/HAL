package HAL.Tools.InteractiveModel;

import HAL.Gui.*;
import HAL.Util;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import static HAL.Util.*;

/**
 * Created by Rafael on 9/26/2017.
 */
/*
TODO:
    - for now, just find a nice arrangement for the paper...
    - fix click loading earlier identical state after clicking late in timeline bug
    - fix timeline jump incorrect population draw bug (there is much jank, could use a refactor)
    -fix reverting to main color cycle after clicking on screen (what is this? investigate)
    -fix model error (???)
    - make double click set line
    - add save feature (eventually)
*/

class StateDebugger{
    InteractiveModel mySim;
    byte[][]saves;
    StateDebugger(InteractiveModel mySim){
        this.mySim=mySim;
        saves=new byte[mySim.nSteps][];
    }
    boolean IsIdentical(byte[]state){
        if(saves[mySim.step]==null){
            return false;
        }
        byte[]prev=saves[mySim.step];
        if(prev.length!=state.length){
            return false;
        }
        for (int i = 0; i < prev.length; i++) {
            if(state[i]!=prev[i]){
                return false;
            }
        }
        return true;
    }
    boolean Save(){
        byte[]state=mySim.SaveState();
        boolean same=IsIdentical(state);
        saves[mySim.step]=mySim.SaveState();
        return same;
    }
}

class TreatmentBar {
    final int index;
    final InteractiveModel myLab;
    final SectionalGGV intensitySelect;
    final SectionalGGV treatline;
//    final UIButton setAllBtn;
    final UILabel nameLbl;
    final double hue;
    final int antiHSB;
    int chosenIntensity;
    int chosenStart;
    int maxIntensity;

    TreatmentBar(InteractiveModel myLab, int index, int timescaleX, int timescaleY, int intensityScaleX, int maxIntensity) {
        this.index = index;
        this.myLab = myLab;
        this.maxIntensity=maxIntensity;
        nameLbl = new UILabel(myLab.treatNames[index]+" ["+(index+1)+"]");
        intensitySelect = new SectionalGGV(maxIntensity + 1, 1, intensityScaleX, timescaleY, 1, 1);
        treatline = new SectionalGGV(myLab.nSteps, 1, timescaleX, timescaleY, 1, 1);
//        setAllBtn=new UIButton("SetBlock Line",1,1,false,(e)->{
//            myLab.SavePlansToUndo();
//            SetLine();
//        });
        chosenStart=-1;
        chosenIntensity=1;
        hue = myLab.treatHues[index];
        myLab.AddCol(0, intensitySelect);
        myLab.AddCol(1, nameLbl);
        myLab.AddCol(2, treatline);
        antiHSB=HSBColor((float) (hue+0.5), 1, 1);
        intensitySelect.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                chosenIntensity = intensitySelect.ClickX(e);
                long time=System.currentTimeMillis();
                if(time-intensitySelect.clickTime<300){
                    //double click event
                    SetLine();

                }
                intensitySelect.clickTime=time;
                DrawIntensityLine();
            }
        });
        treatline.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                //UNSET OTHER CHOSENSTARTS
                //set treatment start and end
                if(chosenStart==-1){
                    chosenStart=treatline.ClickX(e);
                    myLab.UnsetChosenStarts(index);
                }
                else{
                    myLab.SavePlansToUndo();
                    int chosenEnd=treatline.ClickX(e);
                    int start=Math.min(chosenStart,chosenEnd);
                    int end=Math.max(chosenStart,chosenEnd);
                    for (int i = start; i <= end ; i++) {
                        myLab.treatPlan[i][index]=chosenIntensity;
                    }
                    myLab.UnsetChosenStarts(-1);
                }
                myLab.DrawTreatline(index);
            }
        });
    }

    public void SetLine(){
        myLab.UnsetChosenStarts(-1);
        for (int i = 0; i < myLab.nSteps; i++) {
            myLab.treatPlan[i][index]=chosenIntensity;
        }
        myLab.DrawTreatline(index);
    }
    public void DrawIntensityLine() {
        for (int i = 0; i < maxIntensity + 1; i++) {
            if (i != 0) {
                intensitySelect.SetBlock(i, 0, HSBColor((float) hue, 1, ((float) (i + 2.0)) / (maxIntensity + 2)));
            }
            else{
                intensitySelect.SetBlock(i,0,BLACK);
            }
            if (i == chosenIntensity) {
                for (int j = 0; j < intensitySelect.entryX; j++) {
                    for (int k = 0; k < intensitySelect.entryY; k++) {
                        if ((j < 2 || intensitySelect.entryX - j<3) || (k < 2 || intensitySelect.entryY - k<3)) {
                            intensitySelect.SetPix(i * intensitySelect.entryX + j, k, antiHSB);
                        }
                    }
                }
            }
        }
    }

    public void DrawTreatmentLine(int i) {
        if (i == chosenStart) {
            treatline.SetBlock(i, 0, antiHSB);
        } else {
            if (myLab.treatPlan[i][index] == 0) {
                treatline.SetBlock(i, 0, BLACK);
            } else {
                treatline.SetBlock(i, 0, HSBColor((float) hue, 1, ((float) (myLab.treatPlan[i][index] + 2)) / (maxIntensity + 2)));
            }
        }
        treatline.SetPix(i,treatline.yDim-1,YELLOW);
    }
    public void UnsetChosenStart(){
        chosenStart=-1;
    }
}

class SectionalUIPlot extends UIPlot{
    final int entryX;
    final int entryY;
    UIGrid timeBarDisp;
    int validStep;
    public SectionalUIPlot(int xPix, int yPix, int scaleFactor,int entryX,int entryY, double xMin, double yMin, double xMax, double yMax,int compX,int compY) {
        super(xPix, yPix, scaleFactor, xMin, yMin, xMax, yMax,compX,compY);
        timeBarDisp =new UIGrid(xPix,yPix,scaleFactor);
        grid.AddAlphaGrid(timeBarDisp);
        this.entryX=entryX;
        this.entryY=entryY;
    }
    public int ClickX(MouseEvent e){
        return grid.ClickXsq(e)/entryX;
    }
    public int ClickY(MouseEvent e){
        return grid.ClickYsq(e)/entryY;
    }
}

class SectionalGGV extends UIGrid {
    long clickTime;
    final int entryX;
    final int entryY;
    public SectionalGGV(int gridW, int gridH, int entryX, int entryY, int compX, int compY) {
        super(gridW*entryX, gridH*entryY, 1, compX, compY);
        this.entryX=entryX;
        this.entryY=entryY;
    }
        public void SetBlock(int x, int y, int color){
        int minX=x*entryX,maxX=(x+1)*entryX,minY=y*entryY,maxY=(y+1)*entryY;
            for (int i = minX; i < maxX; i++) {
                for (int j = minY; j < maxY; j++) {
                    SetPix(i, j, color);
                }
            }
    }
    public int ClickX(MouseEvent e){
            return ClickXsq(e)/entryX;
    }
    public int ClickY(MouseEvent e){
        return ClickYsq(e)/entryY;
    }

    }
public class InteractiveModel extends UIWindow {
    //GUI CONSTANTS
    final private static int PLAY=0,SET_START=1,SET_END=2;
    //final UILabel treatScoreLbl=new UILabel("Tolerable Toxicity:________________%");
    //final UILabel burdenScoreLbl=new UILabel("Tolerable Burden:________________%");
    //final SectionalGGV treatScoreBar;
    //final SectionalGGV burdenScoreBar;
    //final UILabel totalScoreLbl=new UILabel("__________________score:____________________");
    //final UILabel winLbl =new UILabel("keep playing...______________________");
    final int nTreatments;
    final int nSteps;
    final double[]treatHues;
    final String[] treatNames;
    //final String[] switchNames;
    final int stateSaveFreq;
    final int stepMSmax;
    //final int nSwitches;
    final double[]treatVals;
    //final boolean multiSwitch;
    final boolean redrawOnSelectionSwitch;

    //GUI STATE
    boolean paused;
    int stepMS;
    int chosenStart;
    int hopStep=-1;
    int[][] treatHistory;
    int[][] treatPlan;
    int[][] undoTreatPlan;
    int[]maxIntensities;
    double[][]plotVals;
    PlotLine[]plotLines;
    //double[] toxs;
    //double[] burdens;
    byte[][] saves;
    boolean[] switchVals;
    int step;
    int pauseStep;
    int iLastMainSelected=-1;
    int iLastSelected=-1;
    static final int BLANK=RGBA(0,0,0,0);

    //GUI COMPONENTS
    TreatableTumor myModel;
    final UILabel tickLbl;
    final public UIGrid vis;
    final SectionalUIPlot timeline;
    final SectionalGGV speedControl;
    //final SectionalGGV treatline;
    final UIButton pauseButton;
    final UIButton undoButton;
    final UIButton clearButton;
    final UIButton resetButton;
    //final UIComboBoxInput treatmentSelect;
    //final UIGrid intensitySelect;
    final TreatmentBar[]bars;
    //final UIBoolInput[]guiSwitches;
    //final KeyRecorder keyRecorder=new KeyRecorder();

    //EXTRA
    float[] colorScratch=new float[3];
    public InteractiveModel(TreatableTumor myModel, int nSteps, int stateSaveFreq, int visX, int visY, int visScale, int timeScaleY, int intensityScaleX, int stepMSmax, boolean redrawOnTreatmentSwitch, boolean quickStart) {
        super("Clinician Simulator");
        this.stepMSmax=stepMSmax;
        this.stepMS=0;
        this.myModel =myModel;
        this.stateSaveFreq=stateSaveFreq;
        this.redrawOnSelectionSwitch =redrawOnTreatmentSwitch;
        int barScaleY=20;
        tickLbl = new UILabel("tick:____________",2,1);
        //this.multiSwitch=myModel.AllowMultiswitch();
        treatNames =myModel.GetTreatmentNames();
        nTreatments=treatNames.length;
        maxIntensities=myModel.GetNumIntensities();
        treatHues=new double[nTreatments];
        int[]treatColors=myModel.GetTreatmentColors();
        //switchNames = myModel.GetSwitchNames();

        //nSwitches=switchNames==null?0:switchNames.length;
        //if(this.multiSwitch&&this.nSwitches>1){

        //}
        //switchVals=new boolean[nSwitches];
        float[]hsbScratch=new float[3];
        for (int i = 0; i < nTreatments; i++) {
            Util.ColorToHSB(treatColors[i],hsbScratch);
            treatHues[i]=hsbScratch[0];
        }
        CheckValidModel();
        treatVals=new double[nTreatments];
        this.nSteps=nSteps;
        treatHistory =new int[nSteps][nTreatments];
        for (int i = 0; i < treatHistory.length; i++) {
            for (int j = 0; j < treatHistory[i].length; j++) {
                treatHistory[i][j]=-1;
            }
        }
        bars=new TreatmentBar[nTreatments];
        //toxs=new double[nSteps];
        //burdens=new double[nSteps];
        int nSaves=nSteps/stateSaveFreq;
        treatPlan =new int[nSteps][nTreatments];
        undoTreatPlan =new int[nSteps][nTreatments];
        saves=new byte[nSaves][];
        this.chosenStart=-1;

        vis=new UIGrid(visX,visY,visScale,4,1);
        vis.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                AdjustDrawState();
            }
        });
        timeline=new SectionalUIPlot(nSteps,timeScaleY,1,1,timeScaleY,0,0,nSteps,1,1,3);
        int[]plotColors=myModel.GetPlotColors();
        String[]plotLegend=myModel.GetPlotLegendNames();
        timeline.AddUpdateFn((grid)->{grid.Legend(plotLegend,plotColors,WHITE,BLACK,0,grid.yDim-10);});
        plotLines=new PlotLine[plotColors.length];
        for (int i = 0; i < plotColors.length; i++) {
            plotLines[i]=new PlotLine(timeline,plotColors[i]);
        }
        plotVals=new double[nSteps][plotLines.length];
        pauseButton=new UIButton("Pause [Space]",2,1,false,(e)->{
            TogglePause();
        });
        paused=false;
        if(!quickStart) {
            TogglePause();
        }
        undoButton =new UIButton("Undo [U]",2,1,false,(e)->{
            LoadUndo();
        });
        resetButton=new UIButton("Restart [R]",2,1,false, e->{
            hopStep=1;
            if(paused) {
                TogglePause();
                pauseStep=1;
            }
        });
        speedControl=new SectionalGGV(200,1,1,barScaleY,2,1);
        ColorSpeedBar(speedControl.xDim);
        speedControl.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int pos=speedControl.ClickX(e)+1;
                double frac=pos*1.0/speedControl.xDim;
                ColorSpeedBar(pos);
                stepMS=(int)Math.exp(Math.log(stepMSmax)*(1-frac));
            }
        });
        clearButton =new UIButton("Clear [C]",2,1,false,(e)->{
            SavePlansToUndo();
            UpdateAfterTreatmentChange();
            ClearAllTreatments();
            DrawTimeline();
            DrawTreatlines();
        });
        //treatScoreBar=new SectionalGGV(nSteps*timeScaleX/4,1,1,barScaleY,2,1);
        //burdenScoreBar=new SectionalGGV(nSteps*timeScaleX/4,1,1,barScaleY,2,1);

        //AddCol(0,treatScoreLbl);
        //AddCol(0,treatScoreBar);
        //AddCol(0,burdenScoreLbl);
        //AddCol(0,burdenScoreBar);
        //AddCol(2, winLbl);
        //AddCol(2,totalScoreLbl);
        //guiSwitches=new UIBoolInput[nSwitches];
        //if(nSwitches>0){
            //init Switch Gui components, etc. (may want to add to side!)
        //    for (int i = 0; i < nSwitches; i++) {
        //        UIBoolInput field = new UIBoolInput(switchNames[i],false);
        //        guiSwitches[i]=field;
        //        AddCol(i,field);
        //    }
        //}
        AddCol(0,vis);
        AddCol(0,tickLbl);
        AddCol(2,new UILabel("Speed Control",1,1));
        AddCol(2,speedControl);
        AddCol(0,resetButton);
        AddCol(0,pauseButton);
        AddCol(0, clearButton);
        AddCol(0, undoButton);
        AddCol(2,timeline);
        for (int i = 0; i < nTreatments; i++) {
            bars[i]=new TreatmentBar(this,i,1,barScaleY,intensityScaleX,maxIntensities[i]);
        }
        AddKeyResponses((c,i) ->{
                //key was pressed for the first time
                if(c==' '){
                    pauseButton.doClick(0);
                }
                else if(c=='c'){
                    clearButton.doClick(0);
                }
                else if(c=='u'){
                    undoButton.doClick(0);
                }
                else if(c=='r'){
                    resetButton.doClick(0);
                }
            },null);

        timeline.grid.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int clickStep=timeline.ClickX(e);
                hopStep=clickStep;
                if(paused==true) {
                    TogglePause();
                    pauseStep=clickStep+1;
                }
                //JumpStep(timeline.ClickX(e));
            }
        });
        saves[0]=SaveState();
        //toxs[0]=myModel.GetTox();
        //burdens[0]=myModel.GetBurden();
        DrawIntensityLines();
        DrawTimeline();
        DrawTreatlines();
        myModel.Draw(vis,iLastSelected);
    }
    void ColorSpeedBar(int pos){
        for (int i = 0; i < speedControl.xDim; i++) {
            if(i<=pos){
                speedControl.SetBlock(i,0, HeatMapRGB(i*1.0/speedControl.xDim));
            }
            else{
                speedControl.SetBlock(i,0,Color.BLACK.getRGB());
            }
        }

    }
    void AdjustDrawState(){
        iLastSelected++;
    }

    int GetMode(){
        if(!paused){
            return PLAY;
        }
        if(chosenStart!=-1){
            return SET_END;
        }
        return SET_START;
    }
    byte[] SaveState(){
        ByteArrayOutputStream bos=new ByteArrayOutputStream();
        ObjectOutput out;
        try{
            out= new ObjectOutputStream(bos);
            out.writeObject(myModel);
            out.flush();
            return bos.toByteArray();
        } catch (IOException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            try{
                bos.close();
            }
                catch (IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
        return null;
    }
    void UnsetChosenStarts(int skipI){
        for (int i = 0; i < nTreatments; i++) {
            if(i!=skipI) {
                bars[i].UnsetChosenStart();
            }
        }
    }
    void LoadState(int id){
        ByteArrayInputStream bis=new ByteArrayInputStream(saves[id]);
        ObjectInput in=null;
        try{
            in=new ObjectInputStream(bis);
            myModel= (TreatableTumor) in.readObject();
            myModel.SetupConstructors();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally{
            try{
                if(in!=null){
                    in.close();
                }
            }
            catch (IOException e){
                System.out.println(e.getMessage());
                e.printStackTrace();
            }
        }
    }
    void SavePlansToUndo(){
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                undoTreatPlan[i][j]=treatPlan[i][j];
            }
        }
    }
    void UpdateAfterTreatmentChange(){
        timeline.Clear();
        timeline.validStep=0;
        DrawTreatlines();
        int divStep=GetFirstDivergence(step);
        if(step-1!=divStep){
            JumpStep(divStep);
        }
    }
    void LoadUndo(){
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                int swap=treatPlan[i][j];
                treatPlan[i][j]=undoTreatPlan[i][j];
                undoTreatPlan[i][j]=swap;
            }
        }
        UpdateAfterTreatmentChange();
    }
    void ClearAllTreatments(){
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                treatPlan[i][j]=0;
            }
        }
    }
    void SetPauseText(){
        if(GetMode()!=0){
            pauseButton.SetText("Pause [space]");
        }
        else{
            pauseButton.SetText("Pause [space]");
        }
    }
    public void TogglePause(){
        paused=paused==false?true:false;
        pauseStep=-1;
        SetPauseText();
    }
    //treatment intensities fill a CAPACITY_POP
    public void CheckValidModel(){
        if(treatNames.length==0){
            throw new IllegalStateException("treatment names must have nonzero length");
        }
        if(treatHues.length==0){
            throw new IllegalStateException("treatment hues must have nonzero length");
        }
        if(treatNames.length!=treatHues.length){
            throw new IllegalStateException("treatment names and treatment hues arrays must be of same length");
        }
        for (int i = 0; i < maxIntensities.length; i++) {
            if(maxIntensities[i]<=0) {
                throw new IllegalStateException("must have at least one intensity option");
            }
        }
    }
    public void DrawTimeline(){
        if(treatHistory[timeline.validStep][0]==-1){
            timeline.Clear();
            timeline.validStep=0;
        }
        for (int i = timeline.validStep; i < nSteps; i++) {
            if (treatHistory[i][0] != -1) {
                for (int j = 0; j < plotLines.length; j++) {
                    plotLines[j].AddSegment(i, plotVals[i][j]);
                }
                timeline.validStep=i;
            }
        }
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < timeline.timeBarDisp.yDim; j++) {
                if(i==step) {
                    timeline.timeBarDisp.SetPix(i, j, YELLOW);
                }
                else{
                    timeline.timeBarDisp.SetPix(i, j, BLANK);
                }
            }
        }
        if(step==nSteps-1){
            timeline.FitPointsY(1);
        }
    }
    public String ToPercent(double prop){
        String ret="";
        String num= Integer.toString(((int)(prop*100)));
        for (int i = num.length(); i < 3; i++) {
            ret+="   ";
        }
        ret+=num;
        return ret+"%";
    }
    public int ToScore(double toxProp,double treatProp){
        return (int)(1000*Math.exp(2-(toxProp+treatProp)));
    }

    public void DrawIntensityLines(){
        for (TreatmentBar bar : bars) {
            bar.DrawIntensityLine();
        }
    }
    public void PlanToHistory(int step){
        for (int i = 0; i < nTreatments; i++) {
            treatHistory[step][i]=treatPlan[step][i];
        }
    }
    public void InvalidateHistory(int tick){
        for (int i = tick; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                treatHistory[i][j]=-1;
            }
        }
    }
    public int GetFirstDivergence(int max){
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                if(i==max||treatHistory[i][j]!=treatPlan[i][j]){
                    if(treatHistory[i][j]!=treatPlan[i][j]) {
                        InvalidateHistory(i);
                    }
                    return i-1;
                }
            }
        }
        return nSteps;
    }
    public void JumpStep(int jumpStep){
        int startStep=GetFirstDivergence(jumpStep);
        int saveStep=startStep/stateSaveFreq;
        step=saveStep*stateSaveFreq;
        LoadState(saveStep);
        if(step!=0&&paused){
            TogglePause();
            pauseStep=jumpStep;
        }
        DrawTimeline();
        myModel.Draw(vis,iLastSelected);
    }
    public void RunNextStep() {
        for (int i = 0; i < nTreatments; i++) {
            if (IsKeyDown((char) (i + 49))) {
                treatPlan[step][i] = bars[i].chosenIntensity;
            }
        }
        int divStep;
        if(hopStep!=-1){
            divStep=GetFirstDivergence(hopStep);
            hopStep=-1;
        }
        else {
            divStep = GetFirstDivergence(step);
        }
        if (step - 1 != divStep) {
            JumpStep(divStep);
        }
        if (step != 0 && step % stateSaveFreq == 0) {
            saves[step / stateSaveFreq] = SaveState();
        }
        PlanToHistory(step);
        TickPause(stepMS);
        for (int i = 0; i < nTreatments; i++) {
            treatVals[i] = treatPlan[step][i] * 1.0 / maxIntensities[i];
        }
        myModel.InteractiveStep(treatVals, step);
        double[]stepVals=myModel.GetPlotVals();
        for (int i = 0; i < plotLines.length; i++) {
            plotVals[step][i]=stepVals[i];
        }
        step++;
        if (step == nSteps) {
            pauseStep = -1;
            LoadState(0);
            step=0;
        }
        if (step == pauseStep && !paused) {
            this.TogglePause();
        }
        myModel.Draw(vis,iLastSelected);
        //burdens[step] = myModel.GetBurden();
        //toxs[step] = myModel.GetTox();
        DrawTimeline();
        DrawTreatlines();
        SetTickLbl();
    }
    void DrawTreatlines(){
        for (int i = 0; i < nSteps; i++) {
            for (int j = 0; j < nTreatments; j++) {
                bars[j].DrawTreatmentLine(i);
            }
        }
    }
    void DrawTreatline(int treatI){
        for (int i = 0; i < nSteps; i++) {
            bars[treatI].DrawTreatmentLine(i);
        }

    }
    void SetTickLbl(){
        tickLbl.SetText("Tick: "+step);
    }
    public int GetTick(){
        return step;
    }
    public void RunGui(){
        super.RunGui();
        SetTickLbl();
        while(true){
            if(!paused){
                RunNextStep();
            }
            else{
                TickPause(100);
            }
        }
    }
}

