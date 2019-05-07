package bgu.spl.net.messages;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Register extends Message {
    private String userName;
    private String password;

    public Register(ArrayList<Byte> format,short opcode){
       super(opcode);
       ArrayList<Byte> username = new ArrayList<>();
       ArrayList<Byte> password = new ArrayList<>();
       int i = 2;
       while(format.get(i) != 0){
           username.add(format.get(i));
           i++;
       }
       i++;
       while (format.get(i) != 0){
           password.add(format.get(i));
           i++;
       }
       byte[] usernameArr = this.toArray(username);
       byte[] passwordArr = this.toArray(password);
       this.userName = new String(usernameArr, StandardCharsets.UTF_8);
       this.password = new String(passwordArr, StandardCharsets.UTF_8);
    }
    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }
}
