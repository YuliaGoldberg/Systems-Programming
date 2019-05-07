package bgu.spl.net.messages;

import java.util.ArrayList;

public class UserList extends Message {

    public UserList(ArrayList<Byte> format, short opcode) {
        super(opcode);
    }
}
