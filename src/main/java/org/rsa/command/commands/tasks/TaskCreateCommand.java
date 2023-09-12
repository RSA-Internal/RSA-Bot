package org.rsa.command.commands.tasks;

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
import org.rsa.exception.ValidationException;
import org.rsa.util.ValidationUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import static org.rsa.cache.TaskCache.guildTaskListCache;

public class TaskCreateCommand extends CommandObject {

    private static final int MAX_TASKS_PER_GUILD = 5;

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
    public void handleSlashCommand(@NotNull SlashCommandInteractionEvent event) throws ValidationException {
        Guild guild = event.getGuild();
        String taskName = event.getOption("task-name", OptionMapping::getAsString);
        Role roleReward = event.getOption("role-reward", OptionMapping::getAsRole);
        Message.Attachment taskPrompt = event.getOption("task-prompt", OptionMapping::getAsAttachment);
        Message.Attachment testsFile = event.getOption("tests-file", OptionMapping::getAsAttachment);

        ValidationUtil.notNull("Guild", guild);
        ValidationUtil.notNull("Role reward", roleReward);
        ValidationUtil.notNull("Task prompt", taskPrompt);
        ValidationUtil.notNull("Tests file", testsFile);
        ValidationUtil.areEqual("Task prompt file type", taskPrompt.getFileExtension(), "md");
        ValidationUtil.areEqual("Tests file file type", testsFile.getFileExtension(), "lua");

        int taskCount = TaskDAO.getGuildTaskCount(guild.getId());
        if (taskCount >= MAX_TASKS_PER_GUILD) {
            throw new ValidationException(String.format("Guild has %s tasks, max allowed is %s", taskCount, MAX_TASKS_PER_GUILD));
        }

        String taskPromptContents = getAttachmentContent(taskPrompt);
        String testFileContents = getAttachmentContent(testsFile);

        ValidationUtil.notNull("Task prompt contents", taskPromptContents);
        ValidationUtil.notNull("Test file contents", testFileContents);

        PutItemResponseWithStatus response = TaskDAO.writeTask(guild.getId(), taskName, roleReward.getId(), taskPromptContents, testFileContents);
        if (response.failed()) {
            event.reply("Failed to write data to DB.\n" + response.message()).setEphemeral(true).queue();
            return;
        }

        guildTaskListCache.refresh(guild.getId());
        event.reply("Successfully created a new task: `" + taskName + "`.").setEphemeral(true).queue();
    }
}
