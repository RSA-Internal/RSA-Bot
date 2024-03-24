package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

import java.util.function.Function;
import java.util.function.Supplier;

@Getter
public class OptionValueWrapper<O, V> {

    private final O option;
    private final V value;

    public OptionValueWrapper(SlashCommandInteractionEvent event,
                              Function<? super OptionMapping, ? extends O> optionParser, Supplier<O> optionSupplier,
                              Function<? super OptionMapping, ? extends V> valueParser, Supplier<V> valueSupplier) {
        option = event.getOption("option", optionSupplier, optionParser);
        value = event.getOption("value", valueSupplier, valueParser);
    }

    public OptionValueWrapper(SlashCommandInteractionEvent event,
                              Function<? super OptionMapping, ? extends O> optionParser, O optionFallback,
                              Function<? super OptionMapping, ? extends V> valueParser, V valueFallback) {
        this(event, optionParser, () -> optionFallback, valueParser, () -> valueFallback);
    }

    public OptionValueWrapper(SlashCommandInteractionEvent event,
                              Function<? super OptionMapping, ? extends O> optionParser,
                              Function<? super OptionMapping, ? extends V> valueParser) {
        this(event, optionParser, () -> null, valueParser, () -> null);
    }

    public OptionValueWrapper(O option, V value) {
        this.option = option;
        this.value = value;
    }

    public boolean isValid() {
        return option != null && value != null;
    }
}
