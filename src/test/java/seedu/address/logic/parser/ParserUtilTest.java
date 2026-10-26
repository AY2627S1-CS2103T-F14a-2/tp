package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.parser.ParserUtil.MESSAGE_INVALID_INDEX;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.Messages;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Email;
import seedu.address.model.person.Name;
import seedu.address.model.person.Phone;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.tag.Tag;

public class ParserUtilTest {
    private static final String INVALID_NAME = "R@chel";
    private static final String INVALID_PHONE = "+651234";
    private static final String INVALID_EMAIL = "example.com";
    private static final String INVALID_TAG = "#friend";

    private static final String VALID_NAME = "Rachel Walker";
    private static final String VALID_PHONE = "123456";
    private static final String VALID_EMAIL = "rachel@example.com";
    private static final String VALID_TAG_1 = "friend";
    private static final String VALID_TAG_2 = "neighbour";

    private static final String WHITESPACE = " \t\r\n";
    private static final String FUTURE_DATE_MESSAGE = "The record date cannot be in the future.";

    @Test
    public void parseIndex_invalidInput_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseIndex("10 a"));
    }

    @Test
    public void parseIndex_outOfRangeInput_throwsParseException() {
        assertThrows(ParseException.class, MESSAGE_INVALID_INDEX, ()
            -> ParserUtil.parseIndex(Long.toString(Integer.MAX_VALUE + 1)));
    }

    @Test
    public void parseIndex_validInput_success() throws Exception {
        // No whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("1"));

        // Leading and trailing whitespaces
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseIndex("  1  "));
    }

    @Test
    public void parseClientIndex_validInput_success() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseClientIndex(" 1 "));
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseClientIndex(" 1 ", "Expected command format"));
        assertEquals(Index.fromOneBased(Integer.MAX_VALUE),
                ParserUtil.parseClientIndex(Integer.toString(Integer.MAX_VALUE), "Expected command format"));
    }

    @Test
    public void parseClientIndex_invalidNumber_usesCommonClientIndexError() {
        for (String index : List.of("0", "-1", "2147483648", "99999999999999999999")) {
            assertThrows(ParseException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                    ParserUtil.parseClientIndex(index));
            assertThrows(ParseException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, () ->
                    ParserUtil.parseClientIndex(index, "Expected command format"));
        }
    }

    @Test
    public void parseClientIndex_malformedInput_usesCallerFormatError() {
        for (String input : List.of("", " ", "Sarah", "1 2", "1.5", "+1", "1 d/2026-10-22")) {
            assertThrows(ParseException.class, "Expected command format", () ->
                    ParserUtil.parseClientIndex(input, "Expected command format"));
        }
    }

    @Test
    public void parseClientIndex_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClientIndex(null));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClientIndex(null, "Expected command format"));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseClientIndex("1", null));
    }

    @Test
    public void parseRecordDate_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRecordDate(null, FUTURE_DATE_MESSAGE));
        assertThrows(NullPointerException.class, () -> ParserUtil.parseRecordDate("2026-09-18", null));
    }

    @Test
    public void parseRecordDate_wrongFormat_throwsParseException() {
        for (String wrongFormat : List.of("", "18-09-2026", "2026/09/18", "2026-9-18", "12026-09-18",
                "+2026-09-18", "today")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_RECORD_DATE, ()
                -> ParserUtil.parseRecordDate(wrongFormat, FUTURE_DATE_MESSAGE));
        }
    }

    @Test
    public void parseRecordDate_nonexistentDate_throwsParseException() {
        // the format is right but the date does not exist, so the message names the date instead of the format
        for (String nonexistentDate : List.of("2026-02-30", "2027-02-29", "2026-13-01", "2026-00-10", "2026-04-31",
                "0000-01-01")) {
            assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_NONEXISTENT_RECORD_DATE,
                    nonexistentDate), () -> ParserUtil.parseRecordDate(nonexistentDate, FUTURE_DATE_MESSAGE));
        }

        // surrounding whitespace is not repeated in the message
        assertThrows(ParseException.class, "2026-02-30 is not a real date.", ()
            -> ParserUtil.parseRecordDate("  2026-02-30 ", FUTURE_DATE_MESSAGE));
    }

    @Test
    public void parseRecordDate_futureDate_throwsParseException() {
        String tomorrow = LocalDate.now().plusDays(1).toString();
        assertThrows(ParseException.class, FUTURE_DATE_MESSAGE, ()
            -> ParserUtil.parseRecordDate(tomorrow, FUTURE_DATE_MESSAGE));
    }

    @Test
    public void parseExistingRecordDate_invalidDate_throwsParseException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseExistingRecordDate(null));
        for (String wrongFormat : List.of("", "18-09-2026", "12026-09-18")) {
            assertThrows(ParseException.class, ParserUtil.MESSAGE_INVALID_RECORD_DATE, ()
                -> ParserUtil.parseExistingRecordDate(wrongFormat));
        }
        for (String nonexistentDate : List.of("2026-02-30", "0000-01-01")) {
            assertThrows(ParseException.class, String.format(ParserUtil.MESSAGE_NONEXISTENT_RECORD_DATE,
                    nonexistentDate), () -> ParserUtil.parseExistingRecordDate(nonexistentDate));
        }
    }

    @Test
    public void parseClientIndex() throws Exception {
        assertEquals(INDEX_FIRST_PERSON, ParserUtil.parseClientIndex(" 1 "));

        // zero, negative and overflowing indexes are invalid client indexes, not format errors
        for (String invalidIndex : List.of("0", "-1", "+1", "99999999999")) {
            assertThrows(ParseException.class, Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX, ()
                -> ParserUtil.parseClientIndex(invalidIndex));
        }
    }

    @Test
    public void parseExerciseName() throws Exception {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseExerciseName(null));
        assertThrows(ParseException.class, ExerciseName.MESSAGE_CONSTRAINTS, ()
            -> ParserUtil.parseExerciseName("Bench@Press"));
        assertEquals(new ExerciseName("Bench Press"), ParserUtil.parseExerciseName("  Bench   Press "));
        assertEquals("Bench Press", ParserUtil.parseExerciseName("  Bench   Press ").value);
    }

    @Test
    public void parseExistingRecordDate_anyRealDate_returnsDate() throws Exception {
        // future dates are accepted, so that future-dated records loaded from a data file can be deleted
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertEquals(tomorrow, ParserUtil.parseExistingRecordDate(tomorrow.toString()));
        assertEquals(LocalDate.of(2026, 9, 18), ParserUtil.parseExistingRecordDate(" 2026-09-18 "));
    }

    @Test
    public void parseRecordDate_pastOrPresentDate_returnsDate() throws Exception {
        assertEquals(LocalDate.of(2026, 9, 18), ParserUtil.parseRecordDate("2026-09-18", FUTURE_DATE_MESSAGE));
        assertEquals(LocalDate.of(2024, 2, 29), ParserUtil.parseRecordDate("2024-02-29", FUTURE_DATE_MESSAGE));
        assertEquals(LocalDate.now(), ParserUtil.parseRecordDate(LocalDate.now().toString(), FUTURE_DATE_MESSAGE));

        // leading and trailing whitespace
        assertEquals(LocalDate.of(2026, 9, 18), ParserUtil.parseRecordDate("  2026-09-18  ", FUTURE_DATE_MESSAGE));
    }

    @Test
    public void parseName_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseName((String) null));
    }

    @Test
    public void parseName_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseName(INVALID_NAME));
    }

    @Test
    public void parseName_tooLong_throwsLengthMessage() {
        assertThrows(ParseException.class, Name.MESSAGE_LENGTH_CONSTRAINTS, ()
            -> ParserUtil.parseName("a".repeat(Name.MAX_LENGTH + 1)));
    }

    @Test
    public void parseName_validValueWithoutWhitespace_returnsName() throws Exception {
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(VALID_NAME));
    }

    @Test
    public void parseName_validValueWithWhitespace_returnsTrimmedName() throws Exception {
        String nameWithWhitespace = WHITESPACE + VALID_NAME + WHITESPACE;
        Name expectedName = new Name(VALID_NAME);
        assertEquals(expectedName, ParserUtil.parseName(nameWithWhitespace));
    }

    @Test
    public void parsePhone_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parsePhone((String) null));
    }

    @Test
    public void parsePhone_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parsePhone(INVALID_PHONE));
    }

    @Test
    public void parsePhone_tooLong_throwsLengthMessage() {
        assertThrows(ParseException.class, Phone.MESSAGE_LENGTH_CONSTRAINTS, ()
            -> ParserUtil.parsePhone("1".repeat(Phone.MAX_LENGTH + 1)));
    }

    @Test
    public void parsePhone_validValueWithoutWhitespace_returnsPhone() throws Exception {
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(VALID_PHONE));
    }

    @Test
    public void parsePhone_validValueWithWhitespace_returnsTrimmedPhone() throws Exception {
        String phoneWithWhitespace = WHITESPACE + VALID_PHONE + WHITESPACE;
        Phone expectedPhone = new Phone(VALID_PHONE);
        assertEquals(expectedPhone, ParserUtil.parsePhone(phoneWithWhitespace));
    }

    @Test
    public void parsePhone_spacesBetweenDigits_returnsDigitsOnly() throws Exception {
        assertEquals(new Phone("99272758"), ParserUtil.parsePhone("9927 2758")); // as shown on the card
        assertEquals(new Phone("99272758"), ParserUtil.parsePhone("9927  2758")); // repeated spaces
        assertEquals(new Phone("99272758"), ParserUtil.parsePhone(" 9927 2758 ")); // surrounding spaces
        assertEquals(new Phone("1234"), ParserUtil.parsePhone("123 4")); // uneven groups
        assertEquals(new Phone("123"), ParserUtil.parsePhone("1 2 3")); // shortest length after removal
        assertEquals(new Phone("1".repeat(Phone.MAX_LENGTH)),
                ParserUtil.parsePhone("1".repeat(8) + " " + "1".repeat(Phone.MAX_LENGTH - 8))); // longest
    }

    @Test
    public void parsePhone_spacesWithOtherCharacters_throwsFormatMessage() {
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("9927-2758"));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("+65 9927 2758"));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("   "));
        assertThrows(ParseException.class, Phone.MESSAGE_CONSTRAINTS, () -> ParserUtil.parsePhone("1 2")); // 2 digits
    }

    @Test
    public void parsePhone_tooLongAfterRemovingSpaces_throwsLengthMessage() {
        assertThrows(ParseException.class, Phone.MESSAGE_LENGTH_CONSTRAINTS, ()
            -> ParserUtil.parsePhone("1".repeat(8) + " " + "1".repeat(Phone.MAX_LENGTH - 7)));
    }

    @Test
    public void parseEmail_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseEmail((String) null));
    }

    @Test
    public void parseEmail_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseEmail(INVALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithoutWhitespace_returnsEmail() throws Exception {
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(VALID_EMAIL));
    }

    @Test
    public void parseEmail_validValueWithWhitespace_returnsTrimmedEmail() throws Exception {
        String emailWithWhitespace = WHITESPACE + VALID_EMAIL + WHITESPACE;
        Email expectedEmail = new Email(VALID_EMAIL);
        assertEquals(expectedEmail, ParserUtil.parseEmail(emailWithWhitespace));
    }

    @Test
    public void parseTag_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTag(null));
    }

    @Test
    public void parseTag_invalidValue_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTag(INVALID_TAG));
    }

    @Test
    public void parseTag_validValueWithoutWhitespace_returnsTag() throws Exception {
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(VALID_TAG_1));
    }

    @Test
    public void parseTag_validValueWithWhitespace_returnsTrimmedTag() throws Exception {
        String tagWithWhitespace = WHITESPACE + VALID_TAG_1 + WHITESPACE;
        Tag expectedTag = new Tag(VALID_TAG_1);
        assertEquals(expectedTag, ParserUtil.parseTag(tagWithWhitespace));
    }

    @Test
    public void parseTags_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> ParserUtil.parseTags(null));
    }

    @Test
    public void parseTags_collectionWithInvalidTags_throwsParseException() {
        assertThrows(ParseException.class, () -> ParserUtil.parseTags(List.of(VALID_TAG_1, INVALID_TAG)));
    }

    @Test
    public void parseTags_emptyCollection_returnsEmptySet() throws Exception {
        assertTrue(ParserUtil.parseTags(List.of()).isEmpty());
    }

    @Test
    public void parseTags_collectionWithValidTags_returnsTagSet() throws Exception {
        Set<Tag> actualTagSet = ParserUtil.parseTags(List.of(VALID_TAG_1, VALID_TAG_2));
        Set<Tag> expectedTagSet = Set.of(new Tag(VALID_TAG_1), new Tag(VALID_TAG_2));

        assertEquals(expectedTagSet, actualTagSet);
    }
}
