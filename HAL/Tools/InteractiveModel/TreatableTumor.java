package HAL.Tools.InteractiveModel;

import HAL.Gui.UIGrid;
import HAL.Interfaces.SerializableModel;

/**
 * Created by Rafael on 9/26/2017.
 */
public interface TreatableTumor extends SerializableModel {
    void Draw(UIGrid vis, int drawState);//alphaVis draws over vis if alpha values are set to nonzero
    void InteractiveStep(double[] treatmentVals, int step);//be sure to use the provided random number generator.
    String[] GetTreatmentNames();//returns a list of treatment names
    int[] GetTreatmentColors();//returns the color for each treatment
    int[] GetNumIntensities();//returns how many intensity options the play has to choose between, must be at least 1
    int[] GetPlotColors();//returns the colors of the lines to be plotted
    String[] GetPlotLegendNames();//returns the colors of the lines to be plotted
    double[] GetPlotVals();//returns the values to be plotted in the timeline
}


