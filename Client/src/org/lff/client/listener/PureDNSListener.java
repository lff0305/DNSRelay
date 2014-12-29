package org.lff.client.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.xml.crypto.Data;
import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Arrays;

/**
 * User: LFF
 * Datetime: 2014/12/27 11:34
 */
public class PureDNSListener {

    private static final Logger logger = LoggerFactory.getLogger(PureDNSListener.class);

    public static void main(String[] argu) {
        try {
            new PureDNSListener().run();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }


    public void run() throws SocketException {
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(53);

            logger.info("Listening on DNS Port(53)");

            int ECHOMAX = 2048;

            DatagramPacket packet = new DatagramPacket(new byte[ECHOMAX], ECHOMAX);

            while (true) {
                try {
                    socket.receive(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] data = packet.getData();
                int length = packet.getLength();
                byte[] d = new byte[length];
                System.arraycopy(data, 0, d, 0, length);
                byte[] response = parse(d);

                DatagramPacket re = new DatagramPacket(response, response.length,
                        packet.getAddress(),
                        packet.getPort());
                re.setData(response);
                socket.send(re);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private byte[] parse(byte[] d) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(d));
        short id = dis.readShort();
        byte flag1 = dis.readByte();
        int QR = flag1 >> 7;
        int opcode = (flag1 - QR * 128) >> 4;
        int AA = flag1 & 0x07 >> 2;
        int TC = flag1 & 0x03 >> 1;
        int RD = flag1 & 0x01;
        byte flag2 = dis.readByte();
        int RA = flag2 >> 7;
        int zero = 0;
        int rcode = 0;
        int QDCount = dis.readShort();
        int ANCount = dis.readShort();
        int NSCount = dis.readShort();
        int ARCount = dis.readShort();

        ByteArrayOutputStream questionsSteam = new ByteArrayOutputStream();
        DataOutputStream questions = new DataOutputStream(questionsSteam);

        for (int i=0; i<QDCount; i++) {
            ByteArrayOutputStream name = new ByteArrayOutputStream();
            boolean first = true;
            while (true) {
                byte length = dis.readByte();
                questions.writeByte(length);
                if (length == 0) {
                    break;
                }
                byte[] namePart = new byte[length];
                dis.readFully(namePart);
                questions.write(namePart);
                if (!first) {
                    name.write('.');
                }
                name.write(namePart);
                first = false;
            }
            String host = new String(name.toByteArray());
            logger.info("Query for " + host);
        }

        short QueryType = dis.readShort();
        short QueryClass = dis.readShort();

        questions.close();

        logger.info("QType={}, QClass={}", Integer.toHexString(QueryType),
                Integer.toHexString(QueryClass));


        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeShort(id);
        dos.write((byte)0x81);
        dos.write((byte)0x80);
        //Question Count
        dos.writeShort(1);
        //Answer Count
        dos.writeShort(1);
        dos.writeShort(0);
        dos.writeShort(0);
        dos.write(questionsSteam.toByteArray());
        dos.writeShort(QueryType);
        dos.writeShort(QueryClass);
        dos.writeByte(4);
        dos.write("t66y".getBytes());
        dos.write(3);
        dos.write("com".getBytes());
        dos.writeByte(0);
        dos.writeShort(1);
        dos.writeShort(1);
        dos.writeInt(0xFFFF);
        dos.writeShort(4);
        dos.writeByte(12);
        dos.writeByte(22);
        dos.writeByte(32);
        dos.writeByte(42);
        dos.close();

        logger.info("Response {}", Arrays.toString(bos.toByteArray()));

        return bos.toByteArray();
    }
}

