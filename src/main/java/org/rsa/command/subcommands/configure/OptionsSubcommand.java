package org.rsa.command.subcommands.configure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.SubcommandObject;
import org.rsa.logic.data.managers.GuildConfigurationManager;
import org.rsa.logic.data.models.GuildConfiguration;

public class OptionsSubcommand extends SubcommandObject {

    public OptionsSubcommand() {
        super("options", "Various options for a server.");
        addOptions(
            new OptionData(OptionType.STRING, "option", "Specify the setting to change.", true)
                .addChoice("Required characters for a help thread", "req_chars_for_help_thread")
                .addChoice("Length of help thread title", "help_thread_title_length"),
            new OptionData(OptionType.STRING, "value", "The value for the option.", true)
        );
    }

    @Override
    public void handleSubcommand(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        if (guild == null) {
            event
                .reply("This command can only be used in a Server.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String option = event.getOption("option", OptionMapping::getAsString);
        String value = event.getOption("value", OptionMapping::getAsString);

        if (null == option || null == value) {
            event
                .reply("Something went wrong. Please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        GuildConfiguration guildConfig = GuildConfigurationManager.fetch(guild.getId());

        if (option.equals("req_chars_for_help_thread")) {
            guildConfig.setReq_chars_for_help_thread(value);
        } else if (option.equals("help_thread_title_length")) {
            guildConfig.setHelp_thread_title_length(value);
        }

        GuildConfigurationManager.update(guildConfig);
        event
            .reply("✅ **" + option + "** option changed to " + value + ".")
            .setEphemeral(true)
            .queue();
    }
}
