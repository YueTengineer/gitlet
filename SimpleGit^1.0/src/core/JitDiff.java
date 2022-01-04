    public static void diffBranch(String brname1, String brname2) {
        try {
            Branch br1 = Branch.deserialize(brname1);
            Branch br2 = Branch.deserialize(brname2);
            Commit com1 = Commit.deserialize(br1.getCommitId());
            Commit com2 = Commit.deserialize(br2.getCommitId());
            Index ind1 = com1.getIndexTree();
            Index ind2 = com2.getIndexTree();
            compareIndex(ind1, ind2);
        } catch (IOException e) {
            e.printStackTrace();
        }