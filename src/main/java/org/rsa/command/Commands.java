package org.rsa.command;

import lombok.extern.slf4j.Slf4j;
import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.backup.BackupCommand;
import org.rsa.command.compile.CompileCommand;
import org.rsa.command.configure.ConfigureCommand;
import org.rsa.command.reputation.ReputationCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class Commands {
    private static final HashMap<String, CommandObjectV2> commands = new HashMap<>();

    static {
        log.info("Loading commands.");

        addCommandObject(new BackupCommand());
        addCommandObject(new ConfigureCommand());
        addCommandObject(new ReputationCommand());
        addCommandObject(new CompileCommand());
    }

    private static void addCommandObject(CommandObjectV2 commandObjectV2) {
        log.info("Loading " + commandObjectV2.getName());
        commands.put(commandObjectV2.getName(), commandObjectV2);
    }

    public static List<CommandObjectV2> getCommands() {
        return new ArrayList<>(commands.values());
    }

    public static CommandObjectV2 getCommand(String name) {
        return commands.get(name);
    }
}
