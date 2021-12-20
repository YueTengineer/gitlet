package core;

import fileoperation.FileReader;
import gitobject.*;

public class JitCommit {
    public static void commit(String author, String committer, String message) {
        //读入index文件
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

        try {
            Commit com = new Commit(index, author, committer, message);
            // 将暂存区生成的树、commit类、暂存区内包含的所有的树全部存入objects文件夹中
            index.compressWrite();
            com.compressWrite();
            System.out.println("Commit key 为：" +com.getKey());
            writeTree(index);
            //将HEAD指针指向当前Commit，覆盖原有指针.
            Head head = new Head(com);
            head.compressWrite();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // commit后将暂存区所有跟踪的Tree文件写入objects文件
    private static void writeTree(Tree root) throws Exception {
        for (Tree t : root.getTreeMap().values()) {
            t.compressWrite();
            writeTree(t);
        }
    }
}
