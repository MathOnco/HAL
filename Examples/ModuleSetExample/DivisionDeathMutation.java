package Examples.ModuleSetExample;

import HAL.Tools.Modularity.VarSet;
import HAL.Tools.Modularity.VarSetManager;
import HAL.GridsAndAgents.AgentGrid2D;
import HAL.GridsAndAgents.AgentSQ2Dunstackable;
import HAL.Gui.GridWindow;
import HAL.Rand;
import HAL.Tools.Modularity.ModuleSetManager;
import HAL.Util;

import static HAL.Util.BLACK;

class DivDeathModule{
    final DivisionDeathMutation G;
    DivDeathModule(DivisionDeathMutation G){
        this.G=G;
    }
    public void Setup(){}
    public void OnStep(){}
    public void OnStepCell(CellEx c){}
    public void OnDiv(CellEx dividingCell,CellEx daughter){}
    public void OnDeath(CellEx dyingCell){}
    public void OnMut(CellEx mutatingCell){}
    public double SetDivProb(CellEx dividingCell,double contactInhib){return 0;}
    public double SetDeathProb(CellEx dyingCell,double contactInhib){return 0;}
    public double SetMutProb(CellEx dyingCell,double contactInhib){return 0;}
}

class SetupTumorMod extends DivDeathModule{
    final double radius;
    SetupTumorMod(DivisionDeathMutation G,double radius) {
        super(G);
        this.radius=radius;
    }

    @Override
    public void Setup(){
        //places tumor cells in a circle
        int[]circleHood= Util.CircleHood(true,radius);//generate circle neighborhood [x1,y1,x2,y2,...]
        int len=G.MapHood(circleHood,G.xDim/2,G.yDim/2);
        for (int i = 0; i < len; i++) {
            G.NewCell(circleHood[i],null);
        }
    }
}

class DivDeathProbMod extends DivDeathModule{
    DivDeathProbMod(DivisionDeathMutation G) {
        super(G);
    }

    @Override
    public double SetDivProb(CellEx c,double contact){
        return 0.2;
    }
    @Override
    public double SetDeathProb(CellEx c,double contact){
        return 0.1;
    }
}

class DivMutMod extends DivDeathModule {
    int DIVIDED;
    DivMutMod(DivisionDeathMutation G) {
        super(G);
        DIVIDED=G.vsm.NewVar();
    }

    @Override
    public void OnDiv(CellEx parent,CellEx c){
        if(parent!=null) {
            parent.vars[DIVIDED] = 1;
        }
        c.vars[DIVIDED]=1;
    }

    @Override
    public double SetMutProb(CellEx c,double contactInhib){
        if(c.vars[DIVIDED]==1){
            c.vars[DIVIDED]=0;
            return 0.1;
        }
        return 0;
    }
}

class DrawTumorMod extends DivDeathModule{
    GridWindow vis;
    DrawTumorMod(DivisionDeathMutation G,int scaleFactor) {
        super(G);
        vis=new GridWindow(G.xDim,G.yDim,scaleFactor);
    }

    @Override
    public void OnDiv(CellEx parent,CellEx c){
        Draw(c);
    }

    @Override
    public void OnMut(CellEx c){
        Draw(c);
    }

    @Override
    public void OnDeath(CellEx c){
        vis.SetPix(c.Isq(),BLACK);
    }

    void Draw(CellEx c){
        vis.SetPix(c.Isq(), Util.CategorialColor(c.nMutations));//sets a single pixel
    }
}

//cells grow and mutate
class CellEx extends AgentSQ2Dunstackable<DivisionDeathMutation> implements VarSet {
    int nMutations;
    double[]vars;

    void Mutate(){
        nMutations++;
        for (DivDeathModule mod : G.mods.Iter("OnMut")) {
            mod.OnMut(this);
        }
    }

    @Override
    public double[] GetVars() {
        return vars;
    }

    @Override
    public void SetVars(double[] newVars) {
        this.vars=newVars;
    }
}

public class DivisionDeathMutation extends AgentGrid2D<CellEx> {
    ModuleSetManager<DivDeathModule> mods= new ModuleSetManager<>(DivDeathModule.class);
    VarSetManager vsm=new VarSetManager();
    final static int BLACK= Util.RGB(0,0,0);
    int divProbMods;
    int dieProbMods;
    int mutProbMods;
    int MAX_MUTATIONS =19;
    int[]hood=Util.GenHood2D(new int[]{1,0,-1,0,0,1,0,-1}); //equivalent to int[]hood=Util.VonNeumannHood(false);
    int hoodSize=hood.length/3;
    Rand rn=new Rand(1);
    public DivisionDeathMutation(int x, int y) {
        super(x, y, CellEx.class);
    }
    public void Setup(){
        for (DivDeathModule module : mods.Iter("Setup")) {
            module.Setup();
        }
        divProbMods=mods.CountModsWithMethod("SetDivProb");
        dieProbMods=mods.CountModsWithMethod("SetDeathProb");
        mutProbMods=mods.CountModsWithMethod("SetMutProb");
    }

    CellEx NewCell(int iSq,CellEx parent){
        CellEx c=NewAgentSQ(iSq);
        vsm.AddVarSet(c);
        if(parent==null){
            c.nMutations=0;
        } else{
            c.nMutations=parent.nMutations;
        }
        for (DivDeathModule module : mods.Iter("OnDiv")) {
            module.OnDiv(parent,c);
        }
        return c;
    }

    public void StepCells(){
        for (DivDeathModule module : mods.Iter("OnStep")) {
            module.OnStep();
        }
        for (CellEx c : this) {//iterate over all cells in the grid
            for (DivDeathModule module : mods.Iter("OnStepCell")) {
                module.OnStepCell(c);
            }
            int ct=MapEmptyHood(hood,c.Isq());
            double contactInhib=(hoodSize-ct)/hoodSize;
            double divProb=0;
            double dieProb=0;
            for (DivDeathModule module : mods.Iter("SetDeathProb")) {
                dieProb+=module.SetDeathProb(c,contactInhib);
            }
            dieProb/=dieProbMods;
            if(dieProb>0&&rn.Double()<dieProb){
                for (DivDeathModule module : mods.Iter("OnDeath")) {
                    module.OnDeath(c);
                }
                c.Dispose();
                continue;
            }
            double mutProb=0;
            if(c.nMutations<MAX_MUTATIONS) {
                for (DivDeathModule module : mods.Iter("SetMutProb")) {
                    mutProb += module.SetMutProb(c, contactInhib);
                }
            }
            mutProb/=mutProbMods;
            if(mutProb>0&&rn.Double()<mutProb){
                c.Mutate();
            }

            if(ct>0) {
                for (DivDeathModule module : mods.Iter("SetDivProb")) {
                    divProb += module.SetDivProb(c, contactInhib);
                }
                divProb /= divProbMods;
                if (divProb > 0 && rn.Double() < divProb) {
                    NewCell(hood[rn.Int(ct)], c);
                }
            }
        }
        ShuffleAgents(rn);//shuffles order of for loop iteration
        IncTick();//increments timestep
    }

    public static void main(String[]args){
        int x=250,y=250;
        DivisionDeathMutation grid=new DivisionDeathMutation(x,y);
        grid.mods.AddModule(new SetupTumorMod(grid,10));
        grid.mods.AddModule(new DivDeathProbMod(grid));
        grid.mods.AddModule(new DrawTumorMod(grid,4));
        grid.mods.AddModule(new DivMutMod(grid));
        grid.Setup();
        for (int tick = 0; tick < 1000; tick++) {
            grid.StepCells();
        }
    }
}