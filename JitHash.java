package core;

import fileoperation.FileDeletion;
import repository.Repository;

import java.io.IOException;
import sha1.SHA1;
public class JitHash {
    /**
     * Init repository in your working area.
     * @param filename
     * @throws IOException
     */
    public static void hash(String filename) throws IOException {
        /* Todo: You should pass the filename in this function, and generate a hash file in your repository.
        *   Add your code here.*/
        
        //计算hash值
        //放入仓库并改名

        return SHA1.getHash(value);
        String rootPath=filename.getParent();
        File hashfile=new File(rootPath+ File.separator +.jit+ File.separator +SHA1.getHash(value));

    }
}
