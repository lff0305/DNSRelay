package org.lff.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.pattern.Patterns;
import akka.util.Timeout;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import org.lff.client.actors.DNSListenerActor;
import org.lff.common.messages.OKMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

import java.util.concurrent.TimeoutException;


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

        logger.info("Test remote client started.");
        Timeout timeout = new Timeout(Duration.create(5, "seconds"));
        ActorSelection selection = system.actorSelection("akka.tcp://RelayServer@127.0.0.1:2552/user/dns");
        Future<Object> future = Patterns.ask(selection, new OKMessage(), timeout);
        String result = null;
        try {
            result = (String) Await.result(future, timeout.duration());
        } catch (TimeoutException te) {
            logger.error("Remote client did not response in 5 seconds. Please check.");
            system.shutdown();
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        logger.info("Test result is {}", result);


        d.tell("START", null);
    }
}
