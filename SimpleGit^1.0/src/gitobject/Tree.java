package gitobject;

import sha1.SHA1;

import zlib.ZLibUtils;
import fileoperation.FileReader;

import java.io.*;
import java.lang.reflect.Array;
import java.security.MessageDigest;
import java.util.*;


public class Tree extends GitObject{

    protected ArrayList<GitObject> treeList;	//GitObjects in tree


    public ArrayList<GitObject> getTreeList(){
        return treeList;
    }

    private static ArrayList<GitObject> constructTree(File file) {
        ArrayList<GitObject> ls = new ArrayList<>();
        List<File> files = sortFile(file.listFiles());

        for(File f : files) {
            if (f.isFile()) {
                try {
                    Blob b = new Blob(f);
                    ls.add(b);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            else {
                try {
                    Tree t = new Tree(f, constructTree(f));
                    ls.add(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return ls;
    }

    public Tree(){}
    
    /**
     * Constructor
     * @param file
     * @throws Exception
     */
    public Tree(File file) throws Exception {

        if (file.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.treeList = constructTree(file);
        this.fmt = "tree";
        this.mode = "040000";
        this.name = file.getName();
        this.value = genValue();
        this.key = genKey(file);
        if (!FileReader.objectExists(key)) { compressWrite();}
    }

    public Tree(File file, ArrayList ls) throws Exception {

        if (file.isFile()) throw new IllegalArgumentException("Must be a directory.");
        this.treeList = ls;
        this.fmt = "tree";
        this.mode = "040000";
        this.name = file.getName();
        this.value = genValue();
        this.key = genKey(file);
        if (!FileReader.objectExists(key)) { compressWrite();}
    }

    /**
     * Deserialize a tree object with treeId and its path.
     * @param Id
     * @param Id
     * @throws IOException
     */
    public static Tree deserialize(String Id) throws IOException {
        try{

            File file = new File(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2));

            return FileReader.readCompressedObject(file, Tree.class);

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
     * @param dir
     * @return String
     * @throws Exception
     */
    public String genKey(File dir) throws Exception{
        return SHA1.getHash("040000 tree " + value);
    }

    @Override
    public String toString(){
        return "040000 tree " + key;
    }

}
