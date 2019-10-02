package Testing.OldTests;

import HAL.Util;

public class PMFtests {
    static void NormalVsBinomial(int n,double p,int k){
        System.out.println(Util.BinomialDistPDF(n,p,k)+","+Util.GaussianPDF(n*p,Math.sqrt(n*0.5*0.5),k));
    }
    public static void main(String[] args) {

        NormalVsBinomial(2,0.5,1);
    }
}
