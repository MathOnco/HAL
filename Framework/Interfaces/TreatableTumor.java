package Framework.Interfaces;

import Framework.Gui.GuiGrid;
import Framework.Gui.GuiLabel;
import Framework.Tools.SerializableModel;

/**
 * Created by Rafael on 9/26/2017.
 */
public interface TreatableTumor extends SerializableModel {
    void Draw(GuiGrid vis, GuiGrid alphaVis, int iLastSelected, GuiLabel drawLabel,double[]treatmentVals);//alphaVis draws over vis if alpha values are set to nonzero
    void QuackStep(double[] treatmentVals, int step, int stepMS);//be sure to use the provided random number generator.
    String[] GetTreatmentNames();//returns a list of treatment names
    int[] GetTreatmentColors();//returns the hue values for each treatment in the HSV colormap. for reference:
    int GetNumIntensities();//returns how many intensity options the play has to choose between, must be at least 1
    int VisPixX();//how many pixels tall should the visualization be
    int VisPixY();//how many pixels wide should the visualization be
    int AlphaGridScaleFactor();//how coarse should the alpha grid be compared to the standard visualization
    double GetTox();//returns the cumulative treatment toxicity, compared to max tox in game
    double GetBurden();//returns the tumor burden, compared to max burden in game
    double GetMaxTox();//if the tox goes over this, the player loses
    double GetMaxBurden();//if the burden goes over this, the player loses
//    String[] GetSwitchNames();//gets the names of all switches that the model needs
//    boolean AllowMultiswitch();//does the model allow multiple visualization switches to be set simultaneously?
}

