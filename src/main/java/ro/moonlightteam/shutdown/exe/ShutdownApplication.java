package ro.moonlightteam.shutdown.exe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ShutdownApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShutdownApplication.class, args);
		ShutdownExe.launchApp(args); // Launch JavaFX after Spring Boot starts
	}

}
