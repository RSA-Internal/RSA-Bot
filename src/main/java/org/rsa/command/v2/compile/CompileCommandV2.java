package org.rsa.command.v2.compile;

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
import org.rsa.wandbox.WandboxAPI;
import org.rsa.wandbox.entities.CompileParameter;
import org.rsa.wandbox.entities.CompileResult;
import org.rsa.wandbox.entities.CompilerInfo;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class CompileCommandV2 extends CommandObjectV2 {

    List<CompilerInfo> compilers = WandboxAPI.getList();
    Set<String> languageList = compilers.stream().map(CompilerInfo::getLanguage).collect(Collectors.toSet());
    Map<String, Set<String>> languageVersions = compilers.stream()
        .collect(Collectors.groupingBy(
            CompilerInfo::getLanguage,
            Collectors.mapping(
                CompilerInfo::getVersion, Collectors.toSet()
            )
        ));

    public CompileCommandV2() {
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
            .limit(25)
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

        List<CompilerInfo> matchingCompilers = compilers.stream()
            .filter(compiler -> compiler.getLanguage().equals(language))
            .filter(compiler -> compiler.getVersion().equals(version))
            .toList();
        if (matchingCompilers.isEmpty()) {
            event
                .reply("No valid compilers matching Language: `" + language + "` and Version: `" + version + "`.")
                .setEphemeral(true)
                .queue();
            return;
        }

        CompilerInfo firstMatchingCompiler = matchingCompilers.get(0);
        CompileParameter compileParameter = new CompileParameter();
        compileParameter.setCompiler(firstMatchingCompiler.getName());
        compileParameter.setCode(code);

        CompileResult result = WandboxAPI.compileJson(compileParameter);
        EmbedBuilder resultDisplay = new EmbedBuilder();
        resultDisplay.setTitle("Code Result");
        resultDisplay.setAuthor(event.getUser().getName());
        resultDisplay.addField("Language", language, true);
        resultDisplay.addField("Version", version, true);
        resultDisplay.addField("Compile Status", result.getStatus(), true);
        if (!result.getCompiler_message().isEmpty()) {
            resultDisplay.addField("Compiler message", "```\n" + result.getCompiler_message().substring(0, Math.min(result.getCompiler_message().length(), 250)) + "\n```", false);
        }
        if (!result.getProgram_message().isEmpty()) {
            resultDisplay.addField("Program message", "```\n" + result.getProgram_message().substring(0, Math.min(result.getProgram_message().length(), 250)) + "\n```", false);
        }

        boolean isEphemeral = true;
        OptionMapping displayMapping = event.getOption("display");
        if (displayMapping != null) {
            isEphemeral = !displayMapping.getAsBoolean();
        }

        event.replyEmbeds(resultDisplay.build()).setEphemeral(isEphemeral).queue();
    }
}
