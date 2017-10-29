package Testing;

import Framework.Tools.FileIO;

/**
 * Created by bravorr on 7/5/17.
 */
public class FileIOTest {
    public static void main(String[] args) {
        FileIO[]outs=new FileIO[1000];
        for (int i = 0; i < outs.length; i++) {
            outs[i]=new FileIO("fileIOtesting/test"+i+".csv","w");
        }
        for (FileIO o : outs) {
            o.Write("test");
            o.Close();
        }
    }
}
