package seedu.address.logic.commands;

import static java.util.Objects.requireNonNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import seedu.address.commons.util.PhoneDisplayFormatter;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.Person;
import seedu.address.model.progress.GymPerformance;

/**
 * Builds the text shown by {@code view}: a client's details followed by their full measurement
 * and gym-performance history, newest first.
 * Kept separate from the UI so that the output can be tested without starting JavaFX.
 *
 * <p>AI assistance: Claude Code helped implement this class and its tests.
 */
public final class ClientHistoryFormatter {

    // Also used by the client card, so that the card and view show the same placeholders.
    public static final String NO_EMAIL = "-";
    public static final String NO_SESSION = "- (none booked)";
    static final String NO_TAGS = "-";
    static final String NO_MEASUREMENTS = "Measurements: none recorded yet.";
    static final String NO_PERFORMANCES = "Performance: none recorded yet.";

    // Two spaces separate the date from the record so the columns stay readable.
    private static final String DATE_SEPARATOR = "  ";

    private ClientHistoryFormatter() {} // prevents instantiation

    /**
     * Returns the full details and history of {@code person}.
     *
     * @param now The current time, used to decide whether the next session has already passed.
     */
    public static String format(Person person, LocalDateTime now) {
        requireNonNull(person);
        requireNonNull(now);
        return formatDetails(person, now)
                + "\n\n" + formatMeasurements(person.getMeasurements())
                + "\n\n" + formatPerformances(person.getPerformanceHistory().asUnmodifiableList());
    }

    private static String formatDetails(Person person, LocalDateTime now) {
        String email = person.getEmail().map(e -> e.value).orElse(NO_EMAIL);
        // Matches the "Next session" row of the client card.
        String session = person.getNextSession().map(s -> s.toDisplayString(now)).orElse(NO_SESSION);
        String tags = person.getTags().stream()
                .map(tag -> tag.tagName)
                .sorted()
                .collect(Collectors.joining(", "));
        return person.getName().fullName
                + "\nPhone: " + PhoneDisplayFormatter.format(person.getPhone().value)
                + "\nEmail: " + email
                + "\nNext session: " + session
                + "\nTags: " + (tags.isEmpty() ? NO_TAGS : tags);
    }

    /**
     * Lists measurements newest first; same-date records follow the type order weight, body fat, waist.
     */
    static String formatMeasurements(List<Measurement> measurements) {
        requireNonNull(measurements);
        if (measurements.isEmpty()) {
            return NO_MEASUREMENTS;
        }
        List<String> lines = measurements.stream()
                .sorted(Comparator.comparing(Measurement::getDate).reversed()
                        .thenComparing(Measurement::getType))
                .map(m -> m.getDate() + DATE_SEPARATOR + m.getType() + " " + m.getValue().toPlainString()
                        + " " + m.getType().getUnit())
                .toList();
        return "Measurements " + countHeader(lines.size()) + "\n" + String.join("\n", lines);
    }

    /**
     * Lists performances newest first; same-date records are shown most recently recorded first,
     * so the first line always matches the card's "Latest performance".
     *
     * @param performances Records in the order they were recorded, oldest recorded first.
     */
    static String formatPerformances(List<GymPerformance> performances) {
        requireNonNull(performances);
        if (performances.isEmpty()) {
            return NO_PERFORMANCES;
        }
        // Reversing first puts later recordings ahead; the stable sort keeps that order within a date.
        List<GymPerformance> newestFirst = new ArrayList<>(performances);
        Collections.reverse(newestFirst);
        newestFirst.sort(Comparator.comparing(GymPerformance::getDate).reversed());
        List<String> lines = newestFirst.stream()
                .map(p -> p.getDate() + DATE_SEPARATOR + p.getSummary())
                .toList();
        return "Performance " + countHeader(lines.size()) + "\n" + String.join("\n", lines);
    }

    private static String countHeader(int count) {
        return "(" + count + (count == 1 ? " record" : " records") + ", newest first):";
    }
}
