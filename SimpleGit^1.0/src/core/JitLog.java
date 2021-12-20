package core;

import fileoperation.FileReader;
import gitobject.Commit;
import gitobject.Head;

public class JitLog {

    public static void log() {
        Head head = FileReader.readCompressedObj(Head.getPath() ,Head.class);
        Commit com = Commit.deserialize(head.getCurrentCommit());
        System.out.println(com.getValue());
        while (com.getParent() != null) {
            com = Commit.deserialize(com.getParent());
            System.out.println(com.getValue());
        }
    }
}
