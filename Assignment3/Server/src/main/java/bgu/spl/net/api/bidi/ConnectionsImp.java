package bgu.spl.net.api.bidi;
import bgu.spl.net.srv.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionsImp<T> implements Connections<T>{
    private ConcurrentHashMap<Integer, ConnectionHandler<T>> connectionHandlerMap;//<clientId,>
    private ConcurrentLinkedQueue<ConnectionHandler<T>> connectionHandlersQueue;

    public ConnectionsImp() {
        connectionHandlerMap = new ConcurrentHashMap<>();
        connectionHandlersQueue =new ConcurrentLinkedQueue<>();
    }

    public boolean send(int connectionId, T msg){
        ConnectionHandler<T> client= connectionHandlerMap.get(connectionId);
        if(client!=null){
            client.send(msg);
            return true;
        }
        return false;
    }

    public void broadcast(T msg){
        for (ConnectionHandler<T> connectionHandler : connectionHandlersQueue) {
            connectionHandler.send(msg);
        }
    }

    public void disconnect(int connectionId){
        ConnectionHandler c= connectionHandlerMap.remove(connectionId);
        if(c != null)
            connectionHandlersQueue.remove(c);
    }

    public void connect(int id,ConnectionHandler<T> connectionHandler){
        this.connectionHandlerMap.put(id,connectionHandler);
        this.connectionHandlersQueue.add(connectionHandler);
    }

}
