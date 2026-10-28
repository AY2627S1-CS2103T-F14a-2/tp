package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Checks the shared date-error contract through every command that accepts a calendar date.
 * AI assistance: Helen used OpenAI Codex to draft these regression tests for issue #164.
 */
public class CalendarDateCommandParserTest {
    private static final List<String> COMMANDS = List.of(
            "measure 1 d/%s t/weight v/70",
            "deletemeasure 1 d/%s t/weight",
            "performance 1 d/%s e/Squat s/3 r/5 w/60",
            "deleteperformance 1 d/%s e/Squat",
            "session 1 d/%s t/1700",
            "sessions d/%s");

    @Test
    public void parse_malformedDate_allCommandsShowSameDateError() {
        for (String date : List.of("2026-2-01", "2026-13-01", "2026-00-01", "2026-01-00",
                "2026-01-32", "0000-01-01", "-0001-01-01", "+10000-01-01")) {
            assertDateError(date,
                    "Date must use YYYY-MM-DD format (year 0001-9999, month 01-12, day 01-31).");
        }
    }

    @Test
    public void parse_impossibleDate_allCommandsNameTheInvalidDate() {
        for (String date : List.of("2026-02-30", "2027-02-29", "1900-02-29", "2026-04-31")) {
            assertDateError(date, date + " is not a real date.");
        }
    }

    @Test
    public void parse_validPastDate_allCommandsAcceptIncludingBoundariesAndLeapDays() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String date : List.of("0001-01-01", "2000-02-29", LocalDate.now().minusDays(1).toString(),
                LocalDate.now().toString())) {
            for (String command : COMMANDS) {
                parser.parseCommand(String.format(command, date));
            }
        }
    }

    @Test
    public void parse_futureDate_onlyRecordingCommandsReject() throws Exception {
        AddressBookParser parser = new AddressBookParser();
        for (String date : List.of(LocalDate.now().plusDays(1).toString(), "9999-12-31")) {
            for (String command : COMMANDS) {
                String input = String.format(command, date);
                if (command.startsWith("measure ") || command.startsWith("performance ")) {
                    String record = command.startsWith("measure ") ? "measurement" : "performance";
                    assertEquals("The " + record + " date cannot be in the future.",
                            assertThrows(ParseException.class, () -> parser.parseCommand(input))
                                    .getMessage().lines().findFirst().orElseThrow());
                } else {
                    parser.parseCommand(input);
                }
            }
        }
    }

    private void assertDateError(String date, String message) {
        AddressBookParser parser = new AddressBookParser();
        for (String command : COMMANDS) {
            String input = String.format(command, date);
            ParseException exception = assertThrows(ParseException.class, () -> parser.parseCommand(input), input);
            // Command-specific usage stays below the shared date error.
            assertEquals(message, exception.getMessage().lines().findFirst().orElseThrow(), input);
        }
    }
}
