package org.lff.server.plain;

import org.lff.common.messages.RSAEncryptedRequestMessage;
import org.lff.common.messages.RSAEncryptedResponseMessage;
import org.lff.common.messages.RequestMessage;
import org.lff.common.messages.ResponseMessage;
import org.lff.rsa.RSACipher;
import org.lff.server.worker.Resolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * User: LFF
 * Datetime: 2014/12/26 10:04
 */
public class PlainServer {

    private static final Logger logger = LoggerFactory.getLogger(PlainServer.class);

    private static ExecutorService service = Executors.newCachedThreadPool();

    public static void start(int listenPort) {
        final DatagramSocket socket;
        try {
            socket = new DatagramSocket(listenPort);

            logger.info("Listening on Port({})", listenPort);

            int ECHOMAX = 4096;

            final DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

            while (true) {
                socket.receive(packet);
                final byte[] data = packet.getData();
                final int length = packet.getLength();
                final int port = packet.getPort();
                final InetAddress addr = packet.getAddress();
                service.submit(new Runnable() {
                    public void run() {
                        byte[] d = new byte[length];
                        System.arraycopy(data, 0, d, 0, length);
                        byte[] decrypted = RSACipher.decrypt(d);
                        RequestMessage requestMessage = new RequestMessage(decrypted);
                        byte[] dnsServer = requestMessage.getDnsServer();
                        byte[] dnsRequest = requestMessage.getRequest();

                        try {
                            byte[] result = Resolver.doWork(dnsRequest, dnsServer);
                            if (result == null) {
                                return;
                            }

                            logger.debug("Real DNS Response is {} {}", result.length, Arrays.toString(result));

                            ResponseMessage m = new ResponseMessage();
                            m.setData(result);
                            m.setPort(requestMessage.getPort());
                            m.setInetaddr(requestMessage.getInetaddr());

                            logger.debug("Response Message Hash = " + Arrays.hashCode(m.toByteArray()));

                            byte[] r = RSACipher.encrypt(m.toByteArray());
                            logger.debug("Encrypted Response is {} {}", r.length, Arrays.toString(r));
                            logger.debug("Will send to {}:{}", addr, port);
                            DatagramPacket response = new DatagramPacket(r, r.length,
                                    addr, port);
                            socket.send(response);
                        } catch (IOException e) {
                            logger.error("Error do work", e);
                            return;
                        }
                    }
                });
            }

        } catch (SocketException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
