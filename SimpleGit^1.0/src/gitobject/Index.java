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

            valueMapAdd(key, value);
        }

        else if (fmt.equals("tree")) {
            Tree t = (Tree)go;
            for (GitObject o : t.getTreeList()) {
                addHelper(o, date);
            }
        }
    }


    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    public void getValue() {
        for (String v : valuemap.values()) {
            System.out.println(v);
        }
    }

    public Tree getTree() {
        return root;
    }

    // 向valuemap新增键值对.
    private void valueMapAdd(String key, String value) {
        // 如果文件名相同，而内容不同，会替换value，删除旧节点，尾部插入新节点.
        if (valuemap.containsKey(key)) {
            valuemap.remove(key);
        }
        valuemap.put(key,value);
    }

    public static String getPath() {
        return path;
    }

    public void clear() {
        root = new Tree();
        valuemap = new LinkedHashMap<>();
    }

}
