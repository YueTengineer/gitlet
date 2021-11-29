package repository;

import java.io.File;
import java.io.IOException;
import fileoperation.FileCreation;
import gitobject.Head;
import gitobject.Index;

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

        if(!file.exists()){
            file.mkdirs();
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
        //初始化并将HEAD文件写入.jit/ 文件夹
        Head head = new Head();
        head.compressWrite();
        //初始化并将index文件写入.jit/ 文件夹
        Index index = new Index();
        index.compressWrite();
    }

}
