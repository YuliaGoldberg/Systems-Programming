package bgu.spl.net.impl.BGSServer;

import bgu.spl.net.api.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.srv.DataHolder;
import bgu.spl.net.srv.Server;

public class TPCMain {
    public static void main(String[] args) {
        DataHolder dataHolder = new DataHolder();
        Server.threadPerClient(
                Integer.parseInt(args[0]), //port
                () -> new BidiMessagingProtocolImp(dataHolder), //protocol factory
                MessageEncoderDecoderImp::new //message encoder decoder factory
        ).serve();
    }
}
