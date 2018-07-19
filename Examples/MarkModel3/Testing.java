package Examples.MarkModel3;

import Framework.Rand;

public class Testing {
    public static void main(String[] args) {
        Tissue<Cell> t=new Tissue<Cell>(99,99,true,new Rand(),Cell.class,true);
        t.SetupModelDefault(20);
        TestVis vis=new TestVis(t,1,"visualization");
        for (int i = 0; i < 100000; i++) {
            t.StepAll(null);
            vis.Draw(i);
        }
    }
}
