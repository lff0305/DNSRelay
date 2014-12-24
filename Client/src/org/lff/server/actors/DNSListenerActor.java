package org.lff.server.actors;

import akka.actor.*;
import akka.actor.dsl.Creators;
import org.lff.server.messages.RequestMessage;
import org.lff.server.messages.ResponseMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

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


class DNSListenerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DNSListenerThread.class);

    private ActorContext context;

    public DNSListenerThread(ActorContext context) {
        this.context = context;
    }

    public void run() {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(53);

            logger.info("Listening on DNS Port(53)");

            int ECHOMAX = 2048;

            DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

            while (true) {
                socket.receive(packet);
                int length = packet.getLength();
                byte[] data = packet.getData();

                ActorRef relayActor = context.actorOf(Props.create(DNSRelayActor.class, socket));

                RequestMessage message = new RequestMessage();
                byte[] bs = new byte[]{(byte) 10, (byte) 16, (byte) 33, (byte) 1};
                message.setDnsServer(bs);
                message.setRequest(data);
                message.setInetaddr(packet.getAddress().getAddress());
                message.setPort(packet.getPort());
                relayActor.tell(message, null);

            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}