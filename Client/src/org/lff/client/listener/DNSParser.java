package org.lff.client.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * User: LFF
 * Datetime: 2015/1/6 11:44
 */
public class DNSParser {

    private static final Logger logger = LoggerFactory.getLogger(DNSParser.class);

    List<String> getQueryDomains(byte[] packet) throws IOException {
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(packet));
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
        logger.debug("Question Count is {}", QDCount);
        int ANCount = dis.readShort();
        int NSCount = dis.readShort();
        int ARCount = dis.readShort();

        ByteArrayOutputStream questionsSteam = new ByteArrayOutputStream();
        DataOutputStream questions = new DataOutputStream(questionsSteam);

        List<String> result = new ArrayList<String>();

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
            logger.debug("A Query for " + host);
            result.add(host);
        }
        return result;
    }

}
