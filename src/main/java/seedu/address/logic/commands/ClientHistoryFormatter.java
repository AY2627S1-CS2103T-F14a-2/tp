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

/**
 * Builds the text shown in the detail panel opened by {@code view}: a client's details followed by their
 * full measurement and gym-performance history, newest first.
 * Kept separate from the UI so that the output can be tested without starting JavaFX.
 *
 * <p>AI assistance: Claude Code helped implement this class and its tests.
 * Gabriel used OpenAI Codex to consolidate the separately styled history sections and their tests.
 */
public final class ClientHistoryFormatter {

    // Also used by the client card, so that the card and view show the same placeholders.
    public static final String NO_EMAIL = "-";
    public static final String NO_SESSION = "- (none booked)";
    static final String NO_TAGS = "-";
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
        String email = person.getEmail().map(e -> e.value).orElse(NO_EMAIL);
        // Matches the "Next session" row of the client card.
        String session = person.getNextSession().map(s -> s.toDisplayString(now)).orElse(NO_SESSION);
        String tags = person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
        return "Phone: " + PhoneDisplayFormatter.format(person.getPhone().value)
                + "\nEmail: " + email
                + "\nNext session: " + session
                + "\nTags: " + (tags.isEmpty() ? NO_TAGS : tags);
    }

    /**
     * Formats a history section heading, using the singular label only for one record.
     */
    public static String formatSectionHeading(String title, int count) {
        requireNonNull(title);
        return title + " · " + count + (count == 1 ? " record" : " records");
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
                .sorted(Comparator.comparing(Measurement::getDate).reversed()
                        .thenComparing(Measurement::getType))
                .map(m -> m.getDate() + DATE_SEPARATOR + m.getType() + " " + m.getValue().toPlainString()
                        + " " + m.getType().getUnit())
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
