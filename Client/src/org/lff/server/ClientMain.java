package org.lff.server;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.lff.server.actors.DNSListenerActor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: LFF
 * Datetime: 2014/12/24 11:15
 */
public class ClientMain {

    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);

    public static void main(String[] argu) {

        String config = "akka\n{ actor {\n" +
                "provider = \"akka.remote.RemoteActorRefProvider\"\n" +
                "typed {\n" +
                "timeout = 6000s\n" +
                "}\n" +
                "}\n" +
                "remote {\n" +
                " enabled-transports = [\"akka.remote.netty.tcp\"]\n" +
                "netty.tcp {\n" +
                "  hostname = \"127.0.0.1\"\n" +
                "  port = 2553\n" +
                "} } }";

        logger.info("Client Started.");

        Config c = ConfigFactory.parseString(config);
        ActorSystem system = ActorSystem.create("RelayClient", c);
        ActorRef d = system.actorOf(Props.create(DNSListenerActor.class), "dnsClientListener");
        d.tell("START", null);
    }
}
