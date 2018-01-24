package Framework.Tools;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@FunctionalInterface
public interface SerializableModel extends Serializable{
    //must setup constructors of all grids that are serialized, used when loading
    public void SetupConstructors();//sets up the constructors for all Grid2Ds/Grid3Ds, use the _PassAgentConstructor() function to do this, if no Grid2Ds or Grid3Ds are being used, this function can be left empty
    //default byte[] SaveState(){
    //    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    //    ObjectOutput out;
    //    try{
    //        out= new ObjectOutputStream(bos);
    //        out.writeObject(this);
    //        out.flush();
    //        return bos.toByteArray();
    //    } catch (IOException e) {
    //        System.out.println(e.getMessage());
    //        e.printStackTrace();
    //    } finally {
    //        try{
    //            bos.close();
    //        }
    //        catch (IOException e){
    //            System.out.println(e.getMessage());
    //            e.printStackTrace();
    //        }
    //    }
    //    return null;
    //}
    //default int SaveState(byte[] putHere){
    //    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    //    ObjectOutput out;
    //    try{
    //        out= new ObjectOutputStream(bos);
    //        out.writeObject(this);
    //        out.flush();
    //        if(putHere.length<bos.size()){
    //            throw new IllegalStateException("state buffer array is too small for model state! state size: "+bos.size()+", buffer size: "+putHere.length);
    //        }
    //        bos.write(putHere,0,bos.size());
    //        return bos.size();
    //    } catch (IOException e) {
    //        System.out.println(e.getMessage());
    //        e.printStackTrace();
    //    } finally {
    //        try{
    //            bos.close();
    //        }
    //        catch (IOException e){
    //            System.out.println(e.getMessage());
    //            e.printStackTrace();
    //        }
    //    }
    //    return -1;
    //}
    //default void SaveState(String stateBytesFile){
    //    ByteArrayOutputStream bos=new ByteArrayOutputStream();
    //    ObjectOutput out;
    //    try{
    //        out= new ObjectOutputStream(bos);
    //        out.writeObject(this);
    //        out.flush();
    //        bos.writeTo(new DataOutputStream(new BufferedOutputStream(new FileOutputStream(stateBytesFile,false))));
    //    } catch (IOException e) {
    //        System.out.println(e.getMessage());
    //        e.printStackTrace();
    //    } finally {
    //        try{
    //            bos.close();
    //        }
    //        catch (IOException e){
    //            System.out.println(e.getMessage());
    //            e.printStackTrace();
    //        }
    //    }
    //}
    //default byte[] StateFromFile(String stateBytesFile){
    //    Path path= Paths.get(stateBytesFile);
    //    try {
    //        return Files.readAllBytes(path);
    //    } catch (IOException e) {
    //        System.out.println(e.getMessage());
    //        e.printStackTrace();
    //    }
    //    return null;
    //}

    //default SerializableModel LoadState(String stateBytesFile){
    //    return LoadState(StateFromFile(stateBytesFile));
    //}
    //default SerializableModel LoadState(byte[] state){
    //    ByteArrayInputStream bis=new ByteArrayInputStream(state);
    //    ObjectInput in=null;
    //    SerializableModel ret=null;
    //    try{
    //        in=new ObjectInputStream(bis);
    //        ret= (SerializableModel) in.readObject();
    //        ret.SetupConstructors();
    //    } catch (IOException e) {
    //        e.printStackTrace();
    //    } catch (ClassNotFoundException e) {
    //        System.out.println(e.getMessage());
    //        e.printStackTrace();
    //    } finally{
    //        try{
    //            if(in!=null){
    //                in.close();
    //            }
    //        }
    //        catch (IOException e){
    //            System.out.println(e.getMessage());
    //            e.printStackTrace();
    //        }
    //    }
    //    return ret;
    //}
}
