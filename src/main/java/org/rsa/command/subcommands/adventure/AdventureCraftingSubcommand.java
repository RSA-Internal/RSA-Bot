package org.rsa.command.subcommands.adventure;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import org.rsa.command.SubcommandObject;
import org.rsa.entity.EntityManager;
import org.rsa.entity.recipe.RecipeEntity;
import org.rsa.logic.data.managers.UserAdventureProfileManager;
import org.rsa.logic.data.models.UserAdventureProfile;
import org.rsa.manager.adventure.RecipeBookManager;
import org.rsa.register.adventure.EntityManagerRegister;

import java.util.Optional;
import java.util.stream.Stream;

import static org.rsa.manager.adventure.RecipeBookManager.generateActionRows;
import static org.rsa.util.EmbedBuilderUtil.getRecipeBookEmbedBuilder;

public class AdventureCraftingSubcommand extends SubcommandObject {

    public AdventureCraftingSubcommand() {
        super("craft", "Crafting subcommand");
        addOption(OptionType.STRING, "filter", "Search for all recipes with the provided string.");
        addOption(OptionType.STRING, "query", "Recipe to quick-craft used with quantity.");
        addOption(OptionType.INTEGER, "quantity", "(default: 1) The amount of items to craft, will attempt to craft up to provided value.");
    }

    @Override
    public void handleSubcommand(SlashCommandInteractionEvent event, Guild guild) {
        Member requester = event.getMember();
        if (requester == null) {
            event
                .reply("Something went wrong, please try again.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String filter = event.getOption("filter", OptionMapping::getAsString);
        String query = event.getOption("query", OptionMapping::getAsString);
        Integer quantity = event.getOption("quantity", 1, OptionMapping::getAsInt);

        RecipeBookManager.setUserPage(requester.getId(), 0);
        RecipeBookManager.setUserRecipeIndex(requester.getId(), 0);
        RecipeBookManager.setUserRecipeId(requester.getId(), -1);
        RecipeBookManager.setUserFilter(requester.getId(), "");

        if (filter != null) {
            // Provide filter embed.
            RecipeBookManager.setUserFilter(requester.getId(), filter);
        } else {
            if (query != null) {
                // Attempt quick craft with quantity.
                UserAdventureProfile adventureProfile = UserAdventureProfileManager.fetch(guild.getId(), requester.getId());
                EntityManager<RecipeEntity> recipeManager = EntityManagerRegister.recipeManager;
                Stream<RecipeEntity> recipeStream = recipeManager.getEntityStream();
                Optional<RecipeEntity> optionalRecipe = recipeStream
                    .filter(recipe -> recipe.getName().equalsIgnoreCase(query))
                    .findFirst();

                if (optionalRecipe.isEmpty()) {
                    event.reply("No recipe found with provide name: " + query).setEphemeral(true).queue();
                    return;
                }

                RecipeEntity recipe = optionalRecipe.get();
                int totalCraft = 0;
                for (int i = 0; i < quantity; i++) {
                    if (recipe.performCraft(adventureProfile)) {
                        totalCraft++;
                    } else {
                        break;
                    }
                }

                event
                    .reply("Successfully crafted " + totalCraft + " " + recipe.getName() + ".")
                    .setEphemeral(true)
                    .queue();
                return;
            }
        }

        // Provide indexable recipe book.
        event
            .replyEmbeds(getRecipeBookEmbedBuilder(requester).build())
            .setComponents(generateActionRows(requester))
            .setEphemeral(true)
            .queue();
    }
}
