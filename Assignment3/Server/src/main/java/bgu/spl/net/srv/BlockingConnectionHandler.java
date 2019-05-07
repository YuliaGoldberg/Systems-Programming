package bgu.spl.net.srv;

import bgu.spl.net.api.MessageEncoderDecoder;
import bgu.spl.net.api.bidi.BidiMessagingProtocol;
import bgu.spl.net.api.bidi.Connections;
import bgu.spl.net.messages.Logout;
import bgu.spl.net.messages.LogoutACK;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.sql.SQLOutput;
import java.util.Arrays;

public class BlockingConnectionHandler<T> implements Runnable, ConnectionHandler<T> {

    private final BidiMessagingProtocol<T> protocol;
    private final MessageEncoderDecoder<T> encdec;
    private final Socket sock;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    private volatile boolean connected = true;
    private int id;
    private Connections<T> connections;


    public BlockingConnectionHandler(Socket sock, MessageEncoderDecoder<T> reader, BidiMessagingProtocol<T> protocol, int id , Connections<T> connections) {
        this.sock = sock;
        this.encdec = reader;
        this.protocol = protocol;
        this.id = id;
        this.connections = connections;
    }

    @Override
    public void run() {
        protocol.start(id,connections);
        try (Socket sock = this.sock) { //just for automatic closing
            int read;
            in = new BufferedInputStream(sock.getInputStream());
            out = new BufferedOutputStream(sock.getOutputStream());
            while (!protocol.shouldTerminate() && connected && (read = in.read()) >= 0) {
                T nextMessage = encdec.decodeNextByte((byte) read);
                if (nextMessage != null) {
                    protocol.process(nextMessage);
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        connections.disconnect(this.id);
    }

    @Override
    public void close() throws IOException {
        connected = false;
        sock.close();
    }

    public void send(T msg){
        try {
            byte[] toWrite = encdec.encode(msg);
            synchronized (out) {//while 2 or more clients try to send "this.client" a message
                out.write(toWrite);
                out.flush();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
}
