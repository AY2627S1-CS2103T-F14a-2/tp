package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.ui.JavaFxTestUtil.layOut;
import static seedu.address.ui.JavaFxTestUtil.runOnFxThread;
import static seedu.address.ui.JavaFxTestUtil.textOf;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.layout.Region;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Person;

public class ClientDetailPanelTest {

    @Test
    public void constructor_noClientViewed_showsNothing() throws Exception {
        runOnFxThread(() -> {
            Region panel = new ClientDetailPanel(new SimpleObjectProperty<>()).getRoot();
            layOut(panel);

            assertEquals("", textOf(panel, "#name"));
            assertEquals("", textOf(panel, "#history"));
        });
    }

    @Test
    public void viewedPersonChanged_client_showsClientHistory() throws Exception {
        SimpleObjectProperty<Person> viewedPerson = new SimpleObjectProperty<>();

        runOnFxThread(() -> {
            Region panel = new ClientDetailPanel(viewedPerson).getRoot();
            layOut(panel);
            viewedPerson.set(ALICE);

            assertEquals("Alice Pauline", textOf(panel, "#name"));
            assertEquals(ClientHistoryFormatter.format(ALICE, LocalDateTime.now()), textOf(panel, "#history"));
        });
    }
}
