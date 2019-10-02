package HAL.Tools.PhylogenyTracker;

import HAL.Interfaces.GetGenomeAttrs;

import java.util.ArrayList;
import java.util.Iterator;

//TODO: add sibling and children for-loop iteration, consider adding iter-living as well

/**
 * the genome class is useful for keeping track of shared genetic information and phylogenies. extend this class to add any genetic/inherited information that needs to be tracked. an instance of the Genome class should be created once for every new mutation, and all agents with the same genome should share the same class instance as a member variable
 */
public class Genome<T extends Genome> implements Iterable<T>{
    long pop;
    T parent;
    T firstChild;
    T nextSibling;
    T prevSibling;
    T next;
    T prev;
    int id;
    final PhylogenyTrackerInternal<T> myTracker;

    /**
     * ﻿call this with the parent set to null to create a new phylogeny. the removeLeaves option specifies whether the phylogeny should continue to store dead leaves (lineages with no active individuals). should also be called whenever a new clone is created, along with IncPop to add one individual to the new clone.
     */
    public Genome(T parent, boolean removeLeaves){
        if(parent!=null) {
            myTracker=parent.myTracker;
            parent.NewMutantGenome(this);
            this.parent=parent;
        }
        else{
            myTracker=new PhylogenyTrackerInternal(this,removeLeaves);
        }
        this.id = myTracker.nGenomesEver;
        myTracker.nGenomesEver++;
        myTracker.nLivingGenomes++;
        myTracker.nTreeGenomes++;
        this.pop = 0;
    }

    public Genome(T parent){
        if(parent!=null) {
            myTracker=parent.myTracker;
            parent.NewMutantGenome(this);
            this.parent=parent;
        }
        else{
            myTracker=new PhylogenyTrackerInternal(this,true);
        }
        this.id = myTracker.nGenomesEver;
        myTracker.nGenomesEver++;
        myTracker.nLivingGenomes++;
        myTracker.nTreeGenomes++;
        this.pop = 0;
    }

    /**
     * gets the ID of the genome, which indicates the order in which it arose
     */
    public int GetId() {
        return id;
    }

    /**
     * returns the current active population that shares this genome
     */
    public long GetPop() {
        return pop;
    }

    /**
     * returns the parent genome that was mutated to give rise to this genome
     */
    public T GetParent() {
        return parent;
    }

    /**
     * sets the active population size for this genome to a specific value.
     */
    public void SetPop(long pop) {
        if (myTracker == null) {
            throw new IllegalStateException("Genome must be part of tracker before pop can be changed!");
        }
        if (pop < 0) {
            throw new IllegalStateException("Can't decrease pop below 0!");
        }
        //if (this.pop <= 0) {
        //    throw new IllegalStateException("Can't alter dead Genome!");
        //}
        myTracker.totalPop += pop - this.pop;
        this.pop = pop;
        if (pop == 0) {
            KillGenome();
        }
    }

    /**
     * adds an individual to the genome population, should be called as part of the initialization of all agents that share this genome.
     */
    public void IncPop() {
        SetPop(this.pop + 1);
    }

    /**
     * removes an individual from the genome population, should be called as part of the disposal of all agents that share this genome.
     */
    public void DecPop() {
        SetPop(this.pop - 1);
    }

    /**
     * runs the GenomeFunction argument on every descendant of this genome
     */
    public void Traverse(GenomeFn GenomeFunction) {
        GenomeFunction.GenomeFn(this);
        T child = firstChild;
        while (child != null) {
            child.Traverse(GenomeFunction);
            child = (T)child.nextSibling;
        }
    }

    /**
     * runs the GenomeFunction argument on every descendant of this genome, will pass as argument the lineage from this genome to the descendant
     */
    public void TraverseWithLineage(ArrayList<T> lineageStorage, GenomeLineageFn GenomeFunction) {
        lineageStorage.add((T) this);
        GenomeFunction.GenomeLineageFn(lineageStorage);
        T child = firstChild;
        while (child != null) {
            child.TraverseWithLineage(lineageStorage, GenomeFunction);
            lineageStorage.remove(lineageStorage.size() - 1);
            child = (T)child.nextSibling;
        }
    }

    /**
     * adds all direct descendants of the genome to the arraylist
     */
    public void GetChildren(ArrayList<T> childrenStorage) {
        T child = firstChild;
        while (child != null) {
            childrenStorage.add((T)child);
            child = (T)child.nextSibling;
        }
    }

    /**
     * adds all ancestors of the genome ot the arraylist
     */
    public void GetLineage(ArrayList<T> lineageStorage) {
        T parent = (T)this;
        while (parent != null) {
            lineageStorage.add((T)parent);
            parent = (T)parent.parent;
        }
    }

    /**
     * returns the total number of genomes that have ever existed in the phylogeny
     */
    public int GetNumGenomes() {
        return myTracker.nGenomesEver;
    }

    /**
     * returns the number of currently active unique genomes
     */
    public int GetNumLivingGenomes() {
        return myTracker.nLivingGenomes;
    }

    /**
     * returns the number of genomes that exist in the phylogeny (not counting removed leaves)
     */
    public int GetNumTreeGenomes() {
        return myTracker.nTreeGenomes;
    }

    /**
     * returns the current population size that shares this genome
     */
    public long Pop() {
        return pop;
    }

    /**
     * returns the total population of all living members of the phylogeny
     */
    public long PhylogenyPop(){
        return myTracker.totalPop;
    }

    /**
     * gets the first genome that started the phylogeny
     */
    public T GetRoot(){
        return (T)myTracker.progenitor;
    }

    void NewMutantGenome(T child) {
        T new_clone = child;
        T current_right_child = this.firstChild;
        this.firstChild = new_clone;
        if (current_right_child != null) {
            current_right_child.prevSibling = new_clone;
            new_clone.nextSibling = current_right_child;
        }
        child.next = myTracker.listFirst;
        if (myTracker.listFirst != null) {
            myTracker.listFirst.prev = child;
        }
        myTracker.listFirst = child;
    }


    void RemoveGenomeFromTree() {
        myTracker.nTreeGenomes--;
        T my_left_sib = this.nextSibling;
        T my_right_sib = this.prevSibling;
        T parent = this.parent;
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
        if (prev != null) {
            prev.next = next;
        }
        if (next != null) {
            next.prev = prev;
        }
    }
    public void ResetCloneRecord(){
        myTracker.ResetCloneRecord();
    }

    public void RecordClones(double timepoint){
        myTracker.RecordClones(timepoint);
    }

    public void RecordClones(String timepointLabel){
        myTracker.RecordClones(timepointLabel);
    }

    public void OutputClonesToCSV(String path, String[]AttrHeaders, GetGenomeAttrs<T> GetAttrs, int excludePopCutoff){
        myTracker.OutputClonesToCSV(path, AttrHeaders, GetAttrs,excludePopCutoff);
    }


    void KillGenome() {
        myTracker.nLivingGenomes--;
        if (myTracker.removeEmptyLeaves && firstChild == null) {
            this.RemoveGenomeFromTree();
        }
    }

    @Override
    public Iterator<T> iterator() {
        return myTracker.iterator();
    }
}
