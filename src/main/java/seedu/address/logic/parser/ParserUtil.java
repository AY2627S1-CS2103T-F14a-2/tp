package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import seedu.address.commons.core.index.Index;
import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Address;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.tag.Tag;

/**
 * Contains utility methods used for parsing strings in the various *Parser classes.
 */
public class ParserUtil {
    public static final String MESSAGE_INVALID_INDEX = "Index must be a positive integer.";

    public static final String MESSAGE_SESSION_DATE_FORMAT =
            "Date must use YYYY-MM-DD format (year 0001-9999, month 01-12, day 01-31).";
    public static final String MESSAGE_INVALID_SESSION_DATE = "Date must be a valid calendar date.";
    public static final String MESSAGE_PAST_SESSION_DATE = "Date cannot be in the past.";
    public static final String MESSAGE_INVALID_SESSION_TIME = "Time must be a valid time in HHMM or HH:MM format.";

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("uuuu-MM-dd")
            .withResolverStyle(ResolverStyle.STRICT);
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);

    /**
     * Parses a session date, allowing today but rejecting past dates.
     */
    public static LocalDate parseSessionDate(String date) throws ParseException {
        requireNonNull(date);
        String trimmed = date.trim();
        if (!trimmed.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}")) {
            throw new ParseException(MESSAGE_SESSION_DATE_FORMAT);
        }
        int year = Integer.parseInt(trimmed.substring(0, 4));
        int month = Integer.parseInt(trimmed.substring(5, 7));
        int day = Integer.parseInt(trimmed.substring(8, 10));
        if (year == 0 || month < 1 || month > 12 || day < 1 || day > 31) {
            throw new ParseException(MESSAGE_SESSION_DATE_FORMAT);
        }
        LocalDate parsed;
        try {
            parsed = LocalDate.parse(trimmed, DATE_FORMATTER);
        } catch (DateTimeParseException exception) {
            throw new ParseException(MESSAGE_INVALID_SESSION_DATE);
        }
        if (parsed.isBefore(LocalDate.now())) {
            throw new ParseException(MESSAGE_PAST_SESSION_DATE);
        }
        return parsed;
    }

    /**
     * Parses a strict 24-hour time in HHMM or HH:MM format.
     */
    public static LocalTime parseSessionTime(String time) throws ParseException {
        requireNonNull(time);
        String trimmed = time.trim();
        if (!trimmed.matches("[0-9]{2}:?[0-9]{2}")) {
            throw new ParseException(MESSAGE_INVALID_SESSION_TIME);
        }
        String normalized = trimmed.length() == 4 ? trimmed.substring(0, 2) + ":" + trimmed.substring(2) : trimmed;
        try {
            return LocalTime.parse(normalized, TIME_FORMATTER);
        } catch (DateTimeParseException exception) {
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
     * Parses a {@code String name} into a {@code Name}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code name} is invalid.
     */
    public static Name parseName(String name) throws ParseException {
        requireNonNull(name);
        String trimmedName = name.trim();
        if (!Name.isValidName(trimmedName)) {
            throw new ParseException(Name.MESSAGE_CONSTRAINTS);
        }
        return new Name(trimmedName);
    }

    /**
     * Parses a {@code String phone} into a {@code Phone}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code phone} is invalid.
     */
    public static Phone parsePhone(String phone) throws ParseException {
        requireNonNull(phone);
        String trimmedPhone = phone.trim();
        if (!Phone.isValidPhone(trimmedPhone)) {
            throw new ParseException(Phone.MESSAGE_CONSTRAINTS);
        }
        return new Phone(trimmedPhone);
    }

    /**
     * Parses a {@code String address} into an {@code Address}.
     * Leading and trailing whitespaces will be trimmed.
     *
     * @throws ParseException if the given {@code address} is invalid.
     */
    public static Address parseAddress(String address) throws ParseException {
        requireNonNull(address);
        String trimmedAddress = address.trim();
        if (!Address.isValidAddress(trimmedAddress)) {
            throw new ParseException(Address.MESSAGE_CONSTRAINTS);
        }
        return new Address(trimmedAddress);
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
