package seedu.address.ui;

import static java.util.Objects.requireNonNull;
import static seedu.address.logic.commands.ClientHistoryFormatter.EMAIL_LABEL;
import static seedu.address.logic.commands.ClientHistoryFormatter.MEASUREMENTS_NEWEST_FIRST;
import static seedu.address.logic.commands.ClientHistoryFormatter.NEXT_SESSION_LABEL;
import static seedu.address.logic.commands.ClientHistoryFormatter.PHONE_LABEL;
import static seedu.address.logic.commands.ClientHistoryFormatter.TAGS_ALPHABETICALLY;
import static seedu.address.logic.commands.ClientHistoryFormatter.formatEmail;
import static seedu.address.logic.commands.ClientHistoryFormatter.formatMeasurementValue;
import static seedu.address.logic.commands.ClientHistoryFormatter.formatNextSession;
import static seedu.address.logic.commands.ClientHistoryFormatter.formatRecordCount;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Person;
import seedu.address.model.progress.PerformanceHistory;

/**
 * A UI component that displays a client's details and latest records in the client list.
 */
public class PersonCard extends UiPart<Region> {


    private static final String FXML = "PersonListCard.fxml";
    private static final String PERFORMANCE_ROW_PREFIX = "Latest performance: ";

    // Shared by the measurement and performance rows. An English locale keeps month names,
    // e.g. "Sep", the same on every system.
    private static final DateTimeFormatter CARD_DATE_FORMATTER = DateTimeFormatter.ofPattern("d MMM", Locale.ENGLISH);
    private static final DateTimeFormatter CARD_DATE_WITH_YEAR_FORMATTER =
            DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);

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
    private Label nextSession;
    @FXML
    private Label latestMeasurement;
    @FXML
    private FlowPane tags;
    @FXML
    private Label latestPerformance;

    /**
     * Creates a {@code PersonCard} for {@code person}, a client, shown at {@code displayedIndex}.
     */
    public PersonCard(Person person, int displayedIndex) {
        super(FXML);
        this.person = person;
        id.setText(displayedIndex + ". ");
        name.setText(person.getName().fullName);
        phone.setText(PHONE_LABEL + PhoneDisplayFormatter.format(person.getPhone().value));
        email.setText(EMAIL_LABEL + formatEmail(person));
        nextSession.setText(NEXT_SESSION_LABEL + formatNextSession(person, LocalDateTime.now()));
        LocalDate today = LocalDate.now();
        latestMeasurement.setText(formatLatestMeasurement(person.getMeasurements(), today));
        person.getTags().stream()
                .sorted(TAGS_ALPHABETICALLY)
                .forEach(tag -> tags.getChildren().add(new Label(tag.tagName)));
        // AI assistance: Gabriel used OpenAI Codex to remove empty tag-row spacing.
        tags.setVisible(!tags.getChildren().isEmpty());
        tags.managedProperty().bind(tags.visibleProperty());
        latestPerformance.setText(formatLatestPerformance(person.getPerformanceHistory(), today));
    }

    /**
     * Returns the card date, adding the year when it differs from {@code today}'s so old records don't look recent.
     */
    private static String formatCardDate(LocalDate date, LocalDate today) {
        return date.format(date.getYear() == today.getYear() ? CARD_DATE_FORMATTER : CARD_DATE_WITH_YEAR_FORMATTER);
    }

    /**
     * Returns the card row for the client's latest gym performance and total number of records,
     * e.g. "Latest performance: 15 Sep - Squat, 3 sets of 5 reps at 100 kg (9 records)".
     * Kept separate from the JavaFX constructor so that it can be tested without starting JavaFX.
     *
     * @param today The current date. The record's year is shown only if it differs from this year.
     */
    static String formatLatestPerformance(PerformanceHistory history, LocalDate today) {
        requireNonNull(history);
        requireNonNull(today);
        return history.getLatest()
                .map(latest -> PERFORMANCE_ROW_PREFIX + formatCardDate(latest.getDate(), today)
                        + " - " + latest.getSummary() + " (" + formatRecordCount(history.size()) + ")")
                .orElse(PERFORMANCE_ROW_PREFIX + "-");
    }

    /**
     * Formats the newest record; same-date ties prefer weight, then body fat, then waist.
     * AI assistance: OpenAI Codex helped implement the measurement card row and tests.
     *
     * @param today The current date. The record's year is shown only if it differs from this year.
     */
    static String formatLatestMeasurement(List<Measurement> measurements, LocalDate today) {
        requireNonNull(measurements);
        requireNonNull(today);
        if (measurements.isEmpty()) {
            return "Latest measurement: -";
        }
        Measurement latest = measurements.stream().min(MEASUREMENTS_NEWEST_FIRST).orElseThrow();
        return "Latest measurement: " + formatCardDate(latest.getDate(), today)
                + " - " + formatMeasurementValue(latest) + " (" + formatRecordCount(measurements.size()) + ")";
    }
}
