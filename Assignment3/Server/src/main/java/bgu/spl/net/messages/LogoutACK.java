package bgu.spl.net.messages;

public class LogoutACK extends Message implements SimpleACK {
    private short ACKopcode = 10;

    public LogoutACK(short opcode){
        super(opcode);
    }

    public short getACKopcode() {
        return ACKopcode;
    }
}
