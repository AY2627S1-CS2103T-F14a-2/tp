package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;

@ExtendWith(ApplicationExtension.class)
public class MainWindowTest {

    private Scene scene;

    @Start
    public void start(Stage stage) {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        // view and list never save, so no storage is needed.
        MainWindow mainWindow = new MainWindow(stage, new LogicManager(model, null), Path.of("spotter.json"));
        mainWindow.fillInnerParts();
        mainWindow.show();
        scene = stage.getScene();
    }

    @Test
    public void executeCommand_viewThenList_swapsSidePanel(FxRobot robot) {
        // Upcoming sessions first, then the viewed client's history.
        assertEquals(List.of(true, false), sidePanelVisibilities());

        enter(robot, "view 1");
        assertEquals(List.of(false, true), sidePanelVisibilities());

        enter(robot, "list");
        assertEquals(List.of(true, false), sidePanelVisibilities());
    }

    /**
     * Enters {@code commandText} in the command box as if the user typed it and pressed Enter.
     */
    private void enter(FxRobot robot, String commandText) {
        TextField commandBox = (TextField) scene.lookup("#commandTextField");
        robot.interact(() -> {
            commandBox.setText(commandText);
            commandBox.fireEvent(new ActionEvent());
        });
    }

    private List<Boolean> sidePanelVisibilities() {
        return ((StackPane) scene.lookup("#sidePanelPlaceholder")).getChildren().stream()
                .map(Node::isVisible).toList();
    }
}
