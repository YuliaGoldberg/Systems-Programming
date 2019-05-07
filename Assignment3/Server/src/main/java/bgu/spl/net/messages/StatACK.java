package bgu.spl.net.messages;

public class StatACK extends Message {
    private int ACKopcode = 10;
    private int numPosts;
    private int numFollowers;
    private int numFollowing;

    public StatACK(short opcode,int numPosts,int numFollowers,int numFollowing){
        super(opcode);
        this.numPosts = numPosts;
        this.numFollowers = numFollowers;
        this.numFollowing = numFollowing;
    }

    public int getACKopcode() {
        return ACKopcode;
    }

    public int getNumPosts() {
        return numPosts;
    }

    public int getNumFollowers() {
        return numFollowers;
    }

    public int getNumFollowing() {
        return numFollowing;
    }
}
