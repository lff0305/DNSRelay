package org.lff.server.actors;

import akka.actor.*;
import org.lff.server.listener.DNSListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.DatagramSocket;

/**
 * User: LFF
 * Datetime: 2014/12/24 11:51
 */
public class DNSListenerActor extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(DNSListenerActor.class);

    private ActorRef clientActor;

    private DatagramSocket socket;

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof String) {
            DNSListenerThread t = new DNSListenerThread(this.context());
            new Thread(t).start();
            return;
        }
    }
}


