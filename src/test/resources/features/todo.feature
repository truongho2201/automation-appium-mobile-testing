Feature: Task management

  Background:
    Given the ToDo application session is ready
    And the user is on the task list

  @todo @requiresRealApp
  Scenario: Create a new task successfully
    When the user creates a new task named "Learn Appium"
    Then the new task should appear in the task list

  @todo @requiresRealApp
  Scenario: Create a personal task with a notification date
    When the user starts creating a task named "Scheduled Appium" in the "Personal" list
    And the user sets notification 2 days from today at "07:00 AM" with repeat "Once a Day" and saves it
    Then the new task should appear in the task list
    And the task details should show the selected notification settings and task list

  @todo @update @requiresRealApp
  Scenario: Update an existing task successfully
    Given the user has created a task named "Task to update"
    When the user updates the task name to "Updated Appium"
    Then the updated task should appear and the old task name should no longer be displayed

  @todo @delete @requiresRealApp
  Scenario: Delete an existing task successfully
    Given the user has created a task named "Task to delete"
    When the user deletes the created task
    Then the deleted task should no longer appear in the task list
