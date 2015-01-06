package org.lff.client.callback;

import org.lff.common.messages.RequestMessage;

import java.net.DatagramSocket;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:17
 */
public interface DNSCallback {
    public void callback(RequestMessage message, DatagramSocket socket);

    public byte[] getDNSServer();
}
