package org.lff.server.messages;

import java.io.Serializable;

/**
 * User: LFF
 * Datetime: 2014/12/24 12:11
 */
public class ResponseMessage  implements Serializable {
    private byte[] data;

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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
