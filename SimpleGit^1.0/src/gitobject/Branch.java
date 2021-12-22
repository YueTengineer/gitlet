package gitobject;
import repository.Repository;
import gitobject.*;
import fileoperation.*;

public class Branch{
    //默认master
    protected String branchName = "master"; 	
    protected String commitId;

    public String getBranchName(){
        return branchName;
    }
    public String getCommitId(){
        return commitId;
    }

    public Branch(String branchName, String commitId){
        this.branchName = branchName;
        this.commitId = commitId;
    }

    public Branch(String branchName) throws IOException {
        static String path = Repository.getGitDir() + File.separator + "refs" + File.separator + "heads"+File.separator+branchName;
        this.branchName = branchName;
        FileWriter.writeCompressedObj(path, this);
        commitId=this.getKey();

    }
    public void updateBranch(String commitId){
        this.commitId = commitId;
    }
    public void writeHead() throws IOException {
        Head head = FileReader.readCompressedObj(Head.getPath() ,Head.class);
        head.compressWrite();
    }
    public void writeBranch() throws IOException {
        String path = Repository.getGitDir() + File.separator + "refs" + File.separator + "heads"+File.separator+branchName;
        FileWriter.writeCompressedObj(path, this);
    }
    public static Branch getCurBranch() throws IOException{
        Head head = FileReader.readCompressedObj(Head.getPath() ,Head.class);
        String branchName=head.getCurrentCommit();
        Branch branch = new Branch(branchName);
        return branch;
    }
    
}