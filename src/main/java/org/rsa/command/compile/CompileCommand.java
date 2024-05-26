package org.rsa.command.compile;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.AutoCompleteQuery;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.panda.jda.command.CommandObjectV2;
import org.panda.jda.command.EventEntities;
import org.rsa.net.apis.ApiFactory;
import org.rsa.net.apis.wandbox.WandboxApi;
import org.rsa.net.apis.wandbox.models.CompileParameterModel;
import org.rsa.net.apis.wandbox.models.CompileResultModel;
import org.rsa.net.apis.wandbox.models.CompilerInfoModel;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static net.dv8tion.jda.api.interactions.commands.build.CommandData.MAX_OPTIONS;

public class CompileCommand extends CommandObjectV2 {
    private final WandboxApi wandboxApi;
    private final List<CompilerInfoModel> compilers;
    private final Set<String> languageList;
    private final Map<String, Set<String>> languageVersions;

    public CompileCommand() {
        super("compile", "Compiles code.");
        addOptionData(new OptionData(OptionType.STRING, "language",
            "The language to compile your code in.", true, true));
        addOptionData(new OptionData(OptionType.STRING, "version",
            "The version of the language to use.", true, true));
        addOptionData(new OptionData(OptionType.STRING, "code",
            "The code to compile.", true));
        addOptionData(new OptionData(OptionType.BOOLEAN, "display",
            "Display the result to the channel.", false));
        setAutocomplete(true);

        wandboxApi = ApiFactory.getWandboxApi();
        compilers = wandboxApi.getList();
        languageList = compilers.stream().map(CompilerInfoModel::language).collect(Collectors.toSet());
        languageVersions = compilers.stream()
                .collect(Collectors.groupingBy(
                        CompilerInfoModel::language,
                        Collectors.mapping(
                                CompilerInfoModel::version,
                                Collectors.toSet()
                        )
                ));
    }

    @Override
    public void processAutoComplete(@NotNull EventEntities<CommandAutoCompleteInteractionEvent> entities) {
        CommandAutoCompleteInteractionEvent event = entities.getEvent();
        AutoCompleteQuery focusedOption = event.getFocusedOption();
        Set<String> setToFilter = Collections.emptySet();

        if (focusedOption.getName().equals("language")) {
            setToFilter = languageList;
        } else if(focusedOption.getName().equals("version")) {
            String language = event.getOption("language", OptionMapping::getAsString);

            if (language != null && languageVersions.containsKey(language)) {
                setToFilter = languageVersions.get(language);
            }
        }

        List<Command.Choice> options = getFilteredOptions(focusedOption.getValue(), setToFilter);
        event.replyChoices(options).queue();
    }

    private @NotNull List<Command.Choice> getFilteredOptions(String query,
                                                             Set<String> setToFilter) {
        return setToFilter.stream()
            .filter(e -> e.startsWith(query))
            .map(e -> new Command.Choice(e, e))
            .limit(MAX_OPTIONS)
            .toList();
    }

    @Override
    public void processSlashCommand(@NotNull EventEntities<SlashCommandInteractionEvent> entities) {
        SlashCommandInteractionEvent event = entities.getEvent();

        String language = event.getOption("language", "Lua", OptionMapping::getAsString);
        String version = event.getOption("version", "5.4.3", OptionMapping::getAsString);
        String code = event.getOption("code", OptionMapping::getAsString);

        if (null == code) {
            event.reply("There was no code provided, or it was not properly received.").setEphemeral(true).queue();
            return;
        }

        List<CompilerInfoModel> matchingCompilers = compilers.stream()
            .filter(compiler -> compiler.language().equals(language))
            .filter(compiler -> compiler.version().equals(version))
            .toList();
        if (matchingCompilers.isEmpty()) {
            event
                .reply("No valid compilers matching Language: `" + language + "` and Version: `" + version + "`.")
                .setEphemeral(true)
                .queue();
            return;
        }

        CompilerInfoModel firstMatchingCompiler = matchingCompilers.get(0);
        CompileParameterModel compileParameter = new CompileParameterModel(code, null, firstMatchingCompiler.name(), null);

        CompileResultModel result = wandboxApi.compileJson(compileParameter);
        EmbedBuilder resultDisplay = new EmbedBuilder();
        resultDisplay.setTitle("Code Result");
        resultDisplay.setAuthor(event.getUser().getName());
        resultDisplay.addField("Language", language, true);
        resultDisplay.addField("Version", version, true);
        resultDisplay.addField("Compile Status", result.getStatus(), true);
        if (!result.compiler_message().isEmpty()) {
            resultDisplay.addField("Compiler message", "```\n" + result.compiler_message().substring(0, Math.min(result.compiler_message().length(), 250)) + "\n```", false);
        }
        if (!result.program_message().isEmpty()) {
            resultDisplay.addField("Program message", "```\n" + result.program_message().substring(0, Math.min(result.program_message().length(), 250)) + "\n```", false);
        }

        boolean isEphemeral = true;
        OptionMapping displayMapping = event.getOption("display");
        if (displayMapping != null) {
            isEphemeral = !displayMapping.getAsBoolean();
        }

        event.replyEmbeds(resultDisplay.build()).setEphemeral(isEphemeral).queue();
    }
}
