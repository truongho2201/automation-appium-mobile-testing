package com.automation.hooks;

import com.automation.context.ScenarioContext;
import com.automation.core.DriverManager;
import io.cucumber.java.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class TodoHooks {
    private static final Logger LOGGER = LoggerFactory.getLogger(TodoHooks.class);
    private final ScenarioContext context;

    public TodoHooks(ScenarioContext context) {
        this.context = context;
    }

    @After(value = "@todo", order = 50)
    public void deleteCreatedTask() {
        String taskName = context.getCreatedTaskName();
        if (taskName == null || !DriverManager.hasDriver()) {
            return;
        }

        try {
            context.todo().deleteTask(taskName);
            context.setCreatedTaskName(null);
            LOGGER.info("Deleted test task: {}", taskName);
        } catch (RuntimeException exception) {
            LOGGER.warn("Could not delete test task: {}", taskName, exception);
        }
    }
}
