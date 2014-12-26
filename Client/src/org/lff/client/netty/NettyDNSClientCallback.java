package org.lff.client.netty;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.handler.codec.http.*;
import org.lff.client.callback.DNSCallback;
import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RSAEncryptedResponseMessage;
import org.lff.common.messages.RequestMessage;
import org.lff.common.messages.ResponseMessage;
import org.lff.rsa.RSACipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:22
 */
public class NettyDNSClientCallback implements DNSCallback {

    private static final Logger logger = LoggerFactory.getLogger(NettyDNSClientCallback.class);

    private final HTTPReader httpReader;

    public NettyDNSClientCallback(String host, int port) throws URISyntaxException {
        this.httpReader = new HTTPReader(host, port);

    }

    public void callback(RequestMessage message, DatagramSocket socket) {

        logger.debug("Request Message Hash = " + Arrays.hashCode(message.toByteArray()));

        RSAEncryptedRequestMessage m = new RSAEncryptedRequestMessage(RSACipher.encrypt(message.toByteArray()));

        byte[] result = this.httpReader.run(m.getData(), socket);

        if (result == null) {
            return;
        }

        result = RSACipher.decrypt(result);
        ResponseMessage response = new ResponseMessage(result);

        logger.debug("Response Message Hash = " + Arrays.hashCode(response.toByteArray()));


        logger.debug("Response Data is {} {}", response.getData().length, Arrays.toString(response.getData()));

        DatagramPacket sendPacket = null;
        try {
            logger.debug("Response to " + Arrays.toString(response.getInetaddr()) + " " + response.getPort());
            sendPacket = new DatagramPacket(response.getData(), response.getData().length,
                    InetAddress.getByAddress(response.getInetaddr()), response.getPort());
            socket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
        }


    }
}
