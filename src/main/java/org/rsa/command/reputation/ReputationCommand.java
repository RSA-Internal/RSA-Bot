package org.rsa.command.reputation;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.reputation.subcommand.ReputationView;

public class ReputationCommand extends CommandObjectV2 {
    public ReputationCommand() {
        super("reputation", "Various reputation commands for this server.", true);
        addSubcommand(new ReputationView());
    }
}
