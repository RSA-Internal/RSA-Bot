package org.rsa.task;

import net.dv8tion.jda.api.JDA;
import org.rsa.task.tasks.DevforumUpdateTask;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class TaskScheduler {
    private static final HashMap<String, TaskObject> tasks = new HashMap<>();
    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void initTasks(JDA jda) {
        System.out.println("Starting tasks.");

        addTaskObject(new DevforumUpdateTask(jda, scheduler));

        tasks.forEach((s, taskObject) -> {
            try {
                taskObject.startTask();
            } catch (Exception e) {
                System.err.println(MessageFormat.format("Failed to schedule task: {0}. \n", taskObject.getName()) + e.getMessage());
            }
        });
    }

    private static void addTaskObject(TaskObject taskObject) {
        System.out.println("Loading task " + taskObject.getName());
        tasks.put(taskObject.getName(), taskObject);
    }

    public static TaskObject getTaskObject(String name) {
        return tasks.get(name);
    }

    public static void stopTasks() {
        tasks.forEach((s, taskObject) -> {
            try {
                taskObject.stopTask();
            } catch (Exception e) {
                System.err.println(MessageFormat.format("Failed to stop task: {0}. \n", taskObject.getName()) + e.getMessage());
            }
        });
    }

    public static void stopTask(String name) {
        TaskObject taskObject = getTaskObject(name);
        if (taskObject != null) {
            try {
                taskObject.stopTask();
            } catch (Exception e) {
                System.err.println(MessageFormat.format("Failed to stop task: {0}. \n", name) + e.getMessage());
            }
        }
    }
}
