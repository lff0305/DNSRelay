package org.lff.common.messages;

import java.io.*;

/**
 * User: LFF
 * Datetime: 2014/12/24 12:11
 */
public class ResponseMessage  implements Serializable {
    private byte[] data;

    private int port;
    private byte[] inetaddr;

    public ResponseMessage() {

    }

    public ResponseMessage(byte[] data) {
        ByteArrayInputStream bis = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bis);
        try {
            int length = dis.readInt();
            this.data = new byte[length];
            dis.readFully(this.data);
            port = dis.readInt();
            length = dis.readInt();
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

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public byte[] toByteArray() {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream ds = new DataOutputStream(bos);
        try {
            ds.writeInt(data.length);
            ds.write(data);
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
