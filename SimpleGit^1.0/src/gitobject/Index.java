package gitobject;

import fileoperation.FileReader;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

public class Index implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "index";   //absolute path of index.

    //root 包含所有暂存区中的文件.
    Tree root = new Tree();

    //储存<文件名称,暂存区value>
    //value格式： mode hash值 文件命名 上传时间
    //使用LinkedHashMap,便于索引以及按上传时间顺序展示暂存区信息.
    LinkedHashMap<String,String> valuemap = new LinkedHashMap<>();

    public Index() {}

    public void add(GitObject go) {
        root.add(go);
        //获得命令执行时的时间.
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());

        try {
            addHelper(go, date);
        // Tree更新value与key值
            root.update();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 将加入的Gitobject存入root的tree list中，如果加入的gitobject为tree，则其中的文件(Blob)存入value中.
    private void addHelper(GitObject go, String date) throws Exception {
        String fmt = go.getFmt();
        String mode = go.getMode();
        String key = go.getKey();
        //文件名
        String name = go.getName();

        // 如果已被commit，在objects文件夹内具有相同的文件时，不存入index区内, 返回.
        if (FileReader.objectExists(key)) {
            System.out.println(name + "already exists.");
            return;
        }

        if (fmt.equals("blob")) {
            go.compressWrite();

            String value = mode + " " + key + " " + name + " " + date;

            valueMapAdd(name, value);
        }

        else if (fmt.equals("tree")) {
            Tree t = (Tree)go;
            for (GitObject o : t.getTreeList()) {
                addHelper(o, date);
            }
        }
    }

    // Delete file from staging area (root and valuemap).
    public void deleteFile(String filename) {
        valueMapDelete(filename);
        boolean flag = deleteHelper(filename, root);
        if (!flag) System.out.println(filename + " not found.");
    }

    // Delete directory from staging area (root and valuemap).
    public void deleteDirectory(String dirname) {
        valueMapDeleteDirectory(dirname, root);
        boolean flag = deleteHelper(dirname, root);
        if (!flag) System.out.println(dirname + " not found.");
    }

    // 在root为顶点的树中找到并删除文件名为filename的gitobject，并更新相应的值.
    // 找到了返回 true 否则 返回 false;
    private boolean deleteHelper(String filename, Tree t) {
        for (GitObject go : t.getTreeList()) {
            String fmt = go.getFmt();
            String name = go.getName();
            // 找到对应目标
            if (name.equals(filename)) {
                t.delete(name);
                t.update();
                return true;
            }
            // 未找到
            else {
                //如果是Tree的话继续递归搜索.
                if (fmt.equals("tree")) {
                    if (deleteHelper(filename, (Tree)go)) {
                        //如果出现改变则更新.
                        t.update();
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    // 展示暂存区现存的文件
    public void getValue() {
        for (String v : valuemap.values()) {
            System.out.println(v);
        }
    }

    public Tree getTree() {
        return root;
    }

    // 向valuemap新增键值对.
    private void valueMapAdd(String name, String value) {
        // 如果文件名相同，而内容不同，会替换value，删除旧节点，尾部插入新节点.
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        }
        valuemap.put(name,value);
    }

    private void valueMapDeleteDirectory(String dirname, Tree t) {
        for (GitObject go : t.getTreeList()) {
            String fmt = go.getFmt();
            String name = go.getName();
            // 找到对应目标
            if (name.equals(dirname)) {
                if (!fmt.equals("tree")) throw new IllegalArgumentException("Dirname should be a directory.");
                valueMapDeleteTree((Tree) go);
            }
            // 未找到
            else {
                //如果是Tree的话继续递归搜索.
                if (fmt.equals("tree")) {
                    valueMapDeleteDirectory(dirname, (Tree) go);
                }
            }
        }
    }

    private void valueMapDeleteTree(Tree t) {
        for (GitObject go : t.getTreeList()) {
            String fmt = go.getFmt();
            String name = go.getName();
            if (fmt.equals("blob")) {
                valueMapDelete(name);
            }
            else if (fmt.equals("tree")) {
                valueMapDeleteTree((Tree) go);
            }
        }
    }

    private void valueMapDelete(String name) {
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        } else {
            System.out.println("No file named " + name + " found in staging area,");
        }
    }


    public static String getPath() {
        return path;
    }

    public void clear() {
        root = new Tree();
        valuemap = new LinkedHashMap<>();
    }

}
