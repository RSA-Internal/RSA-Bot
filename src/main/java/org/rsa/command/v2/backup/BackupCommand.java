package org.rsa.command.v2.backup;

import org.rsa.command.v2.CommandObjectV2;

public class BackupCommand extends CommandObjectV2 {
    public BackupCommand() {
        super("backup", "various backup commands");
        addSubcommand(new BrowseBackupSubcommand());
        addSubcommand(new DeleteBackupSubcommand());
        addSubcommand(new RestoreBackupSubcommand());
        addSubcommand(new ViewBackupSubcommand());
    }
}
