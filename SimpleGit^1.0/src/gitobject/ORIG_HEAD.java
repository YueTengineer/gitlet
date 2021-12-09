package gitobject;
import repository.Repository;
public class ORIG_HEAD implements Serializable{
    static String path = Repository.getGitDir() + File.separator + "ORIG_HEAD";
    private String cur_commit = null;

    public ORIG_HEAD() {}

    public ORIG_HEAD(Commit go) {
        this.cur_commit = go.getKey();
    }

    public String getCurrentCommit() {
        return cur_commit;
    }

    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    public static String getPath() {
        return path;
    }
}