package repository;

import java.io.File;
import java.io.IOException;
import JitInit.init;
import fileoperation.FileCreation;
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
                file.mkdirs();
            }
        } catch (IOException e){
            e.printStackTrace();
        }
        //创建名为logs的空文件夹，存储不同分支下commit记录
        FileCreation.createDirectory(gitDir, "logs");
        //创建名为objects的空文件夹，保存blob,tree,commit对象hash文件
        FileCreation.createDirectory(gitDir, "objects");
        //创建名为refs的空文件夹，保存不同分支的具体信息
        FileCreation.createDirectory(gitDir, "refs");
        //在logs文件夹下创建refs文件夹
        FileCreation.createDirectory(gitDir, "logs", "refs");
        //在logs文件夹下的refs的文件夹下创建heads文件夹
        FileCreation.createDirectory(gitDir, "logs", "refs", "heads");
        //在refs文件夹下创建heads文件夹
        FileCreation.createDirectory(gitDir, "refs", "heads");
        //创建名为HEAD的文件，用来存储当前HEAD指针指向的分支
        FileCreation.createFile(gitDir, "HEAD");
        //创建名为index的文件，用来临时存储通过git.add添加的文件
        FileCreation.createFile(gitDir,"index");

    }

}
