package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.ui.JavaFxTestUtil.layOut;
import static seedu.address.ui.JavaFxTestUtil.runOnFxThread;
import static seedu.address.ui.JavaFxTestUtil.textOf;

import org.junit.jupiter.api.Test;

import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UpcomingSessionPanelTest {

    @Test
    public void constructor_noUpcomingSessions_showsHowToBook() throws Exception {
        runOnFxThread(() -> {
            Region panel = new UpcomingSessionPanel(FXCollections.observableArrayList()).getRoot();

            Label placeholder = (Label) ((ListView<?>) panel.lookup("#upcomingSessionListView")).getPlaceholder();
            assertEquals("No upcoming sessions.\nBook one with: session INDEX d/DATE t/TIME", placeholder.getText());
        });
    }

    @Test
    public void constructor_upcomingSession_showsCard() throws Exception {
        Person person = new PersonBuilder(ALICE).withNextSession("2099-12-01 18:00").build();

        runOnFxThread(() -> {
            Region panel = new UpcomingSessionPanel(FXCollections.observableArrayList(person)).getRoot();
            layOut(panel);

            assertEquals("Alice Pauline", textOf(panel, "#name"));
        });
    }
}
