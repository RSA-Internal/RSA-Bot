package org.rsa.command.backup;

import org.panda.jda.command.CommandObjectV2;
import org.rsa.command.backup.subcommand.BrowseBackupSubcommand;
import org.rsa.command.backup.subcommand.DeleteBackupSubcommand;
import org.rsa.command.backup.subcommand.RestoreBackupSubcommand;
import org.rsa.command.backup.subcommand.ViewBackupSubcommand;

public class BackupCommand extends CommandObjectV2 {
    public BackupCommand() {
        super("backup", "various backup commands");
        addSubcommand(new BrowseBackupSubcommand());
        addSubcommand(new DeleteBackupSubcommand());
        addSubcommand(new RestoreBackupSubcommand());
        addSubcommand(new ViewBackupSubcommand());
    }
}
