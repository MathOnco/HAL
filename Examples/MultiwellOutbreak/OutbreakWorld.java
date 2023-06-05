package Examples.MultiwellOutbreak;

import HAL.GridsAndAgents.AgentGrid2D;
import HAL.Gui.GifMaker;
import HAL.Gui.UIGrid;
import HAL.Gui.UIWindow;
import HAL.Rand;
import HAL.Util;

// Outbreak model inspired by: https://www.meltingasphalt.com/interactive/outbreak/

public class OutbreakWorld extends AgentGrid2D<Person> {

    // infection parameters
    public double TRANSMISSION = 0.05;//Math.pow(0.45,3);
    public int ENCOUNTERS_PER_DAY = 8;
    public int TRAVEL_RADIUS = 25;

    public double QUARANTINE_RATE_SYMPTOMATIC = 0.0;
    public double QUARANTINE_RATE_ASYMPTOMATIC = 0.0;
    public double FATALITY_RATE = 0.01;

    public double HOSPITAL_CAPACITY = 0.005;
    public double FATALITY_MULTIPLIER_DUE_TO_EXCEEDING_CAPACITY = 2.0;

    public int TIME_UNTIL_SYMPTOMATIC = 7;
    public int TIME_UNTIL_RECOVERED = 8;

    static final int TOTAL_TIME = 50;
    static final int DIMENSION = 100; // was 300
    static final int SCALE_FACTOR = 2;
    static final int PAUSE = 10; // set a "pause" between timesteps (milliseconds)
    static final boolean SAVE_GIF = true;
    static final int GIF_DRAW_MODIFIER = 1;

    // used for visualization ( do not change )
    public UIGrid vis;
    public Rand rn = new Rand();
    public int[] hood = Util.RectangleHood(false,TRAVEL_RADIUS,TRAVEL_RADIUS);
//    public GifMaker gifMaker = (SAVE_GIF) ? new GifMaker("HAL/Examples/Outbreak/outbreak_world.gif",100*GIF_DRAW_MODIFIER,true) : null;//used for visualization;
    public int[] counts;
    public int current_time;
    public boolean ABOVE_CAPACITY = false;
    public boolean VISUALIZE_CHART = false;

    /*
        OutbreakWorld CONSTRUCTOR
    */

    public OutbreakWorld(boolean include_chart) {
        super(DIMENSION, DIMENSION, Person.class,false,false);

        VISUALIZE_CHART = include_chart;
        SetupVisualization();

        // setup initial counts vector
        counts = new int[] {0,0,0,0,0};
        current_time = 0;

        // set the full domain to "SUSCEPTIBLE" type
        for (int i = 0; i < length; i++) { NewAgentSQ(i).Init(Person.SUSCEPTIBLE); }

        // set the center to be single person asymptomatic
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
    }

    // constructor without chart (used for MultiWell Class)
    public OutbreakWorld() {
        super(DIMENSION, DIMENSION, Person.class,false,false);

        VISUALIZE_CHART = false;
        SetupVisualization();

        // setup initial counts vector
        counts = new int[] {0,0,0,0,0};
        current_time = 0;

        // set the full domain to "SUSCEPTIBLE" type
        for (int i = 0; i < length; i++) { NewAgentSQ(i).Init(Person.SUSCEPTIBLE); }

        // set the center to be single person asymptomatic
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2+1,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2-1,(this.xDim-1)/2).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2+1).Init(Person.ASYMPTOMATIC_INFECTED);
        GetAgent((this.xDim-1)/2,(this.xDim-1)/2-1).Init(Person.ASYMPTOMATIC_INFECTED);
    }

    public static int GetScaledChartDimension() {
        return (DIMENSION * 3) > TOTAL_TIME ? Math.round((DIMENSION * 3) / TOTAL_TIME) : -Math.round(TOTAL_TIME / (DIMENSION * 3));

    }

    public void SetupVisualization() {
        if (VISUALIZE_CHART) {

            int chart_time_axis_scale = GetScaledChartDimension();

            if (chart_time_axis_scale > 0) {
                vis = new UIGrid(DIMENSION + Math.round(TOTAL_TIME * chart_time_axis_scale), DIMENSION, SCALE_FACTOR);
            } else {
                vis = new UIGrid(DIMENSION + Math.round(TOTAL_TIME / Math.abs(chart_time_axis_scale)), DIMENSION, SCALE_FACTOR);
            }
        } else {
            vis = new UIGrid(DIMENSION, DIMENSION, SCALE_FACTOR);
        }
    }

    /*
        StepCells()
            - loop through each cell at every time step
    */

    public void StepCells(){

        Draw();
        if (VISUALIZE_CHART) { DrawChart();}

//        if ((SAVE_GIF) && (current_time % GIF_DRAW_MODIFIER == 0)) { gifMaker.AddFrame(vis); }

        // check how many infected:
        double infected_percent =  (counts[Person.ASYMPTOMATIC_INFECTED] + counts[Person.SYMPTOMATIC_INFECTED]) /  (double)(DIMENSION*DIMENSION);

        // multiple fatality rate, if above hospital capacity
        double effective_fatality_rate = FATALITY_RATE;
        ABOVE_CAPACITY = false;
        if (infected_percent > HOSPITAL_CAPACITY) {
            effective_fatality_rate = FATALITY_RATE*FATALITY_MULTIPLIER_DUE_TO_EXCEEDING_CAPACITY;
            ABOVE_CAPACITY = true;
        }

        for (Person c : this) {
            if (c.type == Person.SUSCEPTIBLE) {
                // nothing yet
            }

            if ((c.type == Person.ASYMPTOMATIC_INFECTED)) {
                if (!c.quarantined) {
                    c.InfectOthers();
                }
                if (rn.Double() < QUARANTINE_RATE_ASYMPTOMATIC) {
                    c.quarantined = true;
                }
                if (rn.Double() < effective_fatality_rate) {
                    c.future_type = Person.DEAD;
                }
            }

            if (c.type == Person.SYMPTOMATIC_INFECTED) {
                if (!c.quarantined) {
                    c.InfectOthers();
                }
                if (rn.Double() < QUARANTINE_RATE_SYMPTOMATIC) {
                    c.quarantined = true;
                }
                if (rn.Double() < effective_fatality_rate) {
                    c.future_type = Person.DEAD;
                }
            }

            if (c.type == Person.RECOVERED) {
                c.quarantined = false;
            }

            c.IncrementCounters();
        }


        // reset & count all types
        counts = new int[] {0,0,0,0,0};
        for (Person c : this) {
            c.type = c.future_type;

            // also update counts vector here:
            counts[c.type]++;
        }
    }

    // draw the outbreak world (without chart included)
    public void Draw() {

        // draw each person's state, override if they're quarantined
        for (int i = 0; i < xDim*yDim; i++) {
            Person c = this.GetAgent(i);
            if (c!=null) {
                this.vis.SetPix(i,(c.quarantined) ? Util.BLUE :  ReturnColor(c.type));
            }
        }

        // Green boundary indicates if above hospital capacity
        if (ABOVE_CAPACITY) {
            // assumes square:
            for (int i = 0; i < xDim; i++) {
                vis.SetPix(0,i,Util.GREEN);
                vis.SetPix(xDim-1,i,Util.GREEN);
                vis.SetPix(i,0,Util.GREEN);
                vis.SetPix(i,xDim-1,Util.GREEN);
            }
        }

        this.vis.TickPause(PAUSE);
    }

    // draw an makeshift chart into the GIF:
    void DrawChart(){

        int chart_time_axis_scale = GetScaledChartDimension();

        int pixels[] = new int[counts.length];
        for (int type = 0; type < counts.length; type++) {
            pixels[type] = (int) Math.round(DIMENSION * (double) counts[type] / (double) (DIMENSION * DIMENSION));
        }

        // smooth out rounding errors
        int check_sum = 0;
        for (int i = 0; i < pixels.length; i++) { check_sum+= pixels[i]; }

        if (pixels[Person.SUSCEPTIBLE] > 0) {
            pixels[Person.SUSCEPTIBLE] += DIMENSION - check_sum;
        } else {
            pixels[Person.RECOVERED] += DIMENSION - check_sum;
        }

        // colors over time:
        if (current_time > 0) {
            int bottom_ticker = 0;
            for (int type = 0; type < counts.length; type++) {
                int percent = pixels[type];
                for (int percent_point = 0; percent_point < percent; percent_point++) {
                    if ((current_time < TOTAL_TIME) && (bottom_ticker + percent_point < DIMENSION)) {
                        int test = DIMENSION - 1 - (bottom_ticker + percent_point);

                        if (chart_time_axis_scale > 0) {
                            for (int i = 0; i < chart_time_axis_scale; i++) {
                                this.vis.SetPix(current_time * chart_time_axis_scale + i + xDim, test, ReturnColor(type));
                            }
                        } else {
                            this.vis.SetPix(current_time / Math.abs(chart_time_axis_scale) + xDim, test, ReturnColor(type));
                        }
                    }
                }
                bottom_ticker += percent;
            }
        }
    }


    public static int ReturnColor(int type) {
        if ((type == Person.SUSCEPTIBLE)) {
            return Util.RGB256(100,100,100);
        } else if ((type == Person.ASYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 221, 226);
        } else if ((type == Person.SYMPTOMATIC_INFECTED)) {
            return Util.RGB256(255, 68, 68);
        } else if ((type == Person.RECOVERED)) {
            return Util.RGB256(50, 168, 82);
        } else if ((type == Person.DEAD)) {
            return Util.BLACK;
        }
        return Util.BLACK;
    }

    /*
        OutbreakWorld() model
            -
    */

    public static void main(String[]args){

        UIWindow win = new UIWindow("Outbreak",true,null,true);
        OutbreakWorld world = new OutbreakWorld(true);
        win.AddCol(0, world.vis);
        win.RunGui();

        for (world.current_time = 0; world.current_time < TOTAL_TIME; world.current_time++) {

            System.out.println("Time:  " + world.current_time);
            world.StepCells();
        }

//        if (world.SAVE_GIF) { world.gifMaker.Close(); }
    }
}