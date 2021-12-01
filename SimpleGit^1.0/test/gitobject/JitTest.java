package gitobject;

import core.*;
import fileoperation.FileReader;

import java.io.File;
import java.io.IOException;


class JitTest {

    public static void main(String[] args) {
        try {
            createRepository();
            testRm();

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

    public static void createCommit() throws Exception{
        Commit com = new Commit("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree",
                "Yue", "Yue", "initial commit.");
        System.out.println("Before serialization:");
        System.out.println(com.getKey());
        System.out.println(com.getValue());
        com.compressWrite();

        System.out.println("After deserialization:");
        Commit com1 = Commit.deserialize(com.getKey());
        System.out.println(com1.getKey());
        System.out.println(com1.getValue());

        /*
        File f =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree" + File.separator + "testTree1");
        Tree t =  new Tree(f);

        Commit com2 = new Commit(t, "Yue", "Yue", "initial commit.");
        System.out.println("Before serialization:");
        System.out.println(com2.getKey());
        System.out.println(com2.getValue());
        com2.compressWrite();

        System.out.println("After deserialization:");
        Commit com3 = Commit.deserialize(com2.getKey());
        System.out.println(com3.getKey());
        System.out.println(com3.getValue());
         */
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
        testIndex();

        JitCommit.commit("Yue", "Yue", "Initial Commit.");

        //检查Head指针是否指向最新Commit
        Head head1 = FileReader.readCompressedObj(Head.getPath(),Head.class);

        System.out.println("上传Commit1后HEAD指针为：");
        System.out.println(head1.getCurrentCommit());

        testIndex();

        JitCommit.commit("Tengyue", "Tengyue", "Second Commit.");

        //检查Head指针是否指向最新Commit
        Head head2 = FileReader.readCompressedObj(Head.getPath(),Head.class);

        System.out.println("上传Commit2后HEAD指针为：");
        System.out.println(head2.getCurrentCommit());

        System.out.println("Commit2为：");
        Commit second = Commit.deserialize(head2.getCurrentCommit());
        System.out.println("Second Commit:");
        System.out.println(second.getValue());
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
            */

            //删除a.txt
            System.out.println("a.txt deleted.");
            index.deleteFile("a.txt");
            //显示暂存区文件
            index.getValue();
            //显示root内文件
            index.root.traverse();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void testHash() {
        File blobfile =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");
        try {
            Blob b = new Blob(blobfile);
            System.out.println("a.txt key值为：");
            System.out.println(b.getKey());
            JitHash.hash("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" + "a.txt");

            File treefile =  new File("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");
            Tree t =  new Tree(treefile);
            System.out.println("testTree key值为：");
            System.out.println(t.getKey());
            JitHash.hash("C:\\Gitee\\java-project\\SimpleGit^1.0\\test\\testRepository\\" +"testTree");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void testRm() {
        try {
            JitAdd.add("a.txt");
            JitAdd.add("testTree");
        } catch (Exception e) {
            e.printStackTrace();
        }

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
        try {
            JitRm.rm("testTree" + File.separator + "testTree1");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读入删除后的index文件
        Index indexrm1 = FileReader.readCompressedObj(Index.getPath(),Index.class);

        //显示暂存区文件
        System.out.println("Valuemap:");
        indexrm1.getValue();
        //显示root内文件
        System.out.println("Root:");
        indexrm1.root.traverse();


        //删除a.txt
        System.out.println("a.txt deleted.");
        try {
            JitRm.rm("a.txt");
        } catch (Exception e) {
            e.printStackTrace();
        }

        //读入删除后的index文件
        Index indexrm2 = FileReader.readCompressedObj(Index.getPath(),Index.class);

        System.out.println("Valuemap:");
        //显示暂存区文件
        indexrm2.getValue();
        //显示root内文件
        System.out.println("Root:");
        indexrm2.root.traverse();

    }
}
