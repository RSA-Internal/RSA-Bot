package org.rsa.task;

import lombok.Getter;
import net.dv8tion.jda.api.JDA;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;

public abstract class TaskObject {
    @Getter
    private final String name;
    protected JDA jda;
    protected ScheduledFuture<?> scheduledTask;
    protected ScheduledExecutorService scheduler;

    public TaskObject(String name, JDA jda, ScheduledExecutorService scheduler) {
        this.name = name;
        this.jda = jda;
        this.scheduler = scheduler;
    }

    protected abstract void execute();
    public abstract void startTask();
    public void stopTask() {
        scheduledTask.cancel(true);
    }
}
