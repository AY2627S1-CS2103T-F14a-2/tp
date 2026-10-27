package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.stage.Stage;
import seedu.address.logic.commands.CommandResult;
import seedu.address.logic.parser.AddressBookParser;

/** Exercises tab-separated input through the real field and operating-system clipboard paste path. */
@ExtendWith(ApplicationExtension.class)
public class CommandBoxTest {
    private final List<String> executed = new ArrayList<>();
    private TextField field;

    @Start
    public void start(Stage stage) {
        CommandBox box = new CommandBox(command -> {
            new AddressBookParser().parseCommand(command);
            executed.add(command);
            return new CommandResult("Accepted");
        });
        Scene scene = new Scene(box.getRoot());
        stage.setScene(scene);
        stage.show();
        field = (TextField) scene.lookup("#commandTextField");
    }

    @Test
    public void setText_tabSeparatedCommand_preservesPrefixSeparators(FxRobot robot) {
        robot.interact(() -> {
            field.setText("measure 1\td/2026-10-02\tt/weight\tv/72.4");
            assertEquals("measure 1 d/2026-10-02 t/weight v/72.4", field.getText());
            field.fireEvent(new ActionEvent());
        });
        assertEquals(List.of("measure 1 d/2026-10-02 t/weight v/72.4"), executed);
        assertEquals("", field.getText());
    }

    @Test
    public void paste_tabSeparatedCommands_replacesSelectionAndPreservesUndo(FxRobot robot) {
        robot.interact(() -> {
            Clipboard clipboard = Clipboard.getSystemClipboard();
            ClipboardContent original = new ClipboardContent();
            clipboard.getContentTypes().forEach(format -> original.put(format, clipboard.getContent(format)));
            try {
                for (String command : List.of("measure 1\td/2026-10-02\tt/weight\tv/72.4",
                        "performance 1\td/2026-10-02\te/Squat\ts/3\tr/5\tw/100")) {
                    ClipboardContent content = new ClipboardContent();
                    content.putString(command);
                    clipboard.setContent(content);
                    field.setText("replace me");
                    field.selectAll();
                    field.paste();
                    assertEquals(command.replace('\t', ' '), field.getText());
                    field.undo();
                    assertEquals("replace me", field.getText());
                    field.redo();
                    field.fireEvent(new ActionEvent());
                    assertEquals("", field.getText());
                }
            } finally {
                clipboard.setContent(original);
            }
        });
        assertEquals(2, executed.size());
    }
}
