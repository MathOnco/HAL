package Framework.GridsAndAgents;

import Framework.Interfaces.OverlapForceResponse2D;
import Framework.Rand;
import Framework.Util;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * Created by bravorr on 6/26/17.
 */
public class SphericalAgent2D<A extends SphericalAgent2D,G extends AgentGrid2D<A>> extends AgentPT2D<G> {
    public double radius;
    public double xVel;
    public double yVel;
    public void Init(double radius){
        this.radius=radius;
        this.xVel=0;
        this.yVel=0;
    }
    public double SumForces(double interactionRad, OverlapForceResponse2D<A> OverlapFun){
        ArrayList<A> scratchAgentList= G.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        G.GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),interactionRad, G.wrapX, G.wrapY);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a, G.wrapX);
                double yComp=Ydisp(a, G.wrapY);
                if(xComp==0&&yComp==0){
                    xComp=Math.random()-0.5;
                    yComp=Math.random()-0.5;
                }
                double dist=Norm(xComp,yComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    if(force>0) {
                        sum += Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }
    public <T extends SphericalAgent2D> double SumForces(double interactionRad, AgentGrid2D<T>otherGrid, OverlapForceResponse2D<T> OverlapFun){
        ArrayList<T> scratchAgentList=otherGrid.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        otherGrid.GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),interactionRad, G.wrapX, G.wrapY);
        for (T a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a, G.wrapX);
                double yComp=Ydisp(a, G.wrapY);
                if(xComp==0&&yComp==0){
                    xComp=Math.random()-0.5;
                    yComp=Math.random()-0.5;
                }
                double dist=Norm(xComp,yComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    if(force>0) {
                        sum += Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    public void ApplyFriction(double frictionConst){
        xVel*=frictionConst;
        yVel*=frictionConst;
    }
    public void CapVelocity(double maxVel){
        double maxVelSq=maxVel*maxVel;
        double normSq= NormSquared(xVel,yVel);
        if(normSq>maxVelSq){
            double convFactor=maxVel/Math.sqrt(normSq);
            xVel*=convFactor;
            yVel*=convFactor;
        }
    }
    public void ForceMove() {
        MoveSafePT(Xpt() + xVel, Ypt() + yVel);
    }
    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn){
        if(rn!=null){
            rn.RandomPointOnCircleEdge(divRadius, scratchCoordArr);
        }
        double normSq= Util.NormSquared(scratchCoordArr[0],scratchCoordArr[1]);
        if(normSq!=divRadius*divRadius){
            double norm=Math.sqrt(normSq);
            scratchCoordArr[0]=scratchCoordArr[0]*divRadius/norm;
            scratchCoordArr[1]=scratchCoordArr[1]*divRadius/norm;
        }
        A child= G.NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Xpt(),Ypt());
        MoveSafePT(Xpt()-scratchCoordArr[0], Ypt()-scratchCoordArr[1]);
        return child;
    }
}
