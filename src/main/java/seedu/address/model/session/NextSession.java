package seedu.address.model.session;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.AppUtil.checkArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;

/**
 * Represents the date and time of a client's next training session.
 * Guarantees: immutable; is valid as declared in {@link #isValidNextSession(String)}
 */
public class NextSession {

    public static final String MESSAGE_CONSTRAINTS =
            "Next session should be a valid date and time in the format yyyy-MM-dd HH:mm";

    /** Format used when entering, displaying, and storing a next session. */
    public static final String FORMAT_PATTERN = "uuuu-MM-dd HH:mm";

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern(FORMAT_PATTERN)
            .withResolverStyle(ResolverStyle.STRICT);

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
        try {
            LocalDateTime.parse(test, FORMATTER);
            return true;
        } catch (DateTimeParseException exception) {
            return false;
        }
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
