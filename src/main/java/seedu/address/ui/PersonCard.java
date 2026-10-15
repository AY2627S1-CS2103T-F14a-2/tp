package seedu.address.ui;

import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;
import seedu.address.model.progress.PerformanceHistory;

/**
 * A UI component that displays information of a {@code Person}.
 */
public class PersonCard extends UiPart<Region> {

    private static final String FXML = "PersonListCard.fxml";
    private static final String PERFORMANCE_ROW_PREFIX = "Latest performance: ";

    // An English locale keeps month names, e.g. "Sep", the same on every system.
    private static final DateTimeFormatter CARD_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);

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
    private Label email;
    @FXML
    private FlowPane tags;
    @FXML
    private Label latestPerformance;

    /**
     * Creates a {@code PersonCard} with the given {@code Person} and index to display.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(person.getPhone().value);
        email.setText(person.getEmail().map(e -> e.value).orElse("-"));
        person.getTags().stream()
                .sorted(Comparator.comparing(tag -> tag.tagName))
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        latestPerformance.setText(formatLatestPerformance(person.getPerformanceHistory()));
    }

    /**
     * Returns the card row for the client's latest gym performance and total number of records,
     * e.g. "Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (9 records)".
     * Kept separate from the JavaFX constructor so that it can be tested without starting JavaFX.
     */
    static String formatLatestPerformance(PerformanceHistory history) {
        return history.getLatest()
                .map(latest -> PERFORMANCE_ROW_PREFIX + latest.getDate().format(CARD_DATE_FORMATTER)
                        + " - " + latest.getSummary() + " (" + history.size()
                        + (history.size() == 1 ? " record)" : " records)"))
                .orElse(PERFORMANCE_ROW_PREFIX + "-");
    }
}
