package gitobject;
import repository.Repository;
import zlib.ZLibUtils;

import java.io.*;
import java.util.*;


public class GitObject implements Serializable {

    protected String fmt;                  //type of object
    protected String key;                  //key of object
    protected String mode;                 //mode of object
    protected static String path = Repository.getGitDir() + File.separator + "objects";          //absolute path of objects
    protected String value;                //value of object
    protected String name;                 // name of object

    public String getFmt(){ return fmt; }
    public String getKey() { return key; }
    public String getMode(){ return mode; }
    public String getPath() { return path; }
    public String getValue(){ return value; }
    public String getName() { return name; }

    public GitObject(){}
    /**
     * Get the value(content) of file
     * @param file
     * @return String
     * @throws IOException
     */


    /**
     * Todo: Serialize the object to the local repository.
     * @throws Exception
     */
    public void writeObject() throws Exception{

        FileOutputStream fos = new FileOutputStream(path + File.separator + key.substring(0, 2) + File.separator + key.substring(2));
        ObjectOutputStream objectStream = new ObjectOutputStream(fos);
        objectStream.writeObject(this);
        objectStream.close();

    }

    /**
     * Todo: Serialize the object and compress with zlib.
     * @throws Exception
     */
    public void compressWrite() throws Exception{
        FileOutputStream fos = new FileOutputStream(path + File.separator + key.substring(0, 2) + File.separator + key.substring(2));
        ZLibUtils.compress(value.getBytes(),fos);
        fos.close();
    }

    public static String getValue(File file) throws IOException {
        StringBuffer bf = new StringBuffer();

        Scanner input = new Scanner(file);
        while (input.hasNextLine()) {
            bf.append(input.nextLine());
        }
        input.close();

        return bf.toString();
    }



}
