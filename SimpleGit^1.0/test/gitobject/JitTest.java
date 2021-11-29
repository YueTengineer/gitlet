package gitobject;

import core.JitAdd;
import core.JitCommit;
import core.JitInit;
import fileoperation.FileReader;

import java.io.File;
import java.io.IOException;


class JitTest {

    public static void main(String[] args) {
        try {
            createRepository();
            testAdd();
            testCommit();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void createRepository() throws IOException {
        JitInit.init("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository");
    }


    public static void createBlob() throws Exception {
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");
        Blob b = new Blob(f);
        System.out.println("Before serialization:");
        System.out.println(b.getKey());
        System.out.println(b.getName());
        System.out.println(b.getValue());
        b.compressWrite();

        System.out.println("After deserialization:");
        Blob b1 = Blob.deserialize(b.getKey());
        System.out.println(b1.getKey());
        System.out.println(b1.getName());
        System.out.println(b1.getValue());
    }


    public static void createTree() throws Exception {
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");
        Tree t =  new Tree(f);

        System.out.println("Before serialization:");
        System.out.println(t.getKey());
        System.out.println(t.getName());
        System.out.println(t.getValue());
        t.compressWrite();

        System.out.println("After deserialization:");
        Tree t1 = Tree.deserialize(t.getKey());
        System.out.println(t1.getKey());
        System.out.println(t1.getName());
        System.out.println(t1.getValue());
    }

    public static void testAdd(){
        try {
            JitAdd.add("a.txt");
            Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);
            index.getValue();

            JitAdd.add("testTree");
            //读入index文件
            Index index1 = FileReader.readCompressedObj(Index.getPath(),Index.class);
            index1.getValue();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testCommit() {
        JitCommit.commit("Yue", "Yue", "Initial Commit.");
        //检查Head指针是否指向最新Commit
        Head head = FileReader.readCompressedObj(Head.getPath(),Head.class);
        Commit com = Commit.deserialize(head.getCurrentCommit());
        System.out.println(com.getValue());

        //检查暂存区是否为空值
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);
        index.getValue();
    }

}