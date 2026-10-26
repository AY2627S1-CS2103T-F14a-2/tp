package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;
import static seedu.address.testutil.TypicalIndexes.INDEX_SECOND_PERSON;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.logic.Messages;
import seedu.address.logic.commands.PerformanceCommand;
import seedu.address.model.progress.ExerciseName;
import seedu.address.model.progress.GymPerformance;
import seedu.address.model.progress.Load;

public class PerformanceCommandParserTest {

    private static final String DATE = " d/2026-09-18";
    private static final String EXERCISE = " e/Bench Press";
    private static final String SETS = " s/3";
    private static final String REPS = " r/8";
    private static final String LOAD = " w/60";
    private static final String ALL_FIELDS = DATE + EXERCISE + SETS + REPS + LOAD;

    private static final GymPerformance BENCH_PRESS = new GymPerformance(LocalDate.of(2026, 9, 18),
            new ExerciseName("Bench Press"), 3, 8, new Load("60"));

    private final PerformanceCommandParser parser = new PerformanceCommandParser();

    @Test
    public void parse_allFieldsPresent_success() {
        PerformanceCommand expectedCommand = new PerformanceCommand(INDEX_FIRST_PERSON, BENCH_PRESS);
        assertParseSuccess(parser, "1" + ALL_FIELDS, expectedCommand);

        // prefixes in a different order
        assertParseSuccess(parser, "1" + LOAD + REPS + SETS + EXERCISE + DATE, expectedCommand);

        // extra whitespace around values
        assertParseSuccess(parser, "  1   d/ 2026-09-18  e/  Bench   Press  s/ 3  r/ 8  w/ 60 ", expectedCommand);
    }

    @Test
    public void parse_boundaryValues_success() {
        // bodyweight exercise with the smallest sets and reps
        GymPerformance pullUp = new GymPerformance(LocalDate.of(2026, 9, 18), new ExerciseName("Pull-up"),
                1, 1, new Load("0"));
        assertParseSuccess(parser, "2" + DATE + " e/Pull-up s/1 r/1 w/0",
                new PerformanceCommand(INDEX_SECOND_PERSON, pullUp));

        // largest sets, reps and load, with two decimal places
        GymPerformance heaviest = new GymPerformance(LocalDate.of(2026, 9, 18), new ExerciseName("Leg Press"),
                100, 1000, new Load("999.99"));
        assertParseSuccess(parser, "1" + DATE + " e/Leg Press s/100 r/1000 w/999.99",
                new PerformanceCommand(INDEX_FIRST_PERSON, heaviest));

        // today is accepted
        LocalDate today = LocalDate.now();
        GymPerformance todayRecord = new GymPerformance(today, new ExerciseName("Bench Press"), 3, 8,
                new Load("60"));
        assertParseSuccess(parser, "1 d/" + today + EXERCISE + SETS + REPS + LOAD,
                new PerformanceCommand(INDEX_FIRST_PERSON, todayRecord));
    }

    @Test
    public void parse_missingOrRepeatedPart_failure() {
        String expectedMessage = PerformanceCommand.MESSAGE_INVALID_FORMAT;

        // no arguments
        assertParseFailure(parser, "", expectedMessage);

        // missing index
        assertParseFailure(parser, ALL_FIELDS, expectedMessage);

        // each prefix missing
        assertParseFailure(parser, "1" + EXERCISE + SETS + REPS + LOAD, expectedMessage);
        assertParseFailure(parser, "1" + DATE + SETS + REPS + LOAD, expectedMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + REPS + LOAD, expectedMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + SETS + LOAD, expectedMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + SETS + REPS, expectedMessage);

        // repeated prefix
        assertParseFailure(parser, "1" + ALL_FIELDS + DATE, expectedMessage);
        assertParseFailure(parser, "1" + ALL_FIELDS + " w/70", expectedMessage);

        // non-numeric index or extra text before the first prefix
        assertParseFailure(parser, "a" + ALL_FIELDS, expectedMessage);
        assertParseFailure(parser, "1 extra" + ALL_FIELDS, expectedMessage);
        assertParseFailure(parser, "1 2" + ALL_FIELDS, expectedMessage);
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expectedMessage = withUsage(PerformanceCommand.MESSAGE_INVALID_INDEX);

        assertParseFailure(parser, "0" + ALL_FIELDS, expectedMessage); // zero
        assertParseFailure(parser, "-1" + ALL_FIELDS, expectedMessage); // negative
        assertParseFailure(parser, "99999999999" + ALL_FIELDS, expectedMessage); // too large for an int
    }

    @Test
    public void parse_invalidDate_failure() {
        String formatMessage = withUsage(ParserUtil.MESSAGE_INVALID_RECORD_DATE);

        assertParseFailure(parser, "1 d/18-09-2026" + EXERCISE + SETS + REPS + LOAD, formatMessage);
        assertParseFailure(parser, "1 d/2026-02-30" + EXERCISE + SETS + REPS + LOAD,
                withUsage(String.format(ParserUtil.MESSAGE_NONEXISTENT_RECORD_DATE, "2026-02-30")));
        assertParseFailure(parser, "1 d/" + EXERCISE + SETS + REPS + LOAD, formatMessage); // empty

        // tomorrow is in the future
        assertParseFailure(parser, "1 d/" + LocalDate.now().plusDays(1) + EXERCISE + SETS + REPS + LOAD,
                withUsage(PerformanceCommand.MESSAGE_FUTURE_DATE));
    }

    @Test
    public void parse_invalidExercise_failure() {
        String expectedMessage = withUsage(ExerciseName.MESSAGE_CONSTRAINTS);

        assertParseFailure(parser, "1" + DATE + " e/" + SETS + REPS + LOAD, expectedMessage); // empty
        assertParseFailure(parser, "1" + DATE + " e/Bench@Press" + SETS + REPS + LOAD, expectedMessage);
        assertParseFailure(parser, "1" + DATE + " e/" + "a".repeat(51) + SETS + REPS + LOAD, expectedMessage);
    }

    @Test
    public void parse_invalidSetsOrReps_failure() {
        String setsMessage = withUsage(GymPerformance.MESSAGE_SETS_CONSTRAINTS);
        assertParseFailure(parser, "1" + DATE + EXERCISE + " s/0" + REPS + LOAD, setsMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + " s/101" + REPS + LOAD, setsMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + " s/3.5" + REPS + LOAD, setsMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + " s/three" + REPS + LOAD, setsMessage);

        String repsMessage = withUsage(GymPerformance.MESSAGE_REPS_CONSTRAINTS);
        assertParseFailure(parser, "1" + DATE + EXERCISE + SETS + " r/0" + LOAD, repsMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + SETS + " r/1001" + LOAD, repsMessage);
        assertParseFailure(parser, "1" + DATE + EXERCISE + SETS + " r/-8" + LOAD, repsMessage);
    }

    @Test
    public void parse_invalidLoad_failure() {
        String prefix = "1" + DATE + EXERCISE + SETS + REPS;

        assertParseFailure(parser, prefix + " w/sixty", withUsage(Load.MESSAGE_NOT_NUMERIC));
        assertParseFailure(parser, prefix + " w/60kg", withUsage(Load.MESSAGE_NOT_NUMERIC));
        assertParseFailure(parser, prefix + " w/-1", withUsage(Load.MESSAGE_RANGE_CONSTRAINTS));
        assertParseFailure(parser, prefix + " w/1000.01", withUsage(Load.MESSAGE_RANGE_CONSTRAINTS));
        assertParseFailure(parser, prefix + " w/60.555", withUsage(Load.MESSAGE_DECIMAL_PLACES_CONSTRAINTS));
    }

    @Test
    public void parse_severalInvalidValues_reportsFirstInCommandOrder() {
        // invalid date and load: the date is reported first
        assertParseFailure(parser, "1 d/2026-13-01" + EXERCISE + SETS + REPS + " w/abc",
                withUsage(ParserUtil.MESSAGE_INVALID_RECORD_DATE));
    }

    private static String withUsage(String message) {
        return Messages.withUsage(message, PerformanceCommand.MESSAGE_USAGE);
    }
}
