package org.rsa.util;

import java.awt.*;
import java.util.Random;

public class HelperUtil {

    private static final Random RANDOM = new Random();

    public static Color getRandomColor() {
        return new Color(
            RANDOM.nextInt(255),
            RANDOM.nextInt(255),
            RANDOM.nextInt(255)
        );
    }
}
