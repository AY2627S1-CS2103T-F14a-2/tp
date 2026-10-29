package seedu.address.logic.parser;

import static seedu.address.logic.parser.CommandParserTestUtil.assertParseFailure;
import static seedu.address.logic.parser.CommandParserTestUtil.assertParseSuccess;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_EMPTY_TAG;
import static seedu.address.logic.parser.FilterCommandParser.MESSAGE_INVALID_TAG;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.FilterCommand;
import seedu.address.model.person.TagContainsKeywordsPredicate;

public class FilterCommandParserTest {

    private static final String MESSAGE_INVALID_FORMAT = FilterCommand.MESSAGE_INVALID_FORMAT;

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
    public void parse_errorMessages_exactWording() {
        assertParseFailure(parser, " rehab", "Invalid command format!\nUse: filter t/TAG [t/MORE_TAGS]...\n"
                + "Example: filter t/rehab t/strength");
        assertParseFailure(parser, " t/", "Tags to filter by cannot be empty.\nUse: filter t/TAG [t/MORE_TAGS]...");
        assertParseFailure(parser, " t/re--hab",
                "Tag names must be 1 to 50 characters: English letters and digits, with single hyphens between them "
                + "(e.g. lower-back).\nUse: filter t/TAG [t/MORE_TAGS]...");
    }

    @Test
    public void parse_extraTextAfterTag_throwsInvalidTag() {
        // the extra text becomes part of the tag, which then contains a space or a slash
        assertParseFailure(parser, " t/rehab extra", MESSAGE_INVALID_TAG);
        assertParseFailure(parser, " t/rehab x/foo", MESSAGE_INVALID_TAG);
    }

    @Test
    public void parse_repeatedTagsAnyCase_keepsFirstSpelling() {
        assertParseSuccess(parser, " t/rehab t/REHAB",
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("rehab"))));
        assertParseSuccess(parser, " t/Knee t/rehab t/knee t/REHAB",
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("Knee", "rehab"))));
    }

    @Test
    public void parse_threeTags_keepsOrder() {
        assertParseSuccess(parser, " t/rehab t/knee t/strength",
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("rehab", "knee", "strength"))));
    }

    @Test
    public void parse_emptyTag_throwsParseException() {
        assertParseFailure(parser, " t/", MESSAGE_EMPTY_TAG);
        assertParseFailure(parser, " t/rehab t/   ", MESSAGE_EMPTY_TAG);

        // an empty tag is reported before an invalid one
        assertParseFailure(parser, " t/ t/a-b", MESSAGE_EMPTY_TAG);
        assertParseFailure(parser, " t/a-b t/", MESSAGE_EMPTY_TAG);
    }

    @Test
    public void parse_invalidTag_throwsParseException() {
        assertParseFailure(parser, " t/best friends", MESSAGE_INVALID_TAG); // space
        assertParseFailure(parser, " t/re--hab", MESSAGE_INVALID_TAG); // repeated hyphen
        assertParseFailure(parser, " t/rehab!", MESSAGE_INVALID_TAG); // symbol

        // one invalid tag among valid ones, in either position
        assertParseFailure(parser, " t/rehab t/re--hab", MESSAGE_INVALID_TAG);
        assertParseFailure(parser, " t/re--hab t/rehab", MESSAGE_INVALID_TAG);
    }

    @Test
    public void parse_validBoundaryTags_returnsFilterCommand() {
        assertParseSuccess(parser, " t/lower-back",
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("lower-back"))));
        assertParseSuccess(parser, " t/" + "a".repeat(50),
                new FilterCommand(new TagContainsKeywordsPredicate(List.of("a".repeat(50)))));
        assertParseFailure(parser, " t/" + "a".repeat(51), MESSAGE_INVALID_TAG);
        // single character
        assertParseSuccess(parser, " t/a", new FilterCommand(new TagContainsKeywordsPredicate(List.of("a"))));

        // upper case is kept; matching ignores case
        assertParseSuccess(parser, " t/REHAB", new FilterCommand(new TagContainsKeywordsPredicate(List.of("REHAB"))));

        // surrounding spaces are ignored
        assertParseSuccess(parser, " t/ rehab ", new FilterCommand(new TagContainsKeywordsPredicate(List.of("rehab"))));
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
