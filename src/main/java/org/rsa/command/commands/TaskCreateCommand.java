package org.rsa.command.commands;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.AttachmentProxy;
import org.jetbrains.annotations.NotNull;
import org.rsa.aws.ddb.PutItemResponseWithStatus;
import org.rsa.aws.ddb.TaskDAO;
import org.rsa.command.CommandObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

public class TaskCreateCommand extends CommandObject {

    private static final int MAX_TASKS_PER_GUILD = 1;

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

    private String getAttachmentContent(Message.Attachment attachment) {
        try {
            AttachmentProxy attachmentProxy = attachment.getProxy();
            CompletableFuture<InputStream> futureStream = attachmentProxy.download();
            InputStream attachmentStream = futureStream.get();
            return new BufferedReader(new InputStreamReader(attachmentStream, StandardCharsets.UTF_8))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (InterruptedException | ExecutionException e) {
            return null;
        }
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

        int taskCount = TaskDAO.getGuildTaskCount(guild.getId());
        if (taskCount >= MAX_TASKS_PER_GUILD) {
            event
                    .reply("Guild currently has " + taskCount + " task, max allowed is " + MAX_TASKS_PER_GUILD + ". Your task was not created.")
                    .setEphemeral(true)
                    .queue();
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

        String taskPromptContents = getAttachmentContent(taskPrompt);

        if (null == taskPromptContents) {
            event
                .reply("Failed to parse task prompt contents, please try again later.")
                .setEphemeral(true)
                .queue();
            return;
        }

        String testFileContents = getAttachmentContent(testsFile);

        if (null == testFileContents) {
            event
                    .reply("Failed to parse test file contents, please try again later.")
                    .setEphemeral(true)
                    .queue();
            return;
        }

        PutItemResponseWithStatus response = TaskDAO.writeTask(guild.getId(), taskName, roleReward.getId(), taskPromptContents, testFileContents);
        if (response.failed()) {
            event.reply("Failed to write data to DB.\n" + response.message()).setEphemeral(true).queue();
            return;
        }

        event.reply("Successfully created a new task: `" + taskName + "`.").setEphemeral(true).queue();
    }
}
