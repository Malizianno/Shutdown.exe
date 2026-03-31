package ro.moonlightteam.shutdown.exe;

import java.util.Optional;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import javafx.animation.PauseTransition;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import ro.moonlightteam.shutdown.exe.controller.ShutdownController;

public class ShutdownExe extends Application {

    public static final String TIME_HOUR_LABEL = "ore";
    public static final String TIME_MINUTE_LABEL = "minute";

    private static String[] args;
    private String timer = "0"; // Default timer value

    private final CheckBox switchBox15 = new CheckBox("15 min");
    private final CheckBox switchBox30 = new CheckBox("30 min");
    private final CheckBox switchBox60 = new CheckBox("1 hour");
    private final CheckBox switchBox90 = new CheckBox("1.5 hours");
    private final CheckBox switchBox180 = new CheckBox("3 hours");
    private final CheckBox switchBox210 = new CheckBox("3.5 hours");

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

        // create the auto-close timer for the window (2 minutes)
        PauseTransition autoCloseTimer = new PauseTransition(Duration.seconds(120)); // 2 minutes

        autoCloseTimer.setOnFinished(event -> {
            System.out.println("Auto-close timer finished, closing the application.");
            handleClose(null); // Close the application when the timer finishes
        });

        autoCloseTimer.play(); // Start the auto-close timer

        try {
            // start with an abort command to cancel any existing shutdown timers
            controller.execAbortCommand();
        } catch (Exception e) {
            System.out.println("Exception while aborting shutdown: " + e.getMessage());
        }

        try {
            // after abort try the default to 2h (7200s) shutdown timer
            controller.execShutdownCommand("7200");
        } catch (Exception e) {
            System.out.println("Exception while setting default shutdown timer: " + e.getMessage());
        }

        Label label = new Label("Shutdown");

        // Create the 'Cancel' button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> {
            System.out.println("Cancel button clicked");

            try {
                controller.execAbortCommand();
            } catch (Exception ex) {
                System.out.println("Exception while executing cancel command: " + ex.getMessage());
            }

            handleClose(null); // Close the application after executing the command
        });

        // Create the "Run" button
        Button runButton = new Button("Run");
        runButton.setOnAction(e -> {
            System.out.println("Run button clicked");

            try {
                // start with an abort command to cancel any existing shutdown timers
                controller.execAbortCommand();
            } catch (Exception ex) {
                System.out.println("Exception while executing run command, but first abort shutdown: " + ex.getMessage());
            }

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
        switchBox90.setSelected(false);
        switchBox180.setSelected(false);
        switchBox210.setSelected(false);

        // Optional: Add listener switchBox15
        switchBox15.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("15: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "900"; // 15 minutes in seconds
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
                switchBox90.setSelected(false);
                switchBox180.setSelected(false);
                switchBox210.setSelected(false);
            }
        });

        // Optional: Add listener switchBox30
        switchBox30.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("30: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "1800"; // 30 minutes in seconds
                switchBox15.setSelected(false);
                switchBox60.setSelected(false);
                switchBox90.setSelected(false);
                switchBox180.setSelected(false);
                switchBox210.setSelected(false);
            }
        });

        // Optional: Add listener switchBox60
        switchBox60.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("60: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "3600"; // 1 hour in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox90.setSelected(false);
                switchBox180.setSelected(false);
                switchBox210.setSelected(false);
            }
        });

        switchBox90.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("90: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "5400"; // 1.5 hours in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
                switchBox180.setSelected(false);
                switchBox210.setSelected(false);
            }
        });

        // Optional: Add listener switchBox180
        switchBox180.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("180: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "7200"; // 3 hours in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
                switchBox90.setSelected(false);
                switchBox210.setSelected(false);
            }
        });

        // Optional: Add listener switchBox210
        switchBox210.selectedProperty().addListener((obs, wasSelected, isSelected) -> {
            System.out.println("210: wasSelected: " + wasSelected + ", isSelected: " + isSelected);

            if (isSelected) {
                timer = "9000"; // 3.5 hours in seconds
                switchBox15.setSelected(false);
                switchBox30.setSelected(false);
                switchBox60.setSelected(false);
                switchBox90.setSelected(false);
                switchBox180.setSelected(false);
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
        grid.add(switchBox90, 1, 1);
        grid.add(switchBox180, 0, 2);
        grid.add(switchBox210, 1, 2);

        // Design the stacking of checkboxes and first row
        VBox switchBox = new VBox(15, topRow, grid);
        switchBox.setAlignment(Pos.CENTER);
        switchBox.setPadding(new Insets(15));

        /*
         * LAYOUT
         */
        // Layout for bottom-right positioning
        HBox buttonsBox = new HBox(20, cancelButton, runButton); // distance between buttons + actual buttons
        buttonsBox.setAlignment(Pos.BOTTOM_RIGHT);
        buttonsBox.setPadding(new Insets(10, 10, 10, 10));

        // Use BorderPane to position elements
        BorderPane root = new BorderPane();
        root.setTop(switchBox);
        // root.setCenter(label);
        root.setBottom(buttonsBox);
        root.setPadding(new Insets(10, 10, 10, 10));

        // Create the scene (with window dimensions) and apply CSS
        Scene scene = new Scene(root, 300, 290);
        scene.getStylesheets().add(getClass().getResource("/switch.css").toExternalForm());

        // Reset the auto-close timer on any mouse/keyboard activity within the application
        scene.addEventFilter(MouseEvent.ANY, event -> autoCloseTimer.playFromStart());
        scene.addEventFilter(KeyEvent.ANY, event -> autoCloseTimer.playFromStart());

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
        System.out.println("Closing the application with event: " + event);
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
