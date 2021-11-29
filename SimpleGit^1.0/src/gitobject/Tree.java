package gitobject;

import sha1.SHA1;

import fileoperation.FileReader;

import java.io.*;

import java.util.*;


public class Tree extends GitObject{

    protected ArrayList<GitObject> treeList;	//GitObjects in tree


    public ArrayList<GitObject> getTreeList(){
        return treeList;
    }

    private static ArrayList<GitObject> constructTree(File dir) {
        return constructTree(dir, dir.getName());
    }

    // workTree下文件对应的Blob名称 01.txt
    // workTree下文件夹内对应的Blob名称 test/01.txt ; test/test1/01.txt

    private static ArrayList<GitObject> constructTree(File dir, String name) {
        ArrayList<GitObject> ls = new ArrayList<>();
        List<File> files = sortFile(dir.listFiles());

        for(File f : files) {
            String newname = name + File.separator + f.getName();

            if (f.isFile()) {
                try {
                    Blob b = new Blob(f, newname);
                    ls.add(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Tree t = new Tree(f, constructTree(f, newname));
                    ls.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ls;
    }

    public Tree(){
        this.fmt = "tree";
        this.mode = "040000";
        this.treeList = new ArrayList<>();
    }
    
    /**
     * Constructor
     * @param dir
     * @throws Exception
     */
    public Tree(File dir) throws Exception {

        if (dir.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.treeList = constructTree(dir);
        this.fmt = "tree";
        this.mode = "040000";
        this.name = dir.getName();
        this.value = genValue();
        this.key = genKey();
    }


    public Tree(File dir, ArrayList ls) throws Exception {

        if (dir.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.treeList = ls;
        this.fmt = "tree";
        this.mode = "040000";
        this.name = dir.getName();
        this.value = genValue();
        this.key = genKey();
    }

    /**
     * Deserialize a tree object with treeId and its path.
     * @param Id
     * @param Id
     * @throws IOException
     */
    public static Tree deserialize(String Id)  {
        try{

            return FileReader.readCompressedObj(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2), Tree.class);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Sort the files in a certain order. You should consider whether it's a file or a directory.
     * @param fs
     * @return List
     */
    public static List sortFile(File[] fs){
        List fileList = Arrays.asList(fs);
        Collections.sort(fileList, new Comparator<File>() {
            // 按照文件大小进行排序
            @Override
            public int compare(File o1, File o2) {
                long diff = o1.length() - o2.length();
                if (diff > 0) return 1;
                else if (diff == 0) return 0;
                else return -1;

            }
        });
        return fileList;
    }

    public String genValue() throws IOException {
        StringBuffer bf = new StringBuffer();

        for (GitObject go : treeList) {
            bf.append(go.toString() + " "  + go.getName() + "\n");
        }
        return bf.toString();
    }


    /**
     * Generate the key of a tree object.
     * @param
     * @return String
     * @throws Exception
     */
    public String genKey() throws Exception{
        return SHA1.getHash("040000 tree " + value);
    }

    public void add(GitObject go) {
        this.treeList.add(go);
    }

    public void update() throws Exception {
        this.value = genValue();
        this.key = SHA1.getHash("040000 tree " + value);
    }

    @Override
    public String toString(){
        return "040000 tree " + key;
    }

}
