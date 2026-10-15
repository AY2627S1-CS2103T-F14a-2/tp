package seedu.address.ui;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Person;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final DateTimeFormatter MEASUREMENT_DATE_FORMAT =
            DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);

    /**
     * Note: Certain keywords such as "location" and "resources" are reserved keywords in JavaFX.
     * As a consequence, UI elements' variable names cannot be set to such keywords
     * or an exception will be thrown by JavaFX during runtime.
     *
     * @see <a href="https://github.com/se-edu/addressbook-level4/issues/336">The issue on AddressBook level 4</a>
     */

    public final Person person;

    @FXML
    private HBox cardPane;
    @FXML
    private Label name;
    @FXML
    private Label id;
    @FXML
    private Label phone;
    @FXML
    private Label address;
    @FXML
    private Label email;
    @FXML
    private Label nextSession;
    @FXML
    private Label latestMeasurement;
    @FXML
    private FlowPane tags;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        address.setText(person.getAddress().value);
        email.setText(person.getEmail().value);
        nextSession.setText("Next session: " + person.getNextSession()
                .map(session -> session.toDisplayString(LocalDateTime.now())).orElse("- (none booked)"));
        latestMeasurement.setText(formatLatestMeasurement(person.getMeasurements()));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
    }

    /**
     * Formats the newest record; same-date ties prefer weight, then body fat, then waist.
     * AI assistance: OpenAI Codex helped implement the measurement card row and tests.
     */
    static String formatLatestMeasurement(List<Measurement> measurements) {
        if (measurements.isEmpty()) {
            return "Latest measurement: -";
        }
        Measurement latest = measurements.stream().min(Comparator.comparing(Measurement::getDate).reversed()
                .thenComparing(Measurement::getType)).orElseThrow();
        int count = measurements.size();
        return "Latest measurement: " + latest.getDate().format(MEASUREMENT_DATE_FORMAT)
                + " - " + latest.getType() + " " + latest.getValue().toPlainString()
                + " " + latest.getType().getUnit() + " (" + count + (count == 1 ? " record)" : " records)");
    }
}
