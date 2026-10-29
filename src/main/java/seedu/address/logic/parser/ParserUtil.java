package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.Messages;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {
    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    // Numeric-looking input uses the client-index error; other input uses the caller's format error.
    // A leading "+" is a format error, so "+1" is not reported as an invalid index when client 1 exists.
    public static final String INTEGER_REGEX = "-?\\d+";
    public static final String MESSAGE_NONEXISTENT_DATE = "%s is not a real date.";

    public static final String MESSAGE_DATE_FORMAT =
            "Date must use YYYY-MM-DD format (year 0001-9999, month 01-12, day 01-31).";
    public static final String MESSAGE_INVALID_SESSION_TIME =
            "Time must be a 24-hour time such as 930, 0930, 9:30 or 19:00.";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    // One- or two-digit hour, optional colon, two-digit minute, e.g. 930, 0930, 9:30, 19:00.
    private static final Pattern TIME_PATTERN = Pattern.compile("(\\d{1,2}):?(\\d{2})");

    private static final Pattern PREFIX_PATTERN = Pattern.compile("(?:^|\\s)([^\\s/]+/)");
    // A whole word "s/o" or "a/l" (son of), or "d/o" or "a/p" (daughter of), in any letter case
    private static final Pattern NAME_CONNECTOR_PATTERN =
            Pattern.compile("(?<=\\s)(?:[sd]/o|a/[lp])(?=\\s|$)", Pattern.CASE_INSENSITIVE);

    /** Rejects prefix-shaped arguments outside the command's accepted fields. */
    public static void rejectUnsupportedPrefixes(String arguments, String message, Prefix... allowed)
            throws ParseException {
        Matcher matcher = PREFIX_PATTERN.matcher(arguments);
        Set<String> prefixes = new HashSet<>();
        for (Prefix prefix : allowed) {
            prefixes.add(prefix.getPrefix());
        }
        while (matcher.find()) {
            if (!prefixes.contains(matcher.group(1))) {
                throw new ParseException(message);
            }
        }
    }

    /**
     * Rejects prefix-shaped arguments outside the client fields of {@code add} and {@code edit}, like
     * {@link #rejectUnsupportedPrefixes}, except that the name connectors {@code s/o}, {@code d/o},
     * {@code a/l} and {@code a/p} (as in "Raju s/o Kumar") are part of a name rather than prefixes.
     */
    public static void rejectUnsupportedClientPrefixes(String arguments, String message, Prefix... allowed)
            throws ParseException {
        String withoutConnectors = NAME_CONNECTOR_PATTERN.matcher(arguments).replaceAll("");
        rejectUnsupportedPrefixes(withoutConnectors, message, allowed);
    }

    /** Normalizes and validates the full-name identifier shared by session commands. */
    public static String parseSessionClientName(String value) throws ParseException {
        String name = Name.normalizeSpaces(requireNonNull(value));
        if (name.isEmpty()) {
            throw new ParseException(SessionCommand.MESSAGE_EMPTY_NAME);
        }
        if (name.contains("/")) {
            throw new ParseException(SessionCommand.MESSAGE_NAME_SLASH);
        }
        return name;
    }

    /**
     * Parses a real YYYY-MM-DD date in years 0001-9999 without restricting it relative to today,
     * for recording, deleting, booking and filtering. Callers apply any relative-date policy.
     */
    // AI assistance: Helen used OpenAI Codex to unify calendar validation and tests for issue #164.
    public static LocalDate parseCalendarDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmed = date.trim();
        if (!trimmed.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new ParseException(MESSAGE_DATE_FORMAT);
        }
        if (trimmed.startsWith("0000") || !hasValidMonthAndDay(trimmed)) {
            throw new ParseException(MESSAGE_DATE_FORMAT);
        }
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new ParseException(String.format(MESSAGE_NONEXISTENT_DATE, trimmed), exception);
        }
        return parsed;
    }

    /**
     * Parses a 24-hour time in HMM, HHMM, H:MM or HH:MM format.
     */
    public static LocalTime parseSessionTime(String time) throws ParseException {
        requireNonNull(time);
        Matcher matcher = TIME_PATTERN.matcher(time.trim());
        if (!matcher.matches()) {
            throw new ParseException(MESSAGE_INVALID_SESSION_TIME);
        }
        try {
            return LocalTime.of(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
        } catch (DateTimeException exception) {
            throw new ParseException(MESSAGE_INVALID_SESSION_TIME);
        }
    }

    /**
     * Parses {@code oneBasedIndex} into an {@code Index} and returns it. Leading and trailing whitespaces will be
     * trimmed.
     * @throws ParseException if the specified index is invalid (not a non-zero unsigned integer).
     */
    public static Index parseIndex(String oneBasedIndex) throws ParseException {
        String trimmedIndex = oneBasedIndex.trim();
        if (!StringUtil.isNonZeroUnsignedInteger(trimmedIndex)) {
            throw new ParseException(MESSAGE_INVALID_INDEX);
        }
        return Index.fromOneBased(Integer.parseInt(trimmedIndex));
    }

    /**
     * Parses a displayed client index after the caller has checked the numeric syntax.
     * Zero, negative, signed-positive and overflowing values use the common invalid-client-index error.
     */
    public static Index parseClientIndex(String oneBasedIndex) throws ParseException {
        requireNonNull(oneBasedIndex);
        try {
            return parseIndex(oneBasedIndex);
        } catch (ParseException exception) {
            throw new ParseException(Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, exception);
        }
    }

    /**
     * Parses a displayed client index, distinguishing malformed command input from an invalid numeric index.
     * The caller supplies its existing format message so command-specific usage text is preserved.
     */
    public static Index parseClientIndex(String oneBasedIndex, String invalidFormatMessage) throws ParseException {
        requireNonNull(oneBasedIndex);
        requireNonNull(invalidFormatMessage);
        if (!oneBasedIndex.trim().matches(INTEGER_REGEX)) {
            throw new ParseException(invalidFormatMessage);
        }
        return parseClientIndex(oneBasedIndex);
    }

    /**
     * Parses a {@code String exerciseName} into an {@code ExerciseName}.
     * Leading, trailing and repeated spaces are normalised by {@code ExerciseName}.
     *
     * @throws ParseException if the given {@code exerciseName} is invalid.
     */
    public static ExerciseName parseExerciseName(String exerciseName) throws ParseException {
        requireNonNull(exerciseName);
        if (!ExerciseName.isValidExerciseName(exerciseName)) {
            throw new ParseException(ExerciseName.MESSAGE_CONSTRAINTS);
        }
        return new ExerciseName(exerciseName);
    }

    /**
     * Parses the date of a progress record, such as a body measurement or a gym performance,
     * ignoring surrounding whitespace.
     * The date must be a real calendar date in yyyy-MM-dd format with a year from 0001, and must not be
     * in the future, as only results that have already happened can be recorded.
     *
     * @param futureDateMessage The message to show if the date is in the future.
     * @throws ParseException if the date is malformed, does not exist, or is in the future.
     */
    public static LocalDate parseRecordDate(String date, String futureDateMessage) throws ParseException {
        requireNonNull(futureDateMessage);
        LocalDate parsedDate = parseCalendarDate(date);
        if (parsedDate.isAfter(LocalDate.now())) {
            throw new ParseException(futureDateMessage);
        }
        return parsedDate;
    }

    /**
     * Returns true if the month and day of a {@code yyyy-MM-dd} string are within 01-12 and 01-31.
     */
    private static boolean hasValidMonthAndDay(String date) {
        int month = Integer.parseInt(date.substring(5, 7));
        int day = Integer.parseInt(date.substring(8, 10));
        return month >= 1 && month <= 12 && day >= 1 && day <= 31;
    }

    /**
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.isTooLong(trimmedName)
                    ? Name.MESSAGE_LENGTH_CONSTRAINTS : Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * The phone model normalizes spacing consistently for commands and manually edited data files.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        if (!Phone.isValidPhone(phone)) {
            throw new ParseException(Phone.isTooLong(phone)
                    ? Phone.MESSAGE_LENGTH_CONSTRAINTS : Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(phone);
    }

    /**
     * Parses a {@code String email} into an {@code Email}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code email} is invalid.
     */
    public static Email parseEmail(String email) throws ParseException {
        requireNonNull(email);
        String trimmedEmail = email.trim();
        if (!Email.isValidEmail(trimmedEmail)) {
            throw new ParseException(Email.MESSAGE_CONSTRAINTS);
        }
        return new Email(trimmedEmail);
    }

    /**
     * Parses a {@code String tag} into a {@code Tag}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code tag} is invalid.
     */
    public static Tag parseTag(String tag) throws ParseException {
        requireNonNull(tag);
        String trimmedTag = tag.trim();
        if (!Tag.isValidTagName(trimmedTag)) {
            throw new ParseException(Tag.MESSAGE_CONSTRAINTS);
        }
        return new Tag(trimmedTag);
    }

    /**
     * Parses {@code Collection<String> tags} into a {@code Set<Tag>}.
     */
    public static Set<Tag> parseTags(Collection<String> tags) throws ParseException {
        requireNonNull(tags);
        final Set<Tag> tagSet = new HashSet<>();
        for (String tagName : tags) {
            tagSet.add(parseTag(tagName));
        }
        return tagSet;
    }
}
