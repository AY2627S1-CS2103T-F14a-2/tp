package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.SessionCommand;
import seedu.address.model.session.NextSession;

/**
 * Exercises both session command forms, strict calendar/time boundaries, and malformed input.
 */
public class SessionCommandParserTest {
    // @@author gabriel-wan
    private final SessionCommandParser parser = new SessionCommandParser();
    private final String date = LocalDate.now().plusDays(1).toString();
    private final NextSession session = new NextSession(date + " 17:00");

    @Test
    public void parse_validIndexAndTimeForms_success() {
        assertParseSuccess(parser, " 1 d/" + date + " t/1700", new SessionCommand(Index.fromOneBased(1), session));
        assertParseSuccess(parser, " 1 t/17:00 d/" + date, new SessionCommand(Index.fromOneBased(1), session));
        assertParseSuccess(parser, "\t1\td/" + date + "\tt/1700\t", new SessionCommand(Index.fromOneBased(1), session));
    }

    @Test
    public void parse_nameAndRepeatedSpaces_success() {
        assertParseSuccess(parser, " n/  sarah   lim  d/" + date + " t/17:00",
                new SessionCommand("sarah lim", session));
        assertParseSuccess(parser, " t/1700 d/" + date + " n/Sarah Lim", new SessionCommand("Sarah Lim", session));
    }

    @Test
    public void parse_missingDuplicateUnknownPrefixesAndIdentifiers_failure() {
        for (String input : List.of("", " 1", " 1 d/" + date, " 1 t/1700", " d/" + date + " t/1700",
                " 1 n/Sarah Lim d/" + date + " t/1700", " Sarah d/" + date + " t/1700",
                " 1 2 d/" + date + " t/1700", " +1 d/" + date + " t/1700", " 1 d/" + date + " d/" + date + " t/1700",
                " 1 d/" + date + " t/1700 t/1800", " n/Sarah n/Lim d/" + date + " t/1700",
                " 1 x/foo d/" + date + " t/1700", " 1 d/" + date + " t/1700 q/foo",
                " n/Sarah q/foo d/" + date + " t/1700")) {
            assertParseFailure(parser, input, SessionCommand.MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_zeroNegativeAndOverflowIndex_failure() {
        for (String index : List.of("0", "-1", "2147483648", "999999999999999999999")) {
            assertParseFailure(parser, " " + index + " d/" + date + " t/1700", SessionCommand.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_emptyOrSlashName_failure() {
        assertParseFailure(parser, " n/ d/" + date + " t/1700", SessionCommand.MESSAGE_EMPTY_NAME);
        assertParseFailure(parser, " n/Sarah/Lim d/" + date + " t/1700", SessionCommand.MESSAGE_NAME_SLASH);
    }

    // @@author
    @Test
    public void parse_invalidDateFormat_failure() {
        for (String malformed : List.of("", "2026-1-01", "0000-01-01", "+2028-01-01", "12028-01-01",
                "01-01-2028", "2026-13-01", "2026-00-10", "2026-01-00", "2026-01-32")) {
            assertParseFailure(parser, " 1 d/" + malformed + " t/1700", ParserUtil.MESSAGE_DATE_FORMAT);
        }
    }

    @Test
    public void parse_impossibleCalendarDate_failure() {
        for (String impossible : List.of("2028-02-30", "2027-02-29", "2028-04-31")) {
            assertParseFailure(parser, " 1 d/" + impossible + " t/1700",
                    String.format(ParserUtil.MESSAGE_NONEXISTENT_DATE, impossible));
        }
    }

    @Test
    public void parse_pastDate_success() {
        LocalDate yesterday = LocalDate.now().minusDays(1);
        assertParseSuccess(parser, " 1 d/" + yesterday + " t/1700",
                new SessionCommand(Index.fromOneBased(1), new NextSession(yesterday + " 17:00")));
    }

    // @@author gabriel-wan
    @Test
    public void parse_invalidTime_failure() {
        for (String time : List.of("", "2400", "24:00", "2360", "23:60", "-100", "9:3", "17:00:00",
                "17.00", "5pm", "17000", "17:00 trailing")) {
            assertParseFailure(parser, " 1 d/" + date + " t/" + time, ParserUtil.MESSAGE_INVALID_SESSION_TIME);
        }
    }

    // @@author
    @Test
    public void parse_midnightAndSingleDigitHours_storesMinutePrecision() {
        for (String time : List.of("000", "0000", "0:00", "00:00")) {
            assertParseSuccess(parser, " 1 d/" + date + " t/" + time,
                    new SessionCommand(Index.fromOneBased(1), new NextSession(date + " 00:00")));
        }
        for (String time : List.of("905", "0905", "9:05", "09:05")) {
            assertParseSuccess(parser, " n/Sarah Lim d/" + date + " t/" + time,
                    new SessionCommand("Sarah Lim", new NextSession(date + " 09:05")));
        }
    }

    @Test
    public void parseDateAndTime_boundaries_success() throws Exception {
        assertEquals(LocalDate.now(), ParserUtil.parseCalendarDate(" " + LocalDate.now() + " "));
        assertEquals(LocalDate.of(9999, 12, 31), ParserUtil.parseCalendarDate("9999-12-31"));
        assertEquals(LocalTime.MIDNIGHT, ParserUtil.parseSessionTime("0000"));
        assertEquals(LocalTime.of(23, 59), ParserUtil.parseSessionTime("23:59"));
        assertEquals(LocalTime.of(9, 5), ParserUtil.parseSessionTime(" 0905 "));
        for (String singleDigitHour : List.of("930", "9:30", "0930", "09:30")) {
            assertEquals(LocalTime.of(9, 30), ParserUtil.parseSessionTime(singleDigitHour));
        }
        assertParseSuccess(parser, " 1 d/" + LocalDate.now() + " t/0000",
                new SessionCommand(Index.fromOneBased(1), new NextSession(LocalDate.now() + " 00:00")));
    }

    // @@author gabriel-wan
    @Test
    public void addressBookParser_dispatchesSession() throws Exception {
        assertEquals(new SessionCommand(Index.fromOneBased(1), session),
                new AddressBookParser().parseCommand("session 1 d/" + date + " t/1700"));
    }
    // @@author
}
