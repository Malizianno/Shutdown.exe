package ro.moonlightteam.shutdown.exe;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import ro.moonlightteam.shutdown.exe.controller.ShutdownController;

public class ShutdownExe extends Application {

    private static String[] args;
    private String timer = "0"; // Default timer value

    private CheckBox switchBox15 = new CheckBox("15 min");
    private CheckBox switchBox30 = new CheckBox("30 min");
    private CheckBox switchBox60 = new CheckBox("1 hour");
    private CheckBox switchBox120 = new CheckBox("2 hours");

    public static void launchApp(String[] args) {
        ShutdownExe.args = args;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ShutdownApplication.class);
        ShutdownController controller = context.getBean(ShutdownController.class);

        Label label = new Label("Shutdown");

        // Create the "Run" button
        Button runButton = new Button("Run");
        runButton.setOnAction(e -> {
            controller.execCommand(timer);
            stage.close(); // Close the application after executing the command
        });

        /*
         * SWITCH BOXES
         */

        // defaults for checkboxes
        switchBox15.setSelected(false);
        switchBox30.setSelected(false);
        switchBox60.setSelected(false);
        switchBox120.setSelected(false);

        // Optional: Add listener switchBox15
        switchBox15.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("15: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "900"; // 15 minutes in seconds
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
                switchBox120.setSelected(false);
            }
        });

        // Optional: Add listener switchBox30
        switchBox30.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("30: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "1800"; // 30 minutes in seconds
                switchBox15.setSelected(false);
                switchBox60.setSelected(false);
                switchBox120.setSelected(false);
            }
        });

        // Optional: Add listener switchBox60
        switchBox60.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("60: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "3600"; // 1 hour in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox120.setSelected(false);
            }
        });

        // Optional: Add listener switchBox120
        switchBox120.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("120: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "7200"; // 2 hours in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
            }
        });

        // Stack them vertically
        VBox switchBox = new VBox(10, switchBox15, switchBox30, switchBox60, switchBox120);
        switchBox.setAlignment(Pos.CENTER_LEFT);
        switchBox.setPadding(new Insets(1));

        /*
         * LAYOUT
         */

        // Layout for bottom-right positioning
        HBox buttonBox = new HBox(runButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10));

        // Use BorderPane to position elements
        BorderPane root = new BorderPane();
        root.setTop(switchBox);
        // root.setCenter(label);
        root.setBottom(buttonBox);

        Scene scene = new Scene(root, 300, 200);
        scene.getStylesheets().add(getClass().getResource("/switch.css").toExternalForm());

        stage.setOnCloseRequest(event -> {
            // Optional: show confirmation dialog
            // Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Are you sure you want
            // to exit?");
            // alert.setHeaderText("Exit Confirmation");

            // Optional<ButtonType> result = alert.showAndWait();
            // if (result.isPresent() && result.get() != ButtonType.OK) {
            // event.consume(); // Cancel the close request
            // }
            // stage.close();
            Platform.exit(); // Exit the application
            System.exit(0); // Ensure the application exits completely
        });
        stage.setTitle(label.getText());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/shutdown.png")));
        stage.show();
    }
}
