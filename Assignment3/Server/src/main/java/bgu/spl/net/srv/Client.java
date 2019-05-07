package bgu.spl.net.srv;

import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.messages.*;

import java.util.concurrent.ConcurrentLinkedQueue;

public class Client {
    private String userName;
    private String password;
    private int connectionId;
    private boolean login;
    private boolean register;
    private ConcurrentLinkedQueue<Client> following;
    private ConcurrentLinkedQueue<Client> followers;
    private ConcurrentLinkedQueue<Post> posts;
    private ConcurrentLinkedQueue<PM> pms;
    private Connections<Message> connections;
    private ConcurrentLinkedQueue<Sendable> waiting;

    public Client(String userName, String password, int connectionId,Connections<Message> connections){
        this.waiting = new ConcurrentLinkedQueue<>();
        this.connections = connections;
        this.userName = userName;
        this.password = password;
        this.connectionId = connectionId;
        this.following = new ConcurrentLinkedQueue<>();
        this.followers = new ConcurrentLinkedQueue<>();
        this.posts = new ConcurrentLinkedQueue<>();
        this.pms = new ConcurrentLinkedQueue<>();
        this.register = false;
        this.login = false;
    }

    public void setLogged(boolean value) {
        this.login = value;
    }

    public void sendWaiting(){
        for (Sendable currSendable : this.waiting) {
            if(currSendable instanceof PM) {
                Notification PMnotification = new Notification('0', currSendable.getSender(), currSendable.getContent());
                this.connections.send(this.connectionId, PMnotification);
            }
            else if (currSendable instanceof Post){
                Notification postNotification = new Notification('1', currSendable.getSender(), currSendable.getContent());
                this.connections.send(this.connectionId, postNotification);
            }
        }
        this.waiting.clear();
    }

    public void setRegistered(boolean registered) {
        register = registered;
    }

    public void setConnectionId(int connectionId) {
        this.connectionId = connectionId;
    }

    public void addFollowing(Client client){
        this.following.add(client);
    }

    public void addWaiting(Sendable toSend){
        this.waiting.add(toSend);
    }

    public void addFollower(Client client){
        this.followers.add(client);
    }

    public void addPost(Post post){
        this.posts.add(post);
    }

    public void addPM(PM pm){
        this.pms.add(pm);
    }

    public int getConnectionId() {
        return connectionId;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public boolean isLogged() {
        return login;
    }

    public boolean isRegistered() {
        return register;
    }

    public ConcurrentLinkedQueue<Client> getFollowing() {
        return following;
    }

    public ConcurrentLinkedQueue<Client> getFollowers() {
        return followers;
    }

    public ConcurrentLinkedQueue<Post> getPosts() {
        return posts;
    }

    public boolean isFollowingContains(Client otherClient){
        for(Client client:this.following)
            if(client.getUserName().equals(otherClient.getUserName()))
                return true;
        return false;
    }
}