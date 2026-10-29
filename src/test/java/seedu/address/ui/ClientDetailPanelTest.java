package seedu.address.ui;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalGymPerformances.BENCH_PRESS;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testfx.api.FxRobot;
import org.testfx.framework.junit5.ApplicationExtension;
import org.testfx.framework.junit5.Start;

import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;
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
        assertEquals("", labelText("#details"));
        assertEquals("", labelText("#measurements"));
        assertEquals("", labelText("#performances"));
    }

    @Test
    public void refresh_clientViewed_showsClientHistory(FxRobot robot) {
        robot.interact(() -> viewedPerson.set(ALICE));

        assertEquals("Alice Pauline", labelText("#name"));
        assertEquals(ClientHistoryFormatter.formatDetails(ALICE, LocalDateTime.now()), labelText("#details"));
        assertEquals("Measurements · 0 records", labelText("#measurementHeading"));
        assertEquals("Performance · 0 records", labelText("#performanceHeading"));
        assertEquals("None recorded yet.", labelText("#measurements"));
        assertEquals("None recorded yet.", labelText("#performances"));
    }

    @Test
    public void refresh_mixedCaseTags_matchesCardOrder(FxRobot robot) {
        Person person = new PersonBuilder().withTags("Zebra", "apple", "Apple", "banana").build();
        robot.interact(() -> {
            viewedPerson.set(person);
            PersonCard card = new PersonCard(person, 1);
            FlowPane tags = (FlowPane) card.getRoot().lookup("#tags");
            assertEquals(List.of("apple", "banana", "Zebra"),
                    tags.getChildren().stream().map(node -> ((Label) node).getText()).toList());
        });
        assertTrue(labelText("#details").endsWith("Tags: apple, banana, Zebra"));
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

    @Test
    public void viewedPersonChanged_newRecords_updatesRowsAndSectionCounts(FxRobot robot) {
        robot.interact(() -> viewedPerson.set(ALICE));
        Person recorded = ALICE.withMeasurement(new Measurement(LocalDate.of(2026, 10, 20),
                MeasurementType.WEIGHT, new BigDecimal("72.4"))).withGymPerformance(BENCH_PRESS);

        robot.interact(() -> viewedPerson.set(recorded));

        assertEquals("Measurements · 1 record", labelText("#measurementHeading"));
        assertEquals("2026-10-20  weight 72.4 kg", labelText("#measurements"));
        assertEquals("Performance · 1 record", labelText("#performanceHeading"));
        assertEquals("2026-09-18  Bench Press, 3 sets of 8 reps at 60 kg", labelText("#performances"));
    }

    private String labelText(String selector) {
        return ((Label) panel.lookup(selector)).getText();
    }
}
