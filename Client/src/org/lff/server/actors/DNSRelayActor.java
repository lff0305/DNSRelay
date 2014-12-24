package org.lff.server.actors;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import org.lff.server.messages.RequestMessage;
import org.lff.server.messages.ResponseMessage;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * User: LFF
 * Datetime: 2014/12/24 11:50
 */
public class DNSRelayActor extends UntypedActor {

    private final DatagramSocket socket;

    public DNSRelayActor(DatagramSocket socket) {
        this.socket = socket;
    }

    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof RequestMessage) {
            RequestMessage message = (RequestMessage) o;
            ActorSelection selection = getContext().actorSelection("akka.tcp://RelayServer@127.0.0.1:2552/user/dns");
            selection.tell(message, getSelf());
        }
        if (o instanceof ResponseMessage) {
            ResponseMessage response = (ResponseMessage) o;

            byte[] result = response.getData();
            InetAddress address = InetAddress.getByAddress(response.getInetaddr());
            int port = response.getPort();

            DatagramPacket sendPacket = new DatagramPacket(result, result.length, address, port);
            try {
                socket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
            }
            this.getContext().stop(getSelf());
        }
    }
}
