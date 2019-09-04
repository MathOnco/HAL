package Examples._5Example3D;
import HAL.GridsAndAgents.*;
import HAL.Gui.OpenGL3DWindow;
import HAL.Interfaces.DoubleToInt;
import HAL.Rand;
import HAL.Util;

import java.util.LinkedList;

import static Examples._5Example3D.Example3D.*;
import static HAL.Util.*;

class ExCell3D extends AgentSQ3D<Example3D>{
    int type;
    void InitVessel(){
        type= VESSEL;
        G.vessels.add(this);
    }
    void InitTumor(){
        type= TUMOR;
    }
    boolean Metastasis(){
        return G.oxygen.Get(Isq())> G.METASTASIS_CONC&& G.rn.Double()< G.METASTASIS_PROB;
    }
    void Metabolism(){
        switch (type){
            case VESSEL:
                G.oxygen.Set(Isq(), G.VESSEL_CONC); break; //vessel source
            case TUMOR:
                G.oxygen.Mul(Isq(), G.TUMOR_METABOLISM_RATE);
                break; //instead of simple multiplication, Util.MichaelisMenten may be more appropriate
        }
    }
    boolean Death(){
        double resVal= G.oxygen.Get(Isq());
        return resVal< G.DEATH_CONC && G.rn.Double()<(1.0-resVal/ G.DEATH_CONC);
    }
    boolean Divide(){
        return G.rn.Double()< G.oxygen.Get(Isq());
    }
    void CellStep(){
        if(type== TUMOR){
            G.countTumor++;
            if(Death()){
                Dispose();
                return;
            }
            if(Divide()){
                int nDivOpts=MapEmptyHood(G.vnHood);//get indices of empty locations in 3D Von Neumann neighborhood around cell
                if(nDivOpts>1){
                    G.NewAgentSQ(G.vnHood[G.rn.Int(nDivOpts)]).InitTumor();
                }
            }
            if(Metastasis()){//choose random vessel location to metastasize to
                Dispose();//kill cell that has entered the vessel
                int whichVessel= G.rn.Int(G.vessels.size());//get a random vessel position
                ExCell3D vessel= G.vessels.get(whichVessel);
                int nMetOpts= G.MapEmptyHood(G.vnHood,vessel.Xsq(),vessel.Ysq(),vessel.Zsq());//get any open positions around a particular vessel location
                if(nMetOpts>1){
                    G.NewAgentSQ(G.vnHood[G.rn.Int(nMetOpts)]).InitTumor();//create and initialize a new cell to model successful metastasis
                }
            }
        }
    }
}
public class Example3D extends AgentGrid3D<ExCell3D> {
    //vessels: cells eat off them, and can go metastatic
    int countTumor;
    final static int BACKGROUND_COLOR =RGB256(38,1,5), VESSEL_COLOR =RGB256(255,78,68);
    final static int VESSEL=0,TUMOR=1;
    double DIFF_RATE=0.5/6;//maximum stable diffusion rate
    double TUMOR_METABOLISM_RATE =-0.04;
    double NORMAL_METABOLISM_RATE =-0.005;
    double VESSEL_CONC=1;
    double DEATH_CONC=0.01;
    double METASTASIS_PROB=0.00001;
    double METASTASIS_CONC=0.3;
    int[]vnHood=VonNeumannHood3D(false);//3D von neuman neighborhood is of the form [x1,y1,z1,x2,y2,z2...]
    int[]vnHood2D=Util.GenHood3D(new int[]{
            1,0,0,
            -1,0,0,
            0,0,1,
            0,0,-1,
    });//3D von neuman neighborhood is of the form [x1,y1,z1,x2,y2,z2...]
    PDEGrid3D oxygen;
    Rand rn=new Rand();
    LinkedList<ExCell3D> vessels=new LinkedList<>();//used to make metastasis more efficient (and as an example)
    public Example3D(int x, int y, int z) {
        super(x, y, z, ExCell3D.class);
        oxygen =new PDEGrid3D(x,y,z);//pde grid used for diffusion of oxygen
    }
    public void DiffStep(){
        for (ExCell3D cellOrVessel : this) {
            cellOrVessel.Metabolism();
        }
        oxygen.MulAll(NORMAL_METABOLISM_RATE);
        oxygen.Diffusion(DIFF_RATE);
        oxygen.Update();
    }
    public void StepAll(){
        countTumor=0;
        for (ExCell3D cell : this) {
            cell.CellStep();
        }
        if(countTumor==0){//ensure that the model is seeded
            int placeLoc=rn.Int(length);
            if(GetAgent(placeLoc)==null){
                NewAgentSQ(placeLoc).InitTumor();
            }
        }
        DiffStep();
    }
    public int GenVessels(double vesselSpacingMin,double migProb){
        //create a Grid to store the locations that are too close for placing another vessel
        Grid2Ddouble openSpots=new Grid2Ddouble(xDim,zDim);
        //create a neighborhood that defines all indices that are too close
        int[]vesselSpacingHood=CircleHood(false,vesselSpacingMin);
        int[]indicesToTry=GenIndicesArray(openSpots.length);
        rn.Shuffle(indicesToTry);
        int vesselCt=0;
        for (int i : indicesToTry) {
            if(openSpots.Get(i)==0){
                int x=openSpots.ItoX(i);
                int y=openSpots.ItoY(i);
                GenVessel(x,y,migProb);
                vesselCt++;
                int nSpots=openSpots.MapHood(vesselSpacingHood,x,y);
                for (int j = 0; j < nSpots; j++) {
                    //mark spot as too close for another vessel
                    openSpots.Set(vesselSpacingHood[j],-1);
                }
            }
        }
        return vesselCt;
    }
    public void DrawCells(OpenGL3DWindow vis, DoubleToInt DrawConcs){
        vis.ClearBox(BACKGROUND_COLOR,RGB(1,0,0));//used to clear gui
        for (ExCell3D cellOrVessel : this) {
            switch (cellOrVessel.type){
                case VESSEL: vis.Circle(cellOrVessel.Xpt(),cellOrVessel.Ypt(),cellOrVessel.Zpt(),1, VESSEL_COLOR);break;
                case TUMOR: vis.Circle(cellOrVessel.Xpt(),cellOrVessel.Ypt(),cellOrVessel.Zpt(),0.3,HeatMapBRG(Math.pow(oxygen.Get(cellOrVessel.Isq()),0.5)*0.8+0.2));
            }
        }
        if(DrawConcs!=null){
            for (int x = 0; x < oxygen.xDim; x++) {
                for (int z = 0; z < oxygen.zDim; z++) {
                    double oxygenSum=0;
                    //add column to avgConcs
                    for (int y = 0; y < oxygen.yDim; y++) {
                        oxygenSum+=oxygen.Get(x,y,z);
                    }
                    oxygenSum/=oxygen.yDim;
                    vis.SetPixXZ(x,z,DrawConcs.DoubleToInt(oxygenSum));
                }
            }
        }
        vis.Update();
    }
    public void DrawConcs(OpenGL3DWindow vis){

    }
    public void GenVessel(int x,int z,double migProb) {
        for (int y = 0; y < yDim; y++) {
            //clear out any agents that are in the path of the vessel
            if (rn.Double() < migProb) {
                int openCt = MapHood(vnHood2D, x, y, z);
                int i = vnHood2D[rn.Int(openCt)];
                x=ItoX(i);
                z=ItoZ(i);
            }
            ExCell3D occupant = GetAgent(x, y, z);
            if (occupant != null) {
                occupant.Dispose();
            }
            NewAgentSQ(x, y, z).InitVessel();
        }
    }
    public void GenCells(int initPopSize){
        int[]indicesToTry=GenIndicesArray(length);
        rn.Shuffle(indicesToTry);
        int nCreated=0;
        for (int i = 0; i < length; i++) {
            //check if position is empty before dropping cell
            if(GetAgent(indicesToTry[i])==null){
                NewAgentSQ(indicesToTry[i]).InitTumor();
                nCreated++;
            }
            if(nCreated==initPopSize){
                break;
            }
        }
    }
    public static void main(String[] args) {
        int x=80,y=80,z=20;
        Example3D ex=new Example3D(x,z,y);
        ex.GenVessels(15,0.8);
        //Diffuse to steady state
        for (int i = 0; i < 100; i++) {
            ex.DiffStep();
        }
        //GridWindow visResource=new GridWindow(x,y,5);
        OpenGL3DWindow vis=new OpenGL3DWindow("TumorVis", 1000,1000,x,z,y);
        while (!vis.IsClosed()){
            ex.StepAll();
            ex.DrawCells(vis, Util::HeatMapRGB);
            //visResource.DrawPDEGridXZ(ex.oxygen, (val)->HeatMapBRG(Math.pow(val,0.5)));
            ex.CleanAgents();//Equivalent to calling CleanAgents, ShuffleAgents, and IncTick grid functions
            ex.ShuffleAgents(ex.rn);//Equivalent to calling CleanAgents, ShuffleAgents, and IncTick grid functions
            vis.TickPause(100);
        }
        vis.Close();
        //visResource.Close();
    }
}