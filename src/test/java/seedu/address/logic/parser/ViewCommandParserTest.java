package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_INVALID_PERSON_DISPLAYED_INDEX;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.testutil.TypicalIndexes.INDEX_FIRST_PERSON;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ViewCommand;

public class ViewCommandParserTest {

    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validArgs_returnsViewCommand() {
        assertParseSuccess(parser, "1", new ViewCommand(INDEX_FIRST_PERSON));
        assertParseSuccess(parser, "  1  ", new ViewCommand(INDEX_FIRST_PERSON)); // surrounding spaces
    }

    @Test
    public void parse_invalidFormat_throwsParseException() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE);
        assertParseFailure(parser, "", expectedMessage); // missing index
        assertParseFailure(parser, "   ", expectedMessage); // blank index
        assertParseFailure(parser, "a", expectedMessage); // non-numeric
        assertParseFailure(parser, "1.5", expectedMessage); // not a whole number
        assertParseFailure(parser, "1 abc", expectedMessage); // extra text
        assertParseFailure(parser, "2 10", expectedMessage); // more than one index
    }

    @Test
    public void parse_nonPositiveOrOverflowingIndex_throwsInvalidIndex() {
        assertParseFailure(parser, "0", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertParseFailure(parser, "-1", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX);
        assertParseFailure(parser, "99999999999", MESSAGE_INVALID_PERSON_DISPLAYED_INDEX); // exceeds int range
    }
}
