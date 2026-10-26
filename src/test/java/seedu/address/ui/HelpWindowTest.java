package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.stage.Stage;

/** Verifies that the existing Help stage can be recovered after it has been minimized. */
@ExtendWith(ApplicationExtension.class)
public class HelpWindowTest {
    private HelpWindow help;

    @Start
    public void start(Stage stage) {
        help = new HelpWindow(stage);
        help.show();
    }

    @Test
    public void focus_iconifiedState_clearsBeforeShowing(FxRobot robot) {
        robot.interact(() -> {
            // A hidden stage has no native peer that can reject minimization, as Xvfb does without a window manager.
            help.hide();
            help.getRoot().setIconified(true);
            assertTrue(help.getRoot().isIconified());
            help.focus();
            assertFalse(help.getRoot().isIconified());
            help.show();
            assertTrue(help.isShowing());
        });
    }

    @Test
    public void focus_shownWindow_restoresExistingStage(FxRobot robot) {
        robot.interact(() -> {
            // Exercise native minimization where supported, without requiring a desktop window manager in CI.
            help.getRoot().setIconified(true);
            help.focus();
            assertTrue(help.isShowing());
            assertFalse(help.getRoot().isIconified());
        });
    }
}
