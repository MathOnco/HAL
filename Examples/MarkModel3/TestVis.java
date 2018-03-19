package Examples.MarkModel3;

import Framework.Gui.GuiGrid;
import Framework.Gui.GuiLabel;
import Framework.Gui.GuiWindow;

public class TestVis {
    public GuiWindow win;
    public GuiGrid cells;
    public GuiGrid acid;
    public GuiGrid oxy;
    public GuiGrid gluc;
    public GuiGrid pheno;
    public GuiGrid vessel;
    public GuiGrid drugDraw;
    public GuiLabel days;
    public Tissue model;
    public Drug drug;
    public final int diffScale;
    //ADD LABEL FOR TIMESTEP
    //ADD LABEL FOR PHENOTYPE
    public TestVis(Tissue model, int scale, String title){
        this.model=model;
        if(model.allDrugs.size()>0) {
            this.drug = (Drug)model.allDrugs.get(0);
        }
        this.diffScale=model.DIFF_SPACE_SCALE;
        win=new GuiWindow(title,true);
        cells=new GuiGrid(model.xDim,model.yDim,scale*3,2,10);
        acid=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        oxy=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        gluc=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        pheno=new GuiGrid(model.xDim,model.yDim,scale);
        vessel=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        drugDraw=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        days=new GuiLabel("days:______________________");
        win.AddCol(0,new GuiLabel("cells"));
        win.AddCol(1,days);
        win.AddCol(0,cells);
        win.AddCol(2,new GuiLabel("acid"));
        win.AddCol(2,acid);
        win.AddCol(2,new GuiLabel("oxy"));
        win.AddCol(2,oxy);
        win.AddCol(2,new GuiLabel("gluc"));
        win.AddCol(2,gluc);
        win.AddCol(3,new GuiLabel("pheno"));
        win.AddCol(3,pheno);
        win.AddCol(3,new GuiLabel("vessel"));
        win.AddCol(3,vessel);
        if(drug!=null) {
            win.AddCol(3, new GuiLabel(drug.name));
            win.AddCol(3, drugDraw);
        }
        win.RunGui();
    }
    public void Draw(int tick){
        model.DrawCells(cells);
        days.SetText("days: "+tick*model.CELL_TIMESTEP);
        model.DrawAcidOld(acid);
        model.DrawOxygenOld(oxy);
        model.DrawGlucoseOld(gluc);
        model.DrawPhenos(pheno);
        model.DrawOxygenMinMaxAngio(vessel);
        if(drug!=null) {
            drug.Draw(drugDraw);
        }
    }
}