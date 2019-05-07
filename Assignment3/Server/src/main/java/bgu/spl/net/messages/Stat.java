package bgu.spl.net.messages;

import java.util.ArrayList;

public class Stat extends Message {
    private String userName;

    public Stat(ArrayList<Byte> format, short opcode) {
        super(opcode);
        int i=2;
        ArrayList<Byte> userNameArray = new ArrayList<>();
        while(format.get(i)!='\0') {
            userNameArray.add(format.get(i));
            i++;
        }
        this.userName=new String(this.toArray(userNameArray));
    }

    public String getUserName() {
        return userName;
    }
}
