package org.lff.server.messages;

import java.io.*;

/**
 * User: LFF
 * Datetime: 2014/12/24 11:54
 */
public class RequestMessage implements Serializable {
    private byte[] dnsServer;
    private byte[] request;

    private int port;
    private byte[] inetaddr;

    public RequestMessage() {

    }

    public RequestMessage(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            int length = dis.readInt();
            this.request = new byte[length];
            dis.readFully(this.request);
            length = dis.readInt();
            this.dnsServer = new byte[length];
            dis.readFully(this.dnsServer);
            this.port = dis.readInt();
            length = dis.readInt();;
            this.inetaddr = new byte[length];
            dis.readFully(this.inetaddr);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

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

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bos);
        try {
            ds.writeInt(request.length);
            ds.write(request);
            ds.writeInt(dnsServer.length);
            ds.write(dnsServer);
            ds.writeInt(port);
            ds.writeInt(inetaddr.length);
            ds.write(inetaddr);
            ds.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bos.toByteArray();
    }
}
