package bgu.spl.net.messages;

public interface Sendable {
    String getSender();
    void setSender(String PMsender);
    String getContent();
}
