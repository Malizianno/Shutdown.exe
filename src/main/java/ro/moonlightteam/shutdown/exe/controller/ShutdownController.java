package ro.moonlightteam.shutdown.exe.controller;

import org.springframework.stereotype.Component;

@Component
public class ShutdownController {
    public void execShutdownCommand(String timer) {
        try {
            if (timer == null || timer.isEmpty() || timer.equals("0")) {
                throw new IllegalArgumentException("Invalid timer value: " + timer);
            }

            // Execute the shutdown command
            Process process = Runtime.getRuntime().exec("powershell.exe shutdown -s -t " + timer);
            process.waitFor(); // Wait for the command to complete
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }

    public void execAbortCommand() {
        try {
            // Execute the abort shutdown command
            Process process = Runtime.getRuntime().exec("powershell.exe shutdown -a");
            process.waitFor(); // Wait for the command to complete
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
}

