package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class UpcomingSessionCardTest {

    private Scene scene;

    @Start
    public void start(Stage stage) {
        Person person = new PersonBuilder(ALICE).withNextSession("2099-12-01 18:00").build();
        scene = new Scene(new UpcomingSessionCard(person).getRoot());
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void constructor_bookedClient_showsSessionClientAndLatestRecords() {
        assertEquals("Tue 1 Dec, 6:00pm", labelText("#sessionTime"));
        assertEquals("Alice Pauline", labelText("#name"));
        assertEquals("Phone: 9435 1253", labelText("#phone"));
        assertEquals("Latest measurement: -", labelText("#latestMeasurement"));
        assertEquals("Latest performance: -", labelText("#latestPerformance"));
    }

    private String labelText(String selector) {
        return ((Label) scene.lookup(selector)).getText();
    }
}
