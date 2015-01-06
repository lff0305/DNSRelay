package org.lff.client.plain;

import java.util.List;

/**
 * User: LFF
 * Datetime: 2015/1/6 12:02
 */
public class ConfigItem {
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
}
