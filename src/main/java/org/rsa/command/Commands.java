package org.rsa.command;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.v2.backup.BackupCommand;
import org.rsa.command.v2.compile.CompileCommandV2;
import org.rsa.command.v2.configure.ConfigureCommandV2;
import org.rsa.command.v2.reputation.ReputationCommandV2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Commands {
    private static final HashMap<String, CommandObjectV2> v2Commands = new HashMap<>();

    static {
        System.out.println("Loading commands.");

        addCommandObjectV2(new BackupCommand());
        addCommandObjectV2(new ConfigureCommandV2());
        addCommandObjectV2(new ReputationCommandV2());
        addCommandObjectV2(new CompileCommandV2());
    }

    private static void addCommandObjectV2(CommandObjectV2 commandObjectV2) {
        System.out.println("Loading " + commandObjectV2.getName());
        v2Commands.put(commandObjectV2.getName(), commandObjectV2);
    }

    public static List<CommandObjectV2> getCommandsV2() {
        return new ArrayList<>(v2Commands.values());
    }

    public static CommandObjectV2 getCommandV2(String name) {
        return v2Commands.get(name);
    }
}
