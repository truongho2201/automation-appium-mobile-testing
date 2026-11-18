package com.automation.context;

import com.automation.core.DriverManager;
import com.automation.facades.TodoFacade;
import java.time.LocalDate;
import java.time.LocalTime;

public final class ScenarioContext {
    private TodoFacade todoFacade;
    private String createdTaskName;
    private String previousTaskName;
    private String deletedTaskName;
    private LocalDate expectedNotificationDate;
    private LocalTime expectedNotificationTime;
    private String expectedRepeatOption;
    private String expectedTaskList;

    public TodoFacade todo() {
        if (todoFacade == null) {
            todoFacade = new TodoFacade(DriverManager.getDriver());
        }
        return todoFacade;
    }

    public String getCreatedTaskName() {
        return createdTaskName;
    }

    public void setCreatedTaskName(String createdTaskName) {
        this.createdTaskName = createdTaskName;
    }

    public void markTaskCreated(String taskName) {
        this.createdTaskName = taskName;
        this.previousTaskName = null;
        this.deletedTaskName = null;
    }

    public String getPreviousTaskName() {
        return previousTaskName;
    }

    public void setPreviousTaskName(String previousTaskName) {
        this.previousTaskName = previousTaskName;
    }

    public void markTaskUpdated(String previousTaskName, String updatedTaskName) {
        this.previousTaskName = previousTaskName;
        this.createdTaskName = updatedTaskName;
    }

    public String getDeletedTaskName() {
        return deletedTaskName;
    }

    public void setDeletedTaskName(String deletedTaskName) {
        this.deletedTaskName = deletedTaskName;
    }

    public void markTaskDeleted(String taskName) {
        this.deletedTaskName = taskName;
        this.createdTaskName = null;
    }

    public LocalDate getExpectedNotificationDate() {
        return expectedNotificationDate;
    }

    public void setExpectedNotificationDate(LocalDate expectedNotificationDate) {
        this.expectedNotificationDate = expectedNotificationDate;
    }

    public LocalTime getExpectedNotificationTime() {
        return expectedNotificationTime;
    }

    public void setExpectedNotificationTime(LocalTime expectedNotificationTime) {
        this.expectedNotificationTime = expectedNotificationTime;
    }

    public String getExpectedRepeatOption() {
        return expectedRepeatOption;
    }

    public void setExpectedRepeatOption(String expectedRepeatOption) {
        this.expectedRepeatOption = expectedRepeatOption;
    }

    public String getExpectedTaskList() {
        return expectedTaskList;
    }

    public void setExpectedTaskList(String expectedTaskList) {
        this.expectedTaskList = expectedTaskList;
    }

    public void setNotificationExpectation(
            LocalDate date, LocalTime time, String repeatOption, String taskList) {
        this.expectedNotificationDate = date;
        this.expectedNotificationTime = time;
        this.expectedRepeatOption = repeatOption;
        this.expectedTaskList = taskList;
    }
}
