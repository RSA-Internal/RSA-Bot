package org.rsa.task.tasks;

import net.dv8tion.jda.api.JDA;
import org.rsa.task.TaskObject;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DevforumUpdateTask extends TaskObject {
    private static final String TASK_NAME = "DevforumUpdateTask";
    private static final long UPDATE_INTERVAL = 30;
    private static final TimeUnit TIME_UNIT = TimeUnit.SECONDS;

    public DevforumUpdateTask(JDA jda, ScheduledExecutorService scheduler) {
        super(TASK_NAME, jda, scheduler);
    }
    protected void execute() {

    }

    public void startTask() {
        scheduledTask = scheduler.scheduleAtFixedRate(this::execute, 0, UPDATE_INTERVAL, TIME_UNIT);
    }
}
