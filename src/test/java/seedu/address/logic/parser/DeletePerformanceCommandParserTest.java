package seedu.address.logic.parser;

import static seedu.address.logic.commands.DeletePerformanceCommand.withUsage;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.DeletePerformanceCommand;
import seedu.address.model.progress.ExerciseName;

public class DeletePerformanceCommandParserTest {

    private static final String DATE = " d/2026-09-18";
    private static final String EXERCISE = " e/Bench Press";
    private static final LocalDate RECORD_DATE = LocalDate.of(2026, 9, 18);
    private static final ExerciseName BENCH_PRESS = new ExerciseName("Bench Press");

    private final DeletePerformanceCommandParser parser = new DeletePerformanceCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        DeletePerformanceCommand expectedCommand =
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, RECORD_DATE, BENCH_PRESS);
        assertParseSuccess(parser, "1" + DATE + EXERCISE, expectedCommand);

        // prefixes in a different order
        assertParseSuccess(parser, "1" + EXERCISE + DATE, expectedCommand);

        // extra whitespace and different letter case
        assertParseSuccess(parser, "  1   d/ 2026-09-18   e/  bench    PRESS ", expectedCommand);

        // another index
        assertParseSuccess(parser, "2" + DATE + EXERCISE,
                new DeletePerformanceCommand(INDEX_SECOND_PERSON, RECORD_DATE, BENCH_PRESS));
    }

    @Test
    public void parse_futureDate_success() {
        // a future-dated record may have been loaded from an edited data file
        LocalDate tomorrow = LocalDate.now().plusDays(1);
        assertParseSuccess(parser, "1 d/" + tomorrow + EXERCISE,
                new DeletePerformanceCommand(INDEX_FIRST_PERSON, tomorrow, BENCH_PRESS));
    }

    @Test
    public void parse_missingOrRepeatedPart_failure() {
        String expectedMessage = DeletePerformanceCommand.MESSAGE_INVALID_FORMAT;

        assertParseFailure(parser, "", expectedMessage); // no arguments
        assertParseFailure(parser, DATE + EXERCISE, expectedMessage); // missing index
        assertParseFailure(parser, "1" + EXERCISE, expectedMessage); // missing date
        assertParseFailure(parser, "1" + DATE, expectedMessage); // missing exercise
        assertParseFailure(parser, "1" + DATE + EXERCISE + DATE, expectedMessage); // repeated date
        assertParseFailure(parser, "1" + DATE + EXERCISE + " e/Squat", expectedMessage); // repeated exercise
        assertParseFailure(parser, "a" + DATE + EXERCISE, expectedMessage); // non-numeric index
        assertParseFailure(parser, "1 extra" + DATE + EXERCISE, expectedMessage); // extra text
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expectedMessage = withUsage(DeletePerformanceCommand.MESSAGE_INVALID_INDEX);

        assertParseFailure(parser, "0" + DATE + EXERCISE, expectedMessage); // zero
        assertParseFailure(parser, "-1" + DATE + EXERCISE, expectedMessage); // negative
        assertParseFailure(parser, "99999999999" + DATE + EXERCISE, expectedMessage); // too large for an int
    }

    @Test
    public void parse_invalidDate_failure() {
        String expectedMessage = withUsage(ParserUtil.MESSAGE_INVALID_RECORD_DATE);

        assertParseFailure(parser, "1 d/18-09-2026" + EXERCISE, expectedMessage); // wrong format
        assertParseFailure(parser, "1 d/2026-02-30" + EXERCISE, expectedMessage); // not a real date
        assertParseFailure(parser, "1 d/0000-01-01" + EXERCISE, expectedMessage); // year 0
        assertParseFailure(parser, "1 d/" + EXERCISE, expectedMessage); // empty
    }

    @Test
    public void parse_invalidExercise_failure() {
        String expectedMessage = withUsage(ExerciseName.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, "1" + DATE + " e/", expectedMessage); // empty
        assertParseFailure(parser, "1" + DATE + " e/Bench@Press", expectedMessage); // unsupported character
        assertParseFailure(parser, "1" + DATE + " e/" + "a".repeat(51), expectedMessage); // too long
    }
}
