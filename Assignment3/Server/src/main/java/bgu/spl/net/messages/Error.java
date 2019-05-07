package bgu.spl.net.messages;

public class Error extends Message {
    private short messageOpcode;

    public Error(short messageOpcode){
        super((short) 11);
        this.messageOpcode = messageOpcode;
    }

    public short getMessageOpcode() {
        return messageOpcode;
    }

}
