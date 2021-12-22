package core;
import fileoperation.*;
import gitobject.Branch;
import gitobject.Head;

import java.io.IOException;

public class JitCheckout{
    //转向某分支
    public static void checkout(String branchname) throws IOException {
        if(FileStatus.branchExist(branchname)){
            Branch branch = Branch.deserialize(branchname);
            Head head = Head.deserialize();
            head.updateTarget(branch);
            head.compressWrite();
        }else{
            System.out.println(branchname + "does not exist.");
        }

    }

    public static void Checkout_b(String branchname)throws IOException {
        try{
            JitBranch.createbranch(branchname);
            checkout(branchname);
        }
        catch(Exception e){
            e.printStackTrace();
        }
        
    }
}