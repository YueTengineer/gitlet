package gitobject;

import fileoperation.FileReader;
import sha1.SHA1;
import zlib.ZLibUtils;

import java.io.*;
import java.util.List;
import java.util.Scanner;

public class Blob extends GitObject{

	public String getFmt(){
        return fmt;
    }
    public String getMode(){
        return mode;
    }
    public String getPath() {
        return path;
    }
    public String getValue(){
        return value;
    }
    public String getKey() { return key; }

    public Blob(){};
    /**
     * Constructing blob object from a file
     * @param file
     * @throws Exception
     */
    public Blob(File file) throws Exception {
        fmt = "blob";
        mode = "100644";
        value = genValue(file);
        name = file.getName();
        key = genKey(file);
        if (!FileReader.objectExists(key)) { compressWrite();}
    }

    /**
     * Deserialize a blob object from an existed hash file in .jit/objects.
     * @param Id
     * @throws IOException
     */
    public static Blob deserialize(String Id) throws IOException {
        try{

            File file = new File(path +  File.separator + Id.substring(0,2) + File.separator + Id.substring(2));

            return FileReader.readCompressedObject(file, Blob.class);

        }
        catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }



    public String genValue(File file) throws IOException {
        StringBuffer bf = new StringBuffer();

        Scanner input = new Scanner(file);
        while (input.hasNextLine()) {
            bf.append(input.nextLine());
        }
        input.close();

        return bf.toString();
    }

    /**
     * Generate key from file.
     * @param file
     * @return String
     * @throws Exception
     */
    public String genKey(File file) throws Exception {
        return SHA1.getHash("100644 blob " + value);
    }



    @Override
    public String toString(){
        return "100644 blob " + key;
    }
}
