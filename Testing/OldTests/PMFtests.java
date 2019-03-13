package Testing.OldTests;

import Framework.Util;

public class PMFtests {
    static void NormalVsBinomial(int n,double p,int k){
        System.out.println(Util.BinomialDistPMF(n,p,k)+","+Util.NormalDistPMF(n*p,Math.sqrt(n*0.5*0.5),k));
    }
    public static void main(String[] args) {

        NormalVsBinomial(2,0.5,1);
    }
}
