package Framework.Extensions.MarkModelFast;

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
    public GuiLabel days;
    public Tissue model;
    public final int diffScale;
    //ADD LABEL FOR TIMESTEP
    //ADD LABEL FOR PHENOTYPE
    public TestVis(Tissue model, int scale){
        this.model=model;
        this.diffScale=model.DIFF_SPACE_SCALE;
        win=new GuiWindow("MMVis",true);
        cells=new GuiGrid(model.xDim,model.yDim,scale*3,2,10);
        acid=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        oxy=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        gluc=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        pheno=new GuiGrid(model.xDim,model.yDim,scale);
        vessel=new GuiGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        days=new GuiLabel("days:_____");
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
        win.RunGui();
    }
    public void Draw(){
        model.DrawCells(cells);
        days.SetText("days: "+model.GetTick()*model.CELL_TIMESTEP);
        model.DrawAcidOld(acid);
        model.DrawOxygenOld(oxy);
        model.DrawGlucoseOld(gluc);
        model.DrawPhenos(pheno);
        model.DrawOxygenMinMaxAngio(vessel);
    }
}