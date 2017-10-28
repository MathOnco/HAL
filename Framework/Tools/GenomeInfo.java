package Framework.Tools;

import Framework.Interfaces.GenomeFn;
import Framework.Interfaces.GenomeLineageFn;

import java.util.ArrayList;

/**
 * Created by bravorr on 8/4/17.
 */
public class GenomeInfo {
    long pop;
    GenomeInfo parent;
    GenomeInfo firstChild;
    GenomeInfo nextSibling;
    GenomeInfo prevSibling;
    GenomeInfo nextLiving;
    GenomeInfo prevLiving;
    int id;
    GenomeTracker myTracker;

    public int GetId() {
        return id;
    }

    public long GetPop() {
        return pop;
    }

    public GenomeInfo GetParent() {
        return parent;
    }

    public GenomeInfo(GenomeInfo parent){
        if(parent!=null) {
            parent.NewMutantGenome(this);
        }
    }

    void Init(GenomeTracker myTracker, GenomeInfo parent, int id) {
        myTracker.nGenomesEver++;
        myTracker.nLivingGenomes++;
        myTracker.nTreeGenomes++;
        this.pop = 1;
        myTracker.totalPop++;
        if (this.myTracker != null) {
            throw new IllegalStateException("Adding GenomeInfo to tree twice!");
        }
        this.myTracker = myTracker;
        this.parent = parent;
        this.id = id;
    }

    <T extends GenomeInfo> void NewMutantGenome(T child) {
        T new_clone = child;
        child.Init(myTracker, this, myTracker.nGenomesEver);
        GenomeInfo current_right_child = this.firstChild;
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
            throw new IllegalStateException("GenomeInfo must be part of tracker before pop can be changed!");
        }
        if (pop < 0) {
            throw new IllegalStateException("Can't decrease pop below 0!");
        }
        if (this.pop <= 0) {
            throw new IllegalStateException("Can't alter dead GenomeInfo!");
        }
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
        GenomeInfo my_left_sib = this.nextSibling;
        GenomeInfo my_right_sib = this.prevSibling;
        GenomeInfo parent = this.parent;
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
        GenomeInfo child = firstChild;
        while (child != null) {
            child.Traverse(GenomeFunction);
            child = child.nextSibling;
        }
    }

    public<T extends GenomeInfo> void TraverseWithLineage(ArrayList<T> lineageStorage, GenomeLineageFn GenomeFunction) {
        lineageStorage.add((T) this);
        GenomeFunction.GenomeLineageFn(lineageStorage);
        GenomeInfo child = firstChild;
        while (child != null) {
            child.TraverseWithLineage(lineageStorage, GenomeFunction);
            lineageStorage.remove(lineageStorage.size() - 1);
            child = child.nextSibling;
        }
    }

    public <T extends GenomeInfo> void GetChildren(ArrayList<T> childrenStorage) {
        GenomeInfo child = firstChild;
        while (child != null) {
            childrenStorage.add((T)child);
            child = child.nextSibling;
        }
    }

    public <T extends GenomeInfo> void GetLineage(ArrayList<T> lineageStorage) {
        GenomeInfo parent = this;
        while (parent != null) {
            lineageStorage.add((T)parent);
            parent = parent.parent;
        }
    }
}
