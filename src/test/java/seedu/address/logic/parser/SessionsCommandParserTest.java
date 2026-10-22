package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.CancelSessionCommand;
import seedu.address.logic.commands.SessionsCommand;

/**
 * Tests strict date filtering syntax, real-calendar boundaries and dispatch of both session-management commands.
 */
public class SessionsCommandParserTest {
    private final SessionsCommandParser parser = new SessionsCommandParser();

    @Test
    public void parse_validDatesIncludingPast_success() {
        for (LocalDate date : List.of(LocalDate.now(), LocalDate.now().minusDays(1), LocalDate.of(2028, 2, 29),
                LocalDate.of(1, 1, 1), LocalDate.of(9999, 12, 31))) {
            assertParseSuccess(parser, " d/" + date, new SessionsCommand(date));
            assertParseSuccess(parser, "\td/ \t" + date + "\t", new SessionsCommand(date));
        }
    }

    @Test
    public void parse_missingRepeatedUnknownPrefixesOrExtraText_failure() {
        for (String input : List.of("", "2026-10-22", "1 d/2026-10-22", "d/2026-10-22 d/2026-10-23",
                "d/2026-10-22 t/1700", "x/foo d/2026-10-22", "d/2026-10-22 extra", "D/2026-10-22")) {
            assertParseFailure(parser, input, SessionsCommand.MESSAGE_INVALID_FORMAT);
        }
    }

    @Test
    public void parse_malformedDates_failure() {
        for (String date : List.of("", "2026-1-01", "0000-01-01", "+2028-01-01", "10000-01-01",
                "22-10-2026", "2026-00-01", "2026-13-01", "2026-01-00", "2026-01-32")) {
            assertParseFailure(parser, "d/" + date, ParserUtil.MESSAGE_SESSION_DATE_FORMAT);
        }
    }

    @Test
    public void parse_impossibleCalendarDates_failure() {
        for (String date : List.of("2026-02-29", "2028-02-30", "2026-04-31")) {
            assertParseFailure(parser, "d/" + date, ParserUtil.MESSAGE_INVALID_SESSION_DATE);
        }
    }

    @Test
    public void addressBookParser_dispatchesBothCommands() throws Exception {
        AddressBookParser dispatcher = new AddressBookParser();
        assertEquals(new SessionsCommand(LocalDate.of(2026, 10, 22)),
                dispatcher.parseCommand("sessions d/2026-10-22"));
        assertEquals(new CancelSessionCommand(Index.fromOneBased(1)), dispatcher.parseCommand("cancelsession 1"));
    }
}
