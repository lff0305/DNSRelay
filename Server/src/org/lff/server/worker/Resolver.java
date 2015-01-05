package org.lff.server.worker;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * User: LFF
 * Datetime: 2014/12/26 11:26
 */
public class Resolver {
    public static byte[] doWork(byte[] data, byte[] bs) throws IOException {
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
            return result;
        }  else {
            return null;
        }


    }
}
