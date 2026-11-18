package com.automation.pages;

import com.automation.core.ConfigReader;
import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

public final class TodoPage extends BasePage {
    private static final String APP_ID = ConfigReader.getRequired("app.package") + ":id/";
    private static final DateTimeFormatter ACCESSIBLE_DATE_FORMAT =
            DateTimeFormatter.ofPattern("EEEE, MMMM d", Locale.ENGLISH);

    private final By taskList = AppiumBy.id(APP_ID + "recycler_tasks");
    private final By addTaskButton = AppiumBy.id(APP_ID + "fab_add_task");
    private final By newTaskTitle = AppiumBy.id(APP_ID + "toolbar_title");
    private final By taskNameInput = AppiumBy.id(APP_ID + "edtTaskName");
    private final By saveTaskButton = AppiumBy.id(APP_ID + "fab_save");
    private final By deleteTaskButton = AppiumBy.id(APP_ID + "delete");
    private final By confirmDeleteButton = AppiumBy.id("android:id/button1");
    private final By setNotificationDateButton = AppiumBy.id(APP_ID + "btnSetD");
    private final By notificationDateField = AppiumBy.id(APP_ID + "edtDueD");
    private final By nextMonthButton = AppiumBy.id(APP_ID + "month_navigation_next");
    private final By previousMonthButton = AppiumBy.id(APP_ID + "month_navigation_previous");
    private final By confirmDateButton = AppiumBy.id(APP_ID + "confirm_button");
    private final By setNotificationTimeButton = AppiumBy.id(APP_ID + "btnSetT");
    private final By notificationTimeField = AppiumBy.id(APP_ID + "edtDueT");
    private final By amButton = AppiumBy.id(APP_ID + "material_clock_period_am_button");
    private final By pmButton = AppiumBy.id(APP_ID + "material_clock_period_pm_button");
    private final By confirmTimeButton = AppiumBy.id(APP_ID + "material_timepicker_ok_button");
    private final By repeatSwitch = AppiumBy.id(APP_ID + "repeatSwitch");
    private final By repeatValue = AppiumBy.id(APP_ID + "btnRepeat");
    private final By taskListSpinner = AppiumBy.id(APP_ID + "spinnerLists");
    private final By navigateUpButton = AppiumBy.accessibilityId("Navigate up");

    public TodoPage(AndroidDriver driver) {
        super(driver);
    }

    public boolean isTaskListDisplayed() {
        return isVisible(taskList);
    }

    public void openNewTaskForm() {
        tap(addTaskButton);
        waitUntilVisible(newTaskTitle);
    }

    public void enterTaskName(String taskName) {
        type(taskNameInput, taskName);
    }

    public void saveTask() {
        tap(saveTaskButton);
        waitUntilVisible(taskList);
    }

    public void createTask(String taskName) {
        openNewTaskForm();
        enterTaskName(taskName);
        saveTask();
    }

    public void prepareTask(String taskName, String listName) {
        openNewTaskForm();
        enterTaskName(taskName);
        chooseTaskList(listName);
    }

    public void setNotificationAndSave(
            LocalDate notificationDate, LocalTime notificationTime, String repeatOption) {
        chooseNotificationDate(notificationDate);
        selectNotificationTime(notificationTime);
        selectRepeatOption(repeatOption);
        saveTask();
    }

    private void chooseNotificationDate(LocalDate notificationDate) {
        tap(setNotificationDateButton);

        YearMonth displayedMonth = YearMonth.from(ConfigReader.today());
        YearMonth targetMonth = YearMonth.from(notificationDate);
        long monthDifference = ChronoUnit.MONTHS.between(displayedMonth, targetMonth);
        By monthNavigationButton = monthDifference >= 0 ? nextMonthButton : previousMonthButton;
        for (long month = 0; month < Math.abs(monthDifference); month++) {
            tap(monthNavigationButton);
        }

        String accessibleDate = notificationDate.format(ACCESSIBLE_DATE_FORMAT);
        waitUntilVisible(AppiumBy.accessibilityId(accessibleDate)).click();
        tap(confirmDateButton);
    }

    private void chooseTaskList(String listName) {
        tap(taskListSpinner);
        waitUntilVisible(exactText(listName)).click();
    }

    public void selectNotificationTime(LocalTime notificationTime) {
        tap(setNotificationTimeButton);

        boolean isPm = notificationTime.getHour() >= 12;
        tap(isPm ? pmButton : amButton);

        int hourOnClock = notificationTime.getHour() % 12;
        if (hourOnClock == 0) {
            hourOnClock = 12;
        }
        waitUntilVisible(AppiumBy.accessibilityId(hourOnClock + " o'clock")).click();

        String minuteDescription = String.format(Locale.ENGLISH, "%02d minutes", notificationTime.getMinute());
        waitUntilVisible(AppiumBy.accessibilityId(minuteDescription)).click();
        tap(confirmTimeButton);
    }

    public void selectRepeatOption(String repeatOption) {
        tap(repeatSwitch);
        waitUntilVisible(exactText(repeatOption)).click();
        waitUntilVisible(repeatValue);
    }

    public boolean hasTask(String taskName) {
        return isVisible(taskWithExactName(taskName));
    }

    public boolean isTaskAbsent(String taskName) {
        return isAbsent(taskWithExactName(taskName));
    }

    public void updateTaskName(String currentTaskName, String updatedTaskName) {
        openTaskDetails(currentTaskName);
        enterTaskName(updatedTaskName);
        saveTask();
    }

    public void openTaskDetails(String taskName) {
        waitUntilVisible(taskWithExactName(taskName)).click();
        waitUntilVisible(deleteTaskButton);
    }

    public String getNotificationDate() {
        return waitUntilVisible(notificationDateField).getText();
    }

    public String getNotificationTime() {
        return waitUntilVisible(notificationTimeField).getText();
    }

    public String getRepeatOption() {
        return waitUntilVisible(repeatValue).getText();
    }

    public String getSelectedTaskList() {
        WebElement spinner = waitUntilVisible(taskListSpinner);
        return spinner.findElement(AppiumBy.className("android.widget.TextView")).getText();
    }

    public void closeTaskDetails() {
        tap(navigateUpButton);
        waitUntilVisible(taskList);
    }

    public void deleteTask(String taskName) {
        By task = taskWithExactName(taskName);
        if (!isPresent(deleteTaskButton)) {
            waitUntilVisible(task).click();
        }
        tap(deleteTaskButton);
        tap(confirmDeleteButton);
        waitUntilVisible(taskList);
        waitUntilInvisible(task);
    }

    private By taskWithExactName(String taskName) {
        return exactText(taskName);
    }

    private By exactText(String text) {
        return AppiumBy.androidUIAutomator(
                "new UiSelector().text(\"" + escapeUiAutomatorText(text) + "\")");
    }

    private String escapeUiAutomatorText(String value) {
        return value.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
