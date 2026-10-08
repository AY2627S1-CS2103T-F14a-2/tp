package seedu.address.logic.parser;

import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_EMPTY_TAG;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.TagContainsKeywordsPredicate;

public class FilterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT =
            String.format(MESSAGE_INVALID_COMMAND_FORMAT, FilterCommand.MESSAGE_USAGE);

    private final FilterCommandParser parser = new FilterCommandParser();

    @Test
    public void parse_noTags_throwsParseException() {
        assertParseFailure(parser, "     ", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_nonEmptyPreamble_throwsParseException() {
        assertParseFailure(parser, " rehab", MESSAGE_INVALID_FORMAT);
        assertParseFailure(parser, " rehab t/strength", MESSAGE_INVALID_FORMAT);
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        assertParseFailure(parser, " t/", MESSAGE_EMPTY_TAG);
        assertParseFailure(parser, " t/rehab t/   ", MESSAGE_EMPTY_TAG);
    }

    @Test
    public void parse_validArgs_returnsFilterCommand() {
        FilterCommand expectedCommand =
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("rehab", "strength")));

        // no leading and trailing whitespaces
        assertParseSuccess(parser, " t/rehab t/strength", expectedCommand);

        // multiple whitespaces between tags
        assertParseSuccess(parser, " \n t/rehab \n \t t/strength  \t", expectedCommand);
    }
}
