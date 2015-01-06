package org.lff.client.plain;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * User: LFF
 * Datetime: 2015/1/6 11:53
 */
public class SettingFactory {

    private static AtomicReference<Settings> settings = new AtomicReference<Settings>();

    public static Settings getSettings() {
        if (settings.get() == null) {
            Settings set = new Settings();
            settings.compareAndSet(null, set);
        }
        return settings.get();
    }



}
