package org.lff.server.actors;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: LFF
 * Datetime: 2014/12/24 10:38
 */
public class AkkaServerMain {

    private static final Logger logger = LoggerFactory.getLogger(AkkaServerMain.class);

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
                "  port = 2552\n" +
                "} } }";

        logger.info("Server Started.");

        Config c = ConfigFactory.parseString(config);
        ActorSystem system = ActorSystem.create("RelayServer", c);
        ActorRef s = system.actorOf(Props.create(DNSActor.class), "dns");
        logger.info("Actor " + s.path() + " created.");
    }
}
