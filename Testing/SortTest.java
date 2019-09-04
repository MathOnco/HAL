package Testing;

import HAL.Util;

public class SortTest {
    public static void main(String[] args) {
        double[]test=new double[]{1,3,6,7,7,7,4,3,2};
        Util.Sort((i1,i2)->test[i1]>test[i2],(i1,i2)->{double temp=test[i1];test[i1]=test[i2];test[i2]=temp;},test.length);
        System.out.println(Util.ArrToString(test,","));
    }
}
