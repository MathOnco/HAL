package Framework.GridsAndAgents;

import Framework.Interfaces.OverlapForceResponse3D;
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
    public void Init(double radius){
        this.radius=radius;
        this.xVel=xVel;
        this.yVel=yVel;
        this.zVel=zVel;
    }
    public double SumForces(double interactionRad, OverlapForceResponse3D<A> OverlapFun){
        ArrayList<A> scratchAgentList=G().GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        G().GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,G().wrapX,G().wrapY,G().wrapZ);
        for (A a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
                double zComp=Zdisp(a,G().wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
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
    public <T extends SphericalAgent3D> double SumForces(double interactionRad,AgentGrid3D<T> otherGrid, OverlapForceResponse3D<T> OverlapFun){
        ArrayList<T> scratchAgentList=otherGrid.GetFreshAgentSearchArr();
        scratchAgentList.clear();
        double sum=0;
        otherGrid.GetAgentsRadApprox(scratchAgentList,Xpt(),Ypt(),Zpt(),interactionRad,G().wrapX,G().wrapY,G().wrapZ);
        for (T a : scratchAgentList) {
            if(a!=this){
                double xComp=Xdisp(a,G().wrapX);
                double yComp=Ydisp(a,G().wrapY);
                double zComp=Zdisp(a,G().wrapZ);
                double dist=Norm(xComp,yComp,zComp);
                if(dist<interactionRad) {
                    double touchDist = (radius + a.radius) - dist;
                    double force=OverlapFun.CalcForce(touchDist,a);
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
        MoveSafePT(Xpt()+xVel, Ypt()+yVel, Zpt()+zVel);
    }

    public A Divide(double divRadius, double[] scratchCoordArr, Rand rn){
        if(rn!=null){
            rn.RandomPointOnSphereEdge(divRadius, scratchCoordArr);
        }
        A child=(G().NewAgentPTSafe(Xpt()+scratchCoordArr[0],Ypt()+scratchCoordArr[1],Zpt()+scratchCoordArr[2],Xpt(),Ypt(),Zpt()));
        MoveSafePT(Xpt()-scratchCoordArr[0], Ypt()-scratchCoordArr[1], Zpt()-scratchCoordArr[2]);
        return child;
    }
}
