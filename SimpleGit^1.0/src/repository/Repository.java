package repository;

import java.io.File;
import java.io.IOException;

/**
 * Todo: Add your own code. JitInit.init("worktree") should be able to create a repository "worktree/.jit" ,
 *       which contains all the default files and other repositories inside. If the repository has
 *       already exists, delete the former one and create a new one. You're welcome to reconstruct the code,
 *       but don't change the code in the core directory.
 */
public class Repository {
    private static String workTree;	//working directory
    private static String gitDir;	//jit repository path

    /**
     * Constructor
     */
    public Repository() throws IOException {
        if(gitDir == ""){
            throw new IOException("The repository does not exist!");
        }
    }
    
    /**
     * Construct a new repository instance with certain path.
     * Constructor
     * @param path
     * @throws IOException
     */
    public Repository(String path) throws IOException {
        this.workTree = path;
        this.gitDir = path + File.separator + ".jit";
    }

    public static String getGitDir() {
        return gitDir;
    }

    public static String getWorkTree() {
        return workTree;
    }
    
    /**
     * Helper functions.
     * @return
     */
    public boolean exist(){ return new File(gitDir).exists(); }

    public boolean isFile(){ return new File(gitDir).isFile(); }

    public boolean isDirectory(){ return new File(gitDir).isDirectory(); }


    /**
     * Create the repository and files and directories inside.
     * @return boolean
     * @throws IOException
     */
    public void createRepo() throws IOException {
    /* Todo：Add your code here. */


    }

}
