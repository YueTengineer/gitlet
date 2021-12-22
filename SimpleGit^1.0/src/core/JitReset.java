/*该类实现回滚功能，主要分为3个模式：soft，mixed和hard。
commitId指的是key值。
*/

package core;
import fileoperation.FileCreation;
import fileoperation.FileDeletion;
import fileoperation.FileReader;
import gitobject.*;
import repository.Repository;
import java.io.*;


public class JitReset{
    public static void reset(String mode,String commitID) {
        if(FileReader.objectExists(commitID)){
            //根据commitID生成一个commit类
            Commit com = Commit.deserialize(commitID);
            //读取commit对应的index 树
            Index indextree = com.getIndexTree();

            //产生一个ORIG_HEAD保存还未reset之前的commitID
            ORIG_HEAD orig_head = new ORIG_HEAD(Head.getCurrentCommit());
            orig_head.compressWrite();

            if(mode =="soft"){
                try{
                    //更新head指针所指向的分支
                    Branch curbranch = Branch.getCurBranch();
                    curbranch.updateBranch(commitID);
                    curbranch.writeBranch();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(mode=="mixed"){
                try{
                    //更新head指针所指向的分支
                    Branch curbranch = Branch.getCurBranch();
                    curbranch.updateBranch(commitID);
                    curbranch.writeBranch();

                    //更新的暂存区,将暂存区回到之前的状态。
                    indextree.compressWrite();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(mode=="hard"){
                try{
                    //更新head指针所指向的分支
                    Branch curbranch = Branch.getCurBranch();
                    curbranch.updateBranch(commitID);
                    curbranch.writeBranch();

                    //更新的暂存区,将暂存区回到之前的状态。
                    indextree.compressWrite();

                    //清空工作区所有文件，把commitID中的内容还原出来。
                    File[] worktree = new File(Repository.getWorkTree()).listFiles();
                    for(File f:worktree){
                        FileDeletion.deleteFile(f);
                    }
                    FileCreation.recoverWorkTree(indextree, Repository.getWorkTree());
                }catch(Exception e){
                    e.printStackTrace();
                }
    
            }
        }else{
            System.out.println(commitID+"does not exist.");
        }
        

    }


}