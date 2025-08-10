package ro.moonlightteam.shutdown.exe.controller;

import org.springframework.stereotype.Component;

@Component
public class ShutdownController {
    public void execCommand(String timer) {
        try {
            // Execute the shutdown command
            Process process = Runtime.getRuntime().exec("powershell.exe shutdown -s -t " + timer);
            process.waitFor(); // Wait for the command to complete
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }
}

