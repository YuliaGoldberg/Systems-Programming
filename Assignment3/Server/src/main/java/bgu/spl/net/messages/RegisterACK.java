package bgu.spl.net.messages;

public class RegisterACK extends Message implements SimpleACK{
    private short ACKopcode = 10;

    public RegisterACK(short opcode){
        super(opcode);
    }

    public short getACKopcode() {
        return ACKopcode;
    }
}
