package Examples._7ModelExtension.MetabolismModel;

import Framework.GridsAndAgents.PDEGrid2D;
import Framework.Gui.GridWindow;
import Framework.Rand;
import Framework.Tools.FileIO;

class Cell extends MetabolismCell<Cell,Grid> {}

class Grid extends MetabolismGrid<Cell> {
    public Grid(int x, int y,  Rand rn) {
        super(x, y, Cell.class, rn);

    }

    public void WritePDE(PDEGrid2D grid,String name){
        FileIO out=new FileIO(name,"w");
            for (int y = 0; y < grid.yDim; y++) {
                for (int x = 0; x < grid.xDim; x++) {
                if(x<xDim-1) {
                    out.Write(grid.Get(x, y) + ",");
                }
                else{
                    out.Write(grid.Get(x, y)+"");
                }
            }
            out.Write("\n");
        }
        out.Close();
    }

    public void WritePDEs(int tick){
        WritePDE(oxygen,"Oxygen"+tick+".csv");
        WritePDE(glucose,"Gluc"+tick+".csv");
        WritePDE(protons,"Protons"+tick+".csv");
    }
    public void SetVesselConcs(){
        for (MetabolismCell v : vessels) {
            oxygen.Set(v.Isq(),OXYGEN_VESSEL_CONC);
            glucose.Set(v.Isq(),GLUCOSE_VESSEL_CONC);
            protons.Set(v.Isq(),ACID_VESSEL_CONC);
            oxygen.Update();
            glucose.Update();
            protons.Update();
        }
    }

    public void SteadyStateTest(){
        protons.SetAll(ACID_VESSEL_CONC);
        glucose.SetAll(GLUCOSE_VESSEL_CONC);
        oxygen.SetAll(OXYGEN_VESSEL_CONC);
        UpdatePDEs();
        for (int i = 0; i < length; i++) {
            GenNormalCell(i,true);
        }
        for (int i = 0; i < 1000; i++) {
            ReactionDiffusion(false,false);
            if(i==999) {
                SetVesselConcs();
                WritePDEs(i+1);
            }
        }
        //int loopCt=DiffLoop(false);
        //System.out.println(loopCt);
        //WritePDEs(loopCt);
    }

    public void StaticCellsTest(){
        protons.SetAll(ACID_VESSEL_CONC);
        protons.Update();
        //ensure that cells don't die or divide, same for vessels
        for (int i = 0; i < length; i++) {
            GenNormalCell(i,true);
        }
        //GenVessel(I(9,19));

        for (int i = 0; i < 10000; i++) {
            ReactionDiffusion(false,false);
            if(i==0||i==9||i==99||i==999||i==9999) {
                SetVesselConcs();
                WritePDEs(i+1);
            }
        }
    }

    public void DiffusionTest(){
        for (int i = 0; i < 1000; i++) {
            ReactionDiffusion(false,false);
            if(i==0||i==9||i==99||i==999) {
                WritePDEs(i+1);
            }
        }
    }

    public void CellGrowthStep(){
        DiffLoop(false);
        for (MetabolismCell c : this) {
            c.DefaultCellStep();
        }
        CleanAgents();
        ShuffleAgents(rn);
    }
    public void DefaultTumorTest(double numDays,double radius,double glycPheno,double arPheno){
        GridWindow vis=new GridWindow(xDim,yDim,10);
        DefaultSetup(radius,glycPheno,arPheno);
        for (int i = 0; i < numDays/CELL_TIME_STEP; i++) {
            DefaultGridStep();
            DrawCells(vis);
            if(i==(int)(10/CELL_TIME_STEP)) {
                vis.ToPNG("DefaultTumor"+numDays+"Days"+radius+"TumorRad"+glycPheno+"glycPheno"+arPheno+"arPheno.png");
            }
        }
        vis.ToPNG("DefaultTumor"+numDays+"Days"+radius+"TumorRad"+glycPheno+"glycPheno"+arPheno+"arPheno.png");
        vis.Close();
    }

    public void CellGrowthTest(double numDays){
        GridWindow vis=new GridWindow(xDim,yDim,10);
        protons.SetAll(ACID_VESSEL_CONC);
        glucose.SetAll(GLUCOSE_VESSEL_CONC);
        oxygen.SetAll(OXYGEN_VESSEL_CONC);
        UpdatePDEs();
        GenVessel(I(24,24));
        //GenNormalCell(I(25,24),true);
        GenTumorCell(I(25,24),NORMAL_GLYCOLYTIC_PHENO,NORMAL_ACID_RESIST_PHENO,true);
        for (int i = 0; i < numDays/CELL_TIME_STEP; i++) {
            CellGrowthStep();
            DrawCells(vis);
            //DrawPhenos(vis);
        }
        System.out.println(Pop());
        //DrawCells(vis);
        vis.ToPNG("CellGrowthVis"+(numDays)+"Days.png");
        vis.Close();
    }
    public void GenVessselsAndCellsTest(){
        protons.SetAll(ACID_VESSEL_CONC);
        glucose.SetAll(GLUCOSE_VESSEL_CONC);
        oxygen.SetAll(OXYGEN_VESSEL_CONC);
        UpdatePDEs();
        GridWindow vis=new GridWindow(xDim,yDim,10);
        GridWindow vis2=new GridWindow(xDim*10,yDim);
        GenVessel(I(24,24));
        GenTumorCell(I(25,24),NORMAL_GLYCOLYTIC_PHENO,NORMAL_ACID_RESIST_PHENO,true);
//        InitNormalCells(0.8);
//        InitVessels((int)((length*1.0)/(MEAN_VESSEL_SPACING*MEAN_VESSEL_SPACING)));
        for (int i = 0; i < 960; i++) {
            Angiogenesis();
            CellGrowthStep();
            if(i==239||i==479||i==719||i==959){
                DrawCells(vis);
                DrawPhenos(vis2);
                vis.ToPNG("TumorGrowth"+(i+1)+".png");
                vis2.ToPNG("TumorPheno"+(i+1)+".png");
            }
        }
        vis.Close();
    }
}


public class MetabolismTester {
    public static void main(String[] args) {
        Grid model=new Grid(20,20,new Rand(0));
        //model.DefaultTumorTest(80,10,model.NORMAL_GLYCOLYTIC_PHENO,model.NORMAL_ACID_RESIST_PHENO);
        model.DefaultTumorTest(20,10,40,6.2);
        //model.GenVessselsAndCellsTest();
    }
}
