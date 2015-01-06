package org.lff.client.plain;

import com.typesafe.config.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * User: LFF
 * Datetime: 2015/1/6 10:48
 */
public class Settings {

    private static final Logger logger = LoggerFactory.getLogger(Settings.class);

    private static final ConcurrentHashMap<String, ConfigItem> cache = new ConcurrentHashMap();

    private String host;
    private String dns;
    private int port;
    private ConfigItem allPurpose;


    private List<ConfigItem> items;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
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
            ConfigList relay = config.getList("relay");
            Iterator<ConfigValue> i = relay.iterator();
            while (i.hasNext()) {
                ConfigObject v = (ConfigObject)i.next();
                String server = (v.toConfig().getString("server"));
                ConfigItem item = new ConfigItem();
                item.setServer(server);
                item.setDomains(v.toConfig().getStringList("domains"));
                this.addItem(item);
                if (item.isAllPurpose()) {
                    this.allPurpose = item;
                }
            }

            this.setHost(host);
            this.setPort(port);
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

    /**
     * Get First DNS Server According to the config.
     * @param domains domain questions in the DNS packet
     * @return DNS server to query
     */
    public byte[] getRealDNS(List<String> domains) {
        for (String domain : domains) {
            ConfigItem item = match(domain);
            if (item != null) {
                return item.getDNSBytes();
            }
        }
        //Nothing found. Returns the ALL purpose DNS.
        return allPurpose.getDNSBytes();
    }

    private ConfigItem match(String domain) {
        ConfigItem i = cache.get(domain);
        if (i != null) {
            return i;
        }

        for (ConfigItem item : items) {
            if (item.match(domain)) {
                cache.putIfAbsent(domain, item);
                return item;
            }
        }

        return null;
    }

    private byte[] toByte(String dns) {
        return null;
    }
}
