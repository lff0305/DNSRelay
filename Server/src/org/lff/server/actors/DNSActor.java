package org.lff.server.actors;

import akka.actor.UntypedActor;
import akka.event.Logging;
import akka.event.LoggingAdapter;
import org.lff.rsa.RSACipher;
import org.lff.server.messages.RSAEncryptedRequestMessage;
import org.lff.server.messages.RSAEncryptedResponseMessage;
import org.lff.server.messages.RequestMessage;
import org.lff.server.messages.ResponseMessage;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.*;

/**
 * User: LFF
 * Datetime: 2014/12/24 11:00
 */
public class DNSActor extends UntypedActor {

    private LoggingAdapter logger = Logging.getLogger(getContext().system(), this);

    @Override
    public void onReceive(Object o) throws Exception {
        RequestMessage requestMessage = null;
        if ((o instanceof RequestMessage)) {
            requestMessage = (RequestMessage) o;
        }
        if (o instanceof RSAEncryptedRequestMessage) {
            RSAEncryptedRequestMessage rsaMessage = (RSAEncryptedRequestMessage)o;
            byte[] data = RSACipher.decrypt(rsaMessage.getData());
            requestMessage = new RequestMessage(data);
        }
        byte[] dns = requestMessage.getDnsServer();
        byte[] data = requestMessage.getRequest();

        byte[] r = doWork(data, dns);
        if (r != null) {
            ResponseMessage message = new ResponseMessage();
            message.setPort(requestMessage.getPort());
            message.setInetaddr(requestMessage.getInetaddr());
            message.setData(r);
            RSAEncryptedResponseMessage m = new RSAEncryptedResponseMessage(RSACipher.encrypt(message.toByteArray()));
            logger.info("Tell " + message + " to " + getSender());
            getSender().tell(m, getSelf());
        }
    }


    private byte[] doWork(byte[] data, byte[] bs) throws IOException {
        InetAddress dns = InetAddress.getByAddress(bs);

        DatagramSocket socket=new DatagramSocket();
        //设置receive()方法的最长阻塞时间
        socket.setSoTimeout(5000);
        DatagramPacket sendPacket = new DatagramPacket(data, data.length, dns, 53);

        byte[] buffer = new byte[1024];

        DatagramPacket receivePacket=new DatagramPacket(buffer, 1024);

        boolean receivedResponse=false;

        int MAXTRIES = 3;
        int tries = 0;

        do{
            //2.使用DatagramSocket类的send()和receive()方法来发送和接收DatagramPacket实例，进行通信
            socket.send(sendPacket);
            try{
                socket.receive(receivePacket);
                if(!receivePacket.getAddress().equals(dns)){
                    throw new IOException("Received packet from an unknown source");
                }
                receivedResponse=true;
            }catch(InterruptedIOException e){
                tries+=1;
                System.out.println("Timed out,"+(MAXTRIES-tries)+" more tries ...");
            }

        }while(!receivedResponse&&(tries<MAXTRIES));

        socket.close();

        byte[] result = null;
        if (receivedResponse) {
            result = new byte[receivePacket.getLength()];
            System.arraycopy(buffer, 0, result, 0, result.length);
        }

        return result;
    }
}
