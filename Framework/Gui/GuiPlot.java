package Framework.Gui;

public class GuiPlot extends GuiGrid {
    boolean logX;
    boolean logY;
    int bkColor;
    int fgColor;
    public GuiPlot(int gridW, int gridH, int compX, int compY, boolean active,boolean logX,boolean logY,int bkColor,int fgColor) {
        super(gridW, gridH, 1, compX, compY, active);
        this.logX=logX;
        this.logY=logY;
        this.bkColor=bkColor;
        this.fgColor=fgColor;
    }
}
