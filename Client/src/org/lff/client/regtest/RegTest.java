package org.lff.client.regtest;

/**
 * User: LFF
 * Datetime: 2015/1/6 13:04
 */
public class RegTest {
    public static void main(String[] argu) {
        String s = "(www\\.)?google.*";
        System.out.println("api.google.com".matches(s));
    }
}
