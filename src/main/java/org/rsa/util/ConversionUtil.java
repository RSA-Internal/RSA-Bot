package org.rsa.util;

public class ConversionUtil {

    public static int parseIntFromString(String toParse) {
        return parseIntFromString(toParse, 0);
    }

    public static int parseIntFromString(String toParse, int defaultValue) {
        try {
            return Integer.parseInt(toParse);
        } catch (NumberFormatException nfe) {
            return defaultValue;
        }
    }
}
