package org.rsa.command;

import org.rsa.command.commands.*;
import org.rsa.command.v2.CommandObjectV2;
import org.rsa.command.v2.backup.BackupCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    private static final HashMap<String, CommandObject> loadedCommands = new HashMap<>();
    private static final HashMap<String, CommandObjectV2> v2Commands = new HashMap<>();

    static {
        System.out.println("Loading commands.");

        addCommandObject(new CheckInCommand());
        addCommandObject(new CheckOutCommand());
        addCommandObject(new CompileCommand());
        addCommandObject(new ConfigureCommand());
        addCommandObject(new ReputationCommand());
        addCommandObject(new ResolveCommand());

        addCommandObjectV2(new BackupCommand());
    }

    private static void addCommandObject(CommandObject commandObject) {
        System.out.println("Loading " + commandObject.getName());
        loadedCommands.put(commandObject.getName(), commandObject);
    }

    private static void addCommandObjectV2(CommandObjectV2 commandObjectV2) {
        System.out.println("Loading " + commandObjectV2.getName());
        v2Commands.put(commandObjectV2.getName(), commandObjectV2);
    }

    public static List<CommandObject> getCommands() {
        return new ArrayList<>(loadedCommands.values());
    }

    public static List<CommandObjectV2> getCommandsV2() {
        return new ArrayList<>(v2Commands.values());
    }

    public static CommandObject getCommand(String name) {
        return loadedCommands.get(name);
    }

    public static CommandObjectV2 getCommandV2(String name) {
        return v2Commands.get(name);
    }
}
