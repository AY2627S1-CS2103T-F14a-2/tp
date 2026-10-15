package seedu.address.ui;

import javafx.stage.Stage;

/**
 * API of the UI component
 */
public interface Ui {

    /** Starts the UI (and the App).  */
    void start(Stage primaryStage);

    /** Shows {@code message} in the result display, e.g. a warning raised while starting the app. */
    void showMessage(String message);

}
