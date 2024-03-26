package org.rsa.adventure.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Getter
@AllArgsConstructor
public enum Rarity {
    COMMON(1, "Common", "C"),
    ALPHA(2, "Alpha", "A*"),
    BETA(3, "Beta", "B*"),
    UNCOMMON(4, "Uncommon", "U"),
    RARE(5, "Rare", "R"),
    EPIC(6, "Epic", "E"),
    MYTHICAL(7, "Mythical", "M");

    private final Integer id;
    private final String name;
    private final String prefix;

    private static Stream<Rarity> rarityStream() {
        return Arrays.stream(Rarity.values());
    }

    public static List<SelectOption> getRarityOptionList() {
        return getRarityOptionList(1);
    }

    public static List<SelectOption> getRarityOptionList(int defaultIndex) {
        return rarityStream()
            .filter(rarity -> rarity.id > 0)
            .map(rarity ->
                SelectOption
                    .of(rarity.name, "rarity-" + rarity.id)
                    .withDescription("")
                    .withDefault(rarity.id == defaultIndex))
            .toList();
    }

    public static Rarity getById(int id) {
        return rarityStream().filter(rarity -> rarity.id.equals(id)).findFirst().orElse(null);
    }

    public String getAsDetails() {
        return "- ID: " + id +
            "\n- Name: " + name +
            "\n- Prefix: " + prefix;
    }
}
