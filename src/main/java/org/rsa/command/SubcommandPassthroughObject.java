package org.rsa.command;

import lombok.Getter;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

import java.util.function.Function;

@Getter
public class SubcommandPassthroughObject extends SubcommandData {

    private final Function<? super SlashCommandInteractionEvent, String> optionParser;
    private final Function<? super SlashCommandInteractionEvent, String> valueParser;

    public SubcommandPassthroughObject(String name, String description,
                                       Function<? super SlashCommandInteractionEvent, String> optionParser,
                                       Function<? super SlashCommandInteractionEvent, String> valueParser) {
        super(name, description);
        this.optionParser = optionParser;
        this.valueParser = valueParser;
    }

    public OptionValueWrapper<String, String> processEvent(SlashCommandInteractionEvent event) {
        return new OptionValueWrapper<>(optionParser.apply(event), valueParser.apply(event));
    }
}
