package Framework.Tools;

import Framework.Interfaces.GenomeFn;
import Framework.Interfaces.GenomeLineageFn;

import java.util.ArrayList;

/**
 * Created by bravorr on 8/4/17.
 */
public abstract class GenomeBase {
    long pop;
    GenomeBase parent;
    GenomeBase firstChild;
    GenomeBase nextSibling;
    GenomeBase prevSibling;
    GenomeBase nextLiving;
    GenomeBase prevLiving;
    int id;
    final GenomeTracker myTracker;

    public int GetId() {
        return id;
    }

    public long GetPop() {
        return pop;
    }

    public GenomeBase GetParent() {
        return parent;
    }

    public GenomeBase(GenomeBase parent, boolean removeLeaves){
        if(parent!=null) {
            myTracker=parent.myTracker;
            parent.NewMutantGenome(this);
        }
        else{
            myTracker=new GenomeTracker(this,removeLeaves);
        }
        this.id = myTracker.nGenomesEver;
        myTracker.nGenomesEver++;
        myTracker.nLivingGenomes++;
        myTracker.nTreeGenomes++;
        this.pop = 0;
        myTracker.totalPop++;
    }

//    void Init(GenomeTracker myTracker, GenomeBase parent, int id) {
//        myTracker.nGenomesEver++;
//        myTracker.nLivingGenomes++;
//        myTracker.nTreeGenomes++;
//        this.pop = 1;
//        myTracker.totalPop++;
//        if (this.myTracker != null) {
//            throw new IllegalStateException("Adding GenomeBase to tree twice!");
//        }
//        this.myTracker = myTracker;
//        this.parent = parent;
//        this.id = id;
//    }

    <T extends GenomeBase> void NewMutantGenome(T child) {
        T new_clone = child;
        GenomeBase current_right_child = this.firstChild;
        this.firstChild = new_clone;
        if (current_right_child != null) {
            current_right_child.prevSibling = new_clone;
            new_clone.nextSibling = current_right_child;
        }
        child.nextLiving = myTracker.living;
        if (myTracker.living != null) {
            myTracker.living.prevLiving = child;
        }
        myTracker.living = child;
        this.DecPop();
    }

    public void SetPop(long pop) {
        if (myTracker == null) {
            throw new IllegalStateException("GenomeBase must be part of tracker before pop can be changed!");
        }
        if (pop < 0) {
            throw new IllegalStateException("Can't decrease pop below 0!");
        }
        //if (this.pop <= 0) {
        //    throw new IllegalStateException("Can't alter dead GenomeBase!");
        //}
        myTracker.totalPop += pop - this.pop;
        this.pop = pop;
        if (pop == 0) {
            KillGenome();
        }
    }

    public void IncPop() {
        SetPop(this.pop + 1);
    }

    public void DecPop() {
        SetPop(this.pop - 1);
    }

    void RemoveGenomeFromTree() {
        myTracker.nTreeGenomes--;
        GenomeBase my_left_sib = this.nextSibling;
        GenomeBase my_right_sib = this.prevSibling;
        GenomeBase parent = this.parent;
        if (parent!=null&&this == parent.firstChild) {
            // clone is parent's most recent child
            parent.firstChild = my_left_sib;
            if (my_left_sib == null && parent.pop == 0) {
                //recursively remove dead branches
                parent.RemoveGenomeFromTree();
            }
        }

        if (my_right_sib != null) {
            my_right_sib.nextSibling = my_left_sib;
        }

        if (my_left_sib != null) {
            my_left_sib.prevSibling = my_right_sib;
        }
    }

    void KillGenome() {
        myTracker.nLivingGenomes--;
        if (prevLiving != null) {
            prevLiving.nextLiving = nextLiving;
        }
        if (nextLiving != null) {
            nextLiving.prevLiving = prevLiving;
        }
        if (myTracker.removeEmptyLeaves && firstChild == null) {
            this.RemoveGenomeFromTree();
        }
    }

    public void Traverse(GenomeFn GenomeFunction) {
        GenomeFunction.GenomeFn(this);
        GenomeBase child = firstChild;
        while (child != null) {
            child.Traverse(GenomeFunction);
            child = child.nextSibling;
        }
    }

    public<T extends GenomeBase> void TraverseWithLineage(ArrayList<T> lineageStorage, GenomeLineageFn GenomeFunction) {
        lineageStorage.add((T) this);
        GenomeFunction.GenomeLineageFn(lineageStorage);
        GenomeBase child = firstChild;
        while (child != null) {
            child.TraverseWithLineage(lineageStorage, GenomeFunction);
            lineageStorage.remove(lineageStorage.size() - 1);
            child = child.nextSibling;
        }
    }

    public <T extends GenomeBase> void GetChildren(ArrayList<T> childrenStorage) {
        GenomeBase child = firstChild;
        while (child != null) {
            childrenStorage.add((T)child);
            child = child.nextSibling;
        }
    }

    public <T extends GenomeBase> void GetLineage(ArrayList<T> lineageStorage) {
        GenomeBase parent = this;
        while (parent != null) {
            lineageStorage.add((T)parent);
            parent = parent.parent;
        }
    }

    public int GetNumGenomes() {
        return myTracker.nGenomesEver;
    }

    public int GetNumLivingGenomes() {
        return myTracker.nLivingGenomes;
    }

    public int GetNumTreeGenomes() {
        return myTracker.nTreeGenomes;
    }

    public long GetTotalPop() {
        return myTracker.totalPop;
    }

    public <T extends GenomeBase> T GetRoot(){
        return (T)myTracker.progenitor;
    }
}
