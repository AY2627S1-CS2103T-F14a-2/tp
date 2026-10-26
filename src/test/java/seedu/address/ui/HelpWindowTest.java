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
    public void focus_minimizedWindow_restoresExistingStage(FxRobot robot) {
        robot.interact(() -> help.getRoot().setIconified(true));
        assertTrue(help.isShowing());
        assertTrue(help.getRoot().isIconified());

        robot.interact(help::focus);

        assertTrue(help.isShowing());
        assertFalse(help.getRoot().isIconified());
        robot.interact(help::hide);
    }
}
