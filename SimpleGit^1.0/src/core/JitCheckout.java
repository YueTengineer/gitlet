package core;
import fileoperation.*;
public class JitCheckout{
    //转向某分支
    public static void checkout(String branchname) throws IOException{
        if(FileStatus.branchExist(branchname)){
            Branch branch = new Branch(branchname);
            branch.writeHead();
        }else{
            System.out.println(branchname+"does not exist.");
        }

    }
    public static void Checkout_b(String branchname)throws IOException {
        try{
            JitBranch.branch(branchname);
            checkout(branchname);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}