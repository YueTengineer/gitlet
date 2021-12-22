package core;
import gitobject.*;
import repository.Repository;
import fileoperation.*;

public class JitBranch{
    //打印所有分支
    public static void branch() throws IOException{
        Branch curBranch = Branch.getCurBranch();
        File[] branchList = new File(Repository.getGitDir() + File.separator + "refs"+ File.separator + "heads").listFiles();
        for(int f:branchList)
            if(curBranch.getBranchName().equals(f.getName())){
                System.out.println("*"+f.getName());
            }
            else{
                System.out.println(" "+f.getName());
            }
        }

    }
    //创建一个新分支
    public static void branch(String branchname) throws IOException{
        Branch master = new Branch("master");
        Branch branch = new bBranch(branchname, master.getCommitId());
        writeBranch();
    }
    //删除一个分支
    public static void deleteBranch(String branchname) throws IOException{
        if(FileStatus.branchExist(branchname)){
            File branch = new File(Repository.getGitDir() + File.separator + "refs" + File.separator +"heads" + File.separator + branchname);
            branch.delete();
        }else{
            System.out.println(branchname+"does not exist.");
        }

    }
  





}