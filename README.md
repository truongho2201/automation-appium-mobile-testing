# Appium Java BDD framework

A beginner-friendly Android automation framework using Java, Appium, Cucumber, JUnit Platform, Maven, Allure, explicit waits, Page Objects, a workflow Facade, and scenario-scoped dependency injection. Real tests and BDD structure checks use separate Maven profiles so a dry-run cannot be mistaken for an executed E2E result.

## Architecture

```text
Feature -> Step Definition -> TodoFacade -> Page Object -> AndroidDriver -> Android app
```

- `features/` describes business behavior in Gherkin.
- `stepdefinitions/` maps business steps to page actions and assertions.
- `context/` stores scenario-scoped state shared through Cucumber PicoContainer.
- `facades/` exposes business workflows without leaking page-level details to steps.
- `pages/` owns locators, waits, and UI interactions.
- `core/` loads configuration and manages one driver per test thread.
- `hooks/` creates sessions, attaches failure screenshots, and always cleans up.
- `runners/` connects Cucumber to the JUnit Platform.

## Prerequisites

- Windows 11 and JDK 25 (the project compiles with `--release 25`)
- Maven 3.6.3 or newer
- Node.js and Appium Server 3
- Appium UiAutomator2 driver
- Android SDK, `adb`, an emulator or USB-connected Android device
- VS Code: Extension Pack for Java, Maven for Java, Test Runner for Java, and a Cucumber/Gherkin extension

Check the tools in Windows CMD:

```bat
java -version
mvn -version
node --version
appium --version
appium driver list --installed
adb version
```

## Prepare Appium and the device

Start Appium in one CMD window:

```bat
appium
```

Check that Android is visible in another window:

```bat
adb devices
```

Accept the USB debugging prompt if the device is `unauthorized`. Start an emulator first if no physical device is connected.

## Add and configure the APK

1. Copy the real APK to `apps\sample.apk`, or choose another filename.
2. Edit `src\test\resources\config.properties`.
3. Keep `app.path` relative to the project root, for example `app.path=apps/my-app.apk`.
4. Set `app.package` and `app.activity` when the APK manifest does not expose the correct launcher activity. The supplied ToDo app uses `com.splendapps.splendo` and `com.splendapps.splendo.activity.MainActivity`.
5. Keep `auto.grant.permissions=true` on a dedicated test device so Android permission dialogs do not block automation.
6. Set `device.udid` to the value from `adb devices` when more than one device is available. Leave it empty for a single device.
7. Set `test.timezone` to the emulator/app timezone so date assertions remain deterministic across machines.
8. Never store credentials in this file or commit them.

The framework validates that the configured path is a real `.apk` file before contacting Appium and reports the resolved absolute path when it is missing. Any property can be overridden with `-D`, for example:

```bat
mvn clean test -Pe2e -Ddevice.udid=emulator-5554 -Dapp.path=apps\my-app.apk
```

## Run tests and reports

The default Maven command is a safe BDD structure check. It validates Gherkin syntax, glue discovery, and step mappings without starting Appium:

```bat
mvn clean test
```

This default run is a dry-run only. It does not execute application behavior and intentionally enables only console output, so its result must not be published as an E2E or Allure test report. You can also use the explicit profile name `mvn clean test -Pbdd-check`.

Run the real Appium scenarios and automatically generate the Allure HTML report through the `e2e` Maven profile:

```bat
mvn clean verify -Pe2e -Ddevice.udid=emulator-5554
```

Override the profile tag expression when a narrower execution is needed:

```bat
mvn clean verify -Pe2e -Ddevice.udid=emulator-5554 -Dcucumber.filter.tags="@smoke and @requiresRealApp"
```

Reports are written to:

- `target\cucumber-reports\cucumber.html`
- `target\cucumber-reports\cucumber.json`
- `target\allure-results` (raw Allure results)
- `target\site\allure-maven-plugin\index.html` (generated automatically during `verify`)
- console output from the `pretty` plugin

Failure screenshots are embedded in Cucumber report events and propagated to Allure by the Cucumber adapter. The Maven plugin uses the Java-based Allure 2.39.0 report runtime to avoid downloaded Node executables being blocked by Windows application-control policies. `verify` generates the static HTML report automatically. To regenerate it manually without rerunning tests, use:

```bat
mvn allure:report
mvn allure:serve
```

## VS Code run and debug

Open this folder (the folder containing `pom.xml`) in VS Code and allow the Java extensions to import the Maven project. Run `Maven: Reload Project` from the Command Palette after dependency changes. Use `mvn clean test -Pbdd-check` for a structure check, or `mvn clean test -Pe2e` while Appium and a device are available.

To debug, open `TestRunner.java` and use the Test Runner for Java debug action. Add required JVM properties to a VS Code Java test configuration if enabling `@requiresRealApp`. Keep Appium running separately.

## Add a feature and Page Object

1. Add a `.feature` file under `src\test\resources\features` using business language.
2. Add Java step methods under `stepdefinitions`; keep locators and lifecycle hooks out of them.
3. Put cross-step state in a scenario-scoped context injected by Cucumber PicoContainer.
4. Create a Page Object under `pages`, extend `BasePage`, and define inspected locators with `AppiumBy`.
5. Add multi-page or business workflows to a class under `facades/`.
6. Use `waitUntilVisible`, `waitUntilClickable`, `type`, and `tap`; do not add `Thread.sleep()`.
7. Add `@requiresRealApp` while the scenario depends on an APK/device. Add narrower tags such as `@smoke` for selection.
8. Use meaningful assertions against actual state; never add unconditional or fake passes.

The included `TodoPage` uses resource IDs verified against the supplied ToDo APK. Prefer `AppiumBy.accessibilityId` or `AppiumBy.id` for future elements and avoid XPath when a stable accessibility ID or resource ID exists.

## Troubleshooting

- **APK file was not found:** copy it to the configured path or fix `app.path`. Run Maven from the project root.
- **Connection refused on port 4723:** start Appium and confirm `appium.server.url`.
- **No devices/emulators found:** run `adb devices`, start an emulator, reconnect USB, and check Android SDK environment variables.
- **More than one device:** set `device.udid` to one exact `adb devices` identifier.
- **Device unauthorized:** unlock it, accept the debugging prompt, then run `adb kill-server` and `adb start-server` if needed.
- **UiAutomator2 not installed:** run `appium driver install uiautomator2` (only if it is absent from `appium driver list --installed`).
- **Session creation fails:** check Appium terminal logs, Android version compatibility, APK validity, and that the device has free space.
- **Element timeout:** inspect the live screen, verify the app state and locator, and prefer accessibility/resource IDs over brittle XPath.
- **Undefined Cucumber step:** ensure its text matches an annotation and the class is below the `com.automation` glue package.
- **Java/module reflection error:** first update to the pinned dependencies. If a dependency specifically documents a required `--add-opens`, add only that narrow option to Surefire.

## Dependency compatibility note

Appium Java Client 11.0.0 is paired with Selenium 4.50.0 according to the Appium project's compatibility matrix. Selenium is pinned through its BOM to prevent Maven from selecting incompatible transitive versions. Appium 11 depends only on Selenium API, so the support and remote-driver modules used by this framework are declared explicitly.
