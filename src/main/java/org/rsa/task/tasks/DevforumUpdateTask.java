package org.rsa.task.tasks;

import net.dv8tion.jda.api.JDA;
import org.rsa.discourse.DiscourseAPIHelper;
import org.rsa.discourse.models.CategoryDetailsModel;
import org.rsa.exception.ApiException;
import org.rsa.task.TaskObject;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class DevforumUpdateTask extends TaskObject {
    private static final String TASK_NAME = "DevforumUpdateTask";
    private static final long UPDATE_INTERVAL = 5;
    private static final TimeUnit TIME_UNIT = TimeUnit.MINUTES;

    public DevforumUpdateTask(JDA jda, ScheduledExecutorService scheduler) {
        super(TASK_NAME, jda, scheduler);
    }
    protected void execute() {
        try {
            Map<String, CategoryDetailsModel> categories = DiscourseAPIHelper.fetchAllCategoryInformation();
        } catch(ApiException | IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void startTask() {
        scheduledTask = scheduler.scheduleAtFixedRate(this::execute, 0, UPDATE_INTERVAL, TIME_UNIT);
    }
}
