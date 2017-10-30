package LEARN_HERE.Other;

import Framework.Tools.FileIO;
import Framework.Utils;

/**
 * Created by bravorr on 7/5/17.
 */
public class FileIOTest {
    public static void main(String[] args) {
        FileIO[]outs=new FileIO[1000];
        Utils.MakeDirs("fileIOtest");
        for (int i = 0; i < outs.length; i++) {
            outs[i]=new FileIO("fileIOtest/test"+i+".csv","w");
        }
        for (FileIO o : outs) {
            o.Write("test");
            o.Close();
        }
    }
}
