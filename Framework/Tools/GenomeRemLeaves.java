package Framework.Tools;

public class GenomeRemLeaves<T extends GenomeRemLeaves> extends GenomeBase<T>{
    public GenomeRemLeaves(T parent) {
        super(parent, true);
    }
}
