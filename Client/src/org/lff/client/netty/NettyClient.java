package org.lff.client.netty;

import org.lff.client.listener.DNSListenerThread;

import java.net.URISyntaxException;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:15
 */
public class NettyClient {
    public static void main(String[] argu) {
        String host = "127.0.0.1";
        int port = 8080;
        DNSListenerThread t = null;
        try {
            t = new DNSListenerThread(new NettyDNSClientCallback(host, port));
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }
        new Thread(t).start();
    }
}
