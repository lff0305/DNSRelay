package org.lff.client.actors;

import akka.actor.ActorContext;
import akka.actor.ActorRef;
import akka.actor.Props;
import org.lff.client.callback.DNSCallback;
import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RequestMessage;
import org.lff.rsa.RSACipher;

import java.net.DatagramSocket;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:18
 */
public class AkkaDNSClientCallback implements DNSCallback {

    private ActorContext context;

    public AkkaDNSClientCallback(ActorContext context) {
        this.context = context;
    }

    public void callback(RequestMessage message, DatagramSocket socket) {
        ActorRef relayActor = context.actorOf(Props.create(DNSRelayActor.class, socket));
        RSAEncryptedRequestMessage m = new RSAEncryptedRequestMessage(RSACipher.encrypt(message.toByteArray()));
        relayActor.tell(m, null);
    }
}
