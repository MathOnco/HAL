package Examples.MarkModel3;

import Framework.Gui.UIGrid;
import Framework.Gui.UILabel;
import Framework.Gui.UIWindow;

public class TestVis {
    public UIWindow win;
    public UIGrid cells;
    public UIGrid acid;
    public UIGrid oxy;
    public UIGrid gluc;
    public UIGrid pheno;
    public UIGrid vessel;
    public UIGrid drugDraw;
    public UILabel days;
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
        win=new UIWindow(title,true);
        cells=new UIGrid(model.xDim,model.yDim,scale*3,2,10);
        acid=new UIGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        oxy=new UIGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        gluc=new UIGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        pheno=new UIGrid(model.xDim,model.yDim,scale);
        vessel=new UIGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        drugDraw=new UIGrid(model.xDim/diffScale,model.yDim/diffScale,scale*diffScale);
        days=new UILabel("days:______________________");
        win.AddCol(0,new UILabel("cells"));
        win.AddCol(1,days);
        win.AddCol(0,cells);
        win.AddCol(2,new UILabel("acid"));
        win.AddCol(2,acid);
        win.AddCol(2,new UILabel("oxy"));
        win.AddCol(2,oxy);
        win.AddCol(2,new UILabel("gluc"));
        win.AddCol(2,gluc);
        win.AddCol(3,new UILabel("pheno"));
        win.AddCol(3,pheno);
        win.AddCol(3,new UILabel("vessel"));
        win.AddCol(3,vessel);
        if(drug!=null) {
            win.AddCol(3, new UILabel(drug.name));
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