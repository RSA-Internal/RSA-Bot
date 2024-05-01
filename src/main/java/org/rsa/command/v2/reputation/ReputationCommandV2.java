package org.rsa.command.v2.reputation;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.v2.reputation.subcommand.ReputationView;

public class ReputationCommandV2 extends CommandObjectV2 {
    public ReputationCommandV2() {
        super("reputation", "Various reputation commands for this server.", true);
        addSubcommand(new ReputationView());
    }
}
