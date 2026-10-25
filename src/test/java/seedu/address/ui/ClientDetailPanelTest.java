package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.stage.Stage;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class ClientDetailPanelTest {

    private final SimpleObjectProperty<Person> viewedPerson = new SimpleObjectProperty<>();
    private ScrollPane panel;

    @Start
    public void start(Stage stage) {
        panel = new ClientDetailPanel(viewedPerson).getRoot();
        // Short enough that a history needs scrolling.
        stage.setScene(new Scene(panel, 300, 100));
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

    @Test
    public void viewedPersonChanged_differentClient_scrollsToTop(FxRobot robot) {
        robot.interact(() -> viewedPerson.set(ALICE));
        robot.interact(() -> panel.setVvalue(1));
        robot.interact(() -> viewedPerson.set(BENSON));

        assertEquals(0, panel.getVvalue());
    }

    @Test
    public void viewedPersonChanged_sameClientUpdated_keepsScrollPosition(FxRobot robot) {
        robot.interact(() -> viewedPerson.set(ALICE));
        robot.interact(() -> panel.setVvalue(1));
        robot.interact(() -> viewedPerson.set(new PersonBuilder(ALICE).withPhone("99998888").build()));

        assertEquals(1, panel.getVvalue());
    }

    private String labelText(String selector) {
        return ((Label) panel.lookup(selector)).getText();
    }
}
