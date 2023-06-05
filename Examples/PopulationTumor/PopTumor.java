package Examples.PopulationTumor;

import HAL.GridsAndAgents.PopulationGrid2D;
import HAL.Gui.GridWindow;
import HAL.Interfaces.DoubleToInt;
import HAL.Rand;
import HAL.Util;
import HAL.Tools.MultinomialCalc;

import static HAL.Util.RGB;

public class PopTumor {
    public final static int TUMOR = 0, M1 = 1, M2 = 2, MBOTH = 3, N_TYPES = 4;
    int sideLen;
    double divRate;
    double divRateMut;
    double deathRate;
    double migrationRate;
    double divRateDoubleMut;
    int latticeCap;
    double mutProb1;
    double mutProb2;
    double fuseProb;
    int drawPauseMS;
    PopulationGrid2D[] cells = new PopulationGrid2D[N_TYPES];
    int[] sums;
    GridWindow win;
    Rand rng = new Rand();
    MultinomialCalc cellMn = new MultinomialCalc(rng);
    MultinomialCalc divMn = new MultinomialCalc(rng);
    DoubleToInt[] colorFns = new DoubleToInt[]{
            (v) -> {
                return RGB(v, v, v);
            },
            Util::HeatMapRGB,
            Util::HeatMapGBR,
            Util::HeatMapBRG,
            (v) -> {
                return RGB(v, v, v);
            },
    };

    public PopTumor(int sideLen, double divRate, double deathRate, double migrationRate, int latticeCap, double mutProb1, double mutProb2, double fuseProb, int drawScale, int drawPauseMS) {
        this.sideLen = sideLen;
        this.divRate = divRate;
        this.divRateMut = divRate + 0.01;
        this.divRateDoubleMut = divRate + 0.02;
        this.deathRate = deathRate;
        this.migrationRate = migrationRate;
        this.latticeCap = latticeCap;
        this.mutProb1 = mutProb1;
        this.mutProb2 = mutProb2;
        this.fuseProb = fuseProb;
        this.drawPauseMS = drawPauseMS;
        //this.win=new GridWindow((sideLen+1)*(N_TYPES+1),sideLen,3);
        this.win = new GridWindow((sideLen + 1) * 4, sideLen, 2);
        win.Clear(Util.WHITE);

        for (int i = 0; i < cells.length; i++) {
            cells[i] = new PopulationGrid2D(sideLen, sideLen, false, false);
            sums = new int[sideLen * sideLen];
        }
    }

    public void Draw(DoubleToInt[] ColorFns) {
        PopulationGrid2D drawMe = null;
        for (int i = 0; i < N_TYPES; i++) {
            drawMe = cells[i];
            for (int x = 0; x < drawMe.xDim; x++) {
                for (int y = 0; y < drawMe.yDim; y++) {
                    win.SetPix(x + i * (drawMe.xDim + 1), y, ColorFns[i].DoubleToInt(cells[i].Get(x, y) * 1.0 / latticeCap));
                }
            }
        }
        for (int x = 0; x < drawMe.xDim; x++) {
            for (int y = 0; y < drawMe.yDim; y++) {
                win.SetPix(x + N_TYPES * (drawMe.xDim + 1), y, ColorFns[N_TYPES].DoubleToInt(sums[drawMe.I(x, y)] * 1.0 / latticeCap));
            }
        }
    }

    public void Draw2(DoubleToInt[] ColorFns, int idraw) {
        for (int x = 0; x < cells[TUMOR].xDim; x++) {
            for (int y = 0; y < cells[TUMOR].yDim; y++) {
                double start = cells[TUMOR].Get(x, y) * 1.0 / latticeCap;
                double r = start, g = start, b = start;
                r += cells[M1].Get(x, y) * 1.0 / latticeCap;
                g += cells[M2].Get(x, y) * 1.0 / latticeCap;
                b += cells[MBOTH].Get(x, y) * 1.0 / latticeCap;
                win.SetPix(x + (cells[TUMOR].xDim + 1)*idraw, y, Util.RGB(r, g, b));
            }
        }
    }

    public void Init(int initPop) {
        cells[TUMOR].Add(cells[TUMOR].xDim / 2, cells[TUMOR].yDim / 2, initPop);
        cells[TUMOR].Update();
    }

    public void Step(int idraw) {
        for (int j = 0; j < N_TYPES; j++) {
            int type = j;
            PopulationGrid2D grid = cells[type];
            grid.ApplyOccupied((i, ct) -> {
                cellMn.Setup(ct);
                int ctDiv = 0;
                if (type == M1 || type == M2) {
                    ctDiv = cellMn.Sample(divRateMut * (1.0 - sums[i] * 1.0 / latticeCap));
                } else if (type == MBOTH) {
                    ctDiv = cellMn.Sample(divRateDoubleMut * (1.0 - sums[i] * 1.0 / latticeCap));
                } else {
                    ctDiv = cellMn.Sample(divRate * (1.0 - sums[i] * 1.0 / latticeCap));
                }
                int ctDie = cellMn.Sample(deathRate);
                int ctM1mut = 0;
                int ctM2mut = 0;
                int ctBothmut = 0;
                if (type == TUMOR) {
                    cellMn.Setup(ctDiv);
                    ctM1mut = cellMn.Sample(mutProb1);
                    ctM2mut = cellMn.Sample(mutProb2);
                } else if (type == M1) {
                    ctBothmut = rng.Binomial(ctDiv, mutProb2);
                } else if (type == M2) {
                    ctBothmut = rng.Binomial(ctDiv, mutProb1);
                }
                grid.Add(i, ctDiv - (ctDie + ctM1mut + ctM2mut + ctBothmut));
                if (ctM1mut > 0) {
                    cells[M1].Add(i, ctM1mut);
                }
                if (ctM2mut > 0) {
                    cells[M2].Add(i, ctM2mut);
                }
                if (ctBothmut > 0) {
                    cells[MBOTH].Add(i, ctBothmut);
                }
            });
        }
        for (int i = 0; i < N_TYPES; i++) {
            cells[i].Update();
        }
        for (int i = 0; i < N_TYPES; i++) {
            cells[i].Diffusion(migrationRate, cellMn);
            cells[i].Update();
        }
        cells[0].CopyTo(sums);
        for (int i = 1; i < N_TYPES; i++) {
            cells[i].AddTo(sums);
        }

        if (drawPauseMS >= 0) {
            if(idraw<4)
            Draw2(colorFns, idraw);
        }
    }

    public static void main(String[] args) {
        PopTumor ft = new PopTumor(150, 0.010, 0.001, 0.008, 100000, 2e-6, 2e-6, 0, 2, 0);
        ft.Init(1000);
        int idraw = -1;
        for (int i = 0; i < 4001; i++) {
            if (i % 1000 == 0) {
                idraw++;
                int pop=0;
                for (PopulationGrid2D g : ft.cells) {
                    pop+=g.Pop();
                }
                System.out.println(pop);
            }
            ft.Step(idraw);
        }
        ft.win.ToPNG("PopulationGrid.png");
        ft.win.Close();
    }
}
