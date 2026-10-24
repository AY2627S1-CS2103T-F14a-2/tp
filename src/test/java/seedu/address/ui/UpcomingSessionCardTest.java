package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.ui.JavaFxTestUtil.runOnFxThread;
import static seedu.address.ui.JavaFxTestUtil.textOf;

import org.junit.jupiter.api.Test;

import javafx.scene.Node;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

public class UpcomingSessionCardTest {

    @Test
    public void constructor_bookedClient_showsSessionClientAndLatestRecords() throws Exception {
        Person person = new PersonBuilder(ALICE).withNextSession("2099-12-01 18:00").build();

        runOnFxThread(() -> {
            Node card = new UpcomingSessionCard(person).getRoot();

            assertEquals("Alice Pauline", textOf(card, "#name"));
            assertEquals("Phone: 9435 1253", textOf(card, "#phone"));
            assertEquals("Latest measurement: -", textOf(card, "#latestMeasurement"));
            assertEquals("Latest performance: -", textOf(card, "#latestPerformance"));
            assertEquals("Tue 1 Dec, 6:00pm", textOf(card, "#sessionTime"));
        });
    }
}
