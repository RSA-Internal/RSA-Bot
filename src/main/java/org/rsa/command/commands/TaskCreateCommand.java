package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.TaskDAO;
import org.rsa.command.CommandObject;

public class TaskCreateCommand extends CommandObject {

    public TaskCreateCommand() {
        super("task-create", "Create a user task.");
        addOptionData(new OptionData(OptionType.STRING, "task-name",
                "The name of the test being created.", true));
        addOptionData(new OptionData(OptionType.ROLE, "role-reward",
                "The role to be granted after successful test.", true));
        addOptionData(new OptionData(OptionType.ATTACHMENT, "task-prompt",
                "The markdown file for test prompt.", true));
        addOptionData(new OptionData(OptionType.ATTACHMENT, "tests-file",
                "The lua file containing test cases. Use `/tests-example` to view expected format.", true));
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        Guild guild = event.getGuild();
        String taskName = event.getOption("task-name", OptionMapping::getAsString);
        Role roleReward = event.getOption("role-reward", OptionMapping::getAsRole);
        Message.Attachment taskPrompt = event.getOption("task-prompt", OptionMapping::getAsAttachment);
        Message.Attachment testsFile = event.getOption("tests-file", OptionMapping::getAsAttachment);

        if (null == guild) {
            event.reply("Guild object is invalid. Please report this as a bug.").setEphemeral(true).queue();
            return;
        }

        if (null == roleReward) {
            event.reply("The supplied role is invalid.").setEphemeral(true).queue();
            return;
        }

        if (null == taskPrompt) {
            event.reply("Task prompt file is missing or invalid.").setEphemeral(true).queue();
            return;
        }

        if (!"md".equals(taskPrompt.getFileExtension())) {
            event.reply("Please provide a valid `.md` file for the task prompt.").setEphemeral(true).queue();
            return;
        }

        if (null == testsFile) {
            event.reply("Tests file is missing or invalid.").setEphemeral(true).queue();
            return;
        }

        if (!"lua".equals(testsFile.getFileExtension())) {
            event.reply("Please provide a valid `.lua` file for the tests file.").setEphemeral(true).queue();
            return;
        }

        PutItemResponseWithStatus response = TaskDAO.writeTask(guild.getId(), taskName, roleReward.getId(), "task-prompt-holder", "test-file-holder");
        if (response.failed()) {
            event.reply("Failed to write data to DB.\n" + response.message()).setEphemeral(true).queue();
            return;
        }

        event.reply("Successfully created a new task: `" + taskName + "`.").setEphemeral(true).queue();
    }
}
