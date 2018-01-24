package Framework.Extensions;

import Framework.GridsAndAgents.AgentPT2D;
import Framework.GridsAndAgents.AgentGrid2D;
import Framework.Interfaces.OverlapForceResponse;
import Framework.Interfaces.OverlapNeighborForceResponse;
import Framework.Tools.Gaussian;
import Framework.Rand;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * Created by bravorr on 6/26/17.
 */
public class SphericalAgent2D<A extends SphericalAgent2D,G extends AgentGrid2D<A>> extends AgentPT2D<G> {
    public double radius;
    public double xVel;
    public double yVel;
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapForceResponse OverlapFun, boolean wrapX, boolean wrapY){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),interactionRad,wrapX,wrapY);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,wrapX);
                double yComp=Ydisp(a,wrapY);
                if(xComp==0&&yComp==0){
                    xComp=Math.random()-0.5;
                    yComp=Math.random()-0.5;
                }
                double dist=Norm(xComp,yComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist);
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
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapNeighborForceResponse OverlapFun){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),interactionRad,G().wrapX,G().wrapY);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
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
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapNeighborForceResponse OverlapFun, boolean wrapX, boolean wrapY){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),interactionRad,wrapX,wrapY);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,wrapX);
                double yComp=Ydisp(a,wrapY);
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
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapForceResponse OverlapFun){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),interactionRad,G().wrapX,G().wrapY);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
                if(xComp==0&&yComp==0){
                    xComp=Math.random()-0.5;
                    yComp=Math.random()-0.5;
                }
                double dist=Norm(xComp,yComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist);
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

    public void ForceMove(boolean wrapX,boolean wrapY){
        MoveSafePT(Xpt()+xVel,Ypt()+yVel,wrapX,wrapY);
    }
    public void ApplyFriction(double frictionConst){
        xVel*=frictionConst;
        yVel*=frictionConst;
    }
    public void CapVelocity(double maxVel){
        double maxVelSq=maxVel*maxVel;
        double normSq=NormSq(xVel,yVel);
        if(normSq>maxVelSq){
            double convFactor=maxVel/Math.sqrt(normSq);
            xVel*=convFactor;
            yVel*=convFactor;
        }
    }
    public void ForceMove() {
        MoveSafePT(Xpt() + xVel, Ypt() + yVel, G().wrapX, G().wrapY);
    }
    public A Divide(double[] scratchCoordArr, Rand rn, double divRadius, boolean wrapX, boolean wrapY){
        if(rn!=null){
            rn.RandomPointOnCircleEdge(divRadius, scratchCoordArr);
        }
        A child=G().NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Xpt(),Ypt(),wrapX,wrapY);
        MoveSafePT(Xpt()-scratchCoordArr[0],Ypt()-scratchCoordArr[1],wrapX,wrapY);
        return child;
    }
    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn){
        if(rn!=null){
            rn.RandomPointOnCircleEdge(divRadius, scratchCoordArr);
        }
        A child=G().NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Xpt(),Ypt(),G().wrapX,G().wrapY);
        MoveSafePT(Xpt()-scratchCoordArr[0],Ypt()-scratchCoordArr[1],G().wrapX,G().wrapY);
        return child;
    }
}
