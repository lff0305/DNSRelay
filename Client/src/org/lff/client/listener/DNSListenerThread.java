package org.lff.client.listener;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.lff.client.callback.DNSCallback;
import org.lff.rsa.RSACipher;
import org.lff.client.actors.DNSRelayActor;
import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RequestMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

/**
 * User: LFF
 * Datetime: 2014/12/24 14:41
 */
public class DNSListenerThread implements Runnable {

    private static final Logger logger = LoggerFactory.getLogger(DNSListenerThread.class);

    private DNSCallback callback;

    public DNSListenerThread(DNSCallback callback) {
        this.callback = callback;
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
                byte[] data = packet.getData();
                int length = packet.getLength();
                byte[] d = new byte[length];
                System.arraycopy(data, 0, d, 0, length);



                RequestMessage message = new RequestMessage();
                byte[] bs = new byte[]{(byte) 10, (byte) 16, (byte) 33, (byte) 1};
                message.setDnsServer(bs);
                message.setRequest(d);
                message.setInetaddr(packet.getAddress().getAddress());
                message.setPort(packet.getPort());
                try {
                    this.callback.callback(message, socket);
                } catch (Exception e) {
                    logger.error("Error in callback", e);
                }
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
