package bgu.spl.net.messages;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class PM extends Message implements Sendable {
    private String PMgetter;
    private String PMsender;
    private String content;

    public PM(ArrayList<Byte> format, short opcode){
        super(opcode);
        ArrayList<Byte> username = new ArrayList<>();
        ArrayList<Byte> content = new ArrayList<>();
        int i = 2;
        while(format.get(i) != 0){
            username.add(format.get(i));
            i++;
        }
        i++;
        while (format.get(i) != 0){
            content.add(format.get(i));
            i++;
        }
        byte[] usernameArr = this.toArray(username);
        byte[] passwordArr = this.toArray(content);
        this.PMgetter = new String(usernameArr, StandardCharsets.UTF_8);
        this.content = new String(passwordArr, StandardCharsets.UTF_8);
    }

    public String getPMgetter() {
        return PMgetter;
    }

    public String getContent() {
        return content;
    }

    public String getSender() {
        return PMsender;
    }

    public void setSender(String PMsender){
        this.PMsender = PMsender;
    }
}
