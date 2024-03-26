package org.rsa.adventure.model;

import lombok.Getter;

@Getter
public enum Currency {
    PANDA_COIN(1, "Panda Coin", "<:panda_coin:816361278007410688>")
    ;

    private final Integer id;
    private final String name;
    private final String emojiId;

    Currency(Integer id, String name) {
        this(id, name, "");
    }

    Currency(Integer id, String name, String emojiId) {
        this.id = id;
        this.name = name;
        this.emojiId = emojiId;
    }
}
