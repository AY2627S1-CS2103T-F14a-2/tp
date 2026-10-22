package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.MeasureCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.Measurement;
import seedu.address.model.person.MeasurementType;

/**
 * Tests measurement syntax, validation and top-level command dispatch.
 */
public class MeasureCommandParserTest {
    private final MeasureCommandParser parser = new MeasureCommandParser();

    @Test
    public void parse_validBoundaryValues_acceptsAllTypes() throws Exception {
        String[][] boundaries = {{"weight", "1"}, {"weight", "500"}, {"bodyfat", "0.01"},
            {"bodyfat", "100"}, {"waist", "1"}, {"waist", "300"}};
        for (String[] boundary : boundaries) {
            Measurement expected = new Measurement(LocalDate.of(2026, 9, 12),
                    MeasurementType.valueOf(boundary[0].toUpperCase(java.util.Locale.ROOT)),
                    new BigDecimal(boundary[1]));
            assertEquals(new MeasureCommand(Index.fromOneBased(1), expected),
                    parser.parse("1 d/2026-09-12 t/" + boundary[0] + " v/" + boundary[1]));
        }
    }

    @Test
    public void parse_validInput_acceptsWhitespaceCaseAndPrefixOrder() throws Exception {
        MeasureCommand expected = new MeasureCommand(Index.fromOneBased(2),
                new Measurement(LocalDate.of(2026, 9, 12), MeasurementType.WEIGHT, new BigDecimal("72.4")));
        assertEquals(expected, parser.parse(" 2  v/72.40 t/ WeIgHt  d/ 2026-09-12 "));
        assertEquals(expected, new AddressBookParser().parseCommand("measure 2 d/2026-09-12 t/weight v/72.4"));
        for (MeasurementType type : MeasurementType.values()) {
            assertEquals(new MeasureCommand(Index.fromOneBased(1),
                    new Measurement(LocalDate.now(), type, BigDecimal.ONE)),
                    parser.parse("1 d/" + LocalDate.now() + " t/" + type + " v/1"));
        }
    }

    @Test
    public void parse_missingOrRepeatedPrefix_rejectsFormat() {
        for (String args : new String[] {"", "1 d/2026-09-12 t/weight", "1 t/weight v/72",
            "1 d/2026-09-12 v/72", "1 d/2026-09-12 t/weight v/72 v/73",
            "1 d/2026-09-12 d/2026-09-13 t/weight v/72", "1 d/2026-09-12 t/weight t/waist v/72"}) {
            assertEquals(MeasureCommand.MESSAGE_INVALID_FORMAT,
                    assertThrows(ParseException.class, () -> parser.parse(args)).getMessage());
        }
    }

    @Test
    public void parse_invalidValues_rejectsWithUsage() {
        String[] invalidArguments = {
            "0 d/2026-09-12 t/weight v/72", "-1 d/2026-09-12 t/weight v/72",
            "2147483648 d/2026-09-12 t/weight v/72", "1 extra d/2026-09-12 t/weight v/72",
            "1 d/2026-02-30 t/weight v/72", "1 d/12/9/2026 t/weight v/72",
            "1 d/2026-9-12 t/weight v/72", "1 d/" + LocalDate.now().plusDays(1) + " t/weight v/72",
            "1 d/2026-09-12 t/height v/72", "1 d/2026-09-12 t/ v/72",
            "1 d/2026-09-12 t/weight v/NaN", "1 d/2026-09-12 t/weight v/Infinity",
            "1 d/2026-09-12 t/weight v/1e2", "1 d/2026-09-12 t/weight v/72 extra",
            "1 d/2026-09-12 t/weight v/", "1 d/2026-09-12 t/weight v/72.123",
            "1 d/2026-09-12 t/weight v/0.99", "1 d/2026-09-12 t/weight v/500.01",
            "1 d/2026-09-12 t/bodyfat v/0", "1 d/2026-09-12 t/bodyfat v/100.01",
            "1 d/2026-09-12 t/waist v/0.99", "1 d/2026-09-12 t/waist v/300.01"
        };
        for (String args : invalidArguments) {
            assertTrue(assertThrows(ParseException.class, () -> parser.parse(args)).getMessage()
                    .endsWith("Use: " + MeasureCommand.MESSAGE_USAGE));
        }
    }

    @Test
    public void parse_specificValidationMessages_matchSpecification() {
        assertError("1 d/2026-02-30 t/weight v/72",
                String.format(ParserUtil.MESSAGE_NONEXISTENT_RECORD_DATE, "2026-02-30"));
        assertError("1 d/12/9/2026 t/weight v/72", ParserUtil.MESSAGE_INVALID_RECORD_DATE);
        assertError("1 d/" + LocalDate.now().plusDays(1) + " t/weight v/72", Measurement.MESSAGE_FUTURE_DATE);
        assertError("1 d/2026-09-12 t/height v/72", MeasurementType.MESSAGE_CONSTRAINTS);
        assertError("1 d/2026-09-12 t/weight v/501", MeasurementType.WEIGHT.getValueConstraints());
        assertError("1 d/2026-09-12 t/bodyfat v/0", MeasurementType.BODYFAT.getValueConstraints());
        assertError("1 d/2026-09-12 t/waist v/301", MeasurementType.WAIST.getValueConstraints());
        assertError("1 d/2026-09-12 t/weight v/abc", MeasureCommandParser.MESSAGE_NON_NUMERIC);
        assertError("1 d/2026-09-12 t/weight v/72.123", Measurement.MESSAGE_PRECISION);
        assertError("0 d/2026-09-12 t/weight v/72", MeasureCommand.MESSAGE_INVALID_INDEX);
    }

    @Test
    public void parseRecordDate_futureMessage_isCallerSupplied() {
        assertEquals("Performance date is in the future.", assertThrows(ParseException.class, () ->
                ParserUtil.parseRecordDate(LocalDate.now().plusDays(1).toString(),
                        "Performance date is in the future.")).getMessage());
    }

    private void assertError(String args, String message) {
        assertEquals(message + "\nUse: " + MeasureCommand.MESSAGE_USAGE,
                assertThrows(ParseException.class, () -> parser.parse(args)).getMessage());
    }
}
