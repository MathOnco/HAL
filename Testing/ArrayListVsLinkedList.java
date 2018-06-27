package Testing;

import Framework.Rand;
import Framework.Tools.LapTimer;

import java.util.ArrayList;

class TestObj{
    int iList;
    TestObj next;
    TestObj prev;
    void RemLL(){
        if(next!=null){
            next.prev=prev;
        }
        if(prev!=null){
            prev.next=next;
        }
    }
    void RemAL(ArrayListGrid G,int iFrom){
        TestObj reorderMe=G.objs2[iFrom].remove(G.objs2[iFrom].size()-1);
        if(reorderMe.iList!=iList) {
            reorderMe.iList = iList;
            G.objs2[iFrom].set(iList, reorderMe);
        }
    }
}

class LinkedGrid{
    TestObj[] objs=new TestObj[100];
    void AddLL(TestObj newObj, int i){
        newObj.prev=null;
        TestObj cur=objs[i];
        newObj.next=cur;
        if(cur!=null){
            cur.prev=newObj;
        }
        objs[i]=newObj;
    }
    void GetLL(ArrayList<TestObj> out,int i){
        TestObj cur=objs[i];
        while(cur!=null){
            out.add(cur);
            cur=cur.next;
        }
    }
    int Pop(){
        int ct=0;
        ArrayList<TestObj> tmp=new ArrayList<>();
        for (int i = 0; i < objs.length; i++) {
            tmp.clear();
            GetLL(tmp,i);
            ct+=tmp.size();
        }
        return ct;
    }
}

class ArrayListGrid{
    ArrayList<TestObj>[] objs2=new ArrayList[100];
    ArrayListGrid(){
        for (int i = 0; i < objs2.length; i++) {
            objs2[i]=new ArrayList<>();
        }
    }
    void AddAL(TestObj newObj, int i){
        newObj.iList=objs2[i].size();
        objs2[i].add(newObj);
    }
    void GetAL(ArrayList<TestObj> out,int i){
        ArrayList<TestObj> objs=objs2[i];
        for (int j = 0; j < objs.size() ; j++) {
            out.add(objs.get(j));
        }
    }
    int Pop(){
        int ct=0;
        ArrayList<TestObj> tmp=new ArrayList<>();
        for (int i = 0; i < objs2.length; i++) {
            tmp.clear();
            GetAL(tmp,i);
            ct+=tmp.size();
        }
        return ct;
    }
}

public class ArrayListVsLinkedList {

    public static void main(String[] args) {
        Rand rng=new Rand();
        int iters=100000;
        int[]rands1=new int[iters];
        for (int i = 0; i < iters; i++) {
            rands1[i]=rng.Int(100);
        }
        int[]rands2=new int[iters];
        for (int i = 0; i < iters; i++) {
            rands1[i]=rng.Int(100);
        }
        LinkedGrid g1=new LinkedGrid();
        LapTimer tim=new LapTimer();
        //adding elements
        for (int i = 0; i < iters; i++) {
            g1.AddLL(new TestObj(),rands1[i]);
        }
        tim.Lap("adding LL");
        //getting
        ArrayList<TestObj> agents=new ArrayList<>();
        for (int i = 0; i < iters; i++) {
            agents.clear();
            g1.GetLL(agents,rands2[i]);
//            if(agents.size()>0){
//                //removing
//                agents.get(agents.size()/2).RemLL();
//            }
        }
        tim.Lap("getting and removing LL");
        ArrayListGrid g2=new ArrayListGrid();
        //adding elements
        tim.Lap();
        for (int i = 0; i < iters; i++) {
            g2.AddAL(new TestObj(),rands1[i]);
        }
        tim.Lap("adding AL");
        //getting
        ArrayList<TestObj> agents2=new ArrayList<>();
        for (int i = 0; i < iters; i++) {
            agents2.clear();
            int iGet=rands2[i];
            g2.GetAL(agents2,iGet);
//            if(agents2.size()>0){
//                //removing
//                agents2.get(agents2.size()/2).RemAL(g2,iGet);
//            }
        }
        tim.Lap("getting and removing AL");
    }
}
