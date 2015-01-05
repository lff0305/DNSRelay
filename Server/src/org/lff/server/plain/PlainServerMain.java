package org.lff.server.plain;

import org.lff.server.netty.NettyServer;
import org.lff.server.plain.PlainServer;

/**
 * User: LFF
 * Datetime: 2015/1/5 10:53
 */
public class PlainServerMain {
    public static void main(String[] argu) {
        PlainServer.start(8080);
    }
}
