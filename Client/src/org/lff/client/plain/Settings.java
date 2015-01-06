package org.lff.client.plain;

import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.typesafe.config.impl.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: LFF
 * Datetime: 2015/1/6 10:48
 */
public class Settings {

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    private String host;
    private String dns;
    private int port;

    private List<ConfigItem> items;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getDns() {
        return dns;
    }

    public void setDns(String dns) {
        this.dns = dns;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public Settings() {
        items = new ArrayList<ConfigItem>();
        loadConfig();
    }

    private void loadConfig() {
        try {
            Config config = ConfigFactory.load("client.conf");
            String host = config.getString("host");
            int port = config.getInt("port");
            logger.info("Read local  from config : {} {}", host, port);
            String dns = config.getString("dns");
            logger.info("Read true dns from config : {}", dns);

            ConfigList relay = config.getList("relay");
            Iterator<ConfigValue> i = relay.iterator();
            while (i.hasNext()) {
                ConfigObject v = (ConfigObject)i.next();
                String server = (v.toConfig().getString("server"));
                ConfigItem item = new ConfigItem();
                item.setServer(server);
                item.setDomains(v.toConfig().getStringList("domains"));
                this.addItem(item);
            }

            this.setHost(host);
            this.setPort(port);
            this.setDns(dns);
        } catch (RuntimeException e) {
            logger.error("Error loading config", e);
            throw e;
        }
    }

    private void addItem(ConfigItem item) {
        items.add(item);
    }

    @Override
    public String toString() {
        return "{\n" +
                "   host=" + host + '\n' +
                "   port=" + port + '\n' +
                "   dns=" + dns + '\n' +
                "   items=" + items + '\n' +
                " } \n\n";
    }
}
