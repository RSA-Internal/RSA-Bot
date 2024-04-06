package org.rsa.task.tasks;

import net.dv8tion.jda.api.JDA;
import org.rsa.task.TaskObject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DevforumUpdateTask extends TaskObject {
    public DevforumUpdateTask(JDA jda, ScheduledExecutorService scheduler) {
        super("DevforumUpdateTask", jda, scheduler);
    }
    protected void execute() {

    }

    public void startTask() {
        scheduledTask = scheduler.scheduleAtFixedRate(this::execute, 0, 30, TimeUnit.SECONDS);
    }
}
