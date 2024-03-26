package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.components.selections.SelectOption;
import net.dv8tion.jda.api.interactions.components.selections.StringSelectMenu;
import org.rsa.adventure.model.Activity;
import org.rsa.command.SubcommandObject;
import org.rsa.util.HelperUtil;

public class AdventureIndexSubcommand extends SubcommandObject {

    public AdventureIndexSubcommand() {
        super("index", "View details about a specific entity index");
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event.reply("Something went wrong, please try again.").setEphemeral(true).queue();
            return;
        }

        // display embed with two select menus
        event
            .replyEmbeds(
                new EmbedBuilder()
                    .setTitle("Index Viewer")
                    .setAuthor(requester.getEffectiveName())
                    .setColor(HelperUtil.getRandomColor())
                    .setThumbnail("https://cdn4.iconfinder.com/data/icons/learning-31/64/dictionary_book_lexicon_work_book_thesaurus-512.png")
                    .addField("Viewing: **Activity** - Hunt", Activity.HUNT.getAsDetails(), true)
                    .build()
            )
            .addActionRow(
                StringSelectMenu
                    .create("index-select-type")
                    .addOptions(
                        SelectOption.of("Activities", "index-activity").withDefault(true),
                        SelectOption.of("Items", "index-item").withDefault(false),
                        SelectOption.of("Rarities", "index-rarity").withDefault(false),
                        SelectOption.of("Skills", "index-skill").withDefault(false),
                        SelectOption.of("Zones", "index-zone").withDefault(false)
                    )
                    .setMaxValues(1)
                    .build()
            )
            .addActionRow(
                StringSelectMenu
                    .create("index-select-entity")
                    .addOptions(Activity.getActivityOptionList())
                    .setMaxValues(1)
                    .build()
            )
            .setEphemeral(true)
            .queue();
    }
}
