package org.lff.client.plain;

import org.lff.client.callback.DNSCallback;
import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RequestMessage;
import org.lff.common.messages.ResponseMessage;
import org.lff.rsa.RSACipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

/**
 * User: LFF
 * Datetime: 2015/1/5 10:40
 */
public class PlainDNSClientCallback implements DNSCallback {

    private static final Logger logger = LoggerFactory.getLogger(PlainDNSClientCallback.class);

    private String host;
    private int port;
    private String dns;
    private byte[] dnsBytes = new byte[4];

    public PlainDNSClientCallback(Settings settings) {
        this.host = settings.getHost();
        this.port = settings.getPort();
        this.dns = settings.getDns();

        String[] dn = dns.split("\\.");
        if (dn.length != 4) {
            logger.error("Invalid dns server {}", dns);
            throw new RuntimeException("Invalid dns server " + dns);
        }
        try {
            dnsBytes[0] = Byte.valueOf(dn[0]).byteValue();
            dnsBytes[1] = Byte.valueOf(dn[1]).byteValue();
            dnsBytes[2] = Byte.valueOf(dn[2]).byteValue();
            dnsBytes[3] = Byte.valueOf(dn[3]).byteValue();
        } catch (Exception e) {
            logger.error("Invalid dns server {}", dns);
            throw new RuntimeException("Invalid dns server " + dns);
        }
    }

    public void callback(RequestMessage message, DatagramSocket socket) {

        logger.debug("Request Message Hash = " + Arrays.hashCode(message.toByteArray()));

        RSAEncryptedRequestMessage m = new RSAEncryptedRequestMessage(RSACipher.encrypt(message.toByteArray()));

        byte[] result = run(m.getData());

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

    public byte[] getDNSServer() {
        return this.dnsBytes;
    }

    /**
     * Send data to remote relayer.
     * @param data
     * @return
     */
    private byte[] run(byte[] data) {

        DatagramSocket socket = null;

        try {
            logger.debug("Relay to {}:{}", host, port);
            DatagramPacket sendPacket = new DatagramPacket(data, data.length,
                    InetAddress.getByName(host), port);
            socket = new DatagramSocket();
            socket.send(sendPacket);
            logger.debug("Data is sent by {}", sendPacket.getSocketAddress());
            byte[] buffer = new byte[1024 * 32];
            DatagramPacket received = new DatagramPacket(buffer, 1024 * 32);
            socket.setSoTimeout(4000);
            socket.receive(received);
            int length = received.getLength();
            byte[] result = new byte[length];
            System.arraycopy(received.getData(), 0, result, 0, length);
            return result;
        } catch (IOException e) {
            logger.error("Error in receiving relay", e);
            return null;
        } finally {
            if (socket != null) {
                socket.close();
            }
        }
    }
}
