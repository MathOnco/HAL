package LEARN_HERE.Other;

import HAL.Tools.FileIO;
import HAL.Util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockExample {
    public static void ConcurrentWrite(FileIO out, Lock lock,boolean isLocking, String text){
        if(isLocking) {
        lock.lock();
        }
        try{
            out.Write(text);
            out.Write(text);
            out.Write(text);
        }finally {
            if(isLocking) {
                lock.unlock();
            }
        }

    }
    public static void main(String[] args) {
        FileIO out=new FileIO("LockExample.txt","w");
        Lock lock= new ReentrantLock();
        Util.MultiThread(10,(i)->{
            ConcurrentWrite(out,lock,true,"Thread "+i+" wrote this test bit of text!\n");
        });
        out.Close();
    }
}
