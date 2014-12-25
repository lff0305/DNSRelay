package org.lff.client.messages;

import java.io.Serializable;

/**
 * User: LFF
 * Datetime: 2014/12/24 15:18
 */
public class EncryptedMessage implements Serializable {
    private byte[] data;

    public EncryptedMessage(byte[] data) {
        this.data = data;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }
}
