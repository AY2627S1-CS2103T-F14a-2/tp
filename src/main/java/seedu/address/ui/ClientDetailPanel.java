package seedu.address.ui;

import java.time.LocalDateTime;

import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import seedu.address.logic.commands.ClientHistoryFormatter;
import seedu.address.model.person.Person;

/**
 * Panel showing the details and full history of the client opened with {@code view}.
 * It redraws whenever the viewed client changes, e.g. after that client is edited or gets a new record.
 *
 * <p>AI assistance: Claude Code helped implement this class.
 * Gabriel used OpenAI Codex to separate and style the history sections.
 */
public class ClientDetailPanel extends UiPart<ScrollPane> {

    private static final String FXML = "ClientDetailPanel.fxml";

    private final ObservableValue<Person> viewedPerson;

    @FXML
    private Label name;
    @FXML
    private Label details;
    @FXML
    private Label measurementHeading;
    @FXML
    private Label measurements;
    @FXML
    private Label performanceHeading;
    @FXML
    private Label performances;

    /**
     * Creates a {@code ClientDetailPanel} that shows {@code viewedPerson} whenever a client is viewed.
     */
    public ClientDetailPanel(ObservableValue<Person> viewedPerson) {
        super(FXML);
        this.viewedPerson = viewedPerson;
        viewedPerson.addListener((observable, oldPerson, newPerson) -> {
            // A different client opens at the top; an update to the same client keeps its scroll position.
            if (newPerson != null && !newPerson.isSamePerson(oldPerson)) {
                getRoot().setVvalue(0);
            }
            refresh();
        });
        refresh();
    }

    /**
     * Redraws the panel, so that time-dependent text, such as a next session that has just passed,
     * matches the client card. Does nothing while no client is viewed, as the panel is then hidden.
     */
    public void refresh() {
        Person person = viewedPerson.getValue();
        if (person == null) {
            return;
        }
        name.setText(person.getName().fullName);
        details.setText(ClientHistoryFormatter.formatDetails(person, LocalDateTime.now()));
        measurementHeading.setText(sectionHeading("Measurements", person.getMeasurements().size()));
        measurements.setText(ClientHistoryFormatter.formatMeasurementRecords(person.getMeasurements()));
        performanceHeading.setText(sectionHeading("Performance", person.getPerformanceHistory().size()));
        performances.setText(ClientHistoryFormatter.formatPerformanceRecords(person.getPerformanceHistory()));
    }

    private static String sectionHeading(String title, int count) {
        return title + " · " + count + (count == 1 ? " record" : " records");
    }
}
