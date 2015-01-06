package org.lff.client.plain;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * User: LFF
 * Datetime: 2015/1/6 12:02
 */
public class ConfigItem {

    private static final Logger logger = LoggerFactory.getLogger(ConfigItem.class);



    private String server;
    private List<String> domains;

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public List<String> getDomains() {
        return domains;
    }

    public void setDomains(List<String> domains) {
        this.domains = domains;
    }

    @Override
    public String toString() {
        return  "\n    {" +
                "\n       server='" + server + '\n' +
                "       domains=" + domains + '\n' +
                "    }";


    }

    public byte[] getDNSBytes() {
        try {
            String[] dn = this.server.split("\\.");
            byte[] result = new byte[4];
            result[0] = Byte.valueOf(dn[0]).byteValue();
            result[1] = Byte.valueOf(dn[1]).byteValue();
            result[2] = Byte.valueOf(dn[2]).byteValue();
            result[3] = Byte.valueOf(dn[3]).byteValue();
            return result;
        } catch (Exception e) {
            logger.error("Cannot parse dns server {}", server);
            return null;
        }
    }

    public boolean isAllPurpose() {
        return this.domains.size() == 1 &&
               this.domains.get(0).equals("*");
    }

    public boolean match(String domain) {
        for (String d : this.domains) {
            if (domain.contains(d)) {
                return true;
            }
        }
        return false;
    }
}
