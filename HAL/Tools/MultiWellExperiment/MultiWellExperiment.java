package HAL.Tools.MultiWellExperiment;

import HAL.Gui.GifMaker;
import HAL.Gui.GridWindow;
import HAL.Util;

/**
 * MultiWellExperiments allow the user to visualize many models in a single GridWindow, and can be easily multithreaded for faster execution
 * @param <T> the type of model class that will run as a single "well"
 */
public class MultiWellExperiment <T> {
    public final int visXdim;
    public final int visYdim;
    public final int wellsX;
    public final int wellsY;
    int borderColor;
    GridWindow win;
    public T[] wells;
    DrawWell<T> Drawer;
    StepWell<T> Stepper;
    int[] wellStarts;
    int nProcs;


    /**
     * generates a MultiWellExperiment. numWellsX, numWellsY define the number of well (model) rows, models[] is an
     * array of the starting conditions of the models. visXdim, visYdim, scaleFactor define the x pixels, y pixels, and
     * scaling of the visualization of each mdoel. borderColor defines the color of the separator between models, StepFn
     * is a function argument. it takes a model, a well index, and a timestep as argument, and should update the model
     * argument for one timestep. ColorFn is a function argument that takes a model, x, and y, and is used to set one
     * pixel of the visualization.
     */
    public MultiWellExperiment(int numWellsX, int numWellsY, T[] models, int visXdim, int visYdim, int scaleFactor, int borderColor, StepWell<T> StepFn, DrawWell<T> ColorFn) {
        this.visYdim = visYdim;
        this.visXdim = visXdim;
        this.wellsX = numWellsX;
        this.wellsY = numWellsY;
        this.borderColor = borderColor;
        this.wells = models;
        this.Drawer = ColorFn;
        this.Stepper = StepFn;
        if (models.length > numWellsX * numWellsY) {
            throw new IllegalArgumentException("model models passed than can be displayed, max: " + numWellsX * numWellsY + " passed: " + models.length);
        }
        SetWellStarts();
        win = new GridWindow("MultiWell", visXdim * numWellsX + numWellsX - 1, visYdim * numWellsY + numWellsY - 1, scaleFactor);
        win.Clear(borderColor);
    }

    /**
     * loads a new set of models into the MultiWellExperiment
     */
    public void LoadWells(T[] models) {
        wells = models;
        if (models.length > wellsX * wellsY) {
            throw new IllegalArgumentException("model models passed than can be displayed, max: " + wellsX * wellsY + " passed: " + models.length);
        }
        SetWellStarts();
    }

    /**
     * runs a single multithreaded step
     */
    public void StepMultiThread() {
        Util.MultiThread(nProcs, nProcs, (iThread) -> {
            int start = wellStarts[iThread];
            int end = iThread < wellStarts.length - 1 ? wellStarts[iThread + 1] : wells.length;
            for (int i = start; i < end; i++) {
                StepWell(i);
                DrawWell(i);
            }
        });
    }

    /**
     * runs a single timestep
     */
    public void Step() {
        for (int j = 0; j < wells.length; j++) {
            StepWell(j);
            DrawWell(j);
        }
    }

    /**
     * runs a multiwell experiment for a numTicks duration. if the multiThread boolean is set to true, the model
     * execution will be multithreaded.
     */
    public void Run(int numTicks, boolean multiThread, int tickPause) {
        for (int i = 0; i < numTicks; i++) {
            win.TickPause(tickPause);
            if (multiThread) {
                StepMultiThread();
            } else {
                Step();
            }
        }
        win.Close();
    }

    /**
     * runs a multiwell experiment for a numTicks duration. if the multiThread boolean is set to true, the model
     * execution will be multithreaded. saves every recordPeriod fames to a gif.
     */
    public void RunGIF(int numTicks, String outFileName, int recordPeriod, boolean multiThread) {
        GifMaker gif = new GifMaker(outFileName, 0, true);
        for (int i = 0; i < numTicks; i++) {
            if (multiThread) {
                StepMultiThread();
            } else {
                Step();
            }
            if (i % recordPeriod == 0) {
                gif.AddFrame(win);
            }
        }
        gif.Close();
        win.Close();
    }

    void SetWellStarts() {
        nProcs = Runtime.getRuntime().availableProcessors();
        int perWell = wells.length / nProcs;
        int extras = wells.length - perWell * nProcs;
        wellStarts = new int[nProcs];
        for (int i = 1; i < wellStarts.length; i++) {
            if (extras > 0) {
                wellStarts[i] = wellStarts[i - 1] + perWell + 1;
                extras--;
            } else {
                wellStarts[i] = wellStarts[i - 1] + perWell;
            }
        }
    }

    void StepWell(int iWell) {
        Stepper.Step(wells[iWell], iWell);
    }

    void DrawWell(int i) {
        int xStart = (i / wellsY) * (visXdim + 1);
        int yStart = (i % wellsY) * (visYdim + 1);
        win.SetRect(xStart, yStart, visXdim, visYdim, (x, y) -> Drawer.GetPixColor(wells[i], x, y));

    }


}
