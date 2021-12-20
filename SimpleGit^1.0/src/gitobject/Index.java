package gitobject;

import fileoperation.FileReader;
import fileoperation.FileWriter;
import repository.Repository;

import java.io.File;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Index extends Tree implements Serializable {
    static String path = Repository.getGitDir() + File.separator + "index";   //absolute path of index.
    protected HashMap<String, String> name_key_map = new HashMap<>();  // 所有节点gitobject的 <name, key>

    //储存<文件名称,暂存区value>
    //value格式： mode hash值 文件命名 上传时间
    //使用LinkedHashMap,便于索引以及按上传时间顺序展示暂存区信息.
    LinkedHashMap<String,String> valuemap = new LinkedHashMap<>();

    public Index() {}
    public static String getPath() {
        return path;
    }
    //加入某个GitObject,新增或修改
    public void add(GitObject go) throws Exception{
        String fmt = go.getFmt();
        String name = go.getName();
        String key = go.getKey();

        // 获得命令执行时间
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = df.format(new Date());

        if (FileReader.objectExists(key)) return;

        if (fmt.equals("blob")) {
            //对应文件已经存在，提交修改内容，写入新的Blob， 更新对应的Tree 以及index内的map.
            if (name_key_map.containsKey(name)) {
                go.compressWrite();
                String target_key = name_key_map.get(name);
                addBlobWithSameName(target_key, key, name);
                updateMap(go, date);
                //如果Blob在某个Tree下，对name_key_map进行更新.
                String[] parentlist =  name.replace("\\","/").split("/");
                if (parentlist.length > 1) {
                    updateMap(parentlist);
                }
            }
            // 对应文件不存在,判断父文件夹的位置，以及是否已经在index树中，如果没有，则创立新树
            else {

                String [] parentList = name.replace("\\","/").split("/");
                if (parentList.length <= 1) {
                    go.compressWrite();
                    blobMap.put(key, name);
                    updateMap(go, date);
                }
                else {
                    Tree root = createTree(parentList, go);
                    updateMap(root, date);
                }
                //更新index的 value 和key值
                update();
            }
        }
        else if (fmt.equals("tree")) {
            //对应文件夹已经存在，找到该tree含有的不同文件，写入objects/, 替换相应的treeMap位置,更新上层的树.
            if (name_key_map.containsKey(name)) {
                String target_key = name_key_map.get(name);
                addTreeWithSameName(target_key, key, (Tree) go);
                updateMap(go, date);
            }

            //对应文件夹不存在，判断父文件夹的位置，以及是否已经在index树中，如果没有，则创立新树
            else {
                String [] parentList = name.replace("\\","/").split("/");
                if (parentList.length <= 1) {
                    treeMap.put(key, (Tree) go);
                    updateMap(go, date);
                }
                else {
                    Tree root = createTree(parentList, go);
                    updateMap(root, date);
                }
                //更新index的 value 和key值
                update();
            }
        }
    }

    private Tree createTree(String[] nameList, GitObject go) throws Exception {

        return createRecursiveTree(nameList, 1, nameList[0], go);
    }

    private Tree createRecursiveTree(String[] nameList, int level, String name, GitObject go) throws Exception {

        Tree root = new Tree(name);

        if (level == nameList.length - 1) {
            String fmt = go.getFmt();
            String n = go.getName();
            String key = go.getKey();
            if (fmt.equals("blob")) {
                root.getBlobMap().put(n, key);
                go.compressWrite();
            }
            else {
                root.getTreeMap().put(key, (Tree)go);
            }
            root.update();
            return root;
        }

        String nextTreeName = name + File.separator + nameList[level];
        root.getTreeMap().put(nextTreeName, createRecursiveTree(nameList, level + 1, nextTreeName, go));
        root.update();
        return root;
    }


    // add后更新name_key_map 以及 valuemap
    private void updateMap(GitObject go, String date) throws Exception {
        String fmt = go.getFmt();
        String mode = go.getMode();
        String key = go.getKey();
        //文件名
        String name = go.getName();

        if (fmt.equals("blob")) {
            String value = mode + " " + key + " " + name + " " + date;
            valueMapAdd(name, value);
            name_key_map.put(name, key);
        }

        else if (fmt.equals("tree")) {
            Tree t = (Tree)go;
            name_key_map.put(name, key);
            for (String blobKey : t.getBlobMap().keySet()) {
                Blob b = Blob.deserialize(blobKey);
                updateMap(b, date);
            }
            for (Tree tree : t.getTreeMap().values()) {
                updateMap(tree, date);
            }
        }
    }

    //更新某个目录下的Blob后，对其上层所有的树name_key_map 全部更新.
    private void updateMap(String[] parentlist) {
        updateMap(parentlist, parentlist[0], 0, this);
    }

    private void updateMap(String[] parentlist, String target_name, int index, Tree t) {

        for (Tree tree : t.getTreeMap().values()) {
            String tree_key = tree.getKey();
            String tree_name = tree.getName();
            if(tree_name.equals(target_name)) {
                name_key_map.put(tree_name, tree_key);
                if (index == parentlist.length - 2) return;
                String next_target_name = target_name + File.separator + parentlist[index + 1];
                updateMap(parentlist, next_target_name,index + 1, tree);
            }
        }
    }


    // Delete file from staging area (blobMap, key_name_map, valuemap).
    public void deleteFile(String filename) {
        String key = name_key_map.get(filename);
        MapDelete(filename);
        // blobMap递归删除，并更新对应tree的值
        boolean flag = deleteBlob(key);
        if (!flag) System.out.println(filename + " not found.");

    }

    // Delete directory from staging area (root and valuemap).
    public void deleteDirectory(String dirname) {
        String key = name_key_map.get(dirname);
        MapDeleteDirectory(dirname, this);
        // treeMap递归删除，并更新对应tree的值
        boolean flag = deleteTree(key);
        if (!flag) System.out.println(dirname + " not found.");
    }

    @Override
    public void compressWrite() {
        FileWriter.writeCompressedObj(path, this);
    }

    // 展示暂存区(valuemap)现存的文件
    public void show() {
        for (String v : valuemap.values()) {
            System.out.println(v);
        }
    }

    // 展示name_key_map现存的文件
    public void showIndexMap() {
        name_key_map.forEach((k, v) -> System.out.println("name: " + k + " key: " + v));
    }

    // 向valuemap新增键值对.
    private void valueMapAdd(String name, String value) {
        // 如果文件名相同，而内容不同，会替换value，删除旧节点，尾部插入新节点.
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        }
        valuemap.put(name,value);
    }

    // 找到对应的directory，然后执行MapDeleteTree操作
    private void MapDeleteDirectory(String dirname, Tree t) {
        if (!name_key_map.containsKey(dirname)) {
            System.out.println("No directory named" + dirname + "found in staging area(name_key_map).");
            return;
        }

        for (Tree tree : t.getTreeMap().values()) {
            String name = tree.getName();
            // 找到对应目标
            if (name.equals(dirname)) {
                MapDeleteTree(tree);
                name_key_map.remove(name);
            }
            // 未找到
            else {
                MapDeleteDirectory(dirname, tree);
            }
        }
    }

    // 递归删除Tree下包含的map(name_key_map, value_map)
    private void MapDeleteTree(Tree t) {
        for (String blobName : t.getBlobMap().values()) {
            MapDelete(blobName);
        }
        for (Tree tree : t.getTreeMap().values()) {
            MapDeleteTree(tree);
            name_key_map.remove(tree.name);
        }
    }

    // 删除Tree下map(name_key_map, value_map)中的文件索引.
    private void MapDelete(String name) {
        if (valuemap.containsKey(name)) {
            valuemap.remove(name);
        } else {
            System.out.println("No file named " + name + " found in staging area (valuemap)");
        }
        if (name_key_map.containsKey(name)) {
            name_key_map.remove(name);
        } else {
            System.out.println("No file named " + name + " found in staging area (name_key_map)");
        }
    }

    public void clear() {
        valuemap = new LinkedHashMap<>();
    }

}
