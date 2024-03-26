package org.rsa.util;

public class StringUtil {

    public static String capitalizeFirst(String string) {
        return string.substring(0, 1).toUpperCase() + string.substring(1).toLowerCase();
    }

    public static void appendElement(StringBuilder builder, String header, String element) {
        builder.append("**");
        builder.append(header);
        builder.append("**:\n");
        if (element.isEmpty()) {
            builder.append(" - None");
        } else {
            builder.append(element);
        }
        builder.append("\n");
    }
}
