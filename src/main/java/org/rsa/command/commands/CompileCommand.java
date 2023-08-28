package org.rsa.command.commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.Command;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import org.jetbrains.annotations.NotNull;
import org.rsa.command.CommandObject;
import org.rsa.wandbox.WandboxAPI;
import org.rsa.wandbox.entities.CompileParameter;
import org.rsa.wandbox.entities.CompileResult;
import org.rsa.wandbox.entities.CompilerInfo;

import java.util.*;
import java.util.stream.Collectors;

public class CompileCommand extends CommandObject {

    List<CompilerInfo> compilers = WandboxAPI.getList();
    List<String> languageList = compilers.stream().map(CompilerInfo::getLanguage).distinct().toList();
    Map<String, Set<String>> languageVersions = compilers.stream()
            .collect(Collectors.groupingBy(
                    CompilerInfo::getLanguage,
                    Collectors.mapping(
                            CompilerInfo::getVersion, Collectors.toSet()
                    )
            ));

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
        setIsAutocomplete();

        int max = 0;

        for (Map.Entry<String, Set<String>> entry : languageVersions.entrySet()) {
            if (entry.getValue().size() >= max) {
                max = entry.getValue().size();
            }
        }

        System.out.println("Largest version set: " + max);
    }

    @Override
    public void onCommandAutoCompleteInteraction(@NotNull CommandAutoCompleteInteractionEvent event) {
        List<Command.Choice> options = Collections.emptyList();
        String focusedOption = event.getFocusedOption().getName();

        if (focusedOption.equals("language")) {
            options = getFilteredLanguages(event);
        } else if(focusedOption.equals("version")) {
            options = getFilteredVersions(event);
        }

        event.replyChoices(options).queue();
    }

    private @NotNull List<Command.Choice> getFilteredLanguages(@NotNull CommandAutoCompleteInteractionEvent event) {
        return languageList.stream()
                .filter(lang -> lang.startsWith(event.getFocusedOption().getValue()))
                .map(lang -> new Command.Choice(lang, lang))
                .limit(25)
                .toList();
    }

    private @NotNull List<Command.Choice> getFilteredVersions(@NotNull CommandAutoCompleteInteractionEvent event) {
        OptionMapping languageOption = event.getOption("language");
        Set<String> validVersions = Collections.emptySet();

        if (languageOption != null && languageVersions.containsKey(languageOption.getAsString())) {
            validVersions = languageVersions.get(languageOption.getAsString());
        }

        return validVersions.stream()
                .filter(ver -> ver.contains(event.getFocusedOption().getValue()))
                .map(ver -> new Command.Choice(ver, ver))
                .limit(25)
                .toList();
    }

    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        // TODO: Ship to wandbox.
        OptionMapping languageMapping = event.getOption("language");
        OptionMapping versionMapping = event.getOption("version");
        OptionMapping codeMapping = event.getOption("code");

        String language;
        String version;
        String code;

        if (languageMapping != null) {
            language = languageMapping.getAsString();
        } else {
            language = null;
        }

        if (versionMapping != null) {
            version = versionMapping.getAsString();
        } else {
            version = null;
        }

        if (codeMapping != null) {
            code = codeMapping.getAsString();
        } else {
            code = null;
        }

        if (null == language || null == version || null == code) {
            event.reply("One or more required parameters were `null`.").setEphemeral(true).queue();
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
