

package bgu.spl.net.impl.newsfeed;
import bgu.spl.net.api.MessageEncoderDecoderImp;
import bgu.spl.net.api.bidi.BidiMessagingProtocolImp;
import bgu.spl.net.srv.DataHolder;
import bgu.spl.net.srv.Server;

public class NewsFeedServerMain {

    public static void main(String[] args) {
        DataHolder dataHolder = new DataHolder();

// you can use any server...
        //   Server.threadPerClient(
        //            7777, //port
        //            () -> new BidiMessagingProtocolImp(dataHolder), //protocol factory
        //           MessageEncoderDecoderImp::new //message encoder decoder factory
        //   ).serve();

        Server.reactor(
                Runtime.getRuntime().availableProcessors(),
                7777, //port
                () ->  new BidiMessagingProtocolImp(dataHolder), //protocol factory
                MessageEncoderDecoderImp::new //message encoder decoder factory
        ).serve();

    }
}
