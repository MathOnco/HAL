package Examples.MarkModel3;

import Examples.MarkModel3.Drugs.HAP;
import Framework.Gui.GifMaker;
import Framework.Rand;
import Framework.Tools.FileIO;
import Framework.Util;

import static Examples.MarkModel3.Tissue.*;
import static Framework.Util.MultiThread;
import static Framework.Util.RGB;


    /*

     "DONE"
        -created variable for switch probability to indicate average hours off (based off existing code's timestep)
        -created boolean for on/off as attribute of vessel obj.
        -modified CellStep() to check vessels for random switch to off using switch probability
        -created method to switch vessels off --> flips the boolean attribute, and changes the vessel color
        ---
        -created proportion occlusion variable to use in DiffStep and boolean variable attribute for made_in_angio
        -added proportion occlusion variable in color scheme to add gradient for level of occlusion (less occlusion = more red)
        -modified vessel switch method to check current state of boolean attribute, and select appropriate color/proportion of occlusion to use
        -modified check in CellStep() also check whether vessel was created during angio
        -added step in angio code to set m_i_angio attribute to true if it is true
        ---
        -the switch works for vessels in the tumor!!! (i.e. only vessels in the tumor turn red) ***
        -changed color scheme so more red = more occlusion in RBG values
        -changed prop_occ to attribute of vessel (kinda)
        ---
        -fixed prop_occ to fully be an attribute of the vessel, and modified the colors in Vessel_Switch() accordingly
        -partial occlusion (theoretically) is functional with SetVesselConcProp in DiffStep()
        ---
        -angiogenesis problem spotted: healthy tumor cells in places without vessels
        -angiogenesis problem fixed (thanks to Rafael)
        ---
        -created function to check when tumor reaches edge (& Rafael implemented it in the code)
        -set up parameters for running 4 cases all at once (+/- hypoxia, stable/unstable) + labels
        -ran 4 simulations to reproduce Mark's model
        --- much later
        -more edits, added all conditions, adjusted stable vessels, mutation rate, vessel density, angio. func
        -"optimized" mutation rate, glyc rate, and angio rate
        -collect data
        ---DRUGS
        -added code for t implant time
        -added code for t implant by pheno
        -preliminary runs with t implant time: 25, 50, 75, 100 for unstable only (b/c of time constraints)




    */


//Avg time off:  4 = 1/p, p = probability of switching between on and off; these are based on 1 hour timesteps --> scale to match code's timestep
//Avg. vessel Deg = HP/(8*P_degradation*avg cell division) --> solve for this, and use for HP and prob degradation | Avg. cell division: 0.344828
//----------------------------------------------------------------------------------------------------------------------
//green = object variables (attribute)
//yellow = function variables/temporary variables
//todos bright yellow
//orange = function definitions
//purple = function calls

class OCHap extends HAP<OCCell,OCTissue>
{
}

class OCCell extends Cell<OCCell,OCTissue> //custom cell/vessel code here
{


    public boolean occluded;  //decides whether flow of vessel should be on or off
    public boolean m_i_angio;  //keeps track of whether vessel was made during angiogenesis
    public double prop_occ;    //keeps track of proportion occluded; 1 = full occlusion; more red = more occluded



    @Override  //Constructs vessel object
    public OCCell InitVessel()               /*Is this to establish every vessel or just ones at start? (If only ones at start,
                                                 then these can't be turned on or off, so don't need the attribute */
    {
        this.type = Tissue.VESSEL;
        this.vesselHP = G().VESSEL_HP_MAX;
        this.drawColor = Tissue.VESSEL_COLOR;
        this.occluded = false;   //Adds a boolean attribute to each vessel; each vessel keeps track of whether it should be on or off
        this.m_i_angio = false;  //Initializes vessels to not having been made during angiogenesis
        this.prop_occ = 0;  //All vessels start off not occluded; colors occluded vessels in different shades of red --> more red = more occlusion; 1 = fully occlude
        G().vesselList.add(this);
        return this;
    }


    public void Vessel_Switch()
    {

        this.occluded =!this.occluded;   //switch state of vessel as attribute of object

        if (this.occluded)                          //if true, then induce full occlusion
        {
            this.prop_occ = 1;
            this.drawColor =RGB(1*this.prop_occ, 0, 0);
        }
        else                                     //if false, then induce no occlusion
        {
            this.prop_occ = 0;
            this.drawColor = RGB(1, 1, 1);  //white
        }


    }

    @Override
    public void CellStep(double[]intensities)
    {
        //TODO may want to return an int that maps to an event code system for the sake of display
        if (type == DEAD)
        {
            if (G().rn.Double() < G().DISPOSE_PROB_DEAD)
            {
                Dispose();
            }
        }
        else if(type==NECRO)

        {
            if(G().rn.Double()<G().DISPOSE_PROB_NECRO)
            {
                Dispose();
            }
        }

//        else if(type==VESSEL)   //Don't you need the vessels to die to reproduce mark's model? stable = last for ~300 days*
//        {
//            if(G().rn.Double()<DieProbVessel())
//            {
//                Die(false);
//            }
//        }

        else if (type == VESSEL && this.m_i_angio)
        {
            if(G().rn.Double()<G().VESSEL_SWITCH_PROB)
            {
                Vessel_Switch();       //Switch the vessel's state
            }

        }

        else if (type == NORMAL||type==TUMOR)
        {
            if(intensities!=null){
                for (Drug drug : G().onCellStep) {
                    drug.OnCellStep(this,intensities[drug.I()]);
                }
            }
            if(G().rn.Double()<DieProb(intensities)){
                if(availableATPprop<G().DEATH_THRESH_ATP)
                {
                    Die(true);
                }else
                    {
                    Die(false);
                }
                return;//death event
            }
            if (availableATPprop < G().QUIESCENCE_THRESH_ATP)
            {
                return;
            }
            //decrement cell cycle
            //if(DivideProb())
            cycleRemaining -= DivideProb();
            //if (G().rn.Double()< DivideProb()) {
            if (cycleRemaining <= 0) {
                //Cycle Complete -> Attempt Prolif
                AttemptDivide();
            }
        }
    }

}

//---------------------------*****

class OCTissue extends Tissue<OCCell>
{

    public OCTissue(int x, int y, boolean reflectiveBoundary, Rand rn)
    {
        super(x, y, reflectiveBoundary, rn, OCCell.class,false);
    }


    //SWITCH PROBABILITY (make sure this is a double)
    public double VESSEL_SWITCH_PROB = 1.0/16;  // 1/16 = for avg. 4 hours off, based on a two-hour cell timestep
//    public double NORMAL_PHENO_ACID_RESIST = 6.85;
//    public double DEATH_PROB_POOR_COND_BASE = 9999;



    @Override
    public boolean DiffStep(boolean checkSteady,double[]intensities)
    {
        //returns true if steady state conditions are met
        for (OCCell vessel : vesselList)
        {
            int x=vessel.Xsq();
            int y=vessel.Ysq();
            oxygen.SetVesselConcProp(x,y, vessel.prop_occ);   //Adjust proportions to match proportion of occlusion; actually effects occlusion
            glucose.SetVesselConcProp(x,y, vessel.prop_occ);
            acid.SetVesselConcProp(x,y,vessel.prop_occ);
        }
        oxygen.grid.CurrIntoSwap();
        glucose.grid.CurrIntoSwap();
        acid.grid.CurrIntoSwap();
        for (OCCell c : this) //this = m for pheno***
        {
            c.Metabolism(intensities);
        }
        oxygen.grid.SwapFields();
        glucose.grid.SwapFields();
        acid.grid.SwapFields();
        RunDiffusion();
        return checkSteady && IsSteady();
    }

//    @Override
//    public void Angiogenesis(double[]intensities)
//    {
//        //find hypoxic areas
//        int nHypox = 0;
//        int nPossible=oxygen.grid.length;
//        for (int i = 0; i < oxygen.grid.length; i++)
//        {
//            double concO2 = oxygen.grid.Get(i);
//            if (concO2 >= HYPOX_ANGIO_ZONE_MIN && concO2 <= HYPOX_ANGIO_ZONE_MAX)
//            {
//                hypoxicIs[nHypox] = i;
//                nHypox++;
//            }
//        }
//        int nNewVessels=(int)(nHypox*(DIFF_SPACE_SCALE * DIFF_SPACE_SCALE)/HYPOX_ZONE_SIZE+0.5);
//        rn.Shuffle(hypoxicIs, nHypox, nNewVessels);
//        for (int i = 0; i < nNewVessels; i++)
//        {
//            double angioProb=ANGIO_RATE*CELL_TIMESTEP;
//            if(intensities!=null) {
//                for (Drug drug : modVesselDivisionProb) {
//                    angioProb *= drug.VesselDivisionProb(hypoxicIs[i], intensities[drug.I()]);
//                }
//            }
//            if (rn.Double() < angioProb)
//            {
//                //replace cell or empty space at random position in hypoxic zone with new vessel
//                int x=oxygen.grid.ItoX(hypoxicIs[i])* DIFF_SPACE_SCALE +rn.Int(DIFF_SPACE_SCALE);
//                int y=oxygen.grid.ItoY(hypoxicIs[i])* DIFF_SPACE_SCALE +rn.Int(DIFF_SPACE_SCALE);
//                OCCell occupant = GetAgent(x,y);
//                if(occupant!=null&&(occupant.type==NECRO||occupant.type==VESSEL))
//                {
//                    break;
//                }
//                if (occupant == null)
//                {
//                    occupant = NewAgentSQ(x,y);
//                }
//                occupant.InitVessel();
//                occupant.m_i_angio = true;      //If made during angio, mark it as true; (orignally initalized to false)
//
//            }
//        }
//    }
}


//---------------------------------------------------------------------------------------------------------------


public class VesselOcclusion {
    public static void SetupModelDefault(OCTissue m, double tumorRad) {
        m.SetupConstants();
        m.SetupVessels();
        m.SetupTissue(0.8);
        m.SetupBoundaryConds();
        m.SetupTumor(tumorRad, m.GetGlycRate(0), m.GetAcidResistPH(0));
    }
    //NOT USED @NOW----------------------------------------------------------------------------------------------------

    public static void RunModel(int x, int y, int visScale, int index, int steps) {
        System.out.println("Started " + index);
        OCTissue m = new OCTissue(x, y, true, new Rand());
        m.MUT_RATE_ACID_RESIST = 0.3; //***
        m.MUT_RATE_GLYC = 0.3; //**
        // m.NORMAL_PHENO_ACID_RESIST = 7;
        SetupModelDefault(m, 20);
//        GifMaker gif0 = new GifMaker("run" + index + ".gif", 0, true);



        TestVis2 visAll=new TestVis2(m,visScale,"test"+index);
        //TestVis visAll=new TestVis(m,visScale,"test"+index);
        double[]intensities=new double[1];

        for (int i = 0; i < steps; i++)
        {
            m.StepAll(intensities);
            visAll.Draw();
//            gif0.AddFrame(visAll.win);

//            if(visScale>0)
//            {
//                visAll.Draw();
//                if(i%10==0) {
//                    gif0.AddFrame(visAll.win);
//                }
//            }
        }
//        gif0.Close();

    }

    public static void RunModel2(int x, int y, int visScale, int index, int steps, double [] stepsTillTumorEdge,int forLoopIndex) {
        OCTissue m = new OCTissue(x, y, true, new Rand());
        m.NORMAL_PHENO_ACID_RESIST = 7.0; //6.85
        m.DEATH_PROB_POOR_COND_BASE = 0.9;
        m.MUT_RATE_ACID_RESIST = 0.05;//***
        m.MUT_RATE_GLYC = 0.05; //**
        m.ANGIO_RATE = 0.9; //***
        HAP happy=new HAP();
        m.AddDrug(happy);
        m.SetupDrugs();



        String name;  //Avg. cell division: 0.344828
        if (index == 0)
        {
            //NO HYPOXIA, STABLE
            name = "No I-hypoxia, stable";
            m.VESSEL_SWITCH_PROB = 0;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.024167/4.0; //Calculated prob deg for stable
        } else if (index == 1)
        {
            //NO HYPOXIA, UNSTABLE
            name = "No I-hypoxia, unstable";
            m.VESSEL_SWITCH_PROB = 0;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.3625; //Calculated prob deg for unstable
        } else if (index == 2)
        {
            //AVG 4 HRS HYPOXIA, STABLE --> hypoxia probability already there
            name = "Avg. 4 hrs, stable";
            m.VESSEL_DEGRADATION_RATE_BASE = 0.024167/4.0;
        } else if (index == 3)
        {
            //AVG 4 HRS HYPOXIA, UNSTABLE
            name = "Avg. 4 hrs, unstable";
            m.VESSEL_DEGRADATION_RATE_BASE = 0.3625;
        }else if (index == 4)
        {
            //AVG 8 HRS HYPOXIA, STABLE
            name = "Avg. 8 hrs, stable";
            m.VESSEL_SWITCH_PROB = 1.0/64;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.024167/4.0;
        } else if (index == 5)
        {
            //AVG 8 HRS HYPOXIA, UNSTABLE
            name = "Avg. 8 hrs, unstable";
            m.VESSEL_SWITCH_PROB = 1.0/64;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.3625;
        } else if (index == 6)
        {
            //AVG 16 HRS HYPOXIA, STABLE
            name = "Avg. 16 hrs, stable";
            m.VESSEL_SWITCH_PROB = 1.0/256;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.024167/4.0;
        }
        else {
            //AVG 16 HRS HYPOXIA, UNSTABLE
            name = "Avg. 16 hrs, unstable";
            m.VESSEL_SWITCH_PROB = 1.0/256;
            m.VESSEL_DEGRADATION_RATE_BASE = 0.3625;
        }

        double[]intensities=new double[]{0};  //used to be 1
        SetupModelDefault(m, 20);
        //GifMaker gif0 = new GifMaker("R20" + name + " " + forLoopIndex+ ".gif", 0, true);
        TestVis2 visAll = new TestVis2(m, visScale, "test" + index + ": " + name);
        int i;
        for ( i = 0; i < steps; i++)
        {
            m.StepAll(intensities);
//                visAll.Draw();
//                visAll.DrawHAP(happy);
//                gif0.AddFrame(visAll.win);
            if(visScale>0)
            {
                visAll.Draw();
                if(i%5==0)
                {
                    //gif0.AddFrame(visAll.win);
                }
            }
            if (TumorOnEdge(m))
            {
                visAll.win.Close();
                break;
            }

        }
        //visAll.win.Dispose();

        stepsTillTumorEdge[index] = i*(m.CELL_TIMESTEP);
        //gif0.Close();
    }

    //END NOT USED @NOW------------------------------------------------------------------------------------------------

    public static boolean TumorOnEdge(OCTissue m) {
        int x1 = 0;
        int x2 = m.xDim - 1;
        int y1 = 0;
        int y2 = m.yDim - 1;
        for (int i= 0; i < m.xDim; i++)
        {
            if (m.GetAgent(i,y1) != null && m.GetAgent(i,y1).type == TUMOR) //check bottom border
            {
                return true;
            }
            if (m.GetAgent(i, y2) != null && m.GetAgent(i, y2).type == TUMOR) //check top border
            {
                return true;
            }
        }
        for (int j = 1; j < m.yDim - 1; j++) {
            if (m.GetAgent(x1, j) != null && m.GetAgent(x1, j).type == TUMOR) //left border, excluding corners
            {
                return true;
            }
            if (m.GetAgent(x2, j) != null && m.GetAgent(x2, j).type == TUMOR) //right border, excluding corners
            {
                return true;
            }
        }
        return false;
    }
    //Copy of RunModel3 used to collect data without drugs  ***********************************************************
    public static void RunModel4(double[][]inputsOutputs,int index,int visScale){ //index is the number of runs
        int steps=99999;
        int x=120;
        int y=120;
        double[]myArgs=inputsOutputs[index];

        OCTissue m = new OCTissue(x, y, true, new Rand());
        m.VESSEL_SWITCH_PROB=myArgs[0];
        m.VESSEL_DEGRADATION_RATE_BASE=myArgs[1];
        m.NORMAL_PHENO_ACID_RESIST = 7.0; //6.85
        m.DEATH_PROB_POOR_COND_BASE = 0.9;
        m.MUT_RATE_ACID_RESIST = 0.05;//***
        m.MUT_RATE_GLYC = 0.05; //**
        m.ANGIO_RATE = 0.9; //***
        SetupModelDefault(m, 20);
        HAP happy=new HAP();
        m.AddDrug(happy);
        m.SetupDrugs();

        GifMaker gif0 = new GifMaker("Run" + index + " VSP" + m.VESSEL_SWITCH_PROB + " VDR" + m.VESSEL_DEGRADATION_RATE_BASE + ".gif", 0, true);
        TestVis2 visAll = new TestVis2(m, visScale, "test:" + index + "," + "VSP:"+m.VESSEL_SWITCH_PROB+",VDR:"+m.VESSEL_DEGRADATION_RATE_BASE);
        int i=0;
        double[]intensities=new double[]{0};
        boolean first100 = true;
        for (i = 0; i < steps; i++)
        {
            //Drug by number of cells in specific quadrant **************
            int numCellsInPink = 0;
            for (OCCell c : m)
            {
                if (c.type == TUMOR && c.GetAcidResistPheno() >=0.67)
                {
                    numCellsInPink++;
                }

            }
            if (numCellsInPink >= 150)
            {
                intensities[0] =1;
                if (first100)
                {
                    inputsOutputs[index][2] = i * (m.CELL_TIMESTEP);
                    first100 = false;
                }
            }
            //RUNS EVERY STEP, use i*CELL_TIMESTEP TO GET DAY
            if(i*m.CELL_TIMESTEP>10){
                intensities[0]=0;   //change intensity = 1 for drug
            }
            m.StepAll(intensities);
//                visAll.Draw();
//                visAll.DrawHAP(happy);
//                gif0.AddFrame(visAll.win);
            if(visScale>0)
            {
                //visAll.DrawHAP(happy);
                visAll.Draw();
                if(i%5==0)
                {
                    gif0.AddFrame(visAll.win);
                }
            }
            if (TumorOnEdge(m))
            {
                //visAll.win.Dispose();
                break;
            }


        }
        //visAll.win.Dispose();
        gif0.Close();
        inputsOutputs[index][3]=i*(m.CELL_TIMESTEP);

    }
    //*****************************************************************************************************************


    public static void RunModel3(double[][]inputsOutputs,int index,int visScale){ //index is the number of runs
        int steps=99999;
        int x=120;
        int y=120;
        double[]myArgs=inputsOutputs[index];

        OCTissue m = new OCTissue(x, y, true, new Rand());
        m.VESSEL_SWITCH_PROB=myArgs[0];
        m.VESSEL_DEGRADATION_RATE_BASE=myArgs[1];
        m.NORMAL_PHENO_ACID_RESIST = 7.0; //6.85
        m.DEATH_PROB_POOR_COND_BASE = 0.9;
        m.MUT_RATE_ACID_RESIST = 0.05;//***
        m.MUT_RATE_GLYC = 0.05; //**
        m.ANGIO_RATE = 0.9; //***
        SetupModelDefault(m, 20);
        //HAP happy=new HAP();
        //m.AddDrug(happy);
        //m.SetupDrugs();

        GifMaker gif0 = new GifMaker("Run" + index + " VSP" + m.VESSEL_SWITCH_PROB + " VDR" + m.VESSEL_DEGRADATION_RATE_BASE + ".gif", 0, true);
        TestVis2 visAll = new TestVis2(m, visScale, "test:" + index + "," + "VSP:"+m.VESSEL_SWITCH_PROB+",VDR:"+m.VESSEL_DEGRADATION_RATE_BASE);
        int i=0;
        double[]intensities=new double[]{0};
        boolean first100 = true;
        for (i = 0; i < steps; i++)
        {
            //Drug by number of cells in specific quadrant **************
            int numCellsInPink = 0;
            for (OCCell c : m)
            {
                if (c.type == TUMOR && c.GetAcidResistPheno() >=0.67)
                {
                        numCellsInPink++;
                }

            }
            if (numCellsInPink >= 150)
            {
                intensities[0] =1;
                if (first100)
                {
                    inputsOutputs[index][2] = i * (m.CELL_TIMESTEP);
                    first100 = false;
                }
            }

            //Drug at specific implant time ********************************
//            if(i*m.CELL_TIMESTEP>50)
//            {
//                intensities[0]=1;   //change intensity = 1 for drug
//            }
//
            m.StepAll(intensities);
            if(visScale>0)
            {
                visAll.Draw();
                //visAll.Draw();
                if(i%5==0)
                {
                    gif0.AddFrame(visAll.win);
                }
            }
            if (TumorOnEdge(m))
            {
                //visAll.win.Dispose();
                break;
            }


        }

        //visAll.win.Dispose();
        gif0.Close();
        inputsOutputs[index][3]=i*(m.CELL_TIMESTEP);


    }


        public static double[][]GenInputsOutputs(int numRuns) {
        double[][]out=new double[numRuns][];
            double VESSEL_SWITCH_PROB;
            double VESSEL_DEGRADATION_RATE_BASE;
            for (int indexPre = 0; indexPre < numRuns; indexPre++) {
                int index=indexPre%8;
                //NORMAL RUNS
//                if (index == 0) {
//                    //NO HYPOXIA, STABLE
//                    VESSEL_SWITCH_PROB = 0;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.024167 / 4.0; //Calculated prob deg for stable
//                } else if (index == 1) {
//                    //NO HYPOXIA, UNSTABLE
//                    VESSEL_SWITCH_PROB = 0;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.3625; //Calculated prob deg for unstable
//                } else if (index == 2) {
//                    //AVG 4 HRS HYPOXIA, STABLE --> hypoxia probability already there
//                    VESSEL_SWITCH_PROB = 1.0/16;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.024167 / 4.0;
//                } else if (index == 3) {
//                    //AVG 4 HRS HYPOXIA, UNSTABLE
//                    VESSEL_SWITCH_PROB = 1.0/16;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.3625;
//                } else if (index == 4) {
//                    //AVG 8 HRS HYPOXIA, STABLE
//                    VESSEL_SWITCH_PROB = 1.0 / 64;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.024167 / 4.0;
//                } else if (index == 5) {
//                    //AVG 8 HRS HYPOXIA, UNSTABLE
//                    VESSEL_SWITCH_PROB = 1.0 / 64;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.3625;
//                } else if (index == 6) {
//                    //AVG 16 HRS HYPOXIA, STABLE
//                    VESSEL_SWITCH_PROB = 1.0 / 256;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.024167 / 4.0;
//                } else {
//                    //AVG 16 HRS HYPOXIA, UNSTABLE
//                    VESSEL_SWITCH_PROB = 1.0 / 256;
//                    VESSEL_DEGRADATION_RATE_BASE = 0.3625;
//                }
                //DRUG RUNS
                if (index == 0) {
                    //NO HYPOXIA, UNSTABLE
                    VESSEL_SWITCH_PROB = 0;
                    VESSEL_DEGRADATION_RATE_BASE = 0.3625; //0.024167 / 4.0;
                } else if (index == 1) {
                    //4 HRS HYPOXIA, UNSTABLE
                    VESSEL_SWITCH_PROB = 1.0/16;
                    VESSEL_DEGRADATION_RATE_BASE = 0.3625; //0.024167 / 4.0;
                } else if (index == 2) {
                    //AVG 8 HRS HYPOXIA, UNSTABLE
                    VESSEL_SWITCH_PROB = 1.0 / 64;
                    VESSEL_DEGRADATION_RATE_BASE = 0.3625;  //0.024167 / 4.0;
                } else {
                    //AVG 16 HRS HYPOXIA, UNSTABLE
                    VESSEL_SWITCH_PROB = 1.0 / 256;
                    VESSEL_DEGRADATION_RATE_BASE = 0.3625;  //0.024167 / 4.0;
                }
                out[indexPre]=new double[]{VESSEL_SWITCH_PROB,VESSEL_DEGRADATION_RATE_BASE,0, 0};//[in,in,out] = [vessel switch prob, vessel deg, time in days]
                System.out.println(Util.ArrToString(out[indexPre],","));
            }

            return out;
        }


        public static void main (String[]args)
        {
            int nRuns=4;  //1 run = 1 simulation --> use multiples of 8
            double[][]io=GenInputsOutputs(nRuns);
            MultiThread(nRuns,6,(runIndex) -> {
                RunModel3(io,runIndex,2);
            });
//            FileIO runRes=new FileIO("runRes.csv","a");
            FileIO runRes=new FileIO("t_pheno100.csv","a");
            for (double[] doubles : io) {
                runRes.WriteDelimit(doubles,",");
                runRes.Write("\n");
            }
            runRes.Close();


            //RunModel(90,90,3,0,999999);


//            int numRun = 1;
//            for (int i = 0; i < numRun; i++)
//            {
//
//                double[] stepsTillTumorEdge = new double[10];
//                MultiThread(8, 8, (runIndex) -> {
//                    System.out.println("started Run " + runIndex);
//                    RunModel2(120, 120, 2, runIndex, 99999, stepsTillTumorEdge,i);
//                    System.out.println("ended Run " + runIndex);
//                });
//
//                String file_name = "invasionRun_" + i +  ".csv";
//                FileIO out_file = new FileIO(file_name, "a");
//                out_file.Write(Util.ArrToString(stepsTillTumorEdge, ",") + "\n");
//                out_file.Close();
//
//                //System.out.println(Util.ArrToString(stepsTillTumorEdge, ","));
//            }
        }

}

