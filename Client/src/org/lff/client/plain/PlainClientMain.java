package org.lff.client.plain;

import org.lff.client.listener.DNSListenerThread;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * User: LFF
 * Datetime: 2015/1/5 10:38
 */
public class PlainClientMain {

    private static final Logger logger = LoggerFactory.getLogger(PlainClientMain.class);

    public static void main(String[] argu) {
        String host = "127.0.0.1";
        int port = 8080;
        DNSListenerThread t = null;
        try {
            t = new DNSListenerThread(new PlainDNSClientCallback(host, port));
        } catch (Exception e) {
            logger.error("Failed to start listener thread", e);
            return;
        }
        new Thread(t).start();
    }
}
