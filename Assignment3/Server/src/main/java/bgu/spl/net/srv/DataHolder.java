package bgu.spl.net.srv;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class DataHolder {
    private ConcurrentHashMap<String,Client> clients; //all the clients who are connected to the server <name,password>
    private ConcurrentLinkedQueue<String> registeredClients;

    public DataHolder() {
        clients = new ConcurrentHashMap<>();
        registeredClients = new ConcurrentLinkedQueue<>();
    }

    public Client getClient(String userName){
        return clients.get(userName);
    }

    public void addClient(Client client){
        this.clients.put(client.getUserName(),client);
        this.registeredClients.add(client.getUserName());
        client.setRegistered(true);
    }

    public ConcurrentLinkedQueue<String> getRegisteredClientsQueue() {
        return registeredClients;
    }
}
