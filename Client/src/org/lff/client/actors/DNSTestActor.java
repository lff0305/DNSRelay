package org.lff.client.actors;

import akka.actor.ActorSelection;
import akka.actor.UntypedActor;
import akka.pattern.Patterns;
import akka.util.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import scala.concurrent.Await;
import scala.concurrent.Future;
import scala.concurrent.duration.Duration;

/**
 * User: LFF
 * Datetime: 2014/12/25 10:34
 */
public class DNSTestActor extends UntypedActor {

    private static final Logger logger = LoggerFactory.getLogger(DNSTestActor.class);


    @Override
    public void onReceive(Object o) throws Exception {
        if (o instanceof String) {
            String msg = (String)o;
            if (msg.equalsIgnoreCase("Test")) {
                logger.info("Test remote client started.");
                Timeout timeout = new Timeout(Duration.create(5, "seconds"));
                ActorSelection selection = getContext().actorSelection("akka.tcp://RelayServer@127.0.0.1:2552/user/dns");
                Future<Object> future = Patterns.ask(selection, msg, timeout);
                String result = (String) Await.result(future, timeout.duration());
                logger.info("Test result is {}", result);
                return;
            }
        }
        unhandled(o);
    }
}
