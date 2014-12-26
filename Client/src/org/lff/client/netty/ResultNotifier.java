package org.lff.client.netty;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

/**
 * User: LFF
 * Datetime: 2014/12/26 11:44
 */
public class ResultNotifier {

    private static final Logger logger = LoggerFactory.getLogger(ResultNotifier.class);

    public static Map<String, Semaphore> map = new HashMap<String, Semaphore>();
    public static Map<String, byte[]> data = new HashMap<String, byte[] >();

    public static void tell(String serial, byte[] array) {
        logger.info("Tell " + serial + " to map. Length = " + array.length);
        Semaphore s = map.get(serial);
        data.put(serial, array);
        if (s == null) {
            return;
        }
        s.release();
    }

    public static byte[] waitFor(String serial) throws InterruptedException {
        byte[] result = data.remove(serial);
        if (result != null) {
            return result;
        }
        Semaphore s = new Semaphore(0);
        map.put(serial, s);
        logger.info("Put " + serial + " to map.");
        boolean b = s.tryAcquire(5, TimeUnit.SECONDS);
        if (b) {
            result = data.remove(serial);
            return result;
        }
        return null;
    }
}
