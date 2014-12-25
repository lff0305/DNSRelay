package org.lff.client.messages;

/**
 * User: LFF
 * Datetime: 2014/12/24 15:19
 */
public class RSAEncryptedRequestMessage extends EncryptedMessage {
    public RSAEncryptedRequestMessage(byte[] data) {
        super(data);
    }
}
