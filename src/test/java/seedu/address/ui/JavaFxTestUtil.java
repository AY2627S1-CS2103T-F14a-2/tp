package seedu.address.ui;

import static org.junit.jupiter.api.Assumptions.assumeTrue;

import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

/**
 * Runs test code on the JavaFX application thread, so that UI parts can be built and checked.
 * Tests using it are skipped where JavaFX cannot start, e.g. on a machine without a display.
 *
 * <p>AI assistance: Claude Code helped write this class and the UI tests that use it.
 */
final class JavaFxTestUtil {

    private static final long TIMEOUT_SECONDS = 10;

    private static Boolean isToolkitStarted;

    private JavaFxTestUtil() {}

    /**
     * Runs {@code action} on the JavaFX application thread and waits for it to finish.
     * A failure inside {@code action} fails the calling test.
     */
    static void runOnFxThread(Runnable action) throws Exception {
        assumeTrue(startToolkit(), "JavaFX cannot start on this machine");
        FutureTask<Void> task = new FutureTask<>(action, null);
        Platform.runLater(task);
        task.get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
    }

    /**
     * Lays out {@code root} in a scene, as controls such as lists and scroll panes only create their content then.
     */
    static void layOut(Parent root) {
        if (root.getScene() == null) {
            new Scene(root, 400, 400);
        }
        root.applyCss();
        root.layout();
    }

    /**
     * Returns the text of the label matching {@code selector} in {@code root}.
     */
    static String textOf(Node root, String selector) {
        return ((Label) root.lookup(selector)).getText();
    }

    /**
     * Starts the JavaFX toolkit once for all tests, and returns whether it is running.
     */
    private static synchronized boolean startToolkit() {
        if (isToolkitStarted == null) {
            try {
                Platform.startup(() -> {});
                // Keeps the toolkit running for later tests after a test's window is closed.
                Platform.setImplicitExit(false);
                isToolkitStarted = true;
            } catch (IllegalStateException e) {
                isToolkitStarted = true; // already started outside this class
            } catch (UnsupportedOperationException | UnsatisfiedLinkError e) {
                isToolkitStarted = false; // no display to start on
            }
        }
        return isToolkitStarted;
    }
}
