package Framework.Tools;

public class GenomeKeepLeaves<T extends GenomeKeepLeaves> extends GenomeBase<T>{
    public GenomeKeepLeaves(T parent) {
        super(parent,true);
    }
}
