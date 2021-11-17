package repository;

import fileoperation.FileCreation;

import java.io.File;
import java.io.IOException;
import JitInit.init;
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
        File file = new File(gitDir);
        try {
            if(!file.exists()){
                file.mkdir();
            }
            String s = " attrib +H "+file.getAbsolutePath();
            Runtime.getRuntime().exec(s);
        } catch (IOException e){
            e.printStackTrace();
        }
        FileCreation.createDirectory(gitDir, "info");
        //创建名为objects的空文件夹
        FileCreation.createDirectory(gitDir, "objects");
        //创建名为refs的空文件夹
        FileCreation.createDirectory(gitDir, "branches");
        //创建名为logs的空文件夹
        FileCreation.createDirectory(gitDir, "logs");*/
        //在logs的文件夹下创建heads文件夹
        FileCreation.createDirectory(gitDir, "logs", "refs", "heads");
        //在refs的文件夹下创建heads文件夹
        FileCreation.createDirectory(gitDir, "refs", "heads");
        //在refs的文件夹下创建tags文件夹
        FileCreation.createDirectory(gitDir, "refs", "tags");
        //创建名为config的空文件,并写入字符串
        FileCreation.createFile(gitDir, "config","[core]\n" +
                "\trepositoryformatversion = 0\n" +
                "\tfilemode = false\n" +
                "\tbare = false\n" +
                "\tlogallrefupdates = true\n" +
                "\tsymlinks = false\n" +
                "\tignorecase = true");
        //创建名为description的空文件，并写入字符串
        FileCreation.createFile(gitDir, "description", "Unnamed repository; edit this file 'description' to name the repository.");
        //创建名为HEAD的空文件，并写入字符串"ref: refs/heads/master"
        FileCreation.createFile(gitDir, "HEAD", "ref: refs/heads/master");

    }

}
