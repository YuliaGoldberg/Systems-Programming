package bgu.spl.net.messages;

import java.util.ArrayList;

public interface ComplicatedACK {
    short getACKopcode();
    short getOpcode();
    byte[] toArray(ArrayList<Byte> bytes);
    ArrayList<String> getUsers();
    short getNumOfUsers();
}
