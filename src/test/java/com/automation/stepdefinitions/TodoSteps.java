package com.automation.stepdefinitions;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.automation.context.ScenarioContext;
import com.automation.core.ConfigReader;
import com.automation.core.DriverManager;
import com.automation.facades.TodoFacade.TaskDetails;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TodoSteps {
    private static final DateTimeFormatter TASK_SUFFIX =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private final ScenarioContext context;

    public TodoSteps(ScenarioContext context) {
        this.context = context;
    }

    @Given("the ToDo application session is ready")
    public void theTodoApplicationSessionIsReady() {
        assertNotNull(DriverManager.getDriver().getSessionId(), "The Appium session was not created.");
    }

    @Given("the user is on the task list")
    public void theUserIsOnTheTaskList() {
        assertTrue(context.todo().isTaskListDisplayed(), "The task list is not visible.");
    }

    @When("the user creates a new task named {string}")
    public void theUserCreatesANewTaskNamed(String taskName) {
        String uniqueTaskName = uniqueTaskName(taskName);
        context.markTaskCreated(uniqueTaskName);
        context.todo().createTask(uniqueTaskName);
    }

    @Given("the user has created a task named {string}")
    public void theUserHasCreatedATaskNamed(String taskName) {
        String uniqueTaskName = uniqueTaskName(taskName);
        context.markTaskCreated(uniqueTaskName);
        context.todo().createTask(uniqueTaskName);
    }

    @When("the user updates the task name to {string}")
    public void theUserUpdatesTheTaskNameTo(String taskName) {
        String currentTaskName = context.getCreatedTaskName();
        assertNotNull(currentTaskName, "No task was created before the update.");

        String updatedTaskName = uniqueTaskName(taskName);
        context.todo().updateTaskName(currentTaskName, updatedTaskName);
        context.markTaskUpdated(currentTaskName, updatedTaskName);
    }

    @When("the user deletes the created task")
    public void theUserDeletesTheCreatedTask() {
        String taskName = context.getCreatedTaskName();
        assertNotNull(taskName, "No task was created before the delete.");

        context.todo().deleteTask(taskName);
        context.markTaskDeleted(taskName);
    }

    @When("the user starts creating a task named {string} in the {string} list")
    public void theUserStartsCreatingATaskInTheList(String taskName, String listName) {
        String uniqueTaskName = uniqueTaskName(taskName);
        context.markTaskCreated(uniqueTaskName);
        context.setExpectedTaskList(listName);
        context.todo().startScheduledTask(uniqueTaskName, listName);
    }

    @When("the user sets notification {int} days from today at {string} with repeat {string} and saves it")
    public void theUserSetsNotificationAndSaves(
            int daysFromToday, String time, String repeatOption) {
        LocalDate notificationDate = ConfigReader.today().plusDays(daysFromToday);
        LocalTime notificationTime = LocalTime.parse(
                time, INPUT_TIME_FORMAT);

        context.setNotificationExpectation(
                notificationDate, notificationTime, repeatOption, context.getExpectedTaskList());
        context.todo().setNotificationAndSave(notificationDate, notificationTime, repeatOption);
    }

    @Then("the new task should appear in the task list")
    public void theNewTaskShouldAppearInTheTaskList() {
        String taskName = context.getCreatedTaskName();
        assertNotNull(taskName, "No task was created by this scenario.");
        assertTrue(context.todo().hasTask(taskName), "The new task was not found: " + taskName);
    }

    @Then("the updated task should appear and the old task name should no longer be displayed")
    public void verifyUpdatedTaskName() {
        String previousTaskName = context.getPreviousTaskName();
        String updatedTaskName = context.getCreatedTaskName();
        assertNotNull(previousTaskName, "No original task name was saved by this scenario.");
        assertNotNull(updatedTaskName, "No updated task name was created by this scenario.");
        assertTrue(
                context.todo().hasTask(updatedTaskName),
                "The updated task was not found: " + updatedTaskName);
        assertTrue(
                context.todo().isTaskAbsent(previousTaskName),
                "The old task name is still displayed: " + previousTaskName);
    }

    @Then("the deleted task should no longer appear in the task list")
    public void verifyDeletedTaskIsAbsent() {
        String deletedTaskName = context.getDeletedTaskName();
        assertNotNull(deletedTaskName, "No task was deleted by this scenario.");
        assertTrue(
                context.todo().isTaskAbsent(deletedTaskName),
                "The deleted task is still displayed: " + deletedTaskName);
    }

    @Then("the task details should show the selected notification settings and task list")
    public void verifySelectedNotificationSettingsAndTaskList() {
        assertNotNull(context.getExpectedNotificationDate(), "No notification date was selected.");
        assertNotNull(context.getExpectedNotificationTime(), "No notification time was selected.");
        assertNotNull(context.getExpectedRepeatOption(), "No repeat option was selected.");
        assertNotNull(context.getExpectedTaskList(), "No task list was selected.");

        TaskDetails actualDetails = context.todo().getTaskDetails(context.getCreatedTaskName());
        String expectedDateText = context.getExpectedNotificationDate().format(EXPECTED_DATE_FORMAT);
        String expectedTimeText = context.getExpectedNotificationTime().format(EXPECTED_TIME_FORMAT);

        assertEquals(expectedDateText, actualDetails.notificationDate(), "Unexpected notification date.");
        assertEquals(expectedTimeText, actualDetails.notificationTime(), "Unexpected notification time.");
        assertEquals(context.getExpectedRepeatOption(), actualDetails.repeatOption(), "Unexpected repeat option.");
        assertEquals(context.getExpectedTaskList(), actualDetails.taskList(), "Unexpected task list.");
    }

    private String uniqueTaskName(String taskName) {
        return taskName + " " + ConfigReader.now().format(TASK_SUFFIX);
    }

    private static final DateTimeFormatter INPUT_TIME_FORMAT =
            DateTimeFormatter.ofPattern("hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter EXPECTED_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEE, MMMM d", Locale.ENGLISH);
    private static final DateTimeFormatter EXPECTED_TIME_FORMAT =
            DateTimeFormatter.ofPattern("h:mm a", Locale.ENGLISH);
}
