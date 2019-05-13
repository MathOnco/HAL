package Testing;

        import Framework.GridsAndAgents.PDEGrid2D;
        import Framework.Gui.GridWindow;
        import Framework.Util;

public class UnitsExample {
    final static double SPACE_STEP=10;//um
    final static double TIME_STEP=0.01;//sec

    final static double SPACE_LIMIT =100;//um
    final static double TIME_LIMIT =1;//sec

    final static double DIFF_RATE =6e2;//diffusion rate of glucose in water, um^2/sec
    final static double DIFF_RATE_CONST = DIFF_RATE *TIME_STEP/(SPACE_STEP*SPACE_STEP);//non-dimensionalized diffusion const

    public static void main(String[] args) {
        PDEGrid2D example=new PDEGrid2D((int)(SPACE_LIMIT /SPACE_STEP),(int)(SPACE_LIMIT /SPACE_STEP));
        GridWindow win=new GridWindow(example.xDim,example.yDim,500/example.xDim);

        for(int i=0;i<TIME_LIMIT/TIME_STEP;i++){
            example.Set(0,0,1);
            example.Diffusion(DIFF_RATE_CONST);
            example.Update();
            win.DrawPDEGrid(example, Util::HeatMapGBR);
            win.TickPause(10);
        }
    }
}
