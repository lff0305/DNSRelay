package org.lff.server.messages;

import java.io.Serializable;

/**
 * User: LFF
 * Datetime: 2014/12/24 11:54
 */
public class RequestMessage implements Serializable {
    private byte[] dnsServer;
    private byte[] request;

    private int port;
    private byte[] inetaddr;

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public byte[] getInetaddr() {
        return inetaddr;
    }

    public void setInetaddr(byte[] inetaddr) {
        this.inetaddr = inetaddr;
    }

    public byte[] getDnsServer() {
        return dnsServer;
    }

    public void setDnsServer(byte[] dnsServer) {
        this.dnsServer = dnsServer;
    }

    public byte[] getRequest() {
        return request;
    }

    public void setRequest(byte[] request) {
        this.request = request;
    }
}
