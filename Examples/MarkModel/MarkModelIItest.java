package Examples.MarkModel;

import Framework.Extensions.MarkModel_II.DefaultMMVis;
import Framework.Extensions.MarkModel_II.MarkCell_II;
import Framework.Extensions.MarkModel_II.MarkModel_II;
import Framework.Gui.GifMaker;
import Framework.Gui.GuiGrid;
import Framework.Rand;

class Cell extends MarkCell_II<Cell,Model>{

}

class Model extends MarkModel_II<Cell>{
    public Model(int x, int y, boolean reflectiveBoundary, boolean setupConstants, Rand rn) {
        super(x, y, reflectiveBoundary, setupConstants, Cell.class, rn);
    }
}


public class MarkModelIItest {
    public static void RunModel(int x, int y, boolean vis, int index, int steps) {
        GifMaker gif=new GifMaker("invasion"+index+".gif",0,true);
        System.out.println("Started "+index);
        Model m = new Model(x, y, false, true, new Rand());
        DefaultMMVis visAll=null;
        if(vis){
            visAll=new DefaultMMVis(m,3);
        }
        GuiGrid visCells=new GuiGrid(x,y,5,true);
        m.SetupVessels();
        m.SetupTissue(0.8);
        m.MUT_RATE_ACID_RESIST=0.05;
        m.MUT_RATE_GLYC=0.05;
        m.SetupConstants();
        m.SetupBoundaryConds();
        m.SetupTumor(20,m.GetGlycRate(0),m.GetAcidResistPH(0));
        m.IncTick();
        for (int i = 0; i < steps; i++) {
            m.StepAll();
            visAll.Draw();
            gif.AddFrame(visAll.cells);
        }
        gif.Close();
        System.out.println("Ended "+index);
    }
    public static void main(String[] args) {
        //int[][]vessPops=new int[10][];
        //FileIO vessOut=new FileIO("VesselPops.csv","w");
        //SETUP
        RunModel(90,90,true,0,1000);
        //Util.MultiThread(10,8,(runIndex -> RunModel(80,80,false,runIndex,1000,null)));
    }
}
