package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.ui.JavaFxTestUtil.layOut;
import static seedu.address.ui.JavaFxTestUtil.runOnFxThread;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import seedu.address.logic.LogicManager;
import seedu.address.model.ModelManager;

public class MainWindowTest {

    @Test
    public void executeCommand_viewThenList_swapsSidePanel() throws Exception {
        ModelManager model = new ModelManager();
        model.addPerson(ALICE);
        // view and list never save, so no storage is needed.
        LogicManager logic = new LogicManager(model, null);

        runOnFxThread(() -> {
            MainWindow mainWindow = new MainWindow(new Stage(), logic, Path.of("spotter.json"));
            mainWindow.fillInnerParts();
            Parent root = mainWindow.getRoot().getScene().getRoot();
            layOut(root);
            List<Node> sidePanels = ((StackPane) root.lookup("#sidePanelPlaceholder")).getChildren();
            TextField commandBox = (TextField) root.lookup("#commandTextField");

            // Upcoming sessions first, then the viewed client's history.
            assertEquals(List.of(true, false), sidePanels.stream().map(Node::isVisible).toList());

            enter(commandBox, "view 1");
            assertEquals(List.of(false, true), sidePanels.stream().map(Node::isVisible).toList());

            enter(commandBox, "list");
            assertEquals(List.of(true, false), sidePanels.stream().map(Node::isVisible).toList());
        });
    }

    /**
     * Enters {@code commandText} in {@code commandBox} as if the user typed it and pressed Enter.
     */
    private static void enter(TextField commandBox, String commandText) {
        commandBox.setText(commandText);
        commandBox.fireEvent(new ActionEvent());
    }
}
