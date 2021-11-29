package gitobject;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;
import java.io.Serializable;

public class Head implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "HEAD";   //absolute path of HEAD.
    private String cur_commit = null;

    public Head() {}

    public Head(Commit go) {
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
