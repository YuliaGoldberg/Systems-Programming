package bgu.spl.net.messages;

public class PMACK extends Message implements SimpleACK {
    private short ACKopcode = 10;

    public PMACK(short opcode){
        super((short) opcode);
    }

    public short getACKopcode() {
        return ACKopcode;
    }
}
