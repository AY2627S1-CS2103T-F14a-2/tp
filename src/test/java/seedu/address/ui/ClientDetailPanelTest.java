package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Person;

@ExtendWith(ApplicationExtension.class)
public class ClientDetailPanelTest {

    private final SimpleObjectProperty<Person> viewedPerson = new SimpleObjectProperty<>();
    private Scene scene;

    @Start
    public void start(Stage stage) {
        scene = new Scene(new ClientDetailPanel(viewedPerson).getRoot());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void refresh_noClientViewed_showsNothing() {
        assertEquals("", labelText("#name"));
        assertEquals("", labelText("#history"));
    }

    @Test
    public void refresh_clientViewed_showsClientHistory(FxRobot robot) {
        robot.interact(() -> viewedPerson.set(ALICE));

        assertEquals("Alice Pauline", labelText("#name"));
        assertEquals(ClientHistoryFormatter.format(ALICE, LocalDateTime.now()), labelText("#history"));
    }

    private String labelText(String selector) {
        return ((Label) scene.lookup(selector)).getText();
    }
}
