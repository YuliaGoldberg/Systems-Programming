package bgu.spl.net.messages;

import java.util.ArrayList;

public class Message {
    private short opcode;

    public Message(short opcode){
        this.opcode=opcode;
    }

    public short getOpcode() {
        return opcode;
    }

    public void setOpcode(short opcode) {
        this.opcode = opcode;
    }

    public byte[] toArray(ArrayList<Byte> bytes){//creating an array from arrayList
        byte[] arr = new byte[bytes.size()];
        for (int i = 0; i < arr.length ; i++)
            arr[i] = bytes.get(i);
        return arr;
    }
}
