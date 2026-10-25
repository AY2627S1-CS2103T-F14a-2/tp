package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static seedu.address.testutil.TypicalPersons.ALICE;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import seedu.address.model.person.Person;
import seedu.address.testutil.PersonBuilder;

@ExtendWith(ApplicationExtension.class)
public class UpcomingSessionPanelTest {

    private final ObservableList<Person> upcomingSessionList = FXCollections.observableArrayList();
    private Scene scene;

    @Start
    public void start(Stage stage) {
        scene = new Scene(new UpcomingSessionPanel(upcomingSessionList).getRoot(), 400, 400);
        stage.setScene(scene);
        stage.show();
    }

    @Test
    public void constructor_noUpcomingSessions_showsHowToBook() {
        Label placeholder = (Label) scene.lookup(".placeholder .label");

        assertEquals("No upcoming sessions.\nBook one with: session INDEX d/DATE t/TIME", placeholder.getText());
    }

    @Test
    public void updateItem_upcomingSession_showsCard(FxRobot robot) {
        robot.interact(() -> upcomingSessionList.add(
                new PersonBuilder(ALICE).withNextSession("2099-12-01 18:00").build()));

        assertEquals("Alice Pauline", ((Label) scene.lookup("#name")).getText());
    }

    @Test
    public void updateItem_nullItem_showsNoCard(FxRobot robot) {
        robot.interact(() -> upcomingSessionList.add(null));

        assertNull(scene.lookup("#name"));
    }
}
