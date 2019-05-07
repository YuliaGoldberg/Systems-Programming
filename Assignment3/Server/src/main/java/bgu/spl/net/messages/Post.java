package bgu.spl.net.messages;

import java.util.ArrayList;

public class Post extends Message implements Sendable {
    private String content;
    private String postSender;
    private ArrayList<String> taggedUsers;

    public Post(ArrayList<Byte> format, short opcode){
        super(opcode);
        taggedUsers = new ArrayList<>();
        String user;
        ArrayList<Byte> contentArray=new ArrayList<>();
        int i=2;
        while(format.get(i)!='\0'){
            contentArray.add(format.get(i));
            i++;
        }
        this.content=new String(this.toArray(contentArray));
        for(int j=0;j<content.length(); j++){
            if(content.charAt(j)=='@'){
                int endOfMessage = content.length()-1;
                int space = content.indexOf(" ",j);
                if(space == -1)
                    space = Integer.MAX_VALUE;
                int endOfTag = Math.min(space,endOfMessage);
                if(endOfTag == space)
                    user = content.substring(j+1, endOfTag);
                else
                    user = content.substring(j+1, endOfTag+1);
                    if(!taggedUsers.contains(user))
                        taggedUsers.add(user);
            }
        }
    }

    public String getContent() {
        return content;
    }

    public ArrayList<String> getTaggedUsers() {
        return taggedUsers;
    }

    public String getSender() {
        return postSender;
    }

    public void setSender(String postSender) {
        this.postSender = postSender;
    }
}
