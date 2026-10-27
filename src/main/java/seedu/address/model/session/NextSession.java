package seedu.address.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Locale;

/**
 * Represents the date and time of a client's next training session.
 * Guarantees: immutable; is valid as declared in {@link #isValidNextSession(String)}
 */
public class NextSession {

    public static final String MESSAGE_CONSTRAINTS =
            "Next session should be a valid date and time in the format yyyy-MM-dd HH:mm, "
                    + "e.g. 2026-10-08 18:00";

    /** Format used when entering, displaying, and storing a next session. */
    public static final String FORMAT_PATTERN = "uuuu-MM-dd HH:mm";

    private static final String INPUT_REGEX = "[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter CARD_FORMATTER =
            DateTimeFormatter.ofPattern("EEE d MMM, h:mm", Locale.ENGLISH);
    private static final DateTimeFormatter CARD_WITH_YEAR_FORMATTER =
            DateTimeFormatter.ofPattern("EEE d MMM uuuu, h:mm", Locale.ENGLISH);

    public final LocalDateTime value;

    /**
     * Constructs a {@code NextSession}.
     *
     * @param nextSession A valid date and time in {@code yyyy-MM-dd HH:mm} format.
     */
    public NextSession(String nextSession) {
        requireNonNull(nextSession);
        checkArgument(isValidNextSession(nextSession), MESSAGE_CONSTRAINTS);
        value = LocalDateTime.parse(nextSession, FORMATTER);
    }

    /**
     * Returns true if the given string is a valid date and time in the required format.
     */
    public static boolean isValidNextSession(String test) {
        requireNonNull(test);
        // The formatter alone accepts signed years and year zero, which do not match the advertised format.
        if (!test.matches(INPUT_REGEX)) {
            return false;
        }
        try {
            return LocalDateTime.parse(test, FORMATTER).getYear() > 0;
        } catch (DateTimeParseException exception) {
            return false;
        }
    }

    /**
     * Formats the card row in English, retaining the distinction between expired and unbooked sessions.
     */
    public String toDisplayString(LocalDateTime now) {
        requireNonNull(now);
        if (value.isBefore(now)) {
            return "- (none upcoming)";
        }
        // The year is shown only when it differs from now, so a booking next year isn't read as this year's.
        DateTimeFormatter formatter = value.getYear() == now.getYear() ? CARD_FORMATTER : CARD_WITH_YEAR_FORMATTER;
        return value.format(formatter)
                + (value.getHour() < 12 ? "am" : "pm");
    }

    @Override
    public String toString() {
        return value.format(FORMATTER);
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof NextSession otherNextSession)) {
            return false;
        }

        return value.equals(otherNextSession.value);
    }

    @Override
    public int hashCode() {
        return value.hashCode();
    }
}
