package core;

import fileoperation.FileReader;
import gitobject.*;

public class JitCommit {
    public static void commit(String author, String committer, String message) {
        //读入index文件
        Index index = FileReader.readCompressedObj(Index.getPath(),Index.class);

        Tree root = index.getTree();
        try {
            Commit com = new Commit(root, author, committer, message);
            // 将暂存区生成的树、commit类、暂存区内包含的所有的树全部存入objects文件夹中
            root.compressWrite();
            com.compressWrite();
            System.out.println("Commit key 为：" +com.getKey());
            writeTree(root);
            //将HEAD指针指向当前Commit，覆盖原有指针.
            Head head = new Head(com);
            head.compressWrite();

            //清空暂存区，覆盖原文件
            index.clear();
            index.compressWrite();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // commit后将暂存区所有跟踪的Tree文件写入objects文件
    private static void writeTree(Tree root) throws Exception {
        for (GitObject go : root.getTreeList()) {
            String fmt = go.getFmt();

            if (fmt.equals("tree")) {
                String key = go.getKey();
                go.compressWrite();
                writeTree((Tree)go);
            }
        }
    }
}
