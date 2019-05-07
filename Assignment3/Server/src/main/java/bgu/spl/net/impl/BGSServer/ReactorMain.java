
package bgu.spl.net.impl.BGSServer;
import bgu.spl.net.api.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.srv.DataHolder;
import bgu.spl.net.srv.Server;

public class ReactorMain {

    public static void main(String[] args) {
        DataHolder dataHolder = new DataHolder();
        Server.reactor(
                Integer.parseInt(args[1]),
                Integer.parseInt(args[0]), //port
                () ->  new BidiMessagingProtocolImp(dataHolder), //protocol factory
                MessageEncoderDecoderImp::new //message encoder decoder factory
        ).serve();
    }
}