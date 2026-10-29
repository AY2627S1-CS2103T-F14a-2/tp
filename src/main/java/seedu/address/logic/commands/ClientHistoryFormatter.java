package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Person;
import seedu.address.model.progress.PerformanceHistory;
import seedu.address.model.tag.Tag;

/**
 * Builds the text shown in the detail panel opened by {@code view}: a client's details followed by their
 * full measurement and gym-performance history, newest first.
 * Kept separate from the UI so that the output can be tested without starting JavaFX.
 *
 * <p>AI assistance: Claude Code helped implement this class and its tests.
 * Gabriel used OpenAI Codex to consolidate the separately styled history sections and their tests.
 */
public final class ClientHistoryFormatter {

    // Also used by the client cards, so that the cards and the detail panel use the same labels and placeholders.
    public static final String PHONE_LABEL = "Phone: ";
    public static final String EMAIL_LABEL = "Email: ";
    public static final String NEXT_SESSION_LABEL = "Next session: ";
    public static final String NO_EMAIL = "-";
    public static final String NO_SESSION = "- (none booked)";
    public static final String NO_TAGS = "-";

    /** Orders tag labels alphabetically, with a case-sensitive tie-break for deterministic case variants. */
    public static final Comparator<Tag> TAGS_ALPHABETICALLY =
            Comparator.comparing((Tag tag) -> tag.tagName, String.CASE_INSENSITIVE_ORDER)
                    .thenComparing(tag -> tag.tagName);

    /**
     * Orders measurements newest date first; same-date measurements follow the type order weight, body fat, waist.
     * The first measurement in this order is the one the card shows as the latest.
     */
    public static final Comparator<Measurement> MEASUREMENTS_NEWEST_FIRST =
            Comparator.comparing(Measurement::getDate).reversed().thenComparing(Measurement::getType);
    private static final String NO_RECORDS = "None recorded yet.";

    // Two spaces separate the date from the record so the columns stay readable.
    private static final String DATE_SEPARATOR = "  ";

    private ClientHistoryFormatter() {} // prevents instantiation

    /**
     * Formats the contact, session and tag details independently of the history sections.
     * AI assistance: Gabriel used OpenAI Codex to expose section formatting for the detail panel.
     *
     * @param now The current time, used to decide whether the next session has already passed.
     */
    public static String formatDetails(Person person, LocalDateTime now) {
        requireNonNull(person);
        requireNonNull(now);
        String tags = person.getTags().stream()
                .sorted(TAGS_ALPHABETICALLY)
                .map(tag -> tag.tagName)
                .collect(Collectors.joining(", "));
        return PHONE_LABEL + PhoneDisplayFormatter.format(person.getPhone().value)
                + "\n" + EMAIL_LABEL + formatEmail(person)
                + "\n" + NEXT_SESSION_LABEL + formatNextSession(person, now)
                + "\nTags: " + (tags.isEmpty() ? NO_TAGS : tags);
    }

    /**
     * Returns the client's email, or {@link #NO_EMAIL} if they have none.
     */
    public static String formatEmail(Person person) {
        requireNonNull(person);
        return person.getEmail().map(email -> email.value).orElse(NO_EMAIL);
    }

    /**
     * Returns the client's next session as shown on the card, or {@link #NO_SESSION} if none is booked.
     *
     * @param now The current time, used to decide whether the session has already passed.
     */
    public static String formatNextSession(Person person, LocalDateTime now) {
        requireNonNull(person);
        requireNonNull(now);
        return person.getNextSession().map(session -> session.toDisplayString(now)).orElse(NO_SESSION);
    }

    /**
     * Returns "1 record" or "N records".
     */
    public static String formatRecordCount(int count) {
        assert count >= 0;
        return count + (count == 1 ? " record" : " records");
    }

    /**
     * Returns a measurement's type, value and unit, e.g. "weight 72.4 kg".
     */
    public static String formatMeasurementValue(Measurement measurement) {
        requireNonNull(measurement);
        return measurement.getType() + " " + measurement.getValue().toPlainString() + " "
                + measurement.getType().getUnit();
    }

    /**
     * Formats a history section heading, using the singular label only for one record.
     */
    public static String formatSectionHeading(String title, int count) {
        requireNonNull(title);
        return title + " · " + formatRecordCount(count);
    }

    /**
     * Formats measurement rows, without a heading, for the separately styled history section.
     * Records are newest first; same-date records follow the type order weight, body fat, waist.
     */
    public static String formatMeasurementRecords(List<Measurement> measurements) {
        requireNonNull(measurements);
        if (measurements.isEmpty()) {
            return NO_RECORDS;
        }
        List<String> lines = measurements.stream()
                .sorted(MEASUREMENTS_NEWEST_FIRST)
                .map(m -> m.getDate() + DATE_SEPARATOR + formatMeasurementValue(m))
                .toList();
        return String.join("\n", lines);
    }

    /**
     * Formats performance rows without a heading, newest first; same-date records are most recently recorded first,
     * so the first line always matches the card's "Latest performance".
     *
     * @param history Records in the order they were recorded, oldest recorded first.
     */
    public static String formatPerformanceRecords(PerformanceHistory history) {
        requireNonNull(history);
        if (history.size() == 0) {
            return NO_RECORDS;
        }
        // Same order as the card's latest performance, so the first line always matches it.
        List<String> lines = history.getNewestFirst().stream()
                .map(p -> p.getDate() + DATE_SEPARATOR + p.getSummary())
                .toList();
        return String.join("\n", lines);
    }
}
