package HAL.Tools;

import HAL.Interfaces.IntDoubleToVoid;
import HAL.Interfaces.IntToDouble;

import java.io.Serializable;

public class TdmaSolver implements Serializable{
    double[]scratch;
    final static int N_SCRATCHES = 7;
    int scratchLen, AS,BS,CS,WS,DS,YS,ZS;

    public TdmaSolver(int scratchLen){
        this.scratchLen=scratchLen;
        scratch=new double[scratchLen*N_SCRATCHES];
        AS=0;
        BS=scratchLen;
        CS=scratchLen*2;
        WS=scratchLen*3;
        DS=scratchLen*4;
        YS=scratchLen*5;
        ZS=scratchLen*6;
    }
    public void TDMA(int lenToSolve,IntToDouble GetIn,IntDoubleToVoid SetOut,IntToDouble GetB,IntToDouble GetC,IntToDouble GetA){
        //generate more scratch if needed
        if(scratchLen<lenToSolve){
            scratch=new double[lenToSolve*N_SCRATCHES];
            AS=0;
            BS=scratchLen;
            CS=scratchLen*2;
            WS=scratchLen*3;
            DS=scratchLen*4;
            YS=scratchLen*5;
            ZS=scratchLen*6;
        }
        scratch[BS]=GetB.GenDouble(0);
        scratch[CS] = GetC.GenDouble(0);
        scratch[DS]=GetIn.GenDouble(0);
        for (int i = 1; i < lenToSolve; i++) {
            if(i<lenToSolve-1) {
                scratch[CS + i] = GetC.GenDouble(i);
            }
            scratch[BS+i]=GetB.GenDouble(i);
            scratch[AS + i] = GetA.GenDouble(i);
            scratch[DS+i]=GetIn.GenDouble(i);
            scratch[WS+i]=scratch[AS+i]/scratch[BS+i-1];
            scratch[BS+i]=scratch[BS+i]-scratch[WS+i]*scratch[CS+i-1];
            scratch[DS+i]=scratch[DS+i]-scratch[WS+i]*scratch[DS+i-1];
        }
        int i=lenToSolve-1;
        double x=scratch[DS+i]/scratch[BS+i];
        SetOut.Eval(i,x);
        for (i-=1; i >= 0; i--) {
            x=(scratch[DS+i]-scratch[CS+i]*x)/scratch[BS+i];
            SetOut.Eval(i,x);
        }
    }


    public void TDMAperiodic(int lenToSolve,IntToDouble GetIn,IntDoubleToVoid SetOut,IntToDouble GetB,IntToDouble GetC,IntToDouble GetA){
        final double gamma=-GetB.GenDouble(0);
        final double alpha=GetA.GenDouble(0);
        final double beta=GetC.GenDouble(lenToSolve-1);

        TDMA(lenToSolve,GetIn,(i,d)->{
            scratch[YS+i]=d;
        },(i)->{
            if(i==0){
                return GetB.GenDouble(i)-gamma;
            }
            if(i==lenToSolve-1){
                return GetB.GenDouble(i)-(alpha*beta)/gamma;
            }
            return GetB.GenDouble(i);
        },GetC,GetA);

        TDMA(lenToSolve,(i)->{
            if(i==0){
                return gamma;
            }
            if(i==lenToSolve-1){
                return alpha;
            }
            return 0;
        },(i,d)->{
            scratch[ZS+i]=d;
        },(i)->{
            if(i==0){
                return GetB.GenDouble(i)-gamma;
            }
            if(i==lenToSolve-1){
                return GetB.GenDouble(i)-(alpha*beta)/gamma;
            }
            return GetB.GenDouble(i);
        },GetC,GetA);

        double vEnd=beta/gamma;
        //dot product of v and y
        double vy=scratch[YS]+scratch[YS+lenToSolve-1]*vEnd;
        //dot product of v and z
        double vz=scratch[ZS]+scratch[ZS+lenToSolve-1]*vEnd;
        double factor=vy/(1+vz);
        for (int i = 0; i < lenToSolve; i++) {
            SetOut.Eval(i,scratch[YS+i]-factor*scratch[ZS+i]);
        }

    }

//    public static void main(String[]args){
//        TdmaSolver tdma=new TdmaSolver(3);
//        //double[]out=new double[]{2.5,4.0,3.5};
//        //double[]in=new double[]{-1,0,1};
//        double[]in=new double[]{1,2,3};
//        double[]out=new double[3];
//        tdma.TDMA(3,(i)->in[i],(i,d)->out[i]=d,(i)->{
//            if(i==0){ return 6; }
//            if(i==1){ return 3; }
//             return 10.0/3; }
//        ,(i)->-1,(i)->-1);
//        tdma.TDMAperiodic(3,(i)->in[i],(i,d)->out[i]=d,(i)->3,(i)->-1,(i)->-1);
//        tdma.TDMA(3,(i)->in[i],(i,d)->out[i]=d,(i)->{
//            switch (i){
//                case 0:return 6;
//                case 1:return 3;
//                case 2:return 10.0/3;
//                default:return -1;
//            }
//            }
//            ,(i)->-1,(i)->-1);
//        System.out.println(Util.ArrToString(out,","));
//    }
}
