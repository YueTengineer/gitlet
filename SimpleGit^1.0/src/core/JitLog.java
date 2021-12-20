package core;

import fileoperation.FileReader;
import gitobject.Commit;
import gitobject.Head;

public class JitLog {

    public static void log() {
        Head head = FileReader.readCompressedObj(Head.getPath() ,Head.class);
        Commit com = Commit.deserialize(head.getCurrentCommit());
        System.out.println(com.getValue());
        System.out.println();
        while (com.getParent().length() != 0) {
            com = Commit.deserialize(com.getParent());
            System.out.println(com.getValue());
            System.out.println();
        }
    }
}
