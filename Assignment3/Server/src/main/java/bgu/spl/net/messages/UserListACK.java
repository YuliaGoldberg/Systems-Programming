package bgu.spl.net.messages;

import java.util.ArrayList;

public class UserListACK extends Message implements ComplicatedACK {
    private short ACKopcode = 10;
    private ArrayList<String> users;
    private short numOfUsers;

    public UserListACK(short opcode,ArrayList<String> users,short numOfUsers){
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
