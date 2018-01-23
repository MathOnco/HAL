package Framework.Extensions;

import Framework.Gui.*;
import Framework.Interfaces.TreatableTumor;
import Framework.Interfaces.VoidFunction;
import Framework.Tools.KeyRecorder;
import Framework.Util;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.*;

import static Framework.Util.*;

/**
 * Created by Rafael on 9/26/2017.
 */
/*
TODO:
    -add grey regions at beginning and end of timeline to help with selection at the extreme ends
    -fix reverting to main color cycle after clicking on screen
    -add option to set speed when model starts
    -fix model error
*/

class StateDebugger{
    ClinicianSim mySim;
    byte[][]saves;
    StateDebugger(ClinicianSim mySim){
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
    final ClinicianSim myLab;
    final SectionalGGV intensitySelect;
    final SectionalGGV treatline;
    final GuiButton setAllBtn;
    final GuiLabel nameLbl;
    final double hue;
    int chosenIntensity;
    int chosenStart;

    TreatmentBar(ClinicianSim myLab, int index, int timescaleX, int timescaleY, int intensityScaleX) {
        this.index = index;
        this.myLab = myLab;
        nameLbl = new GuiLabel(myLab.treatNames[index]+" ["+(index+1)+"]");
        intensitySelect = new SectionalGGV(myLab.maxIntensity + 1, 1, intensityScaleX, timescaleY, 1, 1);
        treatline = new SectionalGGV(myLab.nSteps, 1, timescaleX, timescaleY, 1, 1);
        setAllBtn=new GuiButton("SetBlock Line",1,1,false,(e)->{
            myLab.SavePlansToUndo();
            SetLine();
            myLab.AdjustDrawState(index);
        });
        chosenStart=-1;
        chosenIntensity=1;
        hue = myLab.treatHues[index];
        myLab.AddCol(0, intensitySelect);
        myLab.AddCol(1, nameLbl);
        myLab.AddCol(2, setAllBtn);
        myLab.AddCol(3, treatline);
        intensitySelect.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                chosenIntensity = intensitySelect.ClickX(e);
                DrawIntensityLine();
                myLab.AdjustDrawState(index);
            }
        }, null);
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
                myLab.AdjustDrawState(index);
            }
        },null);
    }

    public void SetLine(){
        myLab.UnsetChosenStarts(-1);
        for (int i = 0; i < myLab.nSteps; i++) {
            myLab.treatPlan[i][index]=chosenIntensity;
        }
        myLab.DrawTreatline(index);
    }
    public void DrawIntensityLine() {
        for (int i = 1; i < myLab.maxIntensity + 1; i++) {
            if (i <= (chosenIntensity)) {

                intensitySelect.SetBlock(i,0,HSBColor((float) hue, 1, ((float) (i + 2.0)) / (myLab.maxIntensity + 2)));
            } else {
                intensitySelect.SetBlock(i,0, Color.BLACK.getRGB());
            }
        }
    }

    public void DrawTreatmentLine(int i) {
        if (i == chosenStart) {
            treatline.SetBlock(i, 0, Color.WHITE.getRGB());
        } else {
            if (myLab.treatPlan[i][index] == 0) {
                treatline.SetBlock(i, 0, Color.BLACK.getRGB());
            } else {
                treatline.SetBlock(i, 0, HSBColor((float) hue, 1, ((float) (myLab.treatPlan[i][index] + 2)) / (myLab.maxIntensity + 2)));
            }
        }
    }
    public void UnsetChosenStart(){
        chosenStart=-1;
    }
}

class SectionalGGV extends GuiGrid {
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
public class ClinicianSim extends GuiWindow{
    //GUI CONSTANTS
    VoidFunction stepExtra;
    final private static int PLAY=0,SET_START=1,SET_END=2;
    final GuiLabel treatScoreLbl=new GuiLabel("Tolerable Toxicity:________________%");
    final GuiLabel visLbl=new GuiLabel("Default_View______________________________________",4,1);
    final GuiLabel burdenScoreLbl=new GuiLabel("Tolerable Burden:________________%");
    final SectionalGGV treatScoreBar;
    final SectionalGGV burdenScoreBar;
    final GuiLabel totalScoreLbl=new GuiLabel("__________________score:____________________");
    final GuiLabel winLbl =new GuiLabel("keep playing...______________________");
    final int maxIntensity;
    final int nTreatments;
    final int nSteps;
    final double[]treatHues;
    final String[] treatNames;
    //final String[] switchNames;
    final int stateSaveFreq;
    final int stepMSmin;
    final int stepMSmax;
    //final int nSwitches;
    final double maxBurden;
    final double maxTox;
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
    double[] toxs;
    double[] burdens;
    byte[][] saves;
    boolean[] switchVals;
    int step;
    int pauseStep;
    int iLastMainSelected=-1;
    int iLastSelected=-1;

    //GUI COMPONENTS
    TreatableTumor myModel;
    final GuiLabel tickLbl;
    final public GuiGrid vis;
    final public GuiGrid alphaVis;
    final SectionalGGV timeline;
    final SectionalGGV speedControl;
    //final SectionalGGV treatline;
    final GuiButton pauseButton;
    final GuiButton undoButton;
    final GuiButton clearButton;
    final GuiButton setAllButton;
    final GuiButton resetButton;
    //final GuiComboBoxField treatmentSelect;
    //final GuiGrid intensitySelect;
    final ParamSet guiState;
    final TreatmentBar[]bars;
    //final GuiBoolField[]guiSwitches;
    final KeyRecorder keyRecorder=new KeyRecorder();

    //EXTRA
    float[] colorScratch=new float[3];
    public ClinicianSim(TreatableTumor myModel, int nSteps, int stateSaveFreq, int visScale, int timeScaleX, int barScaleY, int intensityScaleX, int stepMSmin, int stepMSmax,boolean redrawOnTreatmentSwitch,boolean quickStart) {
        super("Clinician Simulator",true,true);
        this.stepMSmin=stepMSmin;
        this.stepMSmax=stepMSmax;
        this.stepMS=stepMSmin;
        this.myModel =myModel;
        this.stateSaveFreq=stateSaveFreq;
        this.redrawOnSelectionSwitch =redrawOnTreatmentSwitch;
        tickLbl = new GuiLabel("tick:____________");
        //this.multiSwitch=myModel.AllowMultiswitch();
        treatNames =myModel.GetTreatmentNames();
        nTreatments=treatNames.length;
        maxIntensity=myModel.GetNumIntensities();
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
        maxBurden=myModel.GetMaxBurden();
        maxTox=myModel.GetMaxTox();
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
        toxs=new double[nSteps];
        burdens=new double[nSteps];
        int nSaves=nSteps/stateSaveFreq;
        treatPlan =new int[nSteps][nTreatments];
        undoTreatPlan =new int[nSteps][nTreatments];
        saves=new byte[nSaves][];
        this.chosenStart=-1;

        guiState=new ParamSet();

        vis=new GuiGrid(myModel.VisPixX(),myModel.VisPixY(),visScale,4,1);
        vis.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                AdjustDrawState(-1);
            }
        },null);
        int alphaVisScale=myModel.AlphaGridScaleFactor();
        alphaVis=new GuiGrid(myModel.VisPixX()/alphaVisScale,myModel.VisPixY()/alphaVisScale,alphaVisScale*visScale,4,1);
        vis.AddAlphaGrid(alphaVis);
        for (int i = 0; i < alphaVis.length; i++) {
            alphaVis.SetPix(i, RGBA((double) 0, (double) 0, (double) 0, (double) 0));
        }
        timeline=new SectionalGGV(nSteps,2,timeScaleX,barScaleY,1,2);
        pauseButton=new GuiButton("Pause [Space]",false,(e)->{
            TogglePause();
        });
        paused=false;
        if(!quickStart) {
            TogglePause();
        }
        undoButton =new GuiButton("Undo [U]",2,1,false,(e)->{
            LoadUndo();
        });
        resetButton=new GuiButton("Reset [R]",1,1,false,e->{
            hopStep=1;
            if(paused) {
                TogglePause();
                pauseStep=1;
            }
        });
        setAllButton=new GuiButton("SetBlock All [S]",1,1,false,(e)->{
            SavePlansToUndo();
            for (TreatmentBar bar : bars) {
                bar.SetLine();
            }
        });
        speedControl=new SectionalGGV(nSteps*timeScaleX/4,1,1,barScaleY,2,1);
        ColorSpeedBar(speedControl.xDim);
        speedControl.AddMouseListeners(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int pos=speedControl.ClickX(e)+1;
                double frac=pos*1.0/speedControl.xDim;
                ColorSpeedBar(pos);
                stepMS=stepMSmax-(int)((stepMSmax-stepMSmin)*frac);
            }
        },null);
        clearButton =new GuiButton("Clear [C]",false,(e)->{
            SavePlansToUndo();
            UpdateAfterTreatmentChange();
            ClearAllTreatments();
            DrawTimeline();
            DrawTreatlines();
        });
        treatScoreBar=new SectionalGGV(nSteps*timeScaleX/4,1,1,barScaleY,2,1);
        burdenScoreBar=new SectionalGGV(nSteps*timeScaleX/4,1,1,barScaleY,2,1);

        AddCol(0,treatScoreLbl);
        AddCol(0,treatScoreBar);
        AddCol(0,burdenScoreLbl);
        AddCol(0,burdenScoreBar);
        AddCol(2, winLbl);
        AddCol(2,totalScoreLbl);
        AddCol(3,tickLbl);
        AddCol(2,new GuiLabel("Speed Control",2,1));
        AddCol(2,speedControl);
        //guiSwitches=new GuiBoolField[nSwitches];
        //if(nSwitches>0){
            //init Switch Gui components, etc. (may want to add to side!)
        //    for (int i = 0; i < nSwitches; i++) {
        //        GuiBoolField field = new GuiBoolField(switchNames[i],false);
        //        guiSwitches[i]=field;
        //        AddCol(i,field);
        //    }
        //}
        AddCol(0,vis);
        AddCol(0,visLbl);
        AddCol(1,new GuiLabel("Tumor Burden",2,1));
        AddCol(1,new GuiLabel("Treatment Toxicity",2,1));
        AddCol(0,setAllButton);
        AddCol(0,resetButton);
        AddCol(3,timeline);

        for (int i = 0; i < nTreatments; i++) {
            bars[i]=new TreatmentBar(this,i,timeScaleX,barScaleY,intensityScaleX);
        }
        AddCol(0,pauseButton);
        AddCol(1, clearButton);
        AddCol(2, undoButton);
        AddKeyResponses((c,i) ->{
                //key was pressed for the first time
                if(c==' '){
                    pauseButton.doClick(0);
                }
                else if(c=='s'){
                    setAllButton.doClick(0);
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

        timeline.AddMouseListeners(new MouseAdapter() {
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
        },null);
        saves[0]=SaveState();
        toxs[0]=myModel.GetTox();
        burdens[0]=myModel.GetBurden();
        DrawIntensityLines();
        DrawTimeline();
        DrawTreatlines();
        myModel.Draw(vis,alphaVis,iLastSelected,visLbl,treatVals);
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
    void AdjustDrawState(int selectionIndex){
        int iPrev=iLastSelected;
        if(selectionIndex>=0){//chose a treatment
            iLastSelected=selectionIndex;
        }
        else{//chose main window
            if(iLastSelected>=0) {
                iLastSelected = iLastMainSelected;
            }else{
                iLastSelected-=1;
            }
        }
        if(iPrev!=iLastSelected&&redrawOnSelectionSwitch){
            myModel.Draw(vis,alphaVis,iLastSelected,visLbl,treatVals);
        }
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
    void SetAllTreatment(int iTreatment,int intensity){
        for (int i = 0; i < nSteps; i++) {
            treatPlan[i][iTreatment]=intensity;
        }
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
            pauseButton.SetText("Play [space]");
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
        if(maxIntensity<=0){
            throw new IllegalStateException("must have at least one intensity option");
        }
    }
    public void DrawTimeline(){
        for (int i = 0; i < nSteps; i++) {
            if (i == step) {
                timeline.SetBlock(i, 0, Color.YELLOW.getRGB());
                timeline.SetBlock(i, 1, Color.YELLOW.getRGB());
            }
            else if (treatHistory[i][0]!=-1) {
                if(burdens[i]>maxBurden) {
                    timeline.SetBlock(i, 1,Color.RED.getRGB());
                }
                else{
                    double currBur=burdens[i]/maxBurden;
                    timeline.SetBlock(i, 1, HeatMapBRG(currBur));
                }
                if(toxs[i]>maxTox) {
                    timeline.SetBlock(i, 0, Color.RED.getRGB());
                }
                else{
                    double currTox=toxs[i]/maxTox;
                    timeline.SetBlock(i, 0, HeatMapGRB(currTox));
                }
            }
            else{
                timeline.SetBlock(i,0,Color.BLACK.getRGB());
                timeline.SetBlock(i,1,Color.BLACK.getRGB());
            }
        }
        UpdateScoreDisp();
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
    public void UpdateScoreDisp() {
        int toxState = 0;//0=win, 1=loss, 2=undecided
        int burdenState = 0;//0=win, 1=loss, 2=undecided
        double toxTot = 0;
        double burdenTot = 0;
        int validSteps = GetFirstDivergence(nSteps);
        for (int i = 0; i < validSteps; i++) {
            double currBur = burdens[i];
            double currTox = toxs[i];
            if (currBur > maxBurden) {
                burdenState = 1;
            }
            if (currTox > maxTox) {
                toxState = 1;
            }
            burdenTot += currBur;
            toxTot += currTox;
        }
        double currTox = toxs[step] / maxTox;
        double currBur = burdens[step] / maxBurden;
        for (int i = 0; i < treatScoreBar.xDim; i++) {
            if (currTox > 1) {
                treatScoreBar.SetBlock(i, 0, Color.RED.getRGB());
            } else if (i * 1.0 / treatScoreBar.xDim <= currTox) {
                treatScoreBar.SetBlock(i, 0, HeatMapGRB(i * 1.0 / treatScoreBar.xDim));
            } else {
                treatScoreBar.SetBlock(i, 0, Color.BLACK.getRGB());
            }
        }
        for (int i = 0; i < burdenScoreBar.xDim; i++) {
            if (currBur > 1) {
                burdenScoreBar.SetBlock(i, 0, Color.RED.getRGB());
            } else if (i * 1.0 / burdenScoreBar.xDim <= currBur) {
                burdenScoreBar.SetBlock(i, 0, HeatMapBRG(i * 1.0 / burdenScoreBar.xDim));
            } else {
                burdenScoreBar.SetBlock(i, 0, Color.BLACK.getRGB());
            }
        }
        treatScoreLbl.SetText("Tolerable Toxicity: " + ToPercent(currTox));
        burdenScoreLbl.SetText("Tolerable Burden: " + ToPercent(currBur));
        totalScoreLbl.SetText("Estimated Score: " + ToScore(toxTot / validSteps, burdenTot / validSteps));
        if (validSteps == nSteps && toxState == 0 && burdenState == 0) {
            winLbl.SetText("YOU WIN! SCORE: " + ToScore(toxTot / validSteps, burdenTot / validSteps));
        } else if (toxState == 1 || burdenState == 1) {
            winLbl.SetText("YOU LOSE!");
        } else {
            winLbl.SetText("keep playing...");
        }
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
        myModel.Draw(vis,alphaVis,iLastSelected,visLbl,treatVals);
    }
    public void RunNextStep() {
        for (int i = 0; i < nTreatments; i++) {
            if (keyRecorder.IsPressed((char) (i + 49))) {
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
            treatVals[i] = treatPlan[step][i] * 1.0 / maxIntensity;
        }
        myModel.QuackStep(treatVals, step, stepMS);
        step++;
        if (step == nSteps) {
            pauseStep = -1;
            LoadState(0);
            step=0;
        }
        if (step == pauseStep && !paused) {
            this.TogglePause();
        }
        myModel.Draw(vis, alphaVis,iLastSelected,visLbl,treatVals);
        burdens[step] = myModel.GetBurden();
        toxs[step] = myModel.GetTox();
        DrawTimeline();
        DrawTreatlines();
        SetTickLbl();
        if(stepExtra!=null){
            stepExtra.Execute();
        }
    }
    public void AddExtraStepAction(VoidFunction action){
        stepExtra=action;
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
    public void RunModel(){
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

