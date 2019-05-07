package bgu.spl.net.messages;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Follow extends Message {
    private boolean follow = false;
    private int numOfUsers;
    private ArrayList<String> users;

    public Follow(ArrayList<Byte> format, short opcode) {
        super(opcode);
        if (format.get(2) == '0')
            follow = true;
        this.numOfUsers = (format.get(3) << 8) | format.get(4);
        users = new ArrayList<>();
        int j = 5;
        for (int i = 0; i < numOfUsers; i++) {
            ArrayList<Byte> user = new ArrayList<>();
            while (format.get(j) != 0) {
                user.add(format.get(j));
                j++;
            }
            j++;
            users.add(new String(this.toArray(user), StandardCharsets.UTF_8));
        }
    }

    public ArrayList<String> getUsers() {
        return users;
    }

    public boolean isFollow() {
        return follow;
    }

}
