package org.lff.client.plain;

import org.lff.client.listener.DNSListenerThread;
import org.lff.rsa.RSACipher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



/**
 * User: LFF
 * Datetime:    2015/1/5 10:38
 */
public class PlainClientMain {

    private static final Logger logger = LoggerFactory.getLogger(PlainClientMain.class);

    public static void main(String[] argu) {

        Settings settings;
        PlainDNSClientCallback callback;
        try {
            RSACipher.load();
            logger.info("Public/Private keys loaded successfully.");
        } catch (Exception e) {
            logger.error("Failed to load keys", e);
            return;
        }
        try {
            settings = SettingFactory.getSettings();
            logger.info("Read config as \n {}", settings.toString());
            callback = new PlainDNSClientCallback(settings);
        } catch (Exception e) {
            logger.error("Error loading config", e);
            return;
        }

        DNSListenerThread t = null;
        try {
            t = new DNSListenerThread(callback);
        } catch (Exception e) {
            logger.error("Failed to start listener thread", e);
            return;
        }
        new Thread(t).start();
    }
}
