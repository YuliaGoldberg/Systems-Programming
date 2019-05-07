package bgu.spl.net.messages;

public class Notification extends Message {
    private char notificationType;
    private String postingUser;
    private String content;

    public Notification(char notificationType, String postingUser, String content){
        super((short) 9);
        this.notificationType = notificationType;
        this.postingUser = postingUser;
        this.content = content;
    }

    public char getNotificationType() {
        return notificationType;
    }

    public String getPostingUser() {
        return postingUser;
    }

    public String getContent() {
        return content;
    }
}
