package Framework.Extensions.MarkModelFast;

import Framework.Rand;

/*TODO:
    1) add hooks for drugs[
        every step [set vessel concs]
        every cell step?
        every cell division?
        every cell death?
        every vessel birth?
        every vessel death?
        for every vessel?
        affect prob death?
        affect prob birth?
    ]
    2) add drugs
    3) integrate drugs with QuackSim ui
*/
public class Testing {

    public static void RunModel(int x, int y, int visScale, int index, int steps) {
        System.out.println("Started "+index);
        Tissue m = new Tissue(x, y,3, true, true, new Rand());
        m.SetupVessels();
        m.SetupTissue(0.8);
        m.MUT_RATE_ACID_RESIST=0.05;
        m.MUT_RATE_GLYC=0.05;
        m.SetupBoundaryConds();
        m.SetupTumor(20,m.GetGlycRate(0),m.GetAcidResistPH(0));
        m.IncTick();


        TestVis visAll=null;
        if(visScale>0){
            visAll=new TestVis(m,visScale);
        }
        for (int i = 0; i < steps; i++) {
            m.StepAll();
            if(visScale>0) {
                visAll.Draw();
            }
        }
    }
    public static void main(String[] args) {
        RunModel(90,90,1,0,1200);
        //Util.MultiThread(10,8,(runIndex -> RunModel(80,80,false,runIndex,1000,null)));
    }
}
