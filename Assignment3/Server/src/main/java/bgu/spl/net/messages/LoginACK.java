package bgu.spl.net.messages;

public class LoginACK extends Message implements SimpleACK {
    private short ACKopcode = 10;

    public LoginACK(int opcode){
        super((short) opcode);
    }

    public short getACKopcode() {
        return ACKopcode;
    }
}
