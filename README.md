This project creates a Custom GUI for "shutdown" command in PowerShell (for more run shutdown /? in cmd);
The project is created with SpringBoot and JavaFX;
There are 2 executable files: run.bat + shutdown.vbs;

flow:

- change any behavior
- rebuild .jar with new version (mvn clean package)
- update version in run.bat
- test shortcut (or the shutdown.vbs that the shortcut points to)

v1.2.0:

- added multiple options for hours with selector, and removed 2h
- set the default at startup for 2 hours
- added timer for 2 minutes, if the user doesn't use mouse over window or press any key, autoclose the window

v1.1.3:

- custom field with selector for min/hours in the first line and 4 options in the next
- at startup clean any process already set
- click button "run" to execute
- the shortcut points towards shutdown.vbs
