package ro.moonlightteam.shutdown.exe;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import ro.moonlightteam.shutdown.exe.controller.ShutdownController;

public class ShutdownExe extends Application {

    public static final String TIME_HOUR_LABEL = "ore";
    public static final String TIME_MINUTE_LABEL = "minute";

    private static String[] args;
    private String timer = "0"; // Default timer value

    private CheckBox switchBox15 = new CheckBox("15 min");
    private CheckBox switchBox30 = new CheckBox("30 min");
    private CheckBox switchBox60 = new CheckBox("1 hour");
    private CheckBox switchBox120 = new CheckBox("2 hours");

    TextField inputField = new TextField();
    ComboBox<String> dropdown = new ComboBox<>();

    public static void launchApp(String[] args) {
        ShutdownExe.args = args;
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        ApplicationContext context = new AnnotationConfigApplicationContext(ShutdownApplication.class);
        ShutdownController controller = context.getBean(ShutdownController.class);

        try {
            // start with an abort command to cancel any existing shutdown timers
            controller.execAbortCommand();
        } catch (Exception e) {
            System.out.println("Exception while aborting shutdown: " + e.getMessage());
        }

        Label label = new Label("Shutdown");

        // Create the "Run" button
        Button runButton = new Button("Run");
        runButton.setOnAction(e -> {
            System.out.println("Run button clicked");
            System.out.println("Input field value: " + inputField.getText());
            System.out.println("Dropdown value: " + dropdown.getValue());
            System.out.println("Timer value: " + timer);

            System.out.println("inputField.getText() == 0: " + (inputField.getText().equals("0")));
            System.out.println("timer != 0: " + (!timer.equals("0")));

            System.out.println("isNotInputField(): " + isNotInputField());
            System.out.println("isInputFieldIsInHours(): " + isInputFieldIsInHours());
            System.out.println("isInputFieldIsInMinutes(): " + isInputFieldIsInMinutes());

            if (isNotInputField()) {
                System.out.println("Closed with switchbox; value: " + timer);

                controller.execShutdownCommand(timer);
            } else if (isInputFieldIsInHours()) {
                System.out.println("Closed with input; value: " + inputField.getText() + " hours");
                // Convert hours to seconds
                float hours = Float.parseFloat(inputField.getText());
                float seconds = hours * 3600;

                controller.execShutdownCommand((int) seconds + "");
            } else if (isInputFieldIsInMinutes()) {
                System.out.println("Closed with input; value: " + inputField.getText() + " minutes");
                // Convert minutes to seconds
                int minutes = Integer.parseInt(inputField.getText());
                int seconds = minutes * 60;

                controller.execShutdownCommand(seconds + "");
            }

            if (hasAnythingRun()) {
                handleClose(null); // Close the application after executing the command
            } else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "No time set! Do you want to exit?");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    handleClose(null); // Close the application after executing no command
                }
            }
        });

        // FIRST ROW

        // --- Top row: input + dropdown ---
        inputField.setText("0");

        dropdown.setValue(TIME_HOUR_LABEL); // default value - ore
        dropdown.getItems().addAll(TIME_HOUR_LABEL, TIME_MINUTE_LABEL);

        HBox topRow = new HBox(10, inputField, dropdown);
        topRow.setAlignment(Pos.CENTER);
        HBox.setHgrow(inputField, Priority.ALWAYS);

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

        // Create grid for checkboxes
        GridPane grid = new GridPane();
        grid.setHgap(30);
        grid.setVgap(30);
        grid.setAlignment(Pos.CENTER);

        grid.add(switchBox15, 0, 0);
        grid.add(switchBox30, 1, 0);
        grid.add(switchBox60, 0, 1);
        grid.add(switchBox120, 1, 1);

        // Design the stacking of checkboxes and first row
        VBox switchBox = new VBox(15, topRow, grid);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(15));

        /*
         * LAYOUT
         */

        // Layout for bottom-right positioning
        HBox buttonBox = new HBox(runButton);
        buttonBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonBox.setPadding(new Insets(10, 10, 10, 10));

        // Use BorderPane to position elements
        BorderPane root = new BorderPane();
        root.setTop(switchBox);
        // root.setCenter(label);
        root.setBottom(buttonBox);
        root.setPadding(new Insets(10, 10, 10, 10));

        Scene scene = new Scene(root, 300, 230);
        scene.getStylesheets().add(getClass().getResource("/switch.css").toExternalForm());

        stage.setOnCloseRequest(this::handleClose);
        stage.setTitle(label.getText());
        stage.setScene(scene);
        stage.getIcons().add(new Image(getClass().getResourceAsStream("/prohibition.png")));
        stage.show();
    }

    private boolean isNotInputField() {
        return inputField.getText().equals("0") && !timer.equals("0");
    }

    private boolean isInputFieldIsInHours() {
        return timer.equals("0") && !inputField.getText().equals("0") && dropdown.getValue().equals(TIME_HOUR_LABEL);
    }

    private boolean isInputFieldIsInMinutes() {
        return timer.equals("0") && !inputField.getText().equals("0") && dropdown.getValue().equals(TIME_MINUTE_LABEL);
    }

    private boolean hasAnythingRun() {
        return isNotInputField() || isInputFieldIsInHours() || isInputFieldIsInMinutes();
    }

    private void handleClose(WindowEvent event) {
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
    }
}
