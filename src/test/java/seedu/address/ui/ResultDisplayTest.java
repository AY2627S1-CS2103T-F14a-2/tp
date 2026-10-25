package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;
import org.testfx.util.WaitForAsyncUtils;

import javafx.scene.Scene;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextArea;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/** Tests the feedback space given to short confirmations and long, wrapped errors. */
@ExtendWith(ApplicationExtension.class)
public class ResultDisplayTest {

    private ResultDisplay display;
    private TextArea textArea;
    private Stage stage;
    private VBox container;

    @Start
    public void start(Stage primaryStage) {
        stage = primaryStage;
        display = new ResultDisplay();
        container = new VBox(display.getRoot());
        Scene scene = new Scene(container, 600, 260);
        scene.getStylesheets().add(getClass().getResource("/view/DarkTheme.css").toExternalForm());
        stage.setScene(scene);
        stage.show();
        textArea = (TextArea) scene.lookup("#resultDisplay");
    }

    @Test
    public void feedback_shortConfirmation_usesCompactHeight(FxRobot robot) {
        show(robot, "Listed 2 clients.");

        assertTrue(display.getRoot().getHeight() < 70);
        assertEquals("Listed 2 clients.", textArea.getText());
        assertFalse(textArea.isEditable());
        assertTrue(textArea.isWrapText());
    }

    @Test
    public void feedback_longError_growsAndKeepsAllTextScrollable(FxRobot robot) {
        show(robot, "Listed 2 clients.");
        double compactHeight = display.getRoot().getHeight();
        String error = "Invalid command format!\n" + "Use the displayed client index and a valid date.\n".repeat(12);

        show(robot, error);

        assertTrue(display.getRoot().getHeight() > compactHeight);
        assertTrue(display.getRoot().getHeight() <= 112);
        assertEquals(error, textArea.getText());
        assertEquals(0, textArea.getCaretPosition());
        ScrollBar scrollBar = (ScrollBar) textArea.lookup(".scroll-bar:vertical");
        assertTrue(scrollBar.isVisible());
        robot.interact(() -> scrollBar.setValue(scrollBar.getMax()));
        assertTrue(textArea.getScrollTop() > 0);

        show(robot, "Listed 2 clients.");
        assertEquals(compactHeight, display.getRoot().getHeight());
        assertEquals(0, textArea.getScrollTop());
    }

    @Test
    public void feedback_narrowerWindow_growsToFitWrappedLines(FxRobot robot) throws Exception {
        show(robot, "No client with that name exists. Use the full client name shown in the list.");
        double wideHeight = display.getRoot().getHeight();

        robot.interact(() -> stage.setWidth(280));
        // The native window resize reaches JavaFX asynchronously, particularly on macOS.
        WaitForAsyncUtils.waitFor(5, TimeUnit.SECONDS, () -> {
            robot.interact(container::layout);
            return container.getWidth() < 400 && display.getRoot().getHeight() > wideHeight;
        });

        assertTrue(display.getRoot().getHeight() > wideHeight);
    }

    private void show(FxRobot robot, String feedback) {
        robot.interact(() -> display.setFeedbackToUser(feedback));
        robot.interact(container::layout);
    }
}
