package Framework.Extensions;

import Framework.GridsAndAgents.AgentGrid3D;
import Framework.GridsAndAgents.AgentPT3D;
import Framework.Interfaces.OverlapForceResponse;
import Framework.Interfaces.OverlapNeighborForceResponse;
import Framework.Rand;

import java.util.ArrayList;

import static Framework.Util.*;

/**
 * Created by bravorr on 6/26/17.
 */
public class SphericalAgent3D<A extends SphericalAgent3D,G extends AgentGrid3D<A>> extends AgentPT3D<G> {
    public double radius;
    public double xVel;
    public double yVel;
    public double zVel;
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapForceResponse OverlapFun, boolean wrapX, boolean wrapY, boolean wrapZ){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,wrapX,wrapY,wrapZ);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,wrapX);
                double yComp=Ydisp(a,wrapY);
                double zComp=Zdisp(a,wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    zVel+=(zComp/dist)*force;
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
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,G().wrapX,G().wrapY,G().wrapZ);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
                double zComp=Zdisp(a,G().wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    zVel+=(zComp/dist)*force;
                    if(force>0){
                        sum+=Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList,OverlapNeighborForceResponse<A> OverlapFun){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,G().wrapX,G().wrapY,G().wrapZ);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
                double zComp=Zdisp(a,G().wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    // if (touchDist < 0) cells overlap => should repulse
                    // if (touchDist > 0) cells don't overlap => should attract
                    double force=OverlapFun.CalcForce(touchDist, a);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    zVel+=(zComp/dist)*force;
                    if(force>0){
                        sum+=Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }
    public double SumForces(double interactionRad, ArrayList<A> scratchAgentList, OverlapNeighborForceResponse<A> OverlapFun,boolean wrapX,boolean wrapY,boolean wrapZ){
        scratchAgentList.clear();
        double sum=0;
        G().AgentsInRad(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,wrapX,wrapY,wrapZ);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,wrapX);
                double yComp=Ydisp(a,wrapY);
                double zComp=Zdisp(a,wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    // if (touchDist < 0) cells overlap => should repulse
                    // if (touchDist > 0) cells don't overlap => should attract
                    double force=OverlapFun.CalcForce(touchDist, a);
                    xVel+=(xComp/dist)*force;
                    yVel+=(yComp/dist)*force;
                    zVel+=(zComp/dist)*force;
                    if(force>0){
                        sum+=Math.abs(force);
                    }
                }
            }
        }
        return sum;
    }

    public void ForceMove(boolean wrapX,boolean wrapY,boolean wrapZ){
        MoveSafePT(Xpt()+xVel,Ypt()+yVel,Zpt()+zVel,wrapX,wrapY,wrapZ);
    }
    public void ApplyFriction(double frictionConst){
        xVel*=frictionConst;
        yVel*=frictionConst;
        zVel*=frictionConst;
    }
    public void CapVelocity(double maxVel){
        double maxVelSq=maxVel*maxVel;
        double normSq=NormSq(xVel,yVel,zVel);
        if(normSq>maxVelSq){
            double convFactor=maxVel/Math.sqrt(normSq);
            xVel*=convFactor;
            yVel*=convFactor;
            zVel*=convFactor;
        }
    }

    public void ForceMove(){
        MoveSafePT(Xpt()+xVel,Ypt()+yVel,Zpt()+zVel,G().wrapX,G().wrapY,G().wrapZ);
    }

    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn, boolean wrapX, boolean wrapY, boolean wrapZ){
        if(rn!=null){
            rn.RandomPointOnSphereEdge(divRadius, scratchCoordArr);
        }
        A child=(G().NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Zpt()+scratchCoordArr[2],Xpt(),Ypt(),Zpt(),wrapX,wrapY,wrapZ));
        MoveSafePT(Xpt()-scratchCoordArr[0],Ypt()-scratchCoordArr[1],Zpt()-scratchCoordArr[2],wrapX,wrapY,wrapZ);
        return child;
    }
    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn){
        if(rn!=null){
            rn.RandomPointOnSphereEdge(divRadius, scratchCoordArr);
        }
        A child=(G().NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Zpt()+scratchCoordArr[2],Xpt(),Ypt(),Zpt(),G().wrapX,G().wrapY,G().wrapZ));
        MoveSafePT(Xpt()-scratchCoordArr[0],Ypt()-scratchCoordArr[1],Zpt()-scratchCoordArr[2],G().wrapX,G().wrapY,G().wrapZ);
        return child;
    }
}
