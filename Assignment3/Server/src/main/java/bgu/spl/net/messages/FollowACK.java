package bgu.spl.net.messages;

import java.util.ArrayList;

public class FollowACK extends Message implements ComplicatedACK {
    private short ACKopcode = 10;
    private ArrayList<String> users;
    private short numOfUsers;

    public FollowACK(short opcode,short numOfUsers,ArrayList<String> users){
        super(opcode);
        this.users = users;
        this.numOfUsers = numOfUsers;
    }

    public short getACKopcode() {
        return ACKopcode;
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public short getNumOfUsers() {
        return numOfUsers;
    }
}
