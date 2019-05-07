package bgu.spl.net.api.bidi;

import bgu.spl.net.messages.*;
import bgu.spl.net.messages.Error;
import bgu.spl.net.srv.Client;
import bgu.spl.net.srv.DataHolder;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentLinkedQueue;

public class BidiMessagingProtocolImp implements BidiMessagingProtocol<Message>  {
    private int connectionId;
    private Connections<Message> connections;
    private boolean terminate;
    private DataHolder dataHolder;
    private String userName;


    public BidiMessagingProtocolImp(DataHolder dataHolder){
        this.connectionId = -1;
        this.connections = null;
        this.terminate = false;
        this.dataHolder = dataHolder;
    }

    /**
     * Used to initiate the current client protocol with it's personal connection ID and the connections implementation
     **/
    public void start(int connectionId, Connections<Message> connections){
        this.connectionId = connectionId;
        this.connections = connections;
    }

    public void process(Message message){
        if(message instanceof Register){
            this.registerHandler((Register) message);
        }
        else if(message instanceof Login){
            this.loginHandler((Login) message);
        }
        else if(message instanceof Logout){
            this.logoutHandler((Logout) message);
        }
        else if(message instanceof Follow){
            this.followHandler((Follow) message);
        }
        else if(message instanceof Post){
            this.postHandler((Post)message);
        }
        else if(message instanceof PM){
            this.pmHandler((PM)message);
        }
        else if(message instanceof UserList){
            this.userListHandler((UserList)message);
        }
        else if(message instanceof Stat){
            this.statHandler((Stat)message);
        }
    }

    /**
     * @return true if the connection should be terminated
     */
    public boolean shouldTerminate(){return terminate;}

    public void terminate(){this.terminate = true;}

    private void registerHandler(Register register){
        if(this.dataHolder.getClient(register.getUserName()) != null){ //if the client is already registered "get" will not return null
            Error error = new Error(register.getOpcode());
            this.connections.send(this.connectionId,error);
        }
        else{
            Client client = new Client(register.getUserName(),register.getPassword(),this.connectionId,this.connections);
            this.dataHolder.addClient(client);
            RegisterACK registerACK = new RegisterACK(register.getOpcode());
            this.connections.send(this.connectionId,registerACK);
        }
    }

    private void loginHandler(Login login) {
        boolean isRegistered =false;
        boolean isCorrectPassword = false;
        boolean isLoggedIn =false;
        if (this.dataHolder.getClient(login.getUserName()) != null) {
            isRegistered = this.dataHolder.getClient(login.getUserName()).isRegistered();
            isCorrectPassword = this.dataHolder.getClient(login.getUserName()).getPassword().equals(login.getPassword());
            isLoggedIn = this.dataHolder.getClient(login.getUserName()).isLogged();
        }
        if (!isRegistered || !isCorrectPassword || isLoggedIn) {
            Error error = new Error(login.getOpcode());
            this.connections.send(this.connectionId, error);
        }
        else {
            synchronized (this.dataHolder.getClient(login.getUserName())) {//here we sync so a client will not log in while someone else in the process of sending him a message
                this.dataHolder.getClient(login.getUserName()).setLogged(true);
                LoginACK loginACK = new LoginACK(login.getOpcode());
                this.connections.send(this.connectionId, loginACK);
                this.userName = login.getUserName();
                this.dataHolder.getClient(userName).setConnectionId(this.connectionId);
                this.dataHolder.getClient(userName).sendWaiting();
            }
        }
    }
    private void logoutHandler(Logout logout){
        if(isError()){
            Error error = new Error(logout.getOpcode());
            connections.send(this.connectionId,error);
            return;
        }
        LogoutACK logoutACK = new LogoutACK(logout.getOpcode());
        synchronized (this.dataHolder.getClient(this.userName)) { //here we sync so a client will not log out while someone else in the process of sending him a message
            this.dataHolder.getClient(this.userName).setLogged(false);
            this.connections.send(this.connectionId, logoutACK);
            this.terminate();
        }
    }

    private void followHandler(Follow follow) {
        if(isError()){
            Error error = new Error(follow.getOpcode());
            connections.send(this.connectionId,error);
            return;
        }
        ConcurrentLinkedQueue<Client> following = this.dataHolder.getClient(userName).getFollowing();//get this client following list
        boolean isFollow = follow.isFollow();//true-follow, false-unfollow
        ArrayList<String> successful = new ArrayList<>();//the list of users we succeeded to handle
        ArrayList<String> usersToAct = follow.getUsers();//the users we need to follow/unfollow
        if (isFollow) {//if the user wants to follow
            for (int i = 0; i < usersToAct.size(); i++) {
                if (this.dataHolder.getClient(usersToAct.get(i)) != null) {
                    Client currClient = this.dataHolder.getClient(usersToAct.get(i));
                    if (!this.dataHolder.getClient(userName).isFollowingContains(currClient) && !usersToAct.get(i).equals(userName)) {//if the user is already following this client or the request is to follow ourselves.
                        this.dataHolder.getClient(userName).addFollowing(currClient);
                        currClient.addFollower(this.dataHolder.getClient(userName));
                        successful.add(usersToAct.get(i));
                    }
                }
            }
        }
        else {//if the user wants to unfollow
            for (int i = 0; i < usersToAct.size(); i++) {
                Client currClient = this.dataHolder.getClient(usersToAct.get(i));
                if (this.dataHolder.getClient(userName).isFollowingContains(currClient)) {
                    following.remove(currClient);
                    currClient.getFollowers().remove(dataHolder.getClient(userName));
                    successful.add(usersToAct.get(i));
                }
            }
        }
            if (successful.size() == 0) {//if the user couldn't follow none of the requested users
                Error error = new Error(follow.getOpcode());
                connections.send(this.connectionId, error);
            } else {
                FollowACK followACK = new FollowACK(follow.getOpcode(), (short)successful.size(), successful);
                connections.send(this.connectionId, followACK);
            }
    }

    private void postHandler(Post post){
        post.setSender(this.userName);
        if(isError()){
            Error error = new Error(post.getOpcode());
            connections.send(this.connectionId,error);
            return;
        }
        Notification notification = new Notification('1',userName,post.getContent());
        this.dataHolder.getClient(userName).addPost(post);//this is client's post
        ArrayList<String> taggedUsers = post.getTaggedUsers();
        while (taggedUsers.remove(this.userName)){}//if the client was tagged in his own post
        for (int i = 0; i < taggedUsers.size(); i++) {//tagged users
            if(this.dataHolder.getClient(taggedUsers.get(i)) != null) {
                boolean isRegistered = this.dataHolder.getClient(taggedUsers.get(i)).isRegistered();//if the client is in the system
                boolean isFollowingMe = this.dataHolder.getClient(taggedUsers.get(i)).getFollowing().contains(this.dataHolder.getClient(this.userName));
                if (isRegistered && !isFollowingMe) {//if the client that was tagged is not following me
                    synchronized (this.dataHolder.getClient(userName)) {//so the client won't be able to logout while getting the message. if we thought she's logged  out and saved the post for her to read later, she won't be able to login
                        if (this.dataHolder.getClient(taggedUsers.get(i)).isLogged())
                            connections.send(dataHolder.getClient(taggedUsers.get(i)).getConnectionId(), notification);
                        else
                            this.dataHolder.getClient(taggedUsers.get(i)).addWaiting(post);
                    }
                }
            }
        }
        //if the client that was tagged is following me
        for (String currUser : dataHolder.getRegisteredClientsQueue()) {
            ConcurrentLinkedQueue<Client> currQueue = dataHolder.getClient(currUser).getFollowing();
            if(currQueue.contains(this.dataHolder.getClient(userName))){
                synchronized (this.dataHolder.getClient(userName)) { //so the client won't be able to logout while getting the message. if we thought she's logged  out and saved the post for her to read later, she won't be able to login
                    if (dataHolder.getClient(currUser).isLogged())
                        connections.send(dataHolder.getClient(currUser).getConnectionId(), notification);
                    else
                        this.dataHolder.getClient(currUser).addWaiting(post);
                }
            }
        }
        PostACK postACK = new PostACK(post.getOpcode());
        connections.send(this.connectionId,postACK);
    }

    private void pmHandler(PM pm){
        pm.setSender(this.userName);
        if(this.dataHolder.getClient(this.userName) == null || this.dataHolder.getClient(pm.getPMgetter()) == null || !this.dataHolder.getClient(this.userName).isLogged() || this.userName.equals(pm.getPMgetter())) {
            Error error = new Error(pm.getOpcode());
            connections.send(this.connectionId, error);
            return;
        }
        synchronized (this.dataHolder.getClient(pm.getPMgetter())) { //here we sync so destClient wont be able to login or logout
            boolean isDestLoggedIn = this.dataHolder.getClient(pm.getPMgetter()).isLogged(); //while processing pm message to him
            if (!isDestLoggedIn) {
                this.dataHolder.getClient(pm.getPMgetter()).addWaiting(pm);
                PMACK pmack = new PMACK(pm.getOpcode());
                connections.send(this.connectionId, pmack);
                return;
            }
            Notification notification = new Notification('0', userName, pm.getContent());
            this.dataHolder.getClient(userName).addPM(pm);
            connections.send(this.dataHolder.getClient(pm.getPMgetter()).getConnectionId(), notification);
        }
            PMACK pmack = new PMACK(pm.getOpcode());
            connections.send(this.connectionId, pmack);
    }
    private void userListHandler(UserList userList){
        if(isError()){
            Error error = new Error(userList.getOpcode());
            connections.send(this.connectionId,error);
            return;
        }
        ArrayList<String> users = new ArrayList<>();
        ConcurrentLinkedQueue<String> copy = new ConcurrentLinkedQueue<>(dataHolder.getRegisteredClientsQueue());
        for(String user : copy) //^^making snapshot of registered list in case some threads will change it while iterating
            users.add(user);
        UserListACK userListACK = new UserListACK(userList.getOpcode(),users,(short)users.size());
        connections.send(this.connectionId,userListACK);
    }

    private void statHandler(Stat stat){
        boolean isLoggedIn=false;
        boolean isRegistered=false;
        if(this.dataHolder.getClient(stat.getUserName())!=null) {
            isLoggedIn = this.dataHolder.getClient(stat.getUserName()).isLogged();
            isRegistered = this.dataHolder.getClient(stat.getUserName()).isRegistered();
        }
        if (!isLoggedIn || !isRegistered) {
            Error error = new Error(stat.getOpcode());
            connections.send(this.connectionId,error);
            return;
        }
        Client client=this.dataHolder.getClient(stat.getUserName());
        StatACK statACK=new StatACK(stat.getOpcode(),client.getPosts().size(),client.getFollowers().size(),client.getFollowing().size());
        connections.send(this.connectionId, statACK);
    }

    private boolean isError(){
        if(userName == null)
            return true;
        boolean isLoggedIn=false;
        if( this.dataHolder.getClient(this.userName)!=null)
            isLoggedIn = this.dataHolder.getClient(this.userName).isLogged();
        if (!isLoggedIn) {
            return true;
        }
        return false;
    }

}