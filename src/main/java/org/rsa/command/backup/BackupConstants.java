package org.rsa.command.backup;

import java.util.ArrayList;
import java.util.List;

public final class BackupConstants {

    public static final List<String> SUPPORTED_CATEGORIES = new ArrayList<>() {{
        add("emojis");
        add("stickers");
        add("servericon");
    }};
}
