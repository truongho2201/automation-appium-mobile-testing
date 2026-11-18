package com.automation.facades;

import com.automation.pages.TodoPage;
import io.appium.java_client.android.AndroidDriver;
import java.time.LocalDate;
import java.time.LocalTime;

public final class TodoFacade {
    private final TodoPage todoPage;

    public TodoFacade(AndroidDriver driver) {
        this.todoPage = new TodoPage(driver);
    }

    public boolean isTaskListDisplayed() {
        return todoPage.isTaskListDisplayed();
    }

    public void createTask(String taskName) {
        todoPage.createTask(taskName);
    }

    public void startScheduledTask(String taskName, String listName) {
        todoPage.prepareTask(taskName, listName);
    }

    public void setNotificationAndSave(
            LocalDate notificationDate, LocalTime notificationTime, String repeatOption) {
        todoPage.setNotificationAndSave(notificationDate, notificationTime, repeatOption);
    }

    public void updateTaskName(String currentTaskName, String updatedTaskName) {
        todoPage.updateTaskName(currentTaskName, updatedTaskName);
    }

    public void deleteTask(String taskName) {
        todoPage.deleteTask(taskName);
    }

    public boolean hasTask(String taskName) {
        return todoPage.hasTask(taskName);
    }

    public boolean isTaskAbsent(String taskName) {
        return todoPage.isTaskAbsent(taskName);
    }

    public TaskDetails getTaskDetails(String taskName) {
        todoPage.openTaskDetails(taskName);
        try {
            return new TaskDetails(
                    todoPage.getNotificationDate(),
                    todoPage.getNotificationTime(),
                    todoPage.getRepeatOption(),
                    todoPage.getSelectedTaskList());
        } finally {
            todoPage.closeTaskDetails();
        }
    }

    public record TaskDetails(
            String notificationDate,
            String notificationTime,
            String repeatOption,
            String taskList) {
    }
}
