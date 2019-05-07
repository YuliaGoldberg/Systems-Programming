package bgu.spl.net.messages;

public class PostACK extends Message implements SimpleACK {
    private short ACKopcode = 10;

    public PostACK(short opcode){
        super(opcode);
    }

    public short getACKopcode() {
        return ACKopcode;
    }
}