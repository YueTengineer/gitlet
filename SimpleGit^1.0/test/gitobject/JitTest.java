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
            testIndex();

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

    public static void testIndex() {
        try {
            JitAdd.add("a.txt");
            JitAdd.add("testTree");
            //读入index文件
            Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

            //显示暂存区文件
            System.out.println("Valuemap:");
            index.getValue();
            System.out.print("\n");
            //显示root内文件
            System.out.println("Root:");
            index.root.traverse();

            //删除 testTree/testTree1 文件夹.
            System.out.println("testTree1 deleted.");
            index.deleteDirectory("testTree" + File.separator + "testTree1");
            //显示暂存区文件
            System.out.println("Valuemap:");
            index.getValue();
            System.out.print("\n");
            //显示root内文件
            System.out.println("Root:");
            index.root.traverse();

            /*
            //删除 testTree
            System.out.println("testTree deleted.");
            index.deleteDirectory("testTree");
            //显示暂存区文件
            System.out.println("Valuemap:");
            index.getValue();
            System.out.print("\n");
            //显示root内文件
            System.out.println("Root:");
            index.root.traverse();


            //删除a.txt
            System.out.println("a.txt deleted.");
            index.deleteFile("a.txt");
            //显示暂存区文件
            index.getValue();
            //显示root内文件
            index.root.traverse();

            */
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}