package org.rsa.command;

import org.rsa.command.commands.CheckInCommand;
import org.rsa.command.commands.CompileCommand;
import org.rsa.command.commands.ConfigureCommand;
import org.rsa.command.commands.ResolveCommand;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    private static final HashMap<String, CommandObject> loadedCommands = new HashMap<>();

    static {
        System.out.println("Loading commands.");

        addCommandObject(new CheckInCommand());
        addCommandObject(new CompileCommand());
        addCommandObject(new ConfigureCommand());
        addCommandObject(new ResolveCommand());
    }

    private static void addCommandObject(CommandObject commandObject) {
        System.out.println("Loading " + commandObject.getName());
        loadedCommands.put(commandObject.getName(), commandObject);
    }

    public static List<CommandObject> getCommands() {
        return new ArrayList<>(loadedCommands.values());
    }

    public static CommandObject getCommand(String name) {
        return loadedCommands.get(name);
    }
}
