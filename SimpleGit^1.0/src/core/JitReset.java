/*该类实现回滚功能，主要分为3个模式：soft，mixed和hard。
commitId指的是key值。
*/

package core;
import fileoperation.*;
import gitobject.*;
import java.io.*;
import java.util.*;

public class JitReset{
    public static void reset(String mode,String commitID) throws IOException{
        if(FileReader.objectExists(commitID)){
            //根据commitID生成一个commit类
            Commit com=new Commit(commitID);
            //读取index文件中对象
            Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

            if(mode=="soft"){
                try{
                    //将head指针指向所要指向的Commit，产生一个ORIG_HEAD保存还未reset之前的commitID
                    Head head2=new Head(com);
                    head2.compressWrite();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(mode=="mixed"){
                try{
                    Head head2=new Head(com);
                    head2.compressWrite();
                    //更新的暂存区,将暂存区回到之前的状态。
                    index.clear();
                    Index index2=new Index();
                    index2.compressWrite();
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
            if(mode=="hard"){
                try{
                    Head head=new Head(com);
                    head.compressWrite();
                    Index index2=new Index();
                    index2.compressWrite();
                    //清空工作区所有文件，把commitID中的内容还原出来。
                    FileDeletion.deleteFile(Repository.getGitDir());
                    com.deserialize();
                }catch(Exception e){
                    e.printStackTrace();
                }
    
            }
        }else{
            System.out.println(commitID+"does not exist.");
        }
        

    }


}