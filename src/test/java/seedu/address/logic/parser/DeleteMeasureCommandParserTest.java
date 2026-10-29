package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.index.Index;
import seedu.address.logic.commands.DeleteMeasureCommand;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.person.MeasurementType;

/**
 * Checks deletion syntax, dispatch and validation independently of measurement values.
 */
public class DeleteMeasureCommandParserTest {
    private final DeleteMeasureCommandParser parser = new DeleteMeasureCommandParser();

    @Test
    public void parse_validTypes_acceptsWhitespaceCaseOrderAndFutureDates() throws Exception {
        LocalDate future = LocalDate.now().plusYears(1);
        for (MeasurementType type : MeasurementType.values()) {
            DeleteMeasureCommand expected = new DeleteMeasureCommand(Index.fromOneBased(2), future, type);
            assertEquals(expected, parser.parse(" 2 t/ " + type.name() + " d/ " + future + " "));
            assertEquals(expected, new AddressBookParser().parseCommand(
                    "deletemeasure 2 d/" + future + " t/" + type));
        }
        assertEquals(new DeleteMeasureCommand(Index.fromOneBased(1), LocalDate.of(1, 1, 1), MeasurementType.WEIGHT),
                parser.parse("1 d/0001-01-01 t/WeIgHt"));
    }

    @Test
    public void parse_missingRepeatedOrExtraArguments_rejectsInput() {
        for (String args : new String[] {"", "1", "d/2026-09-12 t/weight", "one d/2026-09-12 t/weight",
            "1 extra d/2026-09-12 t/weight", "+1 d/2026-09-12 t/weight", "1 d/2026-09-12", "1 t/weight",
            "1 d/2026-09-12 d/2026-09-13 t/weight", "1 d/2026-09-12 t/weight t/waist"}) {
            assertEquals(DeleteMeasureCommand.MESSAGE_INVALID_FORMAT,
                    assertThrows(ParseException.class, () -> parser.parse(args)).getMessage());
        }
        assertThrows(ParseException.class, () -> parser.parse("1 d/2026-09-12 t/weight extra"));
    }

    @Test
    public void parse_unknownPrefix_rejectsFormat() {
        // v/ copied from a measure command would otherwise be reported as an invalid type
        for (String args : new String[] {"1 d/2026-09-12 t/weight v/72", "1 d/2026-09-12 x/1 t/weight"}) {
            assertEquals(DeleteMeasureCommand.MESSAGE_INVALID_FORMAT,
                    assertThrows(ParseException.class, () -> parser.parse(args)).getMessage());
        }
    }

    @Test
    public void parse_invalidIndex_showsClientIndexErrorWithUsage() {
        for (String index : new String[] {"0", "-1", "2147483648", "999999999999999999999"}) {
            assertError(index + " d/2026-09-12 t/weight", DeleteMeasureCommand.MESSAGE_INVALID_INDEX);
        }
    }

    @Test
    public void parse_invalidDateOrType_showsValidationErrorWithUsage() {
        for (String date : new String[] {"", "2026-9-12",
            "12-09-2026", "2026-09-12 extra", "2026-13-01", "0000-01-01"}) {
            assertError("1 d/" + date + " t/weight", ParserUtil.MESSAGE_DATE_FORMAT);
        }
        for (String date : new String[] {"2026-02-30", "2027-02-29"}) {
            assertError("1 d/" + date + " t/weight", date + " is not a real date.");
        }
        for (String type : new String[] {"", "height", "body fat", "weight extra"}) {
            assertError("1 d/2026-09-12 t/" + type, MeasurementType.MESSAGE_CONSTRAINTS);
        }
    }

    private void assertError(String args, String message) {
        assertEquals(message + "\nUse: " + DeleteMeasureCommand.MESSAGE_USAGE,
                assertThrows(ParseException.class, () -> parser.parse(args)).getMessage());
    }
}
